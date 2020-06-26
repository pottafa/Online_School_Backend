package ru.otus.onlineSchool;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import ru.otus.onlineSchool.dto.GroupMenuItemDTO;
import ru.otus.onlineSchool.dto.UserMenuItemDTO;
import ru.otus.onlineSchool.entity.Group;
import ru.otus.onlineSchool.entity.User;

import java.util.Properties;

import static org.modelmapper.config.Configuration.AccessLevel.PRIVATE;

@Configuration
public class AppConfiguration {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
     mapper.addMappings(new PropertyMap<Group, GroupMenuItemDTO>() {
            protected void configure() {
                map().setUsersCount(source.getUsers().size());
            }
        });
        mapper.addMappings(new PropertyMap<User, UserMenuItemDTO>() {
            protected void configure() {
                map().setRoles(source.getRoles().toString());
            }
        });
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(PRIVATE);
        return mapper;
    }

    @Bean
    public JavaMailSender getJavaMailSender(@Value("${email_address}") String emailAddress, @Value("${email_password}") String password) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername(emailAddress);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}
