package cn.lx.security.filter;

import cn.lx.security.doamin.SysPermission;
import cn.lx.security.util.JwtUtil;
import cn.lx.security.util.ResponseUtil;
import cn.lx.security.util.Result;
import com.alibaba.fastjson.JSON;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * cn.lx.security.filter
 *
 * @Author Administrator
 * @date 15:29
 */


public class TokenVerifyFilter extends BasicAuthenticationFilter {


    /**
     * Creates an instance which will authenticate against the supplied
     * {@code AuthenticationManager} and which will ignore failed authentication attempts,
     * allowing the request to proceed down the filter chain.
     *
     * @param authenticationManager the bean to submit authentication requests to
     */
    public TokenVerifyFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    /**
     * 过滤请求，判断是否携带令牌
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String header = request.getHeader("Authorization");

        if (header == null || !header.toLowerCase().startsWith("bearer ")) {
            //直接返回json
            ResponseUtil.responseJson(new Result("403", "用户未登录"),response);
            return;
        }

        //得到jwt令牌
        String jwt = StringUtils.replace(header, "bearer ", "");
        //解析令牌
        String[] tokens = JwtUtil.extractAndDecodeJwt(jwt);

        //用户名
        String username = tokens[0];
        //权限
        List<SysPermission> authorities= JSON.parseArray(tokens[1], SysPermission.class);

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                username,
                null,
                authorities
                );

        SecurityContextHolder.getContext().setAuthentication(authRequest);

        chain.doFilter(request, response);
    }
}
