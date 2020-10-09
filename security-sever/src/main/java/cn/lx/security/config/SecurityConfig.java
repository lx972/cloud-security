package cn.lx.security.config;

import cn.lx.security.filter.AuthenticationFilter;
import cn.lx.security.filter.TokenVerifyFilter;
import cn.lx.security.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * cn.lx.security.config
 *
 * @Author Administrator
 * @date 9:39
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 只有这个配置类有AuthenticationManager对象，我们要把这个类中的这个对象放入容器中
     * 这样在别的地方就可以自动注入了
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManager() throws Exception {
        AuthenticationManager authenticationManager = super.authenticationManagerBean();
        return authenticationManager;
    }

    /**
     * Used by the default implementation of {@link #authenticationManager()} to attempt
     * to obtain an {@link AuthenticationManager}. If overridden, the
     * {@link AuthenticationManagerBuilder} should be used to specify the
     * {@link AuthenticationManager}.
     *
     * <p>
     * The {@link #authenticationManagerBean()} method can be used to expose the resulting
     * {@link AuthenticationManager} as a Bean. The {@link #userDetailsServiceBean()} can
     * be used to expose the last populated {@link UserDetailsService} that is created
     * with the {@link AuthenticationManagerBuilder} as a Bean. The
     * {@link UserDetailsService} will also automatically be populated on
     * {@link HttpSecurity#getSharedObject(Class)} for use with other
     * {@link SecurityContextConfigurer} (i.e. RememberMeConfigurer )
     * </p>
     *
     * <p>
     * For example, the following configuration could be used to register in memory
     * authentication that exposes an in memory {@link UserDetailsService}:
     * </p>
     *
     * <pre>
     * &#064;Override
     * protected void configure(AuthenticationManagerBuilder auth) {
     * 	auth
     * 	// enable in memory based authentication with a user named
     * 	// &quot;user&quot; and &quot;admin&quot;
     * 	.inMemoryAuthentication().withUser(&quot;user&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;).and()
     * 			.withUser(&quot;admin&quot;).password(&quot;password&quot;).roles(&quot;USER&quot;, &quot;ADMIN&quot;);
     * }
     *
     * // Expose the UserDetailsService as a Bean
     * &#064;Bean
     * &#064;Override
     * public UserDetailsService userDetailsServiceBean() throws Exception {
     * 	return super.userDetailsServiceBean();
     * }
     *
     * </pre>
     *
     * @param auth the {@link AuthenticationManagerBuilder} to use
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //在内存中注册一个账号
        //auth.inMemoryAuthentication().withUser("user").password("{noop}123").roles("USER");
        //连接数据库，使用数据库中的账号
        auth.userDetailsService(iUserService).passwordEncoder(bCryptPasswordEncoder);


    }

    /**
     * Override this method to configure {@link WebSecurity}. For example, if you wish to
     * ignore certain requests.
     *
     * @param web
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**",
                "/img/**",
                "/plugins/**",
                "/favicon.ico",
                "/loginPage");
    }

    /**
     * Override this method to configure the {@link HttpSecurity}. Typically subclasses
     * should not invoke this method by calling super as it may override their
     * configuration. The default configuration is:
     *
     * <pre>
     * http.authorizeRequests().anyRequest().authenticated().and().formLogin().and().httpBasic();
     * </pre>
     *
     * @param http the {@link HttpSecurity} to modify
     * @throws Exception if an error occurs
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .httpBasic()
                .and()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                /**
                 * 不要将自定义过滤器加component注解，而是在这里直接创建一个过滤器对象加入到过滤器链中，并传入authenticationManager
                 * 启动后，过滤器链中会同时出现自定义过滤器和他的父类，他会自动覆盖，并不会过滤两次
                 *
                 * 使用component注解会产生很多问题：
                 * 1. web.ignoring()会失效，上面的资源还是会经过自定义的过滤器
                 * 2.过滤器链中出现的是他们父类中的名字
                 * 3.登录的时候（访问/login），一直使用匿名访问，不会去数据库中查询
                 */
                .addFilterAt(new AuthenticationFilter(super.authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(new TokenVerifyFilter(super.authenticationManager()), BasicAuthenticationFilter.class)
                //.formLogin().loginPage("/login.jsp").loginProcessingUrl("/login").defaultSuccessUrl("/index.jsp").failureForwardUrl("/failer.jsp").permitAll()
                .formLogin().loginPage("/loginPage").loginProcessingUrl("/login").permitAll()
                .and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/loginPage").invalidateHttpSession(true).permitAll();
    }
}
