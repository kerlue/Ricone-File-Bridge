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
		
		
		// Class to pull data from ric one api
		
		///TODO Search by grade functionality not working - Courses do not have grade information populated in API. blocked from enabling searchbygrade until grades are populated
		
		
		//Lea RefID - 15077B52-7D2A-4855-B41F-37FBA242522E
		//School RefID - 25A10C7C-1BA5-4174-BA3F-1FA81849D076,A5CA3C70-3254-489C-97F8-ED1A2D76FF33
		
		DataReader d = new DataReader(config.getFilterBy(),config.getFilterRefId(),config.getFilterGrades()); // first parameter should be Lea or School, second should be refid of that lea/school, and optional third param is the grade levels as a String[]
		
		
		
		System.out.println("Authenticating........");
        Authenticator auth = new Authenticator(config.getAuthUrl(), config.getClientId(), config.getClientSecret());
        
		//GetDataFromApiTest dat = new GetDataFromApiTest(config);
		
		System.out.println("Finshed Authenticating");
		
		long st_time = System.nanoTime();
		
		
		ArrayList<Data> file_list = new ArrayList<Data>();
		
		int maxi = config.getTextTitle().size();
//		int maxi = 2;
		
		for (int i=0; i < maxi; ++i) {
			System.out.println("File " + (i+1) + " Started");
			ArrayList<DataType> temp = new ArrayList<DataType>();
			
			temp = d.GetDataTypes(config.getColumnNames().get(i), config.getRequiredData().get(i));
			
			System.out.println("---------------------------------Finished Reading Data Requirements---------------------------------");
			
		    
			Data data = d.ReadIn(auth,temp,config.getTextTitle().get(i),i,maxi,st_time);
			file_list.add(data);
			
			System.out.println((i+1) + " files completed in " + ((float)(System.nanoTime()-st_time))/1000000000 + "s");
		}

		System.out.println("-------------------------------------Finished pulling Data------------------------------------");
		
		ExportData ex_data = new ExportData(config, file_list);
		
		System.out.println("Completed!");
		/**/
	}
}
