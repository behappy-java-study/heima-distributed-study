package org.xiaowu.order.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.xiaowu.order.service.pojo.User;

import javax.ws.rs.core.Response;

/**
 *
 * @author xiaowu
 */
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final RestTemplate restTemplate;

    @GetMapping("/order/{id}")
    public User queryById(@PathVariable("id") Long id) {
        // host使用小写service_name
        String url = "http://user-service/user/" + id;
        User user = restTemplate.getForObject(url, User.class);
        return user;
    }
}
