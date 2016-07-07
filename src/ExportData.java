import java.io.File;


public class ExportData {

	public ExportData(LoadConfiguration config, String string) {
		
		setupOutputPath(config);
		//TODO: Write file to path
		//TODO: SFTP is required
		
	}

	private boolean setupOutputPath(LoadConfiguration config) {
		
		if(config.getOutputExport() == GlobalUtilities.LOCAL){			
			String outputPath = (config.getOutputPath().isEmpty())? "":config.getOutputPath();
			String outputFolderTitle = (config.getOutputFolderTitle().isEmpty())
					? "SOME TITLE HERE":config.getOutputPath();
			
			File file = new File(outputPath+"/"+outputFolderTitle);
			
			if (!file.mkdirs()) {
				GlobalUtilities.logError("Failed to create folder");
				return false;
			}else{
				GlobalUtilities.logInfo(file.getAbsolutePath()+" created sucessfully ");
				return true;
			}
			
		}else {
			return false;
		}
		
		 
	}

}
