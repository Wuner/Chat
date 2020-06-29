package heath.com.chat.OKhttp.impl;

import java.util.Map;

import heath.com.chat.OKhttp.IRequest;

public class RequestImpl implements IRequest {

    private String method = POST;
    private String url;
    private Map<String, String> header;
    private Map<String, Object> body;

    public RequestImpl(String url) {
        /**
         *  初始化公共参数和头部信息
         */
        this.url = url;
    }

    @Override
    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    @Override
    public void setBody(Map<String, Object> body) {
        this.body = body;
    }


    @Override
    public String getUrl() {
        if (GET.equals(method)) {
            // 组装post请求参数
            for (String key : body.keySet()) {
                url = url.replace("${" + key + "}", body.get(key).toString());
            }
        }
        return url;
    }

    @Override
    public Map<String, String> getHeader() {
        return header;
    }

    @Override
    public Map<String,Object> getBody() {
        // 组装post请求参数
        return body;
    }
}