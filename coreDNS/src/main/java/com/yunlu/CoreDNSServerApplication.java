package com.yunlu;

import com.yunlu.core.config.ConfigClient;
import com.yunlu.web.api.BaseApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;


@SpringBootApplication(exclude = {JdbcTemplateAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, SecurityAutoConfiguration.class, ActiveMQAutoConfiguration.class})
@ServletComponentScan(basePackages = "com.yunlu")
public class CoreDNSServerApplication extends BaseApplication {
    public static void main(String[] args) {

        String[] applicationArgs = BaseApplication.getArgs(args);
        if (applicationArgs == null) {
            return;
        }
        SpringApplication.run(CoreDNSServerApplication.class, applicationArgs);
    }

    @Override
    protected String getDBConfig() {
        return null;
    }

    @Override
    protected List<String> getIgnoreList() {
        return Arrays.asList("/**");
    }

    @Bean
    ConfigClient getConfigClient() {
        return ConfigClient.instance();
    }
}
