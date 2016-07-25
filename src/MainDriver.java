import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import riconeapi.common.Authenticator;
import riconeapi.common.Util;

public class MainDriver {
	public static void main(String[] args){
		
		
		Util.disableSslVerification();   // TODO remove this line before going to production
		Configuration config = new LoadConfiguration();
		
		System.out.println("---------------------------------Finished Loading Config file---------------------------------");
		
		
		//TODO: Class to pull data from ric one api
		
		//DataReader d = new DataReader("Lea",new String[]{"LeaRefId1","LeaRefId2"},new String[]{"3","8","10","11","12"}); // first parameter should be Lea or School, second should be refid of that lea/school, and optional third param is the grade levels as a String[]
		
		System.out.println("Authenticating........");
        //Authenticator auth = new Authenticator(config.getAuthUrl(), config.getClientId(), config.getClientSecret());
        
		GetDataFromApiTest data = new GetDataFromApiTest(config);
		
		System.out.println("Finshed Authenticating");
		
		
		/*ArrayList<Data> file_list = new ArrayList<Data>();
		
		for (int i=0; i < /*config.getTextTitle().size()*//*1; ++i) { // TODO remove the commented section to do full file
			ArrayList<DataType> temp = new ArrayList<DataType>();
			
			temp = d.GetDataTypes(config.getColumnNames().get(i), config.getRequiredData().get(i));
		    
			System.out.println(temp.get(0));
			
			Data data = d.ReadIn(auth,temp,config.getTextTitle().get(i));
			file_list.add(data);
		}*/
		
		//TODO: Class to format data into required output schema 
		
		System.out.println("-------------------------------------Finished pulling Data------------------------------------");
		
		ExportData ex_data = new ExportData(config, data);
		
		System.out.println("Completed!");
		/**/
	}
	
	
}
