package heath.com.chat.OKhttp;

import java.io.File;
import java.util.Map;

public interface IHttpClient {

    IResponse get(IRequest request);

    /**
     *  json格式的post
     * @param request
     * @return
     */
    IResponse post(IRequest request);

    /**
     *  表单类型的post
     * @param request
     * @param map
     * @param file
     * @return
     */
    IResponse upload_image_post(IRequest request, Map<String, Object> map, File file);
    IResponse delete(IRequest request);
    IResponse put(IRequest request);
}