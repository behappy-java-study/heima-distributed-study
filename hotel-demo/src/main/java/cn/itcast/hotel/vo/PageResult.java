package cn.itcast.hotel.vo;

import cn.itcast.hotel.pojo.HotelDoc;
import lombok.Data;

import java.util.List;

/**
 * 返回值实体类
 */
@Data
public class PageResult {
    private Long total;
    private List<HotelDoc> hotels;

    public PageResult() {
    }

    public PageResult(Long total, List<HotelDoc> hotels) {
        this.total = total;
        this.hotels = hotels;
    }
}