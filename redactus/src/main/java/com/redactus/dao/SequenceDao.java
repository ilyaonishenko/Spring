package com.redactus.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.redactus.exceptions.SequenceException;
import com.redactus.model.Sequence;

@Repository
public class SequenceDao {
    @Autowired private MongoOperations mongoOperations;
    public Long getNextSequenceId(String key) {
        Query query = new Query(Criteria.where("_id").is(key));
        System.out.println(query);
        Update update = new Update();
        update.inc("sequence", 1);
        System.out.println(update);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);
        Sequence sequence = mongoOperations.findAndModify(query, update, options, Sequence.class);
        if(sequence == null) throw new SequenceException("Unable to get sequence for key: " + key);
        return sequence.getSequence();
    }
    public Long setBeforeSequenceId(String key){
        Query query = new Query(Criteria.where("_id").is(key));
        Update update = new Update();
        update.inc("sequence",-1);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);
        Sequence sequence = mongoOperations.findAndModify(query,update,options,Sequence.class);
        if(sequence==null) throw new SequenceException("Unable to get sequence for key: " + key);
        return sequence.getSequence();
    }
}
