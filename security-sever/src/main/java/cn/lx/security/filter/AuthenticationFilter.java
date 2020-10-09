package cn.lx.security.filter;

import cn.lx.security.doamin.SysUser;
import cn.lx.security.util.JwtUtil;
import cn.lx.security.util.ResponseUtil;
import cn.lx.security.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * cn.lx.security.filter
 *
 * @Author Administrator
 * @date 16:32
 */
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public  AuthenticationFilter(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    /**
     * Default behaviour for successful authentication.
     * <ol>
     * <li>Sets the successful <tt>Authentication</tt> object on the
     * {@link SecurityContextHolder}</li>
     * <li>Informs the configured <tt>RememberMeServices</tt> of the successful login</li>
     * <li>Fires an {@link InteractiveAuthenticationSuccessEvent} via the configured
     * <tt>ApplicationEventPublisher</tt></li>
     * <li>Delegates additional behaviour to the {@link AuthenticationSuccessHandler}.</li>
     * </ol>
     * <p>
     * Subclasses can override this method to continue the {@link FilterChain} after
     * successful authentication.
     *
     * @param request
     * @param response
     * @param chain
     * @param authResult the object returned from the <tt>attemptAuthentication</tt>
     *                   method.
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        SecurityContextHolder.getContext().setAuthentication(authResult);

        // Fire event
        if (this.eventPublisher != null) {
            eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(
                    authResult, this.getClass()));
        }
        //创建令牌
        Map<String, Object> claims=new HashMap<>();
        SysUser sysUser = (SysUser) authResult.getPrincipal();
        claims.put("username",sysUser.getUsername());
        claims.put("authorities",authResult.getAuthorities());
        String jwt = JwtUtil.createJwt(claims);
        //直接返回json
        ResponseUtil.responseJson(new Result("200", "登录成功",jwt),response);


    }


    /**
     * Default behaviour for unsuccessful authentication.
     * <ol>
     * <li>Clears the {@link SecurityContextHolder}</li>
     * <li>Stores the exception in the session (if it exists or
     * <tt>allowSesssionCreation</tt> is set to <tt>true</tt>)</li>
     * <li>Informs the configured <tt>RememberMeServices</tt> of the failed login</li>
     * <li>Delegates additional behaviour to the {@link AuthenticationFailureHandler}.</li>
     * </ol>
     *
     * @param request
     * @param response
     * @param failed
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        //直接返回json
        ResponseUtil.responseJson(new Result("500", "登录失败"),response);

    }
}
