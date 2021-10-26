package com.hjh.syt.conf;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author: hasee
 * @date: 2021/6/15 11:22
 * @description: 有时间得学习一下函数式编程
 */
@Configuration
@EnableSwagger2 //启动
public class SwaggerConfig {
    @Bean
    public Docket WebApiConfig(){


       return new Docket(DocumentationType.SWAGGER_2)
               .groupName("webApi")
               .apiInfo(webApiInfo())
               .select()
               //显示api路径下的页面
               .paths(Predicates.and(PathSelectors.regex("/api/.*"))) //函数式编程规则
               .build();
    }
    @Bean
    public Docket adminApiConfig(){

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("adminApi")
                .apiInfo(adminApiInfo())
                .select()
                //显示admin路径下的页面
                .paths(Predicates.and(PathSelectors.regex("/admin/.*"))) //函数式编程规则
                .build();
    }


    private ApiInfo webApiInfo(){
        return new ApiInfoBuilder()
                .title("网站-API文档")
                .description("本文档描述网站微服务接口定义")
                .version("1.0")
                //.contact(new Contact("syt","http://syt.com","1781958156@qq.com"))
                .build();
    }
    private ApiInfo adminApiInfo(){
        return new ApiInfoBuilder()
                .title("后台管理系统-API文档")
                .description("本文档描述后台管理系统微服务接口定义")
                .version("1.0")
               // .contact(new Contact("syt","http://syt.com","1781958156@qq.com"))
                .build();
    }
}
