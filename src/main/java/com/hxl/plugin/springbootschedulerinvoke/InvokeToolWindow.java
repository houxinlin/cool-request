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
import com.intellij.util.io.URLUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.channels.FileLock;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Consumer;


public class InvokeToolWindow implements ClientsTableRenderer.Callback {
    //项目端口
    private static int port = 0;
    private final Project project;
    private JPanel myToolWindowContent;
    private JTable table1;
    private JButton refresh;
    private JButton helpButton;
    private final Map<Integer, String> methodIdMap = new HashMap<>();
    private static final String INVOKE_METHOD_COMMAND = "'{'\"action\":\"invokeMethod\",\"arg\":\"{0}\"}";
    private final NotificationGroup NOTIFICATION_GROUP = new NotificationGroup("com.hxl.plugin.scheduled-invoke", NotificationDisplayType.BALLOON, true);
    private static final String MESSAGE_INIT_OK = "插件依赖已初始化，需重新启动项目";
    private static final String MESSAGE_CONNECT_FAIL = "与项目连接失败,可能是项目未启动";
    private static final String MESSAGE_REFRESH_FAIL = "刷新失败";
    private static final String MESSAGE_CALL_FAIL = "调用失败";
    private static PrintStream logPrintStream;
    private final List<Map<String, Object>> scheduled = new ArrayList<>();

    //数据map
    private final Map<SocketChannel, List<Map<String, Object>>> dataMaps = new HashMap() {
        @Override
        public Object remove(Object key) {
            Object result = super.remove(key);
            logPrintStream.println("删除客户端" + result);
            reloadTable();
            return result;
        }
    };

    static {
        try {
            if (!Files.exists(Constant.CONFIG_WORK_HOME)) Files.createDirectories(Constant.CONFIG_WORK_HOME);
            logPrintStream = new PrintStream(new FileOutputStream(Constant.CONFIG_LOG_PATH.toString()));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException ignored) {

        }
    }

    private boolean checkAndInstallDependency() {
        LibraryTable projectLibraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project);
        final LibraryTable.ModifiableModel projectLibraryModel = projectLibraryTable.getModifiableModel();
        if (projectLibraryModel.getLibraryByName(Constant.LIB_NAME) == null) {
            for (Module module : ModuleManager.getInstance(project).getModules()) {
                logPrintStream.println("检测到模块:" + module.getName());
                addDependency(project, module, List.of(Constant.CONFIG_LIB_PATH));
            }
            return false;
        }
        return true;
    }

    private void addDependency(Project project, Module module, List<Path> classPaths) {
        LibraryTable projectLibraryTable = LibraryTablesRegistrar.getInstance().getLibraryTable(project);
        final LibraryTable.ModifiableModel projectLibraryModel = projectLibraryTable.getModifiableModel();
        for (Path path : classPaths) {
            Library library = projectLibraryModel.getLibraryByName(Constant.LIB_NAME);
            if (library != null) return;
            library = projectLibraryModel.createLibrary(Constant.LIB_NAME);
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

    /**
     * 单击调用
     */
    @Override
    public void click(int row) {
        try {
            Map<String, Object> map = scheduled.get(row);
            logPrintStream.println("调用方法" + map);
            DataWrapper socketData = (DataWrapper) map.get("socketData");
            String format = MessageFormat.format(INVOKE_METHOD_COMMAND, this.methodIdMap.get(row));
            socketData.socketChannel.write(StandardCharsets.UTF_8.encode(format));
        } catch (IOException e) {
            notification(MESSAGE_CALL_FAIL);
            e.printStackTrace(logPrintStream);
        }

    }

    /**
     * 释放依赖
     */
    private void loadJar() {
        if (!Files.exists(Constant.CONFIG_LIB_PATH.getParent())) {
            try {
                Files.createDirectories(Constant.CONFIG_LIB_PATH.getParent());
            } catch (IOException ignored) {
            }
        }
        if (!Files.exists(Constant.CONFIG_LIB_PATH)) {
            URL resource = getClass().getResource(Constant.CLASSPATH_LIB_PATH);
            if (resource == null) {
                logPrintStream.println("lib无法获取");
                return;
            }
            try (InputStream inputStream = resource.openStream();) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[16384];
                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                Files.write(Constant.CONFIG_LIB_PATH, buffer.toByteArray());
            } catch (IOException e) {
                e.printStackTrace(logPrintStream);
            }
        }

    }

    private void reloadTable() {
        scheduled.clear();
        dataMaps.values().forEach(scheduled::addAll);
        notification("获取任务成功，共" + scheduled.size() + "个");
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
        logPrintStream.println("成功");
    }

    private void setTableData(DataWrapper dataWrapper) {
        //当前客户端中的方法
        List<Map<String, Object>> methods = new Gson().fromJson(dataWrapper.toString(), new TypeToken<List<Map<String, Object>>>() {
        }.getType());

        methods.forEach(map -> {
            map.put("socketData", dataWrapper);
        });
        //保持记录
        dataMaps.computeIfAbsent(dataWrapper.socketChannel, k -> new ArrayList<>());
        dataMaps.get(dataWrapper.socketChannel).addAll(methods);
        //合并数据
        reloadTable();
    }

    public InvokeToolWindow(ToolWindow toolWindow, Project project) {
        this.project = project;
        Consumer<SocketChannel> closeConsumer = dataMaps::remove;
        Consumer<DataWrapper> messageConsumer = this::setTableData;
        //启动服务Socket
        FileLock lock = null;
        try (FileOutputStream fileOutputStream = new FileOutputStream(Constant.CONFIG_LOCK_PATH.toString())) {
            lock = fileOutputStream.getChannel().lock();
            port = SocketUtils.findPort();
            String projectPath = project.getBasePath();
            if (project.getBasePath().contains("\\")) projectPath = projectPath.replaceAll("\\\\", "/");
            logPrintStream.println("项目路径" + projectPath);
            Path portPath = Paths.get(Constant.CONFIG_WORK_HOME.toString(), generateMD5(projectPath));
            Files.write(portPath, String.valueOf(port).getBytes());
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            logPrintStream.println(project.getName() + "绑定的端口为" + port);
            serverSocketChannel.bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);
            loadJar();
            checkAndInstallDependency();
            new Thread(new ProjectServerSocket(serverSocketChannel, logPrintStream, messageConsumer, closeConsumer, port)).start();
        } catch (IOException e) {
            e.printStackTrace(logPrintStream);
        } finally {
            if (lock != null) {
                try {
                    lock.release();
                    lock.close();
                } catch (IOException ignored) {
                }
            }
        }

        helpButton.addActionListener(e -> {
            Help.showDocument("http://www.houxinlin.com");
        });
        refresh.addActionListener(e -> {
            //先检测依赖
            loadJar();
            checkAndInstallDependency();
            String[] options = new String[]{"Ok"};
            if (!checkAndInstallDependency()) {
                Messages.showDialog(MESSAGE_INIT_OK, "提示", options, 0, Messages.getInformationIcon());
            } else {
                Messages.showDialog("已初始化", "提示", options, 0, Messages.getInformationIcon());
            }
        });
    }

    private void notification(String msg) {
        final Notification notification = NOTIFICATION_GROUP.createNotification(msg, NotificationType.INFORMATION);
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
        } catch (NoSuchAlgorithmException ignored) {
        }
        return "";
    }

    public JPanel getContent() {
        return myToolWindowContent;
    }

}