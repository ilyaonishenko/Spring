package com.redactus.dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import com.redactus.model.FileMeta;
import java.util.List;
@Repository
public class FileMetaDao{
	@Autowired private MongoOperations mongoOperations;
	public void save(FileMeta fileMeta) {
        mongoOperations.save(fileMeta);
    }
	public FileMeta get(Long id) {
        return mongoOperations.findOne(Query.query(Criteria.where("id").is(id)), FileMeta.class);
    }
	public List<FileMeta> getAll() {
        return mongoOperations.findAll(FileMeta.class);
    }
	public void remove(Long id) {
        mongoOperations.remove(Query.query(Criteria.where("id").is(id)), FileMeta.class);
    }
}
