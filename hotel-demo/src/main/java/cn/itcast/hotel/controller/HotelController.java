package cn.itcast.hotel.controller;

import cn.itcast.hotel.dto.RequestParams;
import cn.itcast.hotel.service.IHotelService;
import cn.itcast.hotel.vo.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 *
 * @author xiaowu
 */
@RestController
@RequiredArgsConstructor
public class HotelController {

    private final IHotelService hotelService;

    /**
     * 案例需求1：实现黑马旅游的酒店搜索功能，完成关键字搜索和分页
     *
     * - 步骤一：定义实体类，接收请求参数的JSON对象
     * - 步骤二：编写controller，接收页面的请求
     * - 步骤三：编写业务实现，利用RestHighLevelClient实现搜索、分页
     *
     *
     * 案例需求2：添加品牌、城市、星级、价格等过滤功能
     */
    @RequestMapping("/hotel/list")
    public PageResult search(@RequestBody RequestParams requestParams) {
        return hotelService.search(requestParams);
    }

    /**
     *
     * @param requestParams
     * @return
     */
    @RequestMapping("/hotel/filters")
    public Map<String, List<String>> filters(@RequestBody RequestParams requestParams) {
        return hotelService.filters(requestParams);
    }

    /**
     *
     * @param key
     * @return
     */
    @RequestMapping("/hotel/suggestion")
    public List<String> suggestion(@RequestParam String key) {
        return hotelService.suggestion(key);
    }
}
