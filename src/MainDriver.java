import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

public class MainDriver {
	public static void main(String[] args){
		
		
		
		
		Configuration config = new LoadConfiguration();
	
		//TODO: Class to pull data from ric one api
		//TODO: Class to format data into required output schema 
		
	
		ExportData data = new ExportData(config, "");
		
		
		System.out.println("--->>" + 
		config.getOutputSchema()  +"-" +
		config.getOutputExport()  +" " +		
		config.getOutputPath() +" " +
		config.getSftpPort()+" " +
		config.getSftpUsername()+" " +
		config.getSftpPassord()+" " +
		config.isZipEnabled()+" " +
		config.getOutputFolderTitle());
		
		
		System.out.println("Completed!");
		/**/
	}
	
	
}
