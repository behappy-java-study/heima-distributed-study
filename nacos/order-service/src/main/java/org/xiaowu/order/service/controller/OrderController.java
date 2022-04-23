package org.xiaowu.order.service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.xiaowu.order.service.feign.UserFeign;
import org.xiaowu.order.service.pojo.User;

/**
 *
 * @author xiaowu
 */
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final RestTemplate restTemplate;

    private final UserFeign userFeign;

    @GetMapping("/order/{id}")
    public User queryById(@PathVariable("id") Long id, @RequestHeader(value = "Truth",required = false) String truth) {
        System.out.printf(truth);
        // host使用小写service_name
        //String url = "http://user-service/user/" + id;
        //User user = restTemplate.getForObject(url, User.class);
        User user = userFeign.queryById(id);
        return user;
    }
}
