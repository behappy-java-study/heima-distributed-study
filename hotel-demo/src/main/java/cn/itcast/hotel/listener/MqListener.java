package cn.itcast.hotel.listener;

import cn.itcast.hotel.config.HotelMqConstants;
import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.HotelDoc;
import cn.itcast.hotel.service.IHotelService;
import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 *
 * @author xiaowu
 */
@Component
@RequiredArgsConstructor
public class MqListener {

    private final IHotelService hotelService;

    private final RestHighLevelClient client;

    /**
     * 监听insert/update数据
     */
    @SneakyThrows
    @RabbitListener(queues = HotelMqConstants.INSERT_QUEUE_NAME)
    public void insertQueueListener(long id){
        // id存在则为修改, 否则为新增
        // 1.查出hotel
        Hotel hotel = hotelService.getById(id);
        // 2.转换为HotelDoc
        HotelDoc hotelDoc = new HotelDoc(hotel);
        // 3.转JSON
        String json = JSON.toJSONString(hotelDoc);

        // 1.准备Request
        IndexRequest request = new IndexRequest("hotel").id(hotelDoc.getId().toString());
        // 2.准备请求参数DSL，其实就是文档的JSON字符串
        request.source(json, XContentType.JSON);
        // 3.发送请求
        client.index(request, RequestOptions.DEFAULT);
    }

    /**
     * 监听delete数据
     */
    @SneakyThrows
    @RabbitListener(queues = HotelMqConstants.DELETE_QUEUE_NAME)
    public void deleteQueueListener(long id){
        // 1.准备Request      // DELETE /hotel/_doc/{id}
        DeleteRequest request = new DeleteRequest("hotel", String.valueOf(id));
        // 2.发送请求
        client.delete(request, RequestOptions.DEFAULT);
    }
}
