package com.sumridge.smart.query;

import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sumridge.smart.entity.*;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.sumridge.smart.bean.TaskStatus;

@Component
public class TaskQuery {
	@Autowired
    private MongoTemplate mongoTemplate;
	
	public List<TaskInfo> queryActiveTask(UserInfo userInfo){
		Query query = new Query();
		query.addCriteria(Criteria.where("ownerId").is(userInfo.getId()).andOperator(Criteria.where("status").ne(TaskStatus.Cancel.getValue()),Criteria.where("status").ne(TaskStatus.Done.getValue())));
		query.with(new Sort(new Order(Direction.ASC, "endTime")));
		return mongoTemplate.find(query, TaskInfo.class);
	}
	
	public List<TaskInfo> queryTaskById(UserInfo userInfo,String id){
		Query query = new Query();
		query.addCriteria(Criteria.where("id").is(id).and("ownerId").is(userInfo.getId()));
		return mongoTemplate.find(query, TaskInfo.class);
	}

	//add by zj 17/01/09
	public List<TaskInfo> loadTaskByStatus(String status){
		Query query = new Query();
		query.addCriteria(Criteria.where("status").is(status));
		return mongoTemplate.find(query, TaskInfo.class);
	}


	public void updateTaskToDone(String id){
		Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
		Update update = new Update();
		update.set("status", "done");
		mongoTemplate.updateFirst(query, update, TaskInfo.class);
	}

	//add by zj 17/01/13
	public BoardInfo findBoardInfoByUserId(String userId){
		DBObject queryObject = new BasicDBObject();
		queryObject.put("ownerId", userId);
		DBObject fields = new BasicDBObject();
		fields.put("_id", true);
		Query query = new BasicQuery(queryObject, fields);
		BoardInfo boardInfo = mongoTemplate.findOne(query, BoardInfo.class);
		return boardInfo;
	}

	//query processing task add by zj 17/03/15
	public List<TaskInfo> queryProcessingTask(){
		Query query = new Query(Criteria.where("status").is("processing").and("taskType").is(1).and("referType").is(0));
		List<TaskInfo> taskInfos = mongoTemplate.find(query, TaskInfo.class);
		return taskInfos;
	}

	//query portfolio by portfolioId add by zj 17/03/17
	public PortfolioInfo getPortfolioById(String portfolioId){
		Query query = new Query(Criteria.where("_id").is(portfolioId));
		PortfolioInfo portfolioInfo = mongoTemplate.findOne(query, PortfolioInfo.class, "portfolios");
		return portfolioInfo;
	}


}
