package cn.itcast.hotel.service.impl;

import cn.itcast.hotel.dto.RequestParams;
import cn.itcast.hotel.mapper.HotelMapper;
import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.HotelDoc;
import cn.itcast.hotel.service.IHotelService;
import cn.itcast.hotel.vo.PageResult;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.SearchExecutionContext;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.*;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class HotelService extends ServiceImpl<HotelMapper, Hotel> implements IHotelService {

    private final RestHighLevelClient client;

    /**
     * @param requestParams
     * @return
     */
    @SneakyThrows
    @Override
    public PageResult search(RequestParams requestParams) {
        SearchRequest searchRequest = new SearchRequest("hotel");
        /*构建request开始*/
        SearchSourceBuilder searchSourceBuilder = buildRequest(requestParams);
        /*构建request结束*/

        searchRequest.source(searchSourceBuilder);

        /*构建response开始*/
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        long value = searchResponse.getHits().getTotalHits().value;
        List<HotelDoc> hotelDocs = new LinkedList<>();
        PageResult pageResult = new PageResult();
        pageResult.setTotal(value);
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            String sourceAsString = hit.getSourceAsString();
            HotelDoc hotelDoc = JSON.parseObject(sourceAsString, HotelDoc.class);
            if (hit.getSortValues().length > 0) {
                hotelDoc.setDistance(hit.getSortValues()[0]);
            }
            hotelDocs.add(hotelDoc);
            pageResult.setHotels(hotelDocs);
        }
        /*构建response结束*/
        return pageResult;
    }

    @SneakyThrows
    @Override
    public Map<String, List<String>> filters(RequestParams requestParams) {
        SearchRequest searchRequest = new SearchRequest("hotel");
        SearchSourceBuilder searchSourceBuilder = buildRequest(requestParams);
        buildAggs(searchSourceBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        Map<String, List<String>> map = new HashMap<>();
        map.put("brand", aggsResponse(response, "brandAggs"));
        map.put("city", aggsResponse(response, "cityAggs"));
        map.put("starName", aggsResponse(response, "starAggs"));
        return map;
    }

    @SneakyThrows
    @Override
    public List<String> suggestion(String key) {
        // 1.准备Request
        SearchRequest request = new SearchRequest("hotel");
        CompletionSuggestionBuilder completionSuggestionBuilder = SuggestBuilders.
                completionSuggestion("suggestion").
                prefix(key).
                skipDuplicates(true).
                analyzer("completion_analyzer").
                size(10);
        SuggestBuilder suggestBuilder = new SuggestBuilder().
                addSuggestion("suggestions", completionSuggestionBuilder);
        SearchSourceBuilder sourceBuilder = request.source().suggest(suggestBuilder);
        request.source(sourceBuilder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        CompletionSuggestion options = (CompletionSuggestion) response.getSuggest().getSuggestion("suggestions");
        List<String> strs = new LinkedList<>();
        for (CompletionSuggestion.Entry.Option option : options.getOptions()) {
            String text = option.getText().toString();
            strs.add(text);
        }
        return strs;
    }

    private void buildAggs(SearchSourceBuilder searchSourceBuilder) {
        TermsAggregationBuilder brandAggs = AggregationBuilders.terms("brandAggs").field("brand").size(100);
        TermsAggregationBuilder cityAggs = AggregationBuilders.terms("cityAggs").field("city").size(100);
        TermsAggregationBuilder starNameAggs = AggregationBuilders.terms("starAggs").field("starName").size(100);
        searchSourceBuilder.aggregation(brandAggs);
        searchSourceBuilder.aggregation(cityAggs);
        searchSourceBuilder.aggregation(starNameAggs);
    }

    private List<String> aggsResponse(SearchResponse response, String aggName) {
        List<String> keys = new LinkedList<>();
        Terms terms = (Terms) response.getAggregations().get(aggName);
        for (Terms.Bucket bucket : terms.getBuckets()) {
            String key = bucket.getKeyAsString();
            keys.add(key);
        }
        return keys;
    }

    private SearchSourceBuilder buildRequest(RequestParams requestParams) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(requestParams.getKey())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("all", requestParams.getKey()));
        } else {
            boolQueryBuilder.must(QueryBuilders.matchAllQuery());
        }
        if (StringUtils.isNotBlank(requestParams.getCity())) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("city", requestParams.getCity()));
        }
        if (StringUtils.isNotBlank(requestParams.getBrand())) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("brand", requestParams.getBrand()));
        }
        if (StringUtils.isNotBlank(requestParams.getStarName())) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("starName", requestParams.getStarName()));
        }
        if (!Objects.isNull(requestParams.getMinPrice()) && !Objects.isNull(requestParams.getMaxPrice())) {
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(requestParams.getMinPrice()).lte(requestParams.getMaxPrice()));
        }
        FunctionScoreQueryBuilder functionScoreQuery = QueryBuilders.functionScoreQuery(boolQueryBuilder, new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        QueryBuilders.termQuery("isAD", true),
                        ScoreFunctionBuilders.weightFactorFunction(100))
        }).boostMode(CombineFunction.MULTIPLY);
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource().query(functionScoreQuery);
        List<SortBuilder<?>> sortBuilders = new LinkedList<>();
        if (StringUtils.isNotBlank(requestParams.getLocation())) {
            GeoDistanceSortBuilder distanceSortBuilder = SortBuilders.geoDistanceSort("location",
                    new GeoPoint(requestParams.getLocation())).order(SortOrder.ASC).unit(DistanceUnit.KILOMETERS);
            sortBuilders.add(distanceSortBuilder);
        }
        if (!"default".equals(requestParams.getSortBy())) {
            FieldSortBuilder fieldSortBuilder = SortBuilders.fieldSort(requestParams.getSortBy()).order(SortOrder.ASC);
            sortBuilders.add(fieldSortBuilder);
        }
        searchSourceBuilder.sort(sortBuilders);
        searchSourceBuilder.from(requestParams.getPage()-1);
        searchSourceBuilder.size(requestParams.getSize());
        return searchSourceBuilder;
    }
}
