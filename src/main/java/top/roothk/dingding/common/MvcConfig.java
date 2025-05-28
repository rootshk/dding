package top.roothk.dingding.common;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Map;

/**
 * MVC配置
 *
 * @author Kevin
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    private final ApplicationContext applicationContext;

    public MvcConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        Map<String, HandlerMethodArgumentResolver> map = applicationContext.getBeansOfType(HandlerMethodArgumentResolver.class);
        map.forEach((k, v) -> resolvers.add(v));
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 设置允许跨域的路径
        registry.addMapping("/**")
                // 设置允许跨域请求的域名
                .allowedOrigins("*")
                .allowedHeaders("*")
                // 设置允许的方法
                .allowedMethods("*")
                // 是否允许证书 不再默认开启
                .allowCredentials(true)
                // 跨域允许时间
                .maxAge(3600);
    }

}
