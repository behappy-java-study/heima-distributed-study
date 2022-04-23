package org.xiaowu.user.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.xiaowu.user.service.UserServiceApplication;
import org.xiaowu.user.service.config.ConfigureProperties;
import org.xiaowu.user.service.pojo.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author xiaowu
 */
@RestController
@RequiredArgsConstructor
public class UserController {

    private final ConfigureProperties configureProperties;

    @GetMapping("/user/{id}")
    public User queryById(@PathVariable("id") Long id) {
        System.out.println(id);
        User user = UserServiceApplication.map.get(id);
        user.setAddress(LocalDateTime.now().format(DateTimeFormatter.ofPattern(configureProperties.getDateFormat())));
        return user;
    }

    @GetMapping("/properties")
    public ResponseEntity properties() {
        return ResponseEntity.ok(configureProperties);
    }
}
