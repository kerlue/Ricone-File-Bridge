
/**
 * @author      Schillaci "Dwayne" McInnis <dmcinnis@lhric.org>
 * @version     1.0
 * @since       Jul 7, 2016
 * Filename		Configuration.java
 */

import java.io.File;


public class ExportData {

	public ExportData(LoadConfiguration config, String string) {
		
		String outputPath = setupOutputPath(config);
		//TODO: Write file to path
		
		if(config.getOutputExport() == GlobalUtilities.SFTP){
			
		}
		else if (config.getOutputExport() == GlobalUtilities.LOCAL && config.isZipEnabled() == true){
			new FolderZiper(outputPath);
		}
		
	}

	private String setupOutputPath(LoadConfiguration config) {

				
		String outputPath = (config.getOutputPath().isEmpty())? "":config.getOutputPath();
		String outputFolderTitle = (config.getOutputFolderTitle().isEmpty())
				? "SOME TITLE HERE":config.getOutputPath();
		
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
	
	
	

}
