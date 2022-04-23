package cn.itcast.mq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiaowu
 */
@Configuration
public class TopicConfig {

    // 声明交换机
    @Bean
    public Exchange exchange(){
        return new TopicExchange("topic.exchange",true,false);
    }

    @Bean
    public Binding binding(){
        return new Binding("object.queue",
                Binding.DestinationType.QUEUE,
                "topic.exchange","#.news",null);
    }

    @Bean
    public Queue objectQueue(){
        return new Queue("object.queue");
    }
}
