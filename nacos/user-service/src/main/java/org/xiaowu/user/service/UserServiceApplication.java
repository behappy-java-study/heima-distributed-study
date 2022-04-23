package org.xiaowu.user.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.xiaowu.user.service.pojo.User;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@EnableDiscoveryClient
@SpringBootApplication
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
    public static Map<Long, User> map = new HashMap<>();

    @PostConstruct
    public void init() {
        User user1 = new User();
        user1.setUsername("1号");
        user1.setAddress("1号");
        user1.setId(1L);
        User user2 = new User();
        user2.setUsername("2号");
        user2.setId(2L);
        User user3 = new User();
        user3.setUsername("3号");
        user3.setId(3L);
        map.put(user1.getId(), user1);
        map.put(user2.getId(), user2);
        map.put(user3.getId(), user3);
    }
}
