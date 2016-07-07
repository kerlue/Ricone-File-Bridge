
public class MainDriver {
	public static void main(String[] args){
		//enable logging
		GlobalUtilities.enableLogging(true);
		
		LoadConfiguration config = new LoadConfiguration();
		//TODO: Class to pull data from ric one api
		//TODO: Class to format data into required output schema 
		ExportData data = new ExportData(config, "");
		
		
		/*System.out.println("--->>" + 
		config.getOutputSchema()  +" " +
		config.getOutputExport()  +" " +		
		config.getOutputPath() +" " +
		config.getSftpPort()+" " +
		config.getSftpUsername()+" " +
		config.getSftpPassord()+" " +
		config.isZipEnabled()+" " +
		config.getOutputFolderTitle()+" " +
		config.getZipMode());
		*/
	}
	
	
}
