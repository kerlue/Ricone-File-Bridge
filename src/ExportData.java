
/**
 * @author      Schillaci "Dwayne" McInnis <dmcinnis@lhric.org>
 * @version     1.0
 * @since       Jul 7, 2016
 * Filename		Configuration.java
 */

import java.io.File;
import java.io.FileInputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;


public class ExportData {

	public ExportData(LoadConfiguration config, String string) {
		
		String outputPath = setupOutputPath(config);
		//TODO: Write file to path
		
		if(config.getOutputExport() == GlobalUtilities.SFTP){
			if(FolderZiper.zipFolder(outputPath, outputPath+".zip")){
				// folder zipped successfully 
				sftpUploader(config.getOutputPath(), 
						config.getSftpUsername(), 
						config.getSftpPassord(), 
						config.getSftpPort(),
						outputPath);
				
			}

			 
		}
		else if (config.getOutputExport() == GlobalUtilities.LOCAL
				&& config.isZipEnabled() == true){
			try {
				GlobalUtilities.logInfo("Zipping in progress..");
				FolderZiper.zipFolder(outputPath, outputPath+".zip");
				GlobalUtilities.logInfo("Zipping completed..");
			} catch (Exception e) {
				GlobalUtilities.logInfo("Failed to zip file..");
				e.printStackTrace();
			}
		}
		
	}

	private String setupOutputPath(LoadConfiguration config) {

				
		String outputPath = "";
		
		if (config.getOutputExport() == GlobalUtilities.LOCAL)
			outputPath = (config.getOutputPath().isEmpty())? "":config.getOutputPath();
		
		String outputFolderTitle = (config.getOutputFolderTitle().isEmpty()) ? "SOME TITLE HERE":config.getOutputFolderTitle();
		
		File file = new File(outputPath+"/"+outputFolderTitle);
		
		if(file.exists())
			file.delete();
		
		if (!file.mkdirs()) {
			GlobalUtilities.logError("Failed to create folder");
			System.exit(1);
			return ""; 
			
		}else{
			GlobalUtilities.logInfo(file.getAbsolutePath()+" created sucessfully ");
			return file.getAbsolutePath();
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
		  ex.printStackTrace();
		  }
		  
		}
	

}
