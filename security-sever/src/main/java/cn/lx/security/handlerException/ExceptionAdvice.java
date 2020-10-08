package cn.lx.security.handlerException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * cn.lx.security.handlerException
 *
 * @Author Administrator
 * @date 18:05
 */
@RestControllerAdvice
public class ExceptionAdvice {

    //403异常就跳转到这个页面
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(){
        return "403异常,权限不足";
    }
}
