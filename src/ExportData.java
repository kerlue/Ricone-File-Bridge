
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

import org.json.JSONObject;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;


public class ExportData {

	public ExportData(Configuration config, GetDataFromApiTest data) {
		
		File file = setupOutputPath(config);
	 
		switch(config.getOutputSchema()){
		     case GlobalUtilities.CSV: 
		    	 new GenerateCsvFile(config,file,data);
			 break;
			 
		     case GlobalUtilities.XML: 
		    	 //TODO: create xml output
			 break;
			 
		     case GlobalUtilities.JSON: 
		    	 //TODO: create JSON output
			 break;
			 
		 }
	
		
		if(config.getOutputExport().matches(GlobalUtilities.SFTP)){
			pushFileToSftpServer(file.getAbsolutePath(), config);
		}
		
		else if (config.getOutputExport().matches(GlobalUtilities.LOCAL)
				&& config.isZipEnabled() == true){
			zipFileOnly(file.getAbsolutePath());
		}
		
	}

	private void zipFileOnly(String outputPath) {
		try {
			FolderZiper.zipFolder(outputPath, outputPath+".zip");
		} catch (Exception e) {
			GlobalUtilities.logInfo("Failed to zip file..");
			e.printStackTrace();
		}
	}

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
		
		
		String outputFolderTitle = (config.getOutputFolderTitle().isEmpty()) 
				? "RicOne Data":config.getOutputFolderTitle();
		
		File file = new File(outputPath+outputFolderTitle);
		
		
		
		if(file.exists()){
			deleteDirectory(file);
			
		}
		
		if (!file.mkdirs()) {
			GlobalUtilities.logError("Failed to create folder");
			System.exit(1);
			return null; 
			
		}
 
		GlobalUtilities.logInfo(file.getAbsolutePath()+" created sucessfully ");
		return file;
		
			
		
	}
	
	static public boolean deleteDirectory(File path) {
	    if (path.exists()) {
	        File[] files = path.listFiles();
	        for (int i = 0; i < files.length; i++) {
	            if (files[i].isDirectory()) {
	                deleteDirectory(files[i]);
	            } else {
	                files[i].delete();
	            }
	        }
	    }
	    return (path.delete());
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
