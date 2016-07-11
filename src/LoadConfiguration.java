/**
 * @author      Schillaci "Dwayne" McInnis <dmcinnis@lhric.org>
 * @version     1.0
 * @since       Jul 7, 2016
 * Filename		LoadConfiguration.java
 */


import java.io.File; 

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory; 

import org.w3c.dom.Document; 

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
    	
    	// assign values from config file to variable
    	 sftpPort = getTextContent("sftp_port");
    	 sftpUsername = getTextContent("sftp_username");
    	 sftpPassword = getTextContent("sftp_password");
    	 outputFolderTitle = getTextContent("output_folder_title");
    	 zipMode = getTextContent("zip_file_mode");
    	 zipEnabled = (getTextContent("zip_enabled").contains("true"));
    	 outputSchema = getTextContent("output_schema");
   	     outputExport = getTextContent("output_export");  	    
   	     outputPath = getTextContent("output_path"); 	
   	     clientSecret = getTextContent("client_secret");
 	     navigationPageSize = getTextContent("navigation_page_size");
 	     providerId = getTextContent("provider_id");
 	     clientId = getTextContent("client_id");
 	     authUrl = getTextContent("auth_url");
    	 
 	     // check that config file settings are valid.
 	     
          if(!(outputSchema.matches("csv")|| 
        	   outputSchema.matches("xml")||
        	   outputSchema.matches("json"))){
        	
        	  GlobalUtilities.logWarning("Incorrect output format! Using CSV as default.");
        	  outputSchema = "csv";			
		  }
          
          if((outputExport.matches("local")  || outputExport.matches("sftp")) == false){
            	
            	  GlobalUtilities.logWarning("Incorrect output destination! Using local as default.");
            	  outputExport = "local";			
    		} 
          
          if(outputPath.isEmpty()){
            	
            	  GlobalUtilities.logWarning("No path specified! Using "+ System.getProperty("user.dir")
            	  + " as default.");

                  outputPath = System.getProperty("user.dir");			
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
          
          if(navigationPageSize.isEmpty()){
        	  navigationPageSize = "1";
        	  GlobalUtilities.logWarning("Navigation page size missing. using 1 as default value.. ");        	  
          }
          
          if(authUrl.isEmpty()){
        	  GlobalUtilities.logError("Authentication url missing!.. ");   
        	  System.exit(1);
          }
          if(clientId.isEmpty()){
        	  GlobalUtilities.logError("Client id missing.. ");   
        	  System.exit(1);
          }
          if(providerId.isEmpty()){
        	  GlobalUtilities.logError("Provider id url missing.. ");   
        	  System.exit(1);
          }
          if(clientSecret.isEmpty()){
        	  GlobalUtilities.logError("Password missing.. ");   
        	  System.exit(1);
          }
          

     	 GlobalUtilities.logInfo("Settings validated...");  
     	 
		return true;
	}
	
	private String getTextContent(String tagId) {
		
		try{
			return (document.getElementsByTagName(tagId).item(0)
			.getTextContent().trim());
			
		}catch(Exception e){
			GlobalUtilities.logError("Incorrect configuration tag...");
			System.exit(1);			
			return "null";
		}
		 
	}

	
}
