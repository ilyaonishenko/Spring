package com.redactus.controllers;
import java.io.IOException;
import java.util.LinkedList;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.redactus.model.FileMeta;
import java.io.File;
public interface ImageRepository extends MongoRepository<FileMeta,String>{

}
