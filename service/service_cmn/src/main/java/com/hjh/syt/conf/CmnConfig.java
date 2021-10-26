package com.hjh.syt.conf;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author: hasee
 * @date: 2021/6/20 21:52
 * @description:
 */
@Configuration
public class CmnConfig {
    @Bean //分页插件 PaginationInterceptor
    public PaginationInterceptor paginationInterceptor (){
        return new PaginationInterceptor();
    }
}
