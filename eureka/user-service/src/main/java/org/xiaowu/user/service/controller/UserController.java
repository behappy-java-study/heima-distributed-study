package org.xiaowu.user.service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.xiaowu.user.service.UserServiceApplication;
import org.xiaowu.user.service.pojo.User;

/**
 *
 * @author xiaowu
 */
@RestController
public class UserController {
    @GetMapping("/user/{id}")
    public User queryById(@PathVariable("id") Long id) {
        System.out.println(id);
        User user = UserServiceApplication.map.get(id);
        return user;
    }
}
