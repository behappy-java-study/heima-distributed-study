package org.xiaowu.order.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.xiaowu.order.service.pojo.User;

/**
 *
 * @author xiaowu
 */
@FeignClient(name = "user-service")
public interface UserFeign {

    @GetMapping("/user/{id}")
    User queryById(@PathVariable("id") Long id);
}
