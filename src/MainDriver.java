/*///////////////////////////////////////////////
 * RICOne FileBridge
 * Version: 1.4
 * Updated: 8/3/2016
*////////////////////////////////////////////////


import java.util.ArrayList;
import riconeapi.common.Authenticator;
import riconeapi.common.Util;

public class MainDriver {
	public static void main(String[] args){
				
		Util.disableSslVerification();   // TODO remove this line before going to production
		Configuration config = new LoadConfiguration();
		
		System.out.println("---------------------------------Finished Loading Config file---------------------------------");
		
		
		System.out.println("Authenticating........");
        Authenticator auth = new Authenticator(config.getAuthUrl(), config.getClientId(), config.getClientSecret());
        System.out.println("Finshed Authenticating");
		
        DataReader.PopulateDataReader(config.getFilterBy(),config.getFilterRefId(),config.getFilterGrades());
		
        ArrayList<Data> file_list = DataReader.GenerateFiles(config,auth);
		
		System.out.println("-------------------------------------Finished pulling Data------------------------------------");
		
		ExportData.Export_Data(config, file_list);
		
		System.out.println("Completed!");
		/**/
	}
}
