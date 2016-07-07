import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class LoadConfiguration extends Configuration {
	private Document document;
	
	LoadConfiguration(){
		
		GlobalUtilities.logInfo("Loading configuration settings...");
		
		// Setup xml parser  
		File file = new File(CONF_FILE);		
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		
		try {
			db = dbf.newDocumentBuilder();
			document = db.parse(file);
			
			// Validate and Load settings into variables. 
			validateSettings();
		
		}catch (Exception e) {
			GlobalUtilities.logError(e.getMessage());
			e.printStackTrace();
		}	 			
	}
	
    private boolean validateSettings() {
    	
    	GlobalUtilities.logInfo("Configuration settings loaded..\nValidating settings...");
    	
    	// check that the configuration output format is valid. 
    	
    	 sftpPort = getTextContent("sftp_port");
    	 sftpUsername = getTextContent("sftp_username");
    	 sftpPassword = getTextContent("sftp_password");
    	 zipTitle = getTextContent("zip_file_title");
    	 zipMode = getTextContent("zip_file_mode");
    	 zipEnabled = (getTextContent("zip_enabled").contains("true"));
    	 outputSchema = getTextContent("output_schema");
   	     outputExport = getTextContent("output_export");
   	     outputPath = getTextContent("output_files_destination");
    	 
          if(!(outputSchema.matches("csv")|| 
        	   outputSchema.matches("xml")||
        	   outputSchema.matches("json"))){
        	
        	  GlobalUtilities.logWarning("Incorrect output format! Using CSV as default.");
        	  outputSchema = "csv";			
		  }
          
          if(!(outputExport.matches("local")||
        		  outputExport.matches("sftp"))){
            	
            	  GlobalUtilities.logWarning("Incorrect output destination! Using local as default.");
            	  outputExport = "local";			
    		} 
          
          if(outputPath.isEmpty()){
            	
            	  GlobalUtilities.logWarning("No path specified! Using "+System.getProperty("user.dir")
            	  + " as default.");

                  outputPath = "local";			
    	   } 
          
          if(outputExport.matches("sftp")){
        	  boolean error = false;
        	  
        	  if(sftpPort.isEmpty()){
        		  error= true;
        		  GlobalUtilities.logError("SFTP export selected but missing sftp port number");       		  
        	  }
        	  if(sftpUsername.isEmpty()){
        		  error= true;
        		  GlobalUtilities.logError("SFTP export selected but missing sftp username");
        	  }
        	  if(sftpPassword.isEmpty()){
        		  error= true;
        		  GlobalUtilities.logError("SFTP export selected but missing sftp password");
        	  } 
        	  
        	  if(error){
        		  GlobalUtilities.logError("Please check configuration settings.. ");
        		  return false;
        	  }
        	  }
         
     	 GlobalUtilities.logInfo("Settings validated...");  
     	 
		return true;
	}
	
	private String getTextContent(String tagId) {
		return document.getElementsByTagName(tagId).item(0)
				.getTextContent().trim().toLowerCase();
	}

	
}
