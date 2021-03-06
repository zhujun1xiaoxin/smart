package com.sumridge.smart.controller;

import com.sumridge.smart.bean.AlertMessageBean;
import com.sumridge.smart.bean.AlertTaskBean;
import com.sumridge.smart.bean.NotificationBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liu on 16/3/30.
 */

@Controller
public class NotificationController {


    //the websocket template to send message to spec user
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @SubscribeMapping("/notification")
    public List<NotificationBean> getNotification(Principal principal) {
        String user = principal.getName();
        NotificationBean info = new NotificationBean();
        info.setMessage("welcome join sumridge smart system!");
        info.setTime(System.currentTimeMillis());
        info.setType("system");
        List<NotificationBean>list = new ArrayList<>();
        list.add(info);
        return  list;
    }

    @SubscribeMapping("/message")
    public List<AlertMessageBean> getMessage(Principal principal) {

        AlertMessageBean msg = new AlertMessageBean();
        msg.setFrom("System");
        msg.setMessage("First message from system.");
        msg.setMessageId("001");
        msg.setTime(System.currentTimeMillis());
        List<AlertMessageBean> list = new ArrayList<>();
        list.add(msg);
        return list;
    }

    @SubscribeMapping("/task")
    public List<AlertTaskBean> getTask(Principal principal) {
        AlertTaskBean taskBean = new AlertTaskBean();
        taskBean.setProcess(80);
        taskBean.setTaskId("1");
        taskBean.setTitle("init system");

        List<AlertTaskBean> list = new ArrayList<>();
        list.add(taskBean);
        return list;
    }

    /**
     * send message by spring sendtouser when the current user send request
     * @param principal
     * @return
     */
    @SendToUser("/notification")
    public List<NotificationBean> pushNotification(Principal principal) {

        return null;
    }

}
