package com.sumridge.smart.query;

import com.sumridge.smart.bean.SaleAggBean;
import com.sumridge.smart.entity.SaleBean;
import com.sumridge.smart.entity.SaleInfo;
import com.sumridge.smart.entity.SaleMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Created by liu on 16/5/12.
 */
@Component
public class SaleQuery {
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<SaleInfo> getSaleList(int limit) {
        Query query = new Query();
        query.limit(limit);
        return mongoTemplate.find(query, SaleInfo.class);
    }


    public List<SaleAggBean> getPercent(String id) {

        Aggregation agg = Aggregation.newAggregation(Aggregation.match(Criteria.where("createUser").is(id)),
                Aggregation.group("contactId").sum("quantity").as("quantity"), Aggregation.limit(5));

        AggregationResults<SaleAggBean> result = mongoTemplate.aggregate(agg, SaleInfo.class, SaleAggBean.class);

        return result.getMappedResults();
    }

    //add by zj
    public List<SaleInfo> getPagedSales(int currentPage, int pageSize){
        Query pageQuery = new Query();
        pageQuery.skip(currentPage*pageSize).limit(pageSize);
        return mongoTemplate.find(pageQuery, SaleInfo.class);
    }

    public long countSales(){
        Query query = new Query();
        long maxSize = mongoTemplate.count(query, SaleInfo.class);
        return maxSize;
    }

    public SaleInfo getLatestSale(String accountId) {
        Query query = Query.query(Criteria.where("accountId").is(accountId)).limit(1);
        query.with(new Sort(Sort.Direction.DESC, "tradeDate"));
        List<SaleInfo> list = mongoTemplate.find(query, SaleInfo.class);

        if(list != null && list.size() >0) {
            return list.get(0);
        }
        return null;
    }

    public List<SaleInfo> salesFuzzyQuery(int page, int size, String match){
        Query query = new Query(Criteria.where("secId").regex("\\w*"+match+"\\w*"));
        query.skip(page*size);
        query.limit(size);
        List<SaleInfo> saleInfos = mongoTemplate.find(query, SaleInfo.class);
        return saleInfos;
    }

    public long countFuzzySales(String match){
        Query query = new Query(Criteria.where("secId").regex("\\w*"+match+"\\w*"));
        long maxSize = mongoTemplate.count(query, SaleInfo.class);
        return maxSize;
    }
}
