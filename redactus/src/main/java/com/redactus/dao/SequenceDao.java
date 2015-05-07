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

/**
 * Код взят с замечательного ресурса www.mkyong.com
 * http://mkyong.com/mongodb/spring-data-mongodb-auto-sequence-id-example/
 */
@Repository
public class SequenceDao {
    @Autowired private MongoOperations mongoOperations;
    public Long getNextSequenceId(String key) {
        // получаем объект Sequence по наименованию коллекции
        Query query = new Query(Criteria.where("_id").is(key));
        System.out.println(query);
        // увеличиваем поле sequence на единицу
        Update update = new Update();
        update.inc("sequence", 1);
        System.out.println(update);
        // указываем опцию, что нужно возвращать измененный объект
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.returnNew(true);
        // немного магии :)
        Sequence sequence = mongoOperations.findAndModify(query, update, options, Sequence.class);
        // if no sequence throws SequenceException
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
