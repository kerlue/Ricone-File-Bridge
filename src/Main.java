/*////////////////////////////////////////////////
 * Created By: Shamus Cardon
 * Date Created: 7/7/2016
 * Version: 0.2.0
 * Updated: 7/11/2016
*/////////////////////////////////////////////////

import riconeapi.models.authentication.Endpoint;
import riconeapi.models.authentication.UserInfo;

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

//import Authorizer;

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
	
	
//	String getStateProvinceId() {
//		return district_code;
//	}
	
}


public class Main {	
	static final String authUrl= Authorizer.getURL();        
    static final String clientId= Authorizer.getID();
    static final String clientSecret = Authorizer.getSecret();
    static final String providerId = Authorizer.getProviderID();
    static final int navigationPageSize = Authorizer.getPageSize();
    
    static String[] keywords = {"-file","-location"};

    // Simple method designed solely to take input from a text file and return it seperated by line.
	public static List<ArrayList<String>> ReadFile(String in_file, boolean parsetoo) {
		ArrayList<String> textData = new ArrayList<String>();
		
		try {
			FileReader fr = new FileReader(in_file);
			BufferedReader textReader = new BufferedReader(fr);
			String t;
			while ((t = textReader.readLine()) != null) {
				if (t.length() > 0) {
					//System.out.println(t + t.length());
					textData.add(t);
				}
			}
			
			textReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		System.out.println(textData);
		
		if (parsetoo) { // should the data just be read in or should it be directed to the parser function
			return ParseFile(textData);
		} else {
			List<ArrayList<String>> t2 = new ArrayList<ArrayList<String>>();
			t2.add(textData);
			return t2;
		}
	}
	
	
	// Method designed to take the data read in from a file and split it into meaningful segments
	public static List<ArrayList<String>> ParseFile(List<String> file_data) {
		List<ArrayList<String>> parsed_data = new ArrayList<ArrayList<String>>();
		for (int i=0; i < file_data.size(); ++i) { // loop through all lines in the input
			//System.out.println(i+ " " + file_data.toArray()[i]);
			int temp = 0;
			ArrayList<String> t_list = new ArrayList<String>(); // list of all file lines split into useful parts
			String[] f = file_data.toArray(new String[0]);
			for (int j=0; j < f[i].length(); ++j) { // loop through all letters on the line and split at spaces
				if (f[i].charAt(j) == ' ') {
					t_list.add(f[i].substring(temp,j));
					temp = j+1;
				}
			}
			ArrayList<String> t_list2 = new ArrayList<String>();
			t_list.add(f[i].substring(temp));
			String[] l = t_list.toArray(new String[0]);
			if (iskeyword(l[0])) {
				t_list2 = t_list;
			} else if (l.length > 2) {
				String t_string = l[1];
				for (int j=2; j < l.length; ++j) {
					t_string += "().get" + l[j];
				}
				t_list2.add(l[0]);
				t_list2.add(t_string);
			} else {
				t_list2 = t_list;
			}
			parsed_data.add(t_list2);
		}
		return parsed_data;
	}    
	
	//used to aid input file parsing. Checks if a word is in list of keywords
	public static boolean iskeyword(String word) {
		if (word.charAt(0) == '-') {
			for (String keyword : keywords) {
				if (keyword.equals(word)) {
					return true;
				}
			}
			System.out.println("Could not find keyword " + word + ". This could cause problems");
			return true;
		}
		return false;
	}
    
    public static void main(String[] args) {
    	System.out.println("Start Program");
    	System.out.println("Start auth");
//		Authenticator auth = new Authenticator(authUrl, clientId, clientSecret);
	    System.out.println("end auth");
    	String c_file = "";
		
		if (args.length > 0) {
			for (int i=0; i < args.length; ++i) {
				if (args[i].equals("-file") && i+2 >= args.length) {
					c_file = args[i+1];
				}
			}
		}
		
		System.out.println(c_file);
		List<ArrayList<String>> dat = new ArrayList<ArrayList<String>>();

		dat = ReadFile(c_file,true);
		
		
		System.out.println("Start");
		String xpress_type = "";
		System.out.println(dat);
		for (ArrayList<String> i : dat) {
			String[] l = i.toArray(new String[0]);
			if (iskeyword(l[0])) {
				if (l[0].equals("-file")) {
					System.out.println("This is a file name: "+l[1]+"");	
					xpress_type = l[2];					
				} else {
					System.out.println("Still need to do something with " + l[0]);
				}
			} else {
//				for(Endpoint e : auth.getEndpoints()) {
//					XPress xPress = new XPress(auth.getToken(), e.getHref());
//					FunctCaller(i.toArray(new String[0])[1], xPress,xpress_type);
//					XLeas_GetXLeas(xPress);
//			    } 
				System.out.println("Full call looks like: X" + xpress_type + "Type var : xPress.getX" + xpress_type + "s().getData() (" + xpress_type + ")var.get"+l[1]+"()");
			}
		}
		
//		System.out.println("userinfo: " + auth.getUserInfo());
//		
//		UserInfo user = auth.getUserInfo();
//		System.out.println("endpoint: " + user.getEndpoint());
		
//		for(Endpoint e : auth.getEndpoints()) {
//			XPress xPress = new XPress(auth.getToken(), e.getHref());
//			//xPress.getXLeas();
//			//XLeas_GetXLeas(xPress);
//	    } 
		
		System.out.println("Finish");		
		
				
		
			
		
		
		
		System.out.println("End of the Program");
    }


	public static void XLeas_GetXLeas(XPress xPress)
	{
		System.out.println("here1");
		if(xPress.getXLeas().getData() != null)
		{
			System.out.println("here2");
			for (XLeaType lea : xPress.getXLeas().getData())
			{	
				System.out.println("here3");
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
	
	//Method designed to take the strings of function names from the previous methods and use them to call the RICONE API for the needed data
	public static <T> void FunctCaller(String funct_name, XPress xPress, String type) {
		System.out.println(funct_name + " of type " + type);
		type = "nothing"; // Delete this line once we can successfully connect to the API server
		switch (type) {
			case "Lea": {
				if(xPress.getXLeas().getData() != null)
				{
					for (XLeaType var : xPress.getXLeas().getData())
					{	
						Method m;
						try {
							m = XLeaType.class.getDeclaredMethod("get"+funct_name);
							System.out.println(m.invoke(var));
						} catch (NoSuchMethodException e) {
							// TODO Auto-generated catch block
							System.err.println("Couldnt find .get" + funct_name + "() as a callable method. Check the spelling?");
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
					}
				}
			}
			case "School": {
				if(xPress.getXSchools().getData() != null)
				{
					for (XSchoolType var : xPress.getXSchools().getData())
					{	
						Method m;
						try {
							m = XSchoolType.class.getDeclaredMethod("get"+funct_name);
							System.out.println(m.invoke(var));
						} catch (NoSuchMethodException e) {
							// TODO Auto-generated catch block
							System.err.println("Couldnt find .get" + funct_name + "() as a callable method. Check the spelling?");
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
					}
				}
			}
			case "Calendar": {
				if(xPress.getXCalendars().getData() != null)
				{
					for (XCalendarType var : xPress.getXCalendars().getData())
					{	
						Method m;
						try {
							m = XCalendarType.class.getDeclaredMethod("get"+funct_name);
							System.out.println(m.invoke(var));
						} catch (NoSuchMethodException e) {
							// TODO Auto-generated catch block
							System.err.println("Couldnt find .get" + funct_name + "() as a callable method. Check the spelling?");
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
					}
				}
			}
			case "Course": {
				if(xPress.getXCourses().getData() != null)
				{
					for (XCourseType var : xPress.getXCourses().getData())
					{	
						Method m;
						try {
							m = XCourseType.class.getDeclaredMethod("get"+funct_name);
							System.out.println(m.invoke(var));
						} catch (NoSuchMethodException e) {
							// TODO Auto-generated catch block
							System.err.println("Couldnt find .get" + funct_name + "() as a callable method. Check the spelling?");
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
					}
				}
			}
			case "Roster": {
				if(xPress.getXRosters().getData() != null)
				{
					for (XRosterType var : xPress.getXRosters().getData())
					{	
						Method m;
						try {
							m = XRosterType.class.getDeclaredMethod("get"+funct_name);
							System.out.println(m.invoke(var));
						} catch (NoSuchMethodException e) {
							// TODO Auto-generated catch block
							System.err.println("Couldnt find .get" + funct_name + "() as a callable method. Check the spelling?");
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
					}
				}
			}
			case "Staff": {
				if(xPress.getXStaffs().getData() != null)
				{
					for (XStaffType var : xPress.getXStaffs().getData())
					{	
						Method m;
						try {
							m = XStaffType.class.getDeclaredMethod("get"+funct_name);
							System.out.println(m.invoke(var));
						} catch (NoSuchMethodException e) {
							// TODO Auto-generated catch block
							System.err.println("Couldnt find .get" + funct_name + "() as a callable method. Check the spelling?");
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
					}
				}
			}
			case "Student": {
				if(xPress.getXStudents().getData() != null)
				{
					for (XStudentType var : xPress.getXStudents().getData())
					{	
						Method m;
						try {
							m = XStudentType.class.getDeclaredMethod("get"+funct_name);
							System.out.println(m.invoke(var));
						} catch (NoSuchMethodException e) {
							// TODO Auto-generated catch block
							System.err.println("Couldnt find .get" + funct_name + "() as a callable method. Check the spelling?");
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
					}
				}
			}
			case "Contact": {
				if(xPress.getXContacts().getData() != null)
				{
					for (XContactType var : xPress.getXContacts().getData())
					{	
						Method m;
						try {
							m = XContactType.class.getDeclaredMethod("get"+funct_name);
							System.out.println(m.invoke(var));
						} catch (NoSuchMethodException e) {
							// TODO Auto-generated catch block
							System.err.println("Couldnt find .get" + funct_name + "() as a callable method. Check the spelling?");
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
					}
				}
			}
			default: {
				//System.out.println("default case. Something went wrong");
				System.out.println("");
			}
		}

	}
}