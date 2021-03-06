
/**
 * @author      Schillaci "Dwayne" McInnis <dmcinnis@lhric.org>
 * @version     1.0
 * @since       Jul 7, 2016
 * Filename		ExportData.java
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.json.JSONObject;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;


public class ExportData {
	
	public ExportData(Configuration config, ArrayList<Data> data) {
		
		// create output directory
		File file = setupOutputPath(config);
	 
		switch(config.getOutputSchema()){
		     case GlobalUtilities.CSV: 
		    	 new GenerateCsvFile(file,data,config);
			 break;
			 
		     case GlobalUtilities.XML: 
		    	 new GenerateXmlFile(file,data,config);
			 break;
			 
		     case GlobalUtilities.JSON: 
		    	 new GenerateJSONFile(file,data,config);
			 break;
			 
		 }
		
		// send file to SFTP server
		if(config.getOutputExport().matches(GlobalUtilities.SFTP)){
			pushFileToSftpServer(file.getAbsolutePath(), config);
		}
		
		else if (config.getOutputExport().matches(GlobalUtilities.LOCAL)
				&& config.isZipEnabled() == true){
			// file is saved locally. Only need to create a zip version.		
			zipFileOnly(file.getAbsolutePath());
		}
		
	}

     

    /**
     * Zip local files if required. 
    */
	
	private void zipFileOnly(String outputPath) {
		try {
			FolderZiper.zipFolder(outputPath, outputPath+".zip");
		} catch (Exception e) {
			GlobalUtilities.logInfo("Failed to zip file..");
			e.printStackTrace();
		}
	}

	
	 /**
     * Zip local files and push to server. 
    */
	private void pushFileToSftpServer(String outputPath, Configuration config) {
		if(FolderZiper.zipFolder(outputPath, outputPath+".zip")){
			// folder zipped successfully 
			sftpUploader(config.getOutputPath(), 
					config.getSftpUsername(), 
					config.getSftpPassord(), 
					config.getSftpPort(),
					outputPath);
		}
		
	}

	private File setupOutputPath(Configuration config) {

		String outputPath = "";
		
		if (config.getOutputExport().matches(GlobalUtilities.LOCAL)){
			outputPath = (config.getOutputPath().isEmpty()) ? "": config.getOutputPath()+File.separator;
		}
		
		// If no title is set then destination folder will be called "RicOne Data"
		String outputFolderTitle = (config.getOutputFolderTitle().isEmpty()) 
				? "RicOne Data":config.getOutputFolderTitle();
		
		// create the new output file
		File file = new File(outputPath+outputFolderTitle);
		
		//If file exists then remove old files.
		if(file.exists()){
			deleteOldFiles(file);
		}else{
			// If file does not exists create new directory.
			file.mkdirs();
		}
		
		GlobalUtilities.logInfo(file.getAbsolutePath()+" created sucessfully ");
		return file;
		
	}
	
	static public void deleteOldFiles(File path) {
	    if (path.exists()) {
	        File[] files = path.listFiles();
	        for (int i = 0; i < files.length; i++) {
	               files[i].delete();
	            }
	        }
	    
	}
	
	public static void sftpUploader(String path, String user, String password, String port, String srcDir) {
	  try {
	    JSch jsch = new JSch();
		Session session = jsch.getSession(user,path, Integer.parseInt(port));
		session.setPassword(password);
		
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		session.connect();
		
		
		Channel channel = session.openChannel("sftp");
		channel.connect();
		ChannelSftp channelSftp = (ChannelSftp) channel;
		//channelSftp.cd("");

		File f1 = new File(srcDir+".zip");
		channelSftp.put(new FileInputStream(f1), f1.getName());
		
		channelSftp.exit();
		session.disconnect();
	
	  } catch (Exception ex) {
		  GlobalUtilities.logError("Failed to upload file to sftp server. Please check sftp host, username, password and port number.");
		  GlobalUtilities.logError(ex.getMessage());
	     ex.printStackTrace();
	  }
	  
	}
	

}
