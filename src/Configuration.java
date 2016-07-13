import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.w3c.dom.Node;

/**
 * @author      Schillaci "Dwayne" McInnis <dmcinnis@lhric.org>
 * @version     1.0
 * @since       Jul 7, 2016
 * Filename		Configuration.java
 */


public abstract class Configuration {
	

	 protected String outputSchema;
	 protected String outputExport;
	 protected String outputPath;
	 protected String sftpPort;
	 protected String sftpUsername;
	 protected String sftpPassword;
	 protected String outputFolderTitle;
	 protected boolean zipEnabled;
	 final String CONF_FILE = "config.xml";
	 protected String clientSecret;
	 protected String navigationPageSize;
	 protected String providerId;
	 protected String clientId;
	 protected String authUrl;
	 public JSONObject requiredJsonData;
	
  
	/**
	 * @param args void.
	 * @param Returns export type. eg CSV, XML or JSON.
	 */
	public String getOutputSchema() {
		return this.outputSchema;
	}

	/**
	 * @param args void.
	 * @param Returns output export type. eg local, sftp.
	 */
	public String getOutputExport() {
		return this.outputExport;
	}
	
	/**
	 * @param args void.
	 * @param Returns output path ex c://user.
	 */
	public String getOutputPath() {
		return this.outputPath;
	}

	/**
	 * @param args void.
	 * @param Returns SFTP Port.
	 */
	public String getSftpPort() {
		return this.sftpPort;
	}

	/**
	 * @param args void.
	 * @param Returns SFTP Username.
	 */
	public String getSftpUsername() {
		return this.sftpUsername;
	}
	
	/**
	 * @param args void.
	 * @param Returns SFTP password.
	 */

	public String getSftpPassord() {
		return this.sftpPassword;
	}

	/**
	 * @param args void.
	 * @param Returns a boolean value to for zip mode (true or false).
	 */
	
	public boolean isZipEnabled() {
		return this.zipEnabled;
	}
	
	/**
	 * @param args void.
	 * @param Returns zip title from configuration settings .
	 */
	
	public String getOutputFolderTitle() {
		return this.outputFolderTitle;
	}

	/**
	 * @param args void.
	 * @param Returns authentication url.
	 */
	
    public String getAuthUrl() {
 		return this.authUrl;
 	}

    /**
	 * @param args void.
	 * @param Returns client id.
	 */
     public String getClientId() {
 		return this.clientId;
 	}

     /**
 	 * @param args void.
 	 * @param Returns provider id.
 	 */
     public String getProviderId() {
 		return this.providerId;
 	}
     
     
     /**
  	 * @param args void.
  	 * @param Returns navigation page size.
  	 */
     public String getNavigationPageSize() {
 		return this.navigationPageSize;
 	}

  
     /**
   	 * @param args void.
   	 * @param Returns client secret.
   	 */
     public String getClientSecret() {
 		return this.clientSecret;
 	}
     
     
     /**
    	 * @param args void.
    	 * @param Returns a list of titles. These titles will used to create .txt files for output data.
    	 */
     
     public List<String> getTextTitle() {
    	List<String> textFiles = new ArrayList<String>();
    	
    	 try{
    		 for(int i=0; i < requiredJsonData.length(); i++){   			
    		     JSONObject jsonObj = new JSONObject(requiredJsonData.get(""+i).toString());
    		     textFiles.add(jsonObj.get("text_file_name").toString());
    		 }
    	 }catch(Exception e){
    		 e.printStackTrace();
    	 }
    	 
  		return textFiles;
  	}

	
     /**
 	 * @param args void.
 	 * @param Returns a list of column names. Each item in the list consist of a
 	 *  string of column names that correspond with the required data field.
 	 */
     
     public List<String> getColumnNames() {
    	 
    	 List<String> columnNames = new ArrayList<String>();
     	
    	 try{
    		 for(int i=0; i < requiredJsonData.length(); i++){   			
    		     JSONObject jsonObj = new JSONObject(requiredJsonData.get(""+i).toString());
    		     columnNames.add(jsonObj.get("column_name").toString());
    		 }
    	 }catch(Exception e){
    		 e.printStackTrace();
    	 }
    	 
  		
		return columnNames;
	 }

     
     /**
  	 * @param args void.
  	 * @param Returns a list of data to search for. Each item in the list consist of a
  	 *  string of required data.
  	 */
     
	public List<String> getRequiredData() {
		 List<String> requiredData = new ArrayList<String>();
	     	
    	 try{
    		 for(int i=0; i < requiredJsonData.length(); i++){   			
    		     JSONObject jsonObj = new JSONObject(requiredJsonData.get(""+i).toString());
    		     requiredData.add(jsonObj.get("required_data").toString());
    		 }
    	 }catch(Exception e){
    		 e.printStackTrace();
    	 }
    	 
  		
		return requiredData;
	}
      
     
	 
	
}
