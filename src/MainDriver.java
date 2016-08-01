import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import riconeapi.common.Authenticator;
import riconeapi.common.Util;
import riconeapi.common.XPress;
import riconeapi.models.authentication.Endpoint;
import riconeapi.models.xpress.XLeaType;
import riconeapi.models.xpress.XOrganizationAddressType;

public class MainDriver {
	public static void main(String[] args){
				
		Util.disableSslVerification();   // TODO remove this line before going to production
		Configuration config = new LoadConfiguration();
		
		System.out.println("---------------------------------Finished Loading Config file---------------------------------");
		
		
		//TODO: Class to pull data from ric one api
		
		DataReader d = new DataReader("Lea",new String[]{"15077B52-7D2A-4855-B41F-37FBA242522E"}, new String[]{"03","12"}); // first parameter should be Lea or School, second should be refid of that lea/school, and optional third param is the grade levels as a String[]
		//DataReader d = new DataReader("School",new String[]{"25A10C7C-1BA5-4174-BA3F-1FA81849D076", "A5CA3C70-3254-489C-97F8-ED1A2D76FF33"}, new String[]{"3","4"}); // first parameter should be Lea or School, second should be refid of that lea/school, and optional third param is the grade levels as a String[]
		
		System.out.println("Authenticating........");
        Authenticator auth = new Authenticator(config.getAuthUrl(), config.getClientId(), config.getClientSecret());
        
		//GetDataFromApiTest dat = new GetDataFromApiTest(config);
		
		System.out.println("Finshed Authenticating");
		
		
		ArrayList<Data> file_list = new ArrayList<Data>();
		
		//for (int i=0; i < config.getTextTitle().size(); ++i) { // TODO remove the commented section to do full file
		for (int i=4; i < 5; ++i) { // TODO remove the commented section to do full file
			System.out.println("File " + (i+1) + " Started");
			ArrayList<DataType> temp = new ArrayList<DataType>();
			
			temp = d.GetDataTypes(config.getColumnNames().get(i), config.getRequiredData().get(i));
			
			System.out.println("---------------------------------Finished Reading Data Requirements---------------------------------");
			
		    
			Data data = d.ReadIn(auth,temp,config.getTextTitle().get(i));
			file_list.add(data);
			
			System.out.println("File " + (i+1) + " Completed");
		}
		
		//TODO: Class to format data into required output schema 
		
		System.out.println("-------------------------------------Finished pulling Data------------------------------------");
		
		
//		for (Data dat : file_list) {
//			dat.Print();
//		}
		
		ExportData ex_data = new ExportData(config, file_list);
		
		System.out.println("Completed!");
		/**/
	}
	
	
}
