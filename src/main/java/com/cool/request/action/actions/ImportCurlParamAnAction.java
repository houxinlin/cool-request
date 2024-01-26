package com.cool.request.action.actions;

import com.cool.request.common.constant.icons.CoolRequestIcons;
import com.cool.request.component.http.net.FormDataInfo;
import com.cool.request.component.http.net.KeyValue;
import com.cool.request.component.http.net.MediaTypes;
import com.cool.request.lib.curl.ArgumentHolder;
import com.cool.request.lib.curl.BasicCurlParser;
import com.cool.request.lib.curl.StringArgumentHolder;
import com.cool.request.utils.MediaTypeUtils;
import com.cool.request.utils.StringUtils;
import com.cool.request.utils.UrlUtils;
import com.cool.request.view.IRequestParamManager;
import com.cool.request.view.dialog.BigInputDialog;
import com.cool.request.view.tool.ProviderManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ImportCurlParamAnAction extends BaseAnAction {
    public ImportCurlParamAnAction(Project project) {
        super(project, () -> "curl", CoolRequestIcons.CURL);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        importParam();
    }

    public void importParam() {
        BigInputDialog bigInputDialog = new BigInputDialog(getProject());
        bigInputDialog.show();

        try {
            BasicCurlParser.Request parse = new BasicCurlParser().parse(bigInputDialog.getValue());

            //找到参数管理器，设置header、formdata、json参数
            ProviderManager.findAndConsumerProvider(IRequestParamManager.class, getProject(), new Consumer<IRequestParamManager>() {
                @Override
                public void accept(IRequestParamManager iRequestParamManager) {
                    List<KeyValue> header = parse.getHeaders()
                            .stream()
                            .map(pair -> new KeyValue(StringUtils.headerNormalized(pair.getKey()), pair.getValue())).collect(Collectors.toList());
                    //设置请求头
                    iRequestParamManager.setHttpHeader(header);
                    String contentType = iRequestParamManager.getContentTypeFromHeader();


                    List<FormDataInfo> formDataInfos = parse.getFormData().stream().map(stringArgumentHolderPair -> {
                        ArgumentHolder argumentHolder = stringArgumentHolderPair.getValue();
                        String value = "";
                        value = argumentHolder.getName();
                        return new FormDataInfo(stringArgumentHolderPair.getKey(), value, argumentHolder instanceof StringArgumentHolder ? "text" : "file");
                    }).collect(Collectors.toList());

                    //设置form data
                    iRequestParamManager.setFormData(formDataInfos);

                    //1.如果没有设置contentType，但是解析到了form data，则设置为form data
                    if (StringUtils.isEmpty(contentType) && (!formDataInfos.isEmpty())) {
                        contentType = MediaTypes.MULTIPART_FORM_DATA;
                    }
                    //2.如果解析到了form data,其他不需要设置了
                    if (MediaTypes.MULTIPART_FORM_DATA.equalsIgnoreCase(contentType)) {
                        iRequestParamManager.setRequestBodyType(MediaTypes.MULTIPART_FORM_DATA); //剩余的类型都设置为raw文本类型
                        return;
                    }
                    //3.如果解析推测post data是json格式，则设置为json数据
                    if (!StringUtils.isEmpty(parse.getPostData()) && StringUtils.isValidJson(parse.getPostData())) {
                        contentType = MediaTypes.APPLICATION_JSON;
                    }

                    //4. 如果解析推测post data是x-www-form-urlencoded格式，则设置为x-www-form-urlencoded数据

                    //根据contentType的不同，设置不同数据
                    if (MediaTypeUtils.isFormUrlencoded(contentType)) {
                        String postData = parse.getPostData();
                        List<KeyValue> keyValues = UrlUtils.parseFormData(postData);
                        iRequestParamManager.setUrlencodedBody(keyValues);//x-www-form-urlencoded
                    } else if (MediaTypeUtils.isJson(contentType) || MediaTypeUtils.isXml(contentType)) {
                        iRequestParamManager.setRequestBody(contentType, parse.getPostData());//json xml
                        iRequestParamManager.setRequestBodyType(contentType);
                    } else {
                        iRequestParamManager.setRequestBody(MediaTypes.TEXT, parse.getPostData());
                        iRequestParamManager.setRequestBodyType(MediaTypes.TEXT); //剩余的类型都设置为raw文本类型
                    }
                }
            });
        } catch (IllegalArgumentException exception) {
            Messages.showErrorDialog("Unable to parse parameters", "Tip");
        }

    }

}
