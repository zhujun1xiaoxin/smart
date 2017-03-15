package com.sumridge.smart.test;

import com.alibaba.fastjson.JSON;
import com.sumridge.smart.Application;
import com.sumridge.smart.bean.BestMktDataBean;
import com.sumridge.smart.query.CusipMktDataQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by liu on 17/3/7.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
public class TestMatchQueryTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestMatchQueryTest.class);
    @Autowired
    private CusipMktDataQuery cusipMktDataQuery;


//    @Test
//    public void testQuery() {
//        Set<String> set = new HashSet<>();
//        set.add("055451AH1");
//        set.add("081437AF2");
//        set.add("037833AQ3");
//        List<BestMktDataBean> bean = cusipMktDataQuery.getMktDataSimpleByCusip(set);
//        LOGGER.info("test-match-query");
//        LOGGER.info(JSON.toJSONString(bean));
//
//    }
}
