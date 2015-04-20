package com.redactus.controllers;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.io.FileOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.redactus.model.FileMeta;
import org.apache.commons.io.FilenameUtils;
import java.io.File;
@Controller
@RequestMapping("/controller")
public class FileController {
	LinkedList<FileMeta> files = new LinkedList<FileMeta>();
	FileMeta fileMeta = null;
	FileMeta lastFileMeta = null;
	@RequestMapping(value="/upload", method = RequestMethod.POST)
	public @ResponseBody LinkedList<FileMeta> upload(MultipartHttpServletRequest request, HttpServletResponse response) {
		Iterator<String> itr =  request.getFileNames();
		MultipartFile mpf = null;
		while(itr.hasNext()){
			mpf = request.getFile(itr.next());
			if(check(mpf.getOriginalFilename())){
			 	System.out.println(mpf.getOriginalFilename() +" uploaded! "+files.size());
			 	if(files.size() >= 10)
				 	files.pop();
			 	fileMeta = new FileMeta();
			 	fileMeta.setFileName(mpf.getOriginalFilename());
			 	fileMeta.setFileSize(mpf.getSize()/1024+" Kb");
			 	fileMeta.setFileType(mpf.getContentType());
			 	try {
					fileMeta.setBytes(mpf.getBytes());
					FileCopyUtils.copy(mpf.getBytes(), new FileOutputStream("files/"+mpf.getOriginalFilename()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 	files.add(fileMeta);
		 	} else{
				System.out.println(mpf.getOriginalFilename()+" is not real image!");
			}
		}
		if (files.size()>1){
			System.out.println("filessize >1");
			if(files.getLast().getFileName().equals(lastFileMeta.getFileName())){
				files.getLast().setFileUuid(lastFileMeta.getUuid());
			}
		}
		return files;
	}
	@RequestMapping(value = "/get/{value}", method = RequestMethod.GET)
	 public void get(HttpServletResponse response,@PathVariable String value){
		FileMeta getFile = files.get(Integer.parseInt(value));
		try {
			 	response.setContentType(getFile.getFileType());
			 	response.setHeader("Content-disposition", "attachment; filename=\""+getFile.getFileName()+"\"");
		        FileCopyUtils.copy(getFile.getBytes(), response.getOutputStream());
		}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	}
	public boolean check(String name){
		String[] parts = name.split("\\.");
		String ext = parts[parts.length-1];
		String[] extensions = {"png","jpeg","jpg","bmp","gif"};
		for(int i=0;i<extensions.length;i++){
			if(ext.equals(extensions[i]))
				return true;
		}
		return false;
	}
	@RequestMapping(value="/uuid",method = RequestMethod.POST)
	public void getUuid(@RequestParam(value="uuid") String uuid,@RequestParam(value="name") String name){
		System.out.println("IT's DONE");
		uuid = parseUuid(uuid);
		name = parseUuid(name);
		name = parsePath(name);
		lastFileMeta = new FileMeta();
		lastFileMeta.setFileName(name);
		lastFileMeta.setFileUuid(uuid);
		if (files.size()==1){
			System.out.println("____________YES");
			if(files.get(0).getFileName().equals(name)){
				files.get(0).setFileUuid(uuid);
			}
			else System.out.println("_________NO");
		}
		/*System.out.println("\n\n\nUUID "+uuid);
		System.out.println("Name "+name+"\n\n\n");
		for(FileMeta fm:files){
			if(fm.getFileName().equals(name)){
				fm.setFileUuid(uuid);
			}
		}*/
	}
	public String parseUuid(String text){
		String newText="";
		for(int i=0;i<text.length();i++){
			if(text.charAt(i)!='"'){
				newText+=text.charAt(i);
			}
		}
		return newText;
	}
	public String parsePath(String path){
		String name = FilenameUtils.getName(path);
		return name;
	}
	@RequestMapping(value="show",method = RequestMethod.GET)
	public void showALlFiles(){
		for(FileMeta fm:files){
			System.out.println("____FileName "+fm.getFileName());
			System.out.println("____FileUUID "+fm.getUuid());
		}
	}
}
