package com.sumridge.smart.dao;

import com.sumridge.smart.entity.Contact;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by liu on 16/5/11.
 */
public interface ContactRepository extends MongoRepository<Contact,String> {
    Contact findByEmail(String email);
}
