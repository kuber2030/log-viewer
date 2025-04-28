package com.example.logviewer.config;

import com.example.logviewer.interceptors.IpInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author EvanZhou666
 * @version 1.0.0
 * @createTime 2025/4/28 16:24
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SysProps sysProps;

    public WebConfig(SysProps sysProps) {
        this.sysProps = sysProps;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new IpInterceptor(sysProps))
                .addPathPatterns("/**")
                .excludePathPatterns("/no-permission",
                                   "/static/**",
                                   "/*.ico",
                                   "/error");
    }

}
