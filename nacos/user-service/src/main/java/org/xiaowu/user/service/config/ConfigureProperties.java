package org.xiaowu.user.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 * @author xiaowu
 */
@Data
@ConfigurationProperties(prefix = "test")
@Component
public class ConfigureProperties {
    private String dateFormat;
    private String name;
}
