/*////////////////////////////////////////////////
 * Created By: Shamus Cardon
 * Date: 7/7/2016
 * Version 0.1
*/////////////////////////////////////////////////

import riconeapi.models.authentication.Endpoint;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import riconeapi.common.Authenticator;
import riconeapi.common.XPress;


import riconeapi.models.xpress.XCalendarType;
import riconeapi.models.xpress.XContactStudentRelationshipType;
import riconeapi.models.xpress.XContactType;
import riconeapi.models.xpress.XCourseType;
import riconeapi.models.xpress.XEmailType;
import riconeapi.models.xpress.XEnrollmentType;
import riconeapi.models.xpress.XLanguageType;
import riconeapi.models.xpress.XLeaType;
import riconeapi.models.xpress.XMeetingTimeType;
import riconeapi.models.xpress.XOtherCourseIdType;
import riconeapi.models.xpress.XOtherOrganizationIdType;
import riconeapi.models.xpress.XOtherPersonIdType;
import riconeapi.models.xpress.XPersonNameType;
import riconeapi.models.xpress.XPersonReferenceType;
import riconeapi.models.xpress.XRaceType;
import riconeapi.models.xpress.XRosterType;
import riconeapi.models.xpress.XSchoolType;
import riconeapi.models.xpress.XSessionType;
import riconeapi.models.xpress.XStaffPersonAssignmentType;
import riconeapi.models.xpress.XStaffReferenceType;
import riconeapi.models.xpress.XStaffType;
import riconeapi.models.xpress.XStudentType;
import riconeapi.models.xpress.XTelephoneType;


class Data {
	String district_code = "test";
	String district_name;
	String address_1;
	String address_2;
	String city;
	String state;
	String zip;
	
	
	int test=0;
	
	Data(int t) {
		this.test = t;
	}
	
	String getStateProvinceId() {
		return district_code;
	}
	
}


public class Main {
	static final String authUrl= "AUTH URL";        
    static final String clientId= "YOUR USERNAME";
    static final String clientSecret = "YOUR PASSWORD";
    static final String providerId = "sandbox";
    static final int navigationPageSize = 1;
	
	public void GetData(XPress xPress) {
		
	}
    
    
    public static void main(String[] args) {
//		 Authenticator auth = new Authenticator(authUrl, clientId, clientSecret);
//	     for(Endpoint e : auth.getEndpoints(providerId)) {
//	         XPress xPress = new XPress(auth.getToken(), e.getHref());
//	         
//	         XLeas_GetXLeas(xPress);
//	     } 
    	
    	String c_file = "";
		
		if (args.length > 0) {
			for (int i=0; i < args.length; ++i) {
				if (args[i].equals("-file") && i+2 >= args.length) {
					c_file = args[i+1];
				}
			}
		}
		
		System.out.println(c_file);

		List<String> textData = new ArrayList<String>();
		
		try {
			FileReader fr = new FileReader(c_file);
			BufferedReader textReader = new BufferedReader(fr);
			String t;
			while ((t = textReader.readLine()) != null) {
				textData.add(t);
			}
			
			textReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i=0; i < textData.size(); ++i) {
			System.out.println(i+ " " + textData.toArray()[i]);
		}
		
		
		Data temp = new Data(5);
		//System.out.println(temp.getInfo());
		
		
		String t = "StateProvinceId";
		
		Method m;
		try {
			m = Data.class.getDeclaredMethod("get"+t);
			System.out.println(m.invoke(temp));
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			System.err.println("Couldnt find " + t + "as a callable method. Check the spelling?");
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
			
		
		
		
		System.out.println("End of Program");
    }


	public static void XLeas_GetXLeas(XPress xPress)
	{
		if(xPress.getXLeas().getData() != null)
		{
			for (XLeaType lea : xPress.getXLeas().getData())
			{	
				System.out.println("refId: " + lea.getRefId());
				System.out.println("leaName: " + lea.getLeaName());
				System.out.println("leaRefId: " + lea.getLeaRefId());
				System.out.println("localId: " + lea.getLocalId());
				System.out.println("ncesId: " + lea.getNcesId());
				System.out.println("stateProvinceId: " + lea.getStateProvinceId());
	
				System.out.println("##### BEGIN ADDRESS #####");
				System.out.println("addressType: " + lea.getAddress().getAddressType());
				System.out.println("city: " + lea.getAddress().getCity());
				System.out.println("line1: " + lea.getAddress().getLine1());
				System.out.println("line2: " + lea.getAddress().getLine2());
				System.out.println("countryCode: " + lea.getAddress().getCountryCode());
				System.out.println("postalCode: " + lea.getAddress().getPostalCode());
				System.out.println("stateProvince: " + lea.getAddress().getStateProvince());
				System.out.println("##### END ADDRESS #####");
	
				System.out.println("##### BEGIN PHONENUMBER #####");
				System.out.println("number: " + lea.getPhoneNumber().getNumber());
				System.out.println("phoneNumberType: " + lea.getPhoneNumber().getPhoneNumberType());
				System.out.println("primaryIndicator: " + lea.getPhoneNumber().isPrimaryIndicator());
				System.out.println("##### END PHONENUMBER #####");
	
				System.out.println("##### BEGIN OTHERPHONENUMBER #####");
	
				for (XTelephoneType p : lea.getOtherPhoneNumbers().getPhoneNumber())
				{
					System.out.println("number: " + p.getNumber());
					System.out.println("phoneNumberType: " + p.getPhoneNumberType());
					System.out.println("primaryIndicator: " + p.isPrimaryIndicator());
				}
				System.out.println("##### END OTHERPHONENUMBER #####");
	
				System.out.println("========================================");
			}
		}
		
	}
}