package cn.lx.security.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * cn.lx.security.util
 *
 * @Author Administrator
 * @date 16:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {

    private String code;
    private String msg;
    private Object data;

    public Result(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
