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
import org.apache.commons.io.FileUtils;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.ApplicationListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.redactus.services.FileMetaService;
@Controller
@RequestMapping("/controller")
public class FileController implements ApplicationListener<ContextRefreshedEvent> {
	@Autowired private FileMetaService fileMetaService;
	LinkedList<FileMeta> files = new LinkedList<FileMeta>();
	LinkedList<FileMeta> files2 = new LinkedList<FileMeta>();
	FileMeta fileMeta = null;
	public static boolean ready = true;
	public static int counter = 0;
	@RequestMapping(value="/upload", method = RequestMethod.POST)
	public synchronized @ResponseBody LinkedList<FileMeta> upload(MultipartHttpServletRequest request, HttpServletResponse response) {
		Iterator<String> itr =  request.getFileNames();
		MultipartFile mpf = null;
		while(itr.hasNext()){
			mpf = request.getFile(itr.next());
			System.out.println("Name: "+mpf.getOriginalFilename());
			if(check(mpf.getOriginalFilename())){
			 	System.out.println(mpf.getOriginalFilename() +" uploaded! "+files.size());
			 	if(files.size() >= 10)
				 	files.pop();
			 	fileMeta = new FileMeta();
			 	fileMeta.setFileName(mpf.getOriginalFilename());
			 	fileMeta.setFileSize(mpf.getSize()/1024+" Kb");
			 	fileMeta.setFileType(mpf.getContentType());
				try{
					fileMeta.setBytes(mpf.getBytes());
					files.add(fileMeta);
				} catch(IOException e){
					System.err.println("IOException");
				}
				System.out.println("files size is "+files.size());
				String uuid="";
				try{
					FileCopyUtils.copy(mpf.getBytes(), new FileOutputStream("files/"+mpf.getOriginalFilename()));
				} catch(IOException e){
					e.printStackTrace();
				}
				checking();
		 	} else{
				System.out.println(mpf.getOriginalFilename()+" is not real image!");
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
	public void checking(){
		for(FileMeta fm:files){
			for(FileMeta fm2:files2){
				if(fm.getFileName().equals(fm2.getFileName())&&fm.getUuid()==null){
					System.out.println("we have problems");
					fm.setFileUuid(fm2.getUuid());
					changeFiles(fm2.getFileName(),fm2.getUuid());
					//adding in mongodb
					// metaData = new BasicDBObject();
					/*metaData.put(fm.getFileName(),fm.getUuid());
					try{
						inputStream = new FileInputStream("files/"+fm.getUuid()+getExtension(fm.getFileName()));
						gridOperations.store(inputStream,fm.getUuid(),"image/png",metaData);
					} catch(FileNotFoundException e){
						e.printStackTrace();
					} finally {
						if(inputStream!=null){
							try{
								inputStream.close();
							} catch(IOException e){
								e.printStackTrace();
							}
						}
					}
					System.out.println("something done");*/
				}
			}
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
	public void changeFiles(String name,String uuid){
		String ext = getExtension(name);
		File file = new File("files/"+name);
		File file2 = new File("files/"+uuid+ext);
		boolean suc = file.renameTo(file2);
	}
	@RequestMapping(value="/uuid",method = RequestMethod.POST)
	public synchronized void getUuid(@RequestParam(value="uuid") String uuid,@RequestParam(value="name") String name){
		uuid = parseUuid(uuid);
		name = parseUuid(name);
		name = parsePath(name);
		System.out.println("name "+name);
		System.out.println("uuid "+uuid);
		FileMeta fm = new FileMeta();
		fm.setFileName(name);
		fm.setFileUuid(uuid);
		files2.add(fm);
		checking();
	}
	@RequestMapping(value="/delete",method=RequestMethod.POST)
	public void deleteElement(@RequestParam(value="uuid") String uuid,@RequestParam(value="name") String name){
		name = parsePath(name);
		int pos = 0;
		for(int i=0;i<files.size();i++){
			if (files.get(i).getUuid().equals(uuid)&&files.get(i).getFileName().equals(name))
				pos =i;
		}
		files.remove(pos);
		try{
			File file = new File("files/"+uuid+getExtension(name));
			FileUtils.forceDelete(file);
		}catch(Exception e){
			System.err.println("Exception");
		}
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
	public String getExtension(String name){
		String[] parts = name.split("\\.");
		String ext="."+parts[parts.length-1];
		return ext;
	}
	@RequestMapping(value="show",method = RequestMethod.GET)
	public void showALlFiles(){
		for(FileMeta fm:files){
			System.out.println("____FileName "+fm.getFileName());
			System.out.println("____FileUUID "+fm.getUuid());
		}
	}
	public void onApplicationEvent(ContextRefreshedEvent event) {
		File f = new File("files/");
		try{
			FileUtils.cleanDirectory(f);
		} catch(IOException e){
			System.err.println("Clearing directory error");
		}
	}
}
