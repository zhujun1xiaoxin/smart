package com.sumridge.smart.task;

import com.sumridge.smart.bean.TaskStatus;
import com.sumridge.smart.dao.ActivityInfoRepository;
import com.sumridge.smart.dao.MatchInfoRepository;
import com.sumridge.smart.dao.TaskInfoRepository;
import com.sumridge.smart.domain.CurrentUser;
import com.sumridge.smart.entity.*;
import com.sumridge.smart.query.TaskQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.data.redis.connection.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by liu on 17/3/13.
 */
@Component
public class FixRateTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(FixRateTask.class);

    private final String[] sides = {"B", "S"};

    @Autowired
    private TaskQuery taskQuery;

    @Autowired
    private MatchInfoRepository matchInfoRepository;

    @Autowired
    private TaskInfoRepository taskInfoRepository;

    @Autowired
    private ActivityInfoRepository activityInfoRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Scheduled(fixedRate = 60000)
    public void reportCurrentTime() {
//        LOGGER.info("check task from db");
        //1、获取当前系统时间的信息 月、日、时、分、秒
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int date = calendar.get(Calendar.DAY_OF_MONTH);
        //2、从数据库中查询出status为processing的task
        List<TaskInfo> taskInfoList = taskQuery.queryProcessingTask();

        //3、获取task的设定的闹铃时间，并和当前系统时间进行比较
        if(taskInfoList != null){
            for(TaskInfo taskInfo: taskInfoList){
                if(isEqual(calendar, taskInfo.getAlertTime())){
                    //4、如果符合闹铃时间吻合，那么再看task的repeatType的类型。
                    int type = taskInfo.getRepeatType();
                    switch(type){
                        //never repeat
                        case 0:
                            break;
                        //only once
                        case 1:
                            LOGGER.info("发送邮件到指定的邮箱，并将task的status置为done");
                            //存储matchInfo
                            saveToMatchInfo(taskInfo);
                            //将taskInfo的状态更新为done
                            taskInfo.setStatus(TaskStatus.Done);
                            taskInfoRepository.save(taskInfo);
                            //向activityInfo中插入一条信息
                            addTaskToActivityInfo(taskInfo);
                            break;
                        //every day
                        case 2:
                            LOGGER.info("发送邮件到指定邮箱，这个是everyday的任务");
                            saveToMatchInfo(taskInfo);
                            addTaskToActivityInfo(taskInfo);
                            break;
                        //workday
                        case 3:
                            if(day > 0 && day < 6){
                                LOGGER.info("发送邮件到指定邮箱，这是工作日的任务");
                                saveToMatchInfo(taskInfo);
                                addTaskToActivityInfo(taskInfo);
                            }
                            break;
                        //every week
                        case 4:
                            if(day == taskInfo.getDay()){
                                LOGGER.info("发送邮件到指定邮箱，这是every week的任务");
                                saveToMatchInfo(taskInfo);
                                addTaskToActivityInfo(taskInfo);
                            }
                            break;
                        //every month
                        case 5:
                            if(date == taskInfo.getDate()){
                                LOGGER.info("发送邮件到指定邮箱，这是every month的任务");
                                saveToMatchInfo(taskInfo);
                                addTaskToActivityInfo(taskInfo);
                            }
                    }
                }
            }
        }
    }

    public Boolean isEqual(Calendar currentDate, Date taskDate){
        int currentHour = currentDate.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentDate.get(Calendar.MINUTE);
        Calendar taskCalendar = Calendar.getInstance();
        taskCalendar.setTime(taskDate);
        int taskHour = taskCalendar.get(Calendar.HOUR_OF_DAY);
        int taskMinute = taskCalendar.get(Calendar.MINUTE);
        if(currentHour == taskHour && currentMinute == taskMinute){
            return true;
        }
        else
        {
            return false;
        }
    }

    public void saveToMatchInfo(TaskInfo taskInfo){
        List<Map> portfolios = taskInfo.getPortfolios();
        for(Map portfolio: portfolios){
            MatchInfo matchInfo = new MatchInfo();
            matchInfo.setTitle("Auto Send By System");
            matchInfo.setContent("The Content Is Created By System");
            matchInfo.setCreator(taskInfo.getOwnerId());
            matchInfo.setCreateDate(new Date());
            Set<MatchDetail> matchDetails = new HashSet();
            PortfolioInfo portfolioInfo = taskQuery.getPortfolioById((String)portfolio.get("id"));
            List<Map> cusipList = portfolioInfo.getList();
            for(Map map: cusipList){
                for(String side: sides){
                    MatchDetail matchDetail = new MatchDetail();
                    matchDetail.setCusip((String)map.get("cusip"));
                    matchDetail.setSide(side);
                    matchDetail.setPrice((String)map.get("price"));
                    matchDetail.setQuantity((String)map.get("quantity"));
                    matchDetails.add(matchDetail);
                }
            }
            matchInfo.setSalesMap(matchDetails);
            matchInfoRepository.save(matchInfo);
        }
    }

    public String createMessage(TaskInfo taskInfo){
        List<Map> portfolioInfos = taskInfo.getPortfolios();
        String message = "{\"sysMessage\":\"The portfolio named ";
        for(Map portfolio: portfolioInfos){
            String title = (String)portfolio.get("text");
            String email = (String)portfolio.get("email");
            message += title + " is sent to "+email+".";
        }
        message += " --Auto Send By System\"}";
        return message;
    }

    public void addTaskToActivityInfo(TaskInfo taskInfo){
        if(taskInfo.getPortfolios() != null){
            ActivityInfo activityInfo = new ActivityInfo();
            BoardInfo boardInfo = taskQuery.findBoardInfoByUserId(taskInfo.getOwnerId());
            String message = createMessage(taskInfo);
            activityInfo.setEventType("task");
            activityInfo.setEventMsg(message);
            activityInfo.setEventDate(new Date());
            activityInfo.setEventUser(taskInfo.getOwnerId());
            activityInfo.setBoardId(boardInfo.getId());
            activityInfoRepository.save(activityInfo);
        }
    }
}
