package com.sumridge.smart.service;

import com.sumridge.smart.bean.AccountAggBean;
import com.sumridge.smart.bean.AccountBean;
import com.sumridge.smart.bean.NameBean;
import com.sumridge.smart.bean.ResultBean;
import com.sumridge.smart.dao.CompanyInfoRepository;
import com.sumridge.smart.entity.CompanyInfo;
import com.sumridge.smart.entity.UserInfo;
import com.sumridge.smart.query.CompanyQuery;
import com.sumridge.smart.util.DateUtil;
import com.sumridge.smart.util.StringUtil;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by liu on 16/4/26.
 */
@Service
public class CompanyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyService.class);
    private final int LIMIT = 5;

    @Autowired
    private CompanyInfoRepository companyInfoRepository;
    @Autowired
    private CompanyQuery companyQuery;

    @Autowired
    private BoardService boardService;
    public ResultBean getCompanyList(UserInfo userInfo, String match) {
        //query company initials
        List<String> initials = new ArrayList();
        List<AccountBean> accountBeanList = companyQuery.queryCompanyInitials(match);
        for(AccountBean bean: accountBeanList){
            initials.add(bean.getId());
        }

        //query companyList by initial
        List<AccountAggBean> companyList = new ArrayList();
        for(String initial: initials){
            AccountAggBean bean = companyQuery.queryCompanyByInitials(initial, LIMIT, match);
            companyList.add(bean);
        }
//        List<AccountAggBean> list = companyQuery.queryList();
        return ResultBean.getSuccessResult(companyList);
    }

    public ResultBean saveCompanyInfo(CompanyInfo companyInfo) {
        if(companyInfo.getId() != null) {
            companyQuery.updateCompanyInfo(companyInfo);
        } else {
            companyInfo.setInitial(StringUtil.getInitChar(companyInfo.getShortName()));
            companyInfo.setAccountInfos(new ArrayList<>());
            companyInfoRepository.save(companyInfo);
            boardService.createCompanyBoard(companyInfo);
        }

        return ResultBean.getSuccessResult();
    }

    public ResultBean getCompanyInfo(String companyId) {

        CompanyInfo info = companyQuery.queryInfo(companyId);
        return ResultBean.getSuccessResult(info);
    }

    public ResultBean getCompanyNameList(UserInfo userInfo) {
        List<NameBean> list = companyQuery.queryNameList();
        return ResultBean.getSuccessResult(list);
    }

    public ResultBean saveFile(UserInfo userInfo, MultipartFile file) {
        //TODO change to async
        //TODO logic validation
        try {

            Reader reader = new InputStreamReader(file.getInputStream());
            CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL.withHeader().withDelimiter(','));
            try {
                List<CompanyInfo> companyList = new ArrayList<>();
                for(final CSVRecord record : parser) {
//                    LOGGER.info(record.toString());
                    //insert into temp sale
                    CompanyInfo info = new CompanyInfo();
                    info.setAcctOpenUid(record.get("AcctOpenUID"));
                    info.setAcctType(record.get("accttype"));
                    info.setAddress(record.get("addressLine1"));
                    info.setAddress2(record.get("addressLine2"));
                    info.setCity(record.get("addressCity"));
                    info.setCloseDate(DateUtil.parseDate(record.get("CloseDate"), "yyyy-MM-dd"));
                    info.setCountry(record.get("CountryCode"));
                    info.setCreateDate(new Date());
                    info.setCreator(userInfo.getId());
                    info.setFirstTradeDate(DateUtil.parseDate(record.get("FirstTradeDate"), "yyyy-MM-dd"));
                    info.setShortName(record.get("ShortName"));
                    if(!StringUtils.isEmpty(info.getShortName())) {
                        info.setInitial(StringUtil.getInitChar(record.get("ShortName")));
                    }
                    info.setLastTradeDate(DateUtil.parseDate(record.get("LastTradeDate"), "yyyy-MM-dd"));
                    info.setLongName(record.get("LongName"));

                    info.setOpenDate(DateUtil.parseDate(record.get("OpenDate"), "yyyy-MM-dd"));
                    info.setState(record.get("addressState"));
                    info.setStatus(record.get("status"));
                    info.setUid(record.get("Uid"));
                    info.setUpdateTms(new Date());
                    info.setZipCode(record.get("addressZip"));
                    companyList.add(info);
                }
                companyInfoRepository.insert(companyList);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                parser.close();
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResultBean.getSuccessResult();
    }

    //load more company add by zj 17/03/24
    public ResultBean loadMoreCompany(String initial, int size, String match){
        AccountAggBean company = companyQuery.queryCompanyByInitials(initial, size+LIMIT, match);
        return ResultBean.getSuccessResult(company);
    }
}
