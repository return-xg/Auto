package com.auto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 * 
 * @author auto
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class AutoApplication
{
    public static void main(String[] args)
    {
        // System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(AutoApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  Auto启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
