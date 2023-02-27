package fr.mspr.retailer.utils;

import org.apache.logging.log4j.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class JavaMailSenderConfig {

    @Value("${app.mailsender.host}")
    private String appHOST;

    @Bean
    public JavaMailSender javaMailSender() {

        String host = appHOST == null ? SecretPropertiesUtil.getProperties("app.mailsender.host") : appHOST;

        String port = SecretPropertiesUtil.getProperties("app.mailsender.port");

        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(host);
        javaMailSender.setPort(Integer.parseInt(port));
        javaMailSender.setUsername(null);
        javaMailSender.setPassword(null);

        Properties props = javaMailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return javaMailSender;
    }
}
