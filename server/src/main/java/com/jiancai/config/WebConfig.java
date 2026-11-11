package com.jiancai.config;

import com.jiancai.security.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置：拦截器、跨域、静态资源
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    @Value("${jiancai.upload.path}")
    private String uploadPath;

    /** 跨域白名单（逗号分隔），默认仅放行本地开发端口 */
    @Value("#{'${jiancai.cors.allowed-origins:}'.split(',')}")
    private java.util.List<String> allowedOrigins;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                // 仅这些前缀需要登录，小程序接口 /api/mp/** 与登录接口放行；解绑需校验 JWT
                .addPathPatterns("/api/admin/**", "/api/merchant/**", "/api/account/**",
                        "/api/mp/merchant/unbind");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = allowedOrigins.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
        // 鉴权走 Authorization 头而非 Cookie，无需携带凭证
        registry.addMapping("/**")
                .allowedOrigins(origins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + uploadPath);
    }
}
