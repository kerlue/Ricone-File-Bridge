/**
 * @author      Schillaci "Dwayne" McInnis <dmcinnis@lhric.org>
 * @version     1.0
 * @since       Jul 7, 2016
 * Filename		LoadConfiguration.java
 */


import java.io.File;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList; 

public class LoadConfiguration extends Configuration {
	private Document document;
	
	LoadConfiguration(){
		
		GlobalUtilities.logInfo("Loading configuration settings...");
		GlobalUtilities.setWorkingDir(getWorkingDir());	 
		GlobalUtilities.enableLogging(true, GlobalUtilities.getWorkingDir().getAbsolutePath());
		
		
		File file = new File(CONF_FILE);
		
		if(!file.exists()){
			//When running jar the config file might be located in the working dir. 
			file = new File(GlobalUtilities.getWorkingDir()+File.separator+CONF_FILE);
		}
		
		// Parse xml settings 
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		
		try {
			db = dbf.newDocumentBuilder();
			document = db.parse(file);			
			// Validate and Load settings into variables. 
			if(validateSettings()){
				super.requiredJsonData = requiredDataToJSON(document);
			}
		
		}catch (Exception e) {
			GlobalUtilities.logError(e.getMessage());
			e.printStackTrace();
		}	 			
	}
	
   
	private JSONObject requiredDataToJSON(Document document) throws DOMException, JSONException {
    	    NodeList nodes = document.getElementsByTagName("required_data")
    	    		   .item(0).getChildNodes();
    	    
    	    JSONObject obj = new JSONObject();
    	    
    	    for(int i=0, j =0; i < nodes.getLength(); i++){
    	    	if(nodes.item(i).getNodeType() == Node.ELEMENT_NODE){
    	    		
    	    		JSONObject tempObj = new JSONObject();
    	    		tempObj.put("text_file_name", nodes.item(i).getNodeName());
    	    		tempObj.put("column_name", nodes.item(i).getAttributes().item(0).getTextContent());
    	    		tempObj.put("required_data", nodes.item(i).getTextContent());    	    		
    	    		obj.put(""+j, tempObj);
    	    		j++;
    	    	}
    	    }
    	    return obj;
	}

	private boolean validateSettings() {
    	
    	GlobalUtilities.logInfo("Configuration settings loaded..\nValidating settings...");
    	
        /**
         *  Check that the configuration output format is valid 
         *  and assign values from config file to variable
        */
    	
    	 sftpPort = getTextContent("sftp_port");
    	 sftpUsername = getTextContent("sftp_username");
    	 sftpPassword = getTextContent("sftp_password");
    	 outputFolderTitle = getTextContent("output_folder_title");
    	 zipEnabled = (getTextContent("zip_enabled").contains("true"));
    	 enableLogging = (getTextContent("log_enabled").contains("true"));
    	 outputSchema = getTextContent("output_schema").toLowerCase();
   	     outputExport = getTextContent("output_export").toLowerCase();  	    
   	     outputPath = getTextContent("output_path"); 	
   	     clientSecret = getTextContent("client_secret");
 	     navigationPageSize = getTextContent("navigation_page_size");
 	     providerId = getTextContent("provider_id");
 	     clientId = getTextContent("client_id");
 	     authUrl = getTextContent("auth_url");
 	     filter_by = getTextContent("filter_by");
 	     filter_refid = getFilterContent("filter_refid");
 	     filter_grades = getFilterContent("filter_grades");
    	 
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
            	
            	  GlobalUtilities.logWarning("No output path specified! Using "+ GlobalUtilities.getWorkingDir()
            	  + " as default.");

                  outputPath = GlobalUtilities.getWorkingDir().getAbsolutePath();			
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
	
private String[] getFilterContent(String tagId) {
		
		try{
			return (document.getElementsByTagName(tagId).item(0)
			.getTextContent().trim().split(","));
			
		}catch(Exception e){
					
			return null;
		}
		 
	}
	
	 private File getWorkingDir() {
	    	
	    	File loca = null;
			try {
				loca = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());	
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
	    	
			return new File(loca.getParentFile()+File.separator);
		}


	
}
