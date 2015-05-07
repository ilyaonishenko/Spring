package com.redactus.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.gridfs.GridFsResource;

import com.redactus.model.FileMeta;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.io.ByteArrayOutputStream;

import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import org.apache.commons.io.IOUtils;

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
			InputStream inputStream = new FileInputStream("files/"+fileMeta.getNewFileName());
			gridFsOperations.store(inputStream,fileMeta.getNewFileName(),"image/png",metaData);
			inputStream.close();
		} catch(IOException e){
			System.err.println("problem with file");
		}
    }
	public FileMeta get(String uuid) {
        //return mongoOperations.findOne(Query.query(Criteria.where("id").is(id)), FileMeta.class);
		FileMeta fileMeta = new FileMeta();
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			GridFSDBFile gridFsDBFile = new GridFSDBFile();
			gridFsDBFile = (gridFsOperations.findOne(Query.query(Criteria.where("metadata.uuid").is(uuid))));
			fileMeta.setFileName(gridFsDBFile.getFilename());
			InputStream inputStream = null;
			inputStream = gridFsDBFile.getInputStream();
			fileMeta.setBytes(IOUtils.toByteArray(inputStream));
			fileMeta.setFileUuid(getNameWithoutExtension(gridFsDBFile.getFilename()));
			//gridFsDBFile.writeTo("filesFromDB/newFile.jpg");
		} catch(Exception e){
			System.out.println("Exception in converting GridFsResource to fileMeta");
		}
		return fileMeta;
    }
	// public List<FileMeta> getAll() {
        // return mongoOperations.findAll(FileMeta.class);
		// return gridFsOperations.findAll(FileMeta.class);
    // }
	public void remove(String uuid) {
        // mongoOperations.remove(Query.query(Criteria.where("id").is(id)), FileMeta.class);
		// mongoOperations.remove(Query.query(Criteria.where("uuid").is(uuid)),FileMeta.class);
		// gridFsOperations.delete(Query.query(Criteria.where("filename").is(fileName)));
		gridFsOperations.delete(Query.query(Criteria.where("metadata.uuid").is(uuid)));
    }
	public String getExtension(String fileName){
		String[] parts = fileName.split("\\.");
		String ext="."+parts[parts.length-1];
		return ext;
	}
	public String getNameWithoutExtension(String fileName){
		String[] parts = fileName.split("\\.");
		String ext="."+parts[0];
		return ext;
	}
}
