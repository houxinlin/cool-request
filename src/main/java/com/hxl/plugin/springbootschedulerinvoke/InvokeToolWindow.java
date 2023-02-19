package com.hxl.plugin.springbootschedulerinvoke;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.notification.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.DependencyScope;
import com.intellij.openapi.roots.ModuleRootModificationUtil;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.util.PathUtil;
import com.intellij.util.io.URLUtil;
import org.apache.commons.compress.utils.ByteUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hxl.plugin.springbootschedulerinvoke.Constant.*;
import static com.hxl.plugin.springbootschedulerinvoke.Constant.CONFIG_LIB_PATH;

public class InvokeToolWindow implements ClientsTableRenderer.Callback {
    private static final String LIBNAME = "invoke-scheduled";
    private static final Path CONFIG_DIR = Paths.get(System.getProperty("user.home"), ".config", "hxl-plugin");
    private int port;
    private Project project;
    private JPanel myToolWindowContent;
    private JTable table1;
    private JButton refresh;
    private Map<Integer, String> methodIdMap = new HashMap<>();
    private final String REFRESH_METHOD_COMMAND = "{\"action\":\"listMethod\",\"arg\":\"\"}";
    private static final String INVOKE_METHOD_COMMAND = "'{'\"action\":\"invokeMethod\",\"arg\":\"{0}\"}";
    private final NotificationGroup NOTIFICATION_GROUP = new NotificationGroup("com.hxl.plugin.scheduled-invoke", NotificationDisplayType.BALLOON, true);
    private static final String MESSAGE_INIT_OK = "插件已初始化，需重新启动项目后再次点击刷新";
    private static final String MESSAGE_CONNECT_FAIL = "与项目连接失败,可能是项目未启动";
    private static final String MESSAGE_REFRESH_FAIL = "刷新失败";
    private static final String MESSAGE_CALL_FAIL = "调用失败";


