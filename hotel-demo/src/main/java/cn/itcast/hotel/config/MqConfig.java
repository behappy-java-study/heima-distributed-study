package cn.itcast.hotel.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author xiaowu
 */
@Configuration
public class MqConfig {

    /**
     * 声明交换机
     * @return
     */
    @Bean
    public Exchange exchange() {
        return new TopicExchange(HotelMqConstants.EXCHANGE_NAME, true, false);
    }

    /**
     * 声明insert/update队列
     * @return
     */
    @Bean
    public Queue insertQueue() {
        return new Queue(HotelMqConstants.INSERT_QUEUE_NAME, true);
    }

    /**
     * 声明delete队列
     * @return
     */
    @Bean
    public Queue deleteQueue() {
        return new Queue(HotelMqConstants.DELETE_QUEUE_NAME, true);
    }

    /**
     * 声明insert binding
     * @return
     */
    @Bean
    public Binding insertBinding() {
        return new Binding(HotelMqConstants.INSERT_QUEUE_NAME, Binding.DestinationType.QUEUE,
                HotelMqConstants.EXCHANGE_NAME, HotelMqConstants.INSERT_KEY, null);
    }

    /**
     * 声明delete binding
     * @return
     */
    @Bean
    public Binding deleteBinding() {
        return new Binding(HotelMqConstants.DELETE_QUEUE_NAME, Binding.DestinationType.QUEUE,
                HotelMqConstants.EXCHANGE_NAME, HotelMqConstants.DELETE_KEY, null);
    }
}
