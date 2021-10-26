package com.hjh.syt.conf;
import com.hjh.syt.hander.MyInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * @author: hasee
 * @date: 2021/7/3 15:07
 * @description:
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public MyInterceptor getMyInterceptor(){
        return new MyInterceptor();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getMyInterceptor())
                .addPathPatterns("/api/**");
    }
}
