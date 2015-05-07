package com.redactus.manager;

import com.redactus.model.FileMeta;
import java.util.LinkedList;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface FileManager{
	public LinkedList<FileMeta> upload(MultipartHttpServletRequest request, HttpServletResponse response);
}
