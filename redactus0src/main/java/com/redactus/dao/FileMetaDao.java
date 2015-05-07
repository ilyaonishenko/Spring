package com.redactus.dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Repository;
import com.redactus.model.FileMeta;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import java.io.ByteArrayOutputStream;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
@Repository
public class FileMetaDao{
	//Autowired private MongoOperations mongoOperations;
	@Autowired private GridFsOperations gridFsOperations;
	public void save(FileMeta fileMeta) {
        //mongoOperations.save(fileMeta);
		try {
			// fileMeta.getUuid();
			DBObject metaData = new BasicDBObject();
			metaData.put("uuid",fileMeta.getUuid());
			metaData.put("name",fileMeta.getFileName());
			String newName = fileMeta.getUuid()+fileMeta.getExtension();
			InputStream inputStream = new FileInputStream("files/"+newName);
			gridFsOperations.store(inputStream,newName,"image/png",metaData);
			inputStream.close();
		} catch(IOException e){
			System.err.println("problem with file");
		}
    }
	/*public FileMeta get(Long id) {
        //return mongoOperations.findOne(Query.query(Criteria.where("id").is(id)), FileMeta.class);
		try{
			FileMeta fileMeta = new FileMeta();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			GridFsResource gridFsResource = new GridFsResource (gridFsOperations.findOne(Query.query(Criteria.where("id").is(id))));
			fileMeta.setFileName(gridFsResource.getFilename());
			gridFsResource.writeTo(baos);
			fileMeta.setBytes(baos.toByteArray());
		}
		return fileMeta;
    }*/
	// public List<FileMeta> getAll() {
        // return mongoOperations.findAll(FileMeta.class);
		// return gridFsOperations.findAll(FileMeta.class);
    // }
	public void remove(String uuid,String ext) {
        // mongoOperations.remove(Query.query(Criteria.where("id").is(id)), FileMeta.class);
		// mongoOperations.remove(Query.query(Criteria.where("uuid").is(uuid)),FileMeta.class);
		String newName = uuid+ext;
		gridFsOperations.delete(Query.query(Criteria.where("filename").is(newName)));
    }
}
