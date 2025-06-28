package it.unisalento.iot2425.watchapp.notification;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class NotificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationApplication.class, args);
    }
    @Bean
    public CommandLineRunner checkBeans(ApplicationContext ctx) {
        return args -> {
            System.out.println("🔎 BEAN REGISTRATI:");
            for (String name : ctx.getBeanDefinitionNames()) {
                if (name.toLowerCase().contains("mqtt")) {
                    System.out.println("🟢 " + name);
                }
            }
        };
    }

}
