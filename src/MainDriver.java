
public class MainDriver {
	public static void main(String[] args){
		//enable logging
		GlobalUtilities.enableLogging(true);
		
		LoadConfiguration config = new LoadConfiguration();
		 
		System.out.println("--->>" + 
		config.getOutputSchema()  +" " +
		config.getOutputExport()  +" " +		
		config.getOutputPath() +" " +
		config.getSftpPort()+" " +
		config.getSftpUsername()+" " +
		config.getSftpPassord()+" " +
		config.isZipEnabled()+" " +
		config.getZipTitle()+" " +
		config.getZipMode());
		 
			
	}
	
	
}