    private boolean checkAndInstallDependency() {
        LibraryTable projectLibraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project);
        final LibraryTable.ModifiableModel projectLibraryModel = projectLibraryTable.getModifiableModel();
        if (projectLibraryModel.getLibraryByName(LIBNAME) == null) {
            for (Module module : ModuleManager.getInstance(project).getModules()) {
                if (module.getName().endsWith("main")) {
                    addDependency(project, module, Arrays.asList(Constant.CONFIG_LIB_PATH));
                }
            }
            return false;
        }
        return true;
    }

    private void addDependency(Project project, Module module, List<Path> classPaths) {
        LibraryTable projectLibraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project);
        final LibraryTable.ModifiableModel projectLibraryModel = projectLibraryTable.getModifiableModel();
        for (Path path : classPaths) {
            Library library = projectLibraryModel.getLibraryByName(LIBNAME);
            if (library != null) return;
            library = projectLibraryModel.createLibrary(LIBNAME);
            final Library.ModifiableModel libraryModel = library.getModifiableModel();

            String pathUrl = VirtualFileManager.constructUrl(URLUtil.JAR_PROTOCOL, path.toString()) + JarFileSystem.JAR_SEPARATOR;
            VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(pathUrl);

            if (file != null) {
                libraryModel.addRoot(file, OrderRootType.CLASSES);
                Library finalLibrary = library;
                ApplicationManager.getApplication().runWriteAction(() -> {
                    libraryModel.commit();
                    projectLibraryModel.commit();
                    ModuleRootModificationUtil.addDependency(module, finalLibrary, DependencyScope.COMPILE, false);
                });
            }
        }
    }

    @Override
    public void click(int row) {
        try (SocketChannel localhost = SocketChannel.open(new InetSocketAddress(this.port))) {
            String format = MessageFormat.format(INVOKE_METHOD_COMMAND, this.methodIdMap.get(row));
            localhost.write(StandardCharsets.UTF_8.encode(format));
        } catch (Exception e) {
            notification(MESSAGE_CALL_FAIL);
        }
    }

    private void refreshScheduledMethods() {
        table1.setModel(new DefaultTableModel());
        new Thread(() -> {
            methodIdMap.clear();
            try (SocketChannel localhost = SocketChannel.open(new InetSocketAddress(port))) {
                localhost.write(StandardCharsets.UTF_8.encode(REFRESH_METHOD_COMMAND));

                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                int readCount = localhost.read(byteBuffer);
                byteBuffer.flip();
                byte[] data = new byte[byteBuffer.limit()];
                System.arraycopy(byteBuffer.array(), 0, data, 0, readCount);
                String json = new String(data);

                List<Map<String, Object>> scheduled = new Gson().fromJson(json, new TypeToken<List<Map<String, Object>>>() {
                }.getType());
                notification("获取任务成功，共" + scheduled.size() + "个", NotificationType.INFORMATION);
                DefaultTableModel dm = new DefaultTableModel() {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return true;
                    }
                };
                Object[][] dataModel = new Object[scheduled.size()][3];
                for (int i = 0; i < scheduled.size(); i++) {
                    Map<String, Object> map = scheduled.get(i);
                    dataModel[i][0] = "调用";
                    dataModel[i][1] = map.get("method");
                    dataModel[i][2] = map.get("className");
                    methodIdMap.put(i, map.get("id").toString());
                }
                dm.setDataVector(dataModel, new Object[]{"Button", "String", "String"});
                table1.setModel(dm);
                table1.getColumnModel().getColumn(0).setCellRenderer(new ClientsTableButtonRenderer());
                table1.getColumnModel().getColumn(0).setCellEditor(new ClientsTableRenderer(new JCheckBox(), this));
                table1.setRowHeight(80);
            } catch (IOException ex) {
                notification(MESSAGE_REFRESH_FAIL);

            }
        }).start();
    }


    private void loadJar() {
        if (!Files.exists(CONFIG_DIR)) {
            try {
                Files.createDirectories(CONFIG_DIR);
            } catch (IOException ignored) {
            }
        }
        try {
            if (!Files.exists(CONFIG_LIB_PATH)) {
                URL resource = MyToolWindowFactory.class.getClassLoader().getResource("/lib");

                URL resource1 = getClass().getResource(CLASSPATH_LIB_PATH);
                InputStream inputStream = resource1.openStream();

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[16384];
                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                Files.write(CONFIG_LIB_PATH, buffer.toByteArray());
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    public InvokeToolWindow(ToolWindow toolWindow, Project project) {
        this.project = project;

        refresh.addActionListener(e -> {
            //先检测依赖
            String[] options = new String[]{"Ok"};
            loadJar();
            if (!checkAndInstallDependency()) {
                Messages.showDialog(MESSAGE_INIT_OK, "提示", options, 0, Messages.getInformationIcon());
                return;
            }
            //有依赖情况
            String portPathName = generateMD5(project.getBasePath()) + "scheduled-invoke.port";
//            notification(portPathName);
//            Messages.showDialog(project.getBasePath(), "提示", options, 0, Messages.getInformationIcon());
            Path path = Paths.get(CONFIG_DIR.toString(), portPathName);
            if (Files.exists(path)) {
                try {
                    this.port = Integer.parseInt(Files.readString(path));
                    refreshScheduledMethods();
                    return;
                } catch (IOException ignored) {
                }
            }

            Messages.showDialog(MESSAGE_CONNECT_FAIL, "提示", options, 0, Messages.getInformationIcon());
        });
    }

    private void notification(String msg) {
        final Notification notification = NOTIFICATION_GROUP.createNotification(msg, NotificationType.ERROR);
        notification.notify(project);
    }

    private void notification(String msg, NotificationType notificationType) {
        final Notification notification = NOTIFICATION_GROUP.createNotification(msg, notificationType);
        notification.notify(project);
    }

    public String generateMD5(String input) {
        MessageDigest md5 = null;
        final char[] HEX_CHARS =
                {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(input.getBytes(StandardCharsets.UTF_8));
            char[] chars = new char[32];
            for (int i = 0; i < chars.length; i = i + 2) {
                byte b = bytes[i / 2];
                chars[i] = HEX_CHARS[(b >>> 0x4) & 0xf];
                chars[i + 1] = HEX_CHARS[b & 0xf];
            }
            return new String(chars);
        } catch (NoSuchAlgorithmException e) {
        }
        return "";
    }

    public JPanel getContent() {
        return myToolWindowContent;
    }

}