package com.hjh.syt.conf;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "mail")
@Getter
@Setter
public class MailParameter {
    private String host;
    private Integer port;
    private String from;
    private String user;
    private String pass;
    private Boolean starttlsEnable;
    private Boolean sslEnable;
    private String socketFactoryClass;
    private Boolean socketFactoryFallback;
    private Integer socketFactoryPort;
    private Integer timeout;
    private Integer connectionTimeout;
}
