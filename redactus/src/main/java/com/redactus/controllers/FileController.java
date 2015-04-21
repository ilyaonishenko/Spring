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
@Controller
@RequestMapping("/controller")
public class FileController implements ApplicationListener<ContextRefreshedEvent> {
	LinkedList<FileMeta> files = new LinkedList<FileMeta>();
	LinkedList<FileMeta> files2 = new LinkedList<FileMeta>();
	FileMeta fileMeta = null;
	FileMeta lastFileMeta = null;
	public static boolean ready = true;
	public static boolean firstIter = false;
	static int counter = 0;
	@RequestMapping(value="/upload", method = RequestMethod.POST)
	public synchronized @ResponseBody LinkedList<FileMeta> upload(MultipartHttpServletRequest request, HttpServletResponse response) {
		Iterator<String> itr =  request.getFileNames();
		/*while (itr.hasNext()){
			MultipartFile mf = null;
			mf = request.getFile(itr.next());
			System.out.println(mf.getOriginalFilename());
		}*/
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
				System.out.println("files2 size is "+files2.size());
				System.out.println("files size is "+files.size());
				String uuid="";
				try{
					FileCopyUtils.copy(mpf.getBytes(), new FileOutputStream("files/"+mpf.getOriginalFilename()));
				} catch(IOException e){
					e.printStackTrace();
				}
				/*if(files.size()>1){
					for(FileMeta fm:files){
						for(FileMeta fm2:files2){
							if(fm.getFileName().equals(fm2.getFileName()))
							{
								fm.setFileUuid(fm2.getUuid());
								uuid = fm2.getUuid();
								System.out.println(fm.getUuid()+" uuid for "+fm.getFileName());
							}
						}
					}
					for(int i=0;i<files2.size();i++){
						if (files2.get(i).getUuid().equals(uuid)){
							files2.remove(i);
							break;
						}
					}
					/*if(files.getLast().getFileName().equals(lastFileMeta.getFileName())){
						files.getLast().setFileUuid(lastFileMeta.getUuid());
					}*/
					/*try{
						String ext = getExtension(files.getLast().getFileName());
						FileCopyUtils.copy(mpf.getBytes(), new FileOutputStream("files/"+files.getLast().getUuid()+ext));
					} catch(IOException e){
						e.printStackTrace();
					}
				}
				else{
					firstIter = true;
					System.out.println("first Iter is "+firstIter);
					try{
						//fileMeta.setBytes(mpf.getBytes());
						///files.add(fileMeta);
						FileCopyUtils.copy(mpf.getBytes(), new FileOutputStream("files/"+mpf.getOriginalFilename()));
					} catch(IOException e){
						e.printStackTrace();
					}
				}
			 	/*try {
					fileMeta.setBytes(mpf.getBytes());
					FileCopyUtils.copy(mpf.getBytes(), new FileOutputStream("files/"+mpf.getOriginalFilename()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 	files.add(fileMeta);*/
		 	} else{
				System.out.println(mpf.getOriginalFilename()+" is not real image!");
			}
		}
		/*if (files.size()>1){
			if(files.getLast().getFileName().equals(lastFileMeta.getFileName())){
				files.getLast().setFileUuid(lastFileMeta.getUuid());
				System.out.println("another file uuid "+lastFileMeta.getUuid());
			}
		}*/
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
	public synchronized void getUuid(@RequestParam(value="uuid") String uuid,@RequestParam(value="name") String name){
		//System.out.println("IT's DONE");
		System.out.println("counter is "+counter);
		uuid = parseUuid(uuid);
		name = parseUuid(name);
		name = parsePath(name);
		System.out.println("name "+name);
		System.out.println("uuid "+uuid);
		for(FileMeta fm:files){
			if (fm.getFileName().equals(name)){
				fm.setFileUuid(uuid);
				String ext = getExtension(fm.getFileName());
				File file = new File("files/"+fm.getFileName());
				File file2 = new File("files/"+fm.getUuid()+ext);
				boolean suc = file.renameTo(file2);
			}
		}
		/*lastFileMeta = new FileMeta();
		lastFileMeta.setFileName(name);
		lastFileMeta.setFileUuid(uuid);*/
		/*System.out.println("files size "+files.size());
		if (counter<=files.size()-1){
			System.out.println("____________YES");
			System.out.println("What now in files");
			for(FileMeta fm:files){
				System.out.println(fm.getFileName());
			}
			/*if(files.get(0).getFileName().equals(name)){
				files.get(0).setFileUuid(uuid);
				System.out.println("first file uuid "+uuid);
				String ext = getExtension(files.get(0).getFileName());
				File file = new File("files/"+files.get(0).getFileName());
				File file2 = new File("files/"+files.get(0).getUuid()+ext);
				boolean suc = file.renameTo(file2);
				// if (suc)
					// System.out.println("suc");
				// else System.out.println("not suc");
			}*/
			/*System.out.println(files.get(counter).getFileName()+" is name we have");
			System.out.println(name+" is name we need");
			if(files.get(counter).getFileName().equals(name)){
				files.get(counter).setFileUuid(uuid);
				System.out.println("__________YES2");
				System.out.println(counter+" file uuid "+uuid);
				String ext = getExtension(files.get(counter).getFileName());
				File file = new File("files/"+files.get(counter).getFileName());
				File file2 = new File("files/"+files.get(counter).getUuid()+ext);
				boolean suc = file.renameTo(file2);
			}*/
			/*for(int i=0;i<files.size();i++){
				if(files.get(i).getFileName().equals(name)){
					files.get(i).setFileUuid(uuid);
					System.out.println("__________YES2");
					System.out.println(i+" file uuid "+uuid);
					System.out.println("filename "+files.get(i).getFileName());
					System.out.println("uuid "+files.get(i).getUuid());
					String ext = getExtension(files.get(i).getFileName());
					File file = new File("files/"+files.get(i).getFileName());
					File file2 = new File("files/"+files.get(i).getUuid()+ext);
					boolean suc = file.renameTo(file2);
				}
			}
		}
			else{
				System.out.println("_________NO");
				FileMeta fm = new FileMeta();
				fm.setFileName(name);
				fm.setFileUuid(uuid);
				files2.add(fm);
			}*/
			// counter++;
	}
	@RequestMapping(value="/delete",method=RequestMethod.POST)
	public void deleteElement(@RequestParam(value="uuid") String uuid,@RequestParam(value="name") String name){
		//System.out.println("\n\n\n\nIN DELETING");
		name = parsePath(name);
		int pos = 0;
		//System.out.println("Size of files "+files.size());
		for(int i=0;i<files.size();i++){
			if (files.get(i).getUuid().equals(uuid))
				pos =i;
		}
		files.remove(pos);
		try{
			File file = new File("files/"+uuid+getExtension(name));
			FileUtils.forceDelete(file);
			//if(deleteQuietly(file))
			//	System.out.println("File deleted "+name);
			//else System.out.println("File not deleted "+name);
		}catch(Exception e){
			System.err.println("Exception");
		}
		//System.out.println("Size of files "+files.size());
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
