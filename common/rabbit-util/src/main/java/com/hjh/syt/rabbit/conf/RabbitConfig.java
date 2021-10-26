package com.hjh.syt.rabbit.conf;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
    创建一个公共的rabbitmq服务
    1.导入使用rabbitmq的架包 springCloud的bus架包（bus整合了rabbit或者kafak）
        、springboot的actuator（通讯健康架包）、fastjson
    2.配置Jackson2JsonMessageConverter的bean
 **/
@Configuration
public class RabbitConfig {

    //在消息传输过程中，将生产端发送的java对象序列化为json格式，在转换为byte构造成message，然后消费端将message反序列化为java对象
    @Bean
    public MessageConverter messageConverter (){
        return new Jackson2JsonMessageConverter();
    }
}
