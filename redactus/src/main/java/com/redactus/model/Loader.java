import java.io.File;
import java.io.IOException;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.ApplicationListener;
import org.apache.commons.io.FileUtils;
public class Loader implements ApplicationListener<ContextRefreshedEvent>{
        public void onApplicationEvent(ContextRefreshedEvent event) {
                 // do whatever you need to do here when app context is initialized / refreshed
				init();
        }
		public void init(){
			System.out.println("\n\n\nI'm here\n\n\n");
			File f = new File("files/");
			try{
				FileUtils.cleanDirectory(f);
			} catch(IOException e){
				System.err.println("Clearing directory error");
			}
		}
}
