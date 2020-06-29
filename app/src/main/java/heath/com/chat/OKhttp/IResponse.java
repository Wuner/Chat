package heath.com.chat.OKhttp;

public interface IResponse {

    /**
     *  响应码
     * @return
     */
    int getCode();

    /**
     *  返回的数据
     * @return
     */
    String getData();
}