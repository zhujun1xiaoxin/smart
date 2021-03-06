package com.sumridge.smart.service;

import com.sumridge.smart.bean.AccountBean;
import com.sumridge.smart.bean.BestMktDataBean;
import com.sumridge.smart.bean.CommonAggBean;
import com.sumridge.smart.bean.ResultBean;
import com.sumridge.smart.dao.ContactRepository;
import com.sumridge.smart.dao.MatchInfoRepository;
import com.sumridge.smart.entity.*;
import com.sumridge.smart.query.*;
import com.sun.corba.se.pept.transport.ContactInfo;
import jodd.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by liu on 16/11/2.
 */
@Service
public class MatchService {


    @Autowired
    private MatchQuery matchQuery;

    @Autowired
    private MatchInfoRepository matchInfoRepository;

    @Autowired
    private ContactQuery contactQuery;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserQuery userQuery;

    @Autowired
    private CusipMktDataQuery cusipMktDataQuery;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AccountQuery accountQuery;

    public ResultBean getMatchList(UserInfo userInfo) {

        return ResultBean.getSuccessResult(matchQuery.getMatchInfoList(userInfo.getId()));
    }

    public ResultBean addSimpleMatch(UserInfo userInfo, MatchInfo bean) {
        if (bean.getId() == null) {

            bean.setCreateDate(new Date());
            bean.setCreator(userInfo.getId());
            bean.setSent(true);
            bean.setStatus("open");
        }


//        alertMatchUser(bean, userInfo);
        matchInfoRepository.save(bean);
        return ResultBean.getSuccessResult();
    }

    private void alertMatchUser(MatchInfo bean, UserInfo userInfo) {

        Map<String, Object> params = new HashMap<>();

        params.put("des", bean.getContent());
        params.put("sendUser", userInfo.getFirstName() + " " + userInfo.getLastName());
        params.put("sendCompany", userInfo.getCompany());
        params.put("matchList", bean.getSalesMap());
        Set<String> toList = new HashSet<>();
        bean.getCustomer().forEach(emailBean -> {
            toList.add(emailBean.getEmail());
        });
        String content = mailService.buildMailContent("match-email", params);
        mailService.sendEmail(StringUtil.join(toList.toArray(), ","), bean.getTitle(), content);
    }

    public ResultBean getNameList(UserInfo userInfo, String query) {


        return ResultBean.getSuccessResult(contactQuery.queryNameList(query));
    }

    public ResultBean getAggSuggestionList(UserInfo userInfo, String filter) {

        List<CommonAggBean> list = matchQuery.getBaseAggList();
        list.forEach(commonAggBean -> {
            commonAggBean.getDetail().forEach(stringStringMap -> {
                UserInfo info = userQuery.getBaseUserInfo(stringStringMap.get("creator"));
                stringStringMap.put("fromName", info.getFirstName() + " " + info.getLastName());
                stringStringMap.put("fromPhoto", info.getPhoto());
            });

        });
        return ResultBean.getSuccessResult(list);
    }

    //get portfolio info via accountId
    public ResultBean queryPortfolioByAccountId(String accountId) {
        List<PortfolioInfo> portfolioInfoList = matchQuery.queryPortfolioByAccountId(accountId);
        return ResultBean.getSuccessResult(portfolioInfoList);
    }

    //query cusip list via portfolioId
    public ResultBean queryCusipListByPortfolioId(String[] portfolioIds) {
        List<PortfolioInfo> portfolioInfos = new ArrayList<>();
        for (String portfolioId : portfolioIds) {
            PortfolioInfo portfolioInfo = matchQuery.getCusipList(portfolioId);
            portfolioInfos.add(portfolioInfo);
        }
        return ResultBean.getSuccessResult(portfolioInfos);
    }

    //call the getMktDataSimpleByCusip Query add by zj 17/03/10
    public ResultBean getMktDataSimpleByCusip(Set<String> cusipSet){
        List<BestMktDataBean> bestMktDataBeanList = cusipMktDataQuery.getMktDataSimpleByCusip(cusipSet);
        return ResultBean.getSuccessResult(bestMktDataBeanList);
    }

    //query salesMap of contact by email add by zj 17/03/23
    public AccountMapping getSalesMapByEmail(String email){
        Contact contact = contactRepository.findByEmail(email);
        return (AccountMapping)contact.getSalesMap().toArray()[0];
    }

    //query accuntInfo by salesMap add by zj 17/03/23
    public AccountBean getAccountBySalesMap(AccountMapping salesMap){
        AccountBean accountBean = accountQuery.queryAccountBySalesMap(salesMap);
        return accountBean;
    }
}
