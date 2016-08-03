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
		
		
		System.out.println("Authenticating........");
        Authenticator auth = new Authenticator(config.getAuthUrl(), config.getClientId(), config.getClientSecret());
        System.out.println("Finshed Authenticating");
		
        DataReader.PopulateDataReader(config.getFilterBy(),config.getFilterRefId(),config.getFilterGrades(),false);
		
        ArrayList<Data> file_list = DataReader.GenerateFiles(config,auth);
        
        System.out.println("file_list: " + file_list); 
		
		System.out.println("-------------------------------------Finished pulling Data------------------------------------");
		
		ExportData ex_data = new ExportData(config, file_list);
		
		System.out.println("Completed!");
		/**/
	}
}
