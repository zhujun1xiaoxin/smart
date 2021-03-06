package com.sumridge.smart.entity;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.annotation.Id;

import com.sumridge.smart.bean.TaskStatus;

/**
 * Created by liu on 16/4/6.
 */
public class TaskInfo {
    @Id
    private String id;
    private String title;
    private Date createTime;
    private String description;
    private String ownerId;
    private String status;
    private Date startTime;
    private Date endTime;
    private String distanceTime;
    private boolean markup;
    private Set<String> shares;

    //add by zj 17/01/09
    private Date alertTime;
    private int taskType;
    private int referType;
    private int repeatType;
    private List<Map> referList;

    //add by zj 17/01/10
    private List<Map> portfolios;

    //add day and date use for the detail of every week and every month 17/03/17 zj
    private int day;

    private int date;

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getTaskType() {
        return taskType;
    }

    public int getReferType() {
        return referType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public void setReferType(int referType) {
        this.referType = referType;
    }

    public int getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(int repeatType) {
        this.repeatType = repeatType;
    }

    public List<Map> getPortfolios() {
        return portfolios;
    }

    public void setPortfolios(List<Map> portfolios) {
        this.portfolios = portfolios;
    }

    public Date getAlertTime() {
        return alertTime;
    }

    public void setAlertTime(Date alertTime) {
        this.alertTime = alertTime;
    }

    public List<Map> getReferList() {
        return referList;
    }

    public void setReferList(List<Map> referList) {
        this.referList = referList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status.getValue();
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Set<String> getShares() {
        return shares;
    }

    public void setShares(Set<String> shares) {
        this.shares = shares;
    }
    
    
    public String getDistanceTime() {
		return distanceTime;
	}

	public void setDistanceTime(String distanceTime) {
		this.distanceTime = distanceTime;
	}

	public boolean isMarkup() {
		return markup;
	}

	public void setMarkup(boolean markup) {
		this.markup = markup;
	}

	@Override
    public String toString() {
        return "TaskInfo{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", createTime='" + createTime + '\'' +
                ", description='" + description + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", status='" + status + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", ownerId='" + ownerId + '\'' +
                '}';
    }
}
