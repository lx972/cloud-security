package cn.lx.security.util;

import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * cn.lx.security.util
 *
 * @Author Administrator
 * @date 16:10
 */
public class ResponseUtil {

    /**
     * 将结果以json格式返回
     * @param result    返回结果
     * @param response
     * @throws IOException
     */
    public static void responseJson(Result result, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(200);
        PrintWriter writer = response.getWriter();
        writer.write(JSON.toJSONString(result));
        writer.flush();
        writer.close();
    }
}
