package com.sumridge.smart.controller;

import com.sumridge.smart.bean.ResultBean;
import com.sumridge.smart.domain.CurrentUser;
import com.sumridge.smart.entity.MatchInfo;
import com.sumridge.smart.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Created by liu on 16/11/2.
 */
@RestController
@RequestMapping("/suggestion")
public class SuggestionController {

    @Autowired
    private MatchService matchService;

    @RequestMapping(value = "/list")
    public ResultBean getSuggestionList(Authentication authentication) {
        CurrentUser user = (CurrentUser) authentication.getPrincipal();
        if (user != null) {
            ResultBean rs = matchService.getMatchList(user.getUserInfo());
            return rs;
        } else {
            return ResultBean.getSuccessResult();
        }
    }


    @RequestMapping(value = "/agg-list")
    public ResultBean getAggSuggestionList(Authentication authentication, @RequestParam("filter") String filter) {
        CurrentUser user = (CurrentUser) authentication.getPrincipal();
        if (user != null) {
            ResultBean rs = matchService.getAggSuggestionList(user.getUserInfo(), filter);
            return rs;
        } else {
            return ResultBean.getSuccessResult();
        }
    }


    @RequestMapping(value = "/names")
    public ResultBean getNameList(Authentication authentication) {
        CurrentUser user = (CurrentUser) authentication.getPrincipal();
        if (user != null) {
            ResultBean rs = matchService.getNameList(user.getUserInfo(), null);
            return rs;
        } else {
            return new ResultBean();
        }
    }

    @RequestMapping(value = "/info", method = RequestMethod.POST)
    public ResultBean addSimpleMatch(Authentication authentication, @RequestBody MatchInfo bean) {
        CurrentUser user = (CurrentUser) authentication.getPrincipal();
        if (user != null) {
            ResultBean rs = matchService.addSimpleMatch(user.getUserInfo(), bean);
            return rs;
        } else {
            return ResultBean.getSuccessResult();
        }

    }

}

