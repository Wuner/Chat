package heath.com.chat.OKhttp;

import java.util.Map;

public interface IRequest {
    public static final String POST = "POST";
    public static final String GET = "GET";
    public static final String DELETE = "DELETE";
    public static final String PUT = "PUT";

    /**
     *  请求方式
     * @param method
     */
    void setMethod(String method);

    /**
     *  指定请求头
     * @param header
     */
    void setHeader(Map<String,String> header);

    /**
     *  指定请求信息
     * @param body
     */
    void setBody(Map<String,Object> body);

    /**
     * 提供给执行库请求行URL
     * @return
     */
    String getUrl();

    /**
     * 提供给执行库请求行URL
     * @return
     */
    Map<String,String> getHeader();

    /**
     *  请求体
     * @return
     */
    Map<String,Object> getBody();

}