package io.flowinquiry.config;

import io.flowinquiry.web.filter.TenantInjectInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
// @EnableWebMvc
public class TenantConfigurer implements WebMvcConfigurer {

    @Autowired private TenantInjectInterceptor tenantInjectInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantInjectInterceptor).addPathPatterns("/api/**");
    }
}
