/*////////////////////////////////////////////////
 * Created By: Shamus Cardon
 * Date Created: 7/7/2016
 * Version: 0.3.0
 * Updated: 7/13/2016
*/////////////////////////////////////////////////

import riconeapi.models.authentication.Endpoint;
import riconeapi.models.authentication.UserInfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import riconeapi.common.Authenticator;
import riconeapi.common.XPress;
import riconeapi.common.Util;


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

public class Main {	
	static final String authUrl= Authorizer.getURL();        
    static final String clientId= Authorizer.getID();
    static final String clientSecret = Authorizer.getSecret();
    static final String providerId = Authorizer.getProviderID();
    static final int navigationPageSize = Authorizer.getPageSize();
    
    static String[] keywords = {"-file","-source"};
    
    static String[] refid;   // Identifies the location the data is being pulled from
    static String ref_type; // Should be either Lea or School
    static int[] grade_nums; // optional param

    // Simple method designed solely to take input from a text file and return it seperated by line.
	public static List<ArrayList<ArrayList<String>>> ReadFile(String in_file, boolean parsetoo) {
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
				
		
		if (parsetoo) { // should the data just be read in or should it be directed to the parser function
			return ParseFile(textData);
		} else {
			ArrayList<ArrayList<String>> t2 = new ArrayList<ArrayList<String>>();
			List<ArrayList<ArrayList<String>>> t3 = new ArrayList<ArrayList<ArrayList<String>>>();
			t2.add(textData);
			t3.add(t2);
			return t3;
		}
	}
	
	
	// Method designed to take the data read in from a file and split it into meaningful segments
	public static List<ArrayList<ArrayList<String>>> ParseFile(List<String> file_data) {
		List<ArrayList<ArrayList<String>>> full_list = new ArrayList<ArrayList<ArrayList<String>>>();
		ArrayList<ArrayList<String>> parsed_data = new ArrayList<ArrayList<String>>();
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
			boolean noadd = false;
			if (iskeyword(l[0])) {
				if (l[0].equals("-file")) {
					full_list.add(parsed_data);
					parsed_data = new ArrayList<ArrayList<String>>();
					t_list2 = t_list;
				}
				//If the source tag is specified, parse that input
				if (l[0].equals("-source")) {
					noadd = true;
					if (l.length == 3 || l.length == 5) {
						if (l[1].equals("Lea")) {
							ref_type = "Lea";
						} else if (l[1].equals("School")) {
							ref_type = "School";
						} else {
							System.err.println("Failed to parse -source input. Failed on " + l[1]);
						}
						refid = l[2].split(",");
//						System.out.println("refid:");
//						for (String s : refid) {
//							System.out.println(s);
//						}
						if (l.length == 5) {
							if (l[3].equals("Grade")) {
								String[] t = l[4].split(",");
								grade_nums = new int[t.length];
								for (int s=0; s < t.length; ++s) {
									grade_nums[s] = Integer.parseInt(t[s]);
								}
							} else {
								System.err.println("Failed to parse -source input. Failed on " + l[3]);
							}
						}
					} else {
						System.out.println("\nSource: ");
						for (String s : l) {
							System.out.print(s + " " );
						}
						System.out.println("\n");
					}
				}
				
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
			if (!noadd) {
				parsed_data.add(t_list2);
			}
		}
		full_list.add(parsed_data);
		return full_list;
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
    	Map<String,Data> file_name_to_data = new TreeMap<String,Data>();
    	System.out.println("Start Program");
    	System.out.println("Start auth");
		Authenticator auth = new Authenticator(authUrl, clientId, clientSecret);
		
		// TODO remove this line before going to production
		Util.disableSslVerification();
		
	    System.out.println("End auth");
    	String c_file = "";
		
		if (args.length > 0) {
			for (int i=0; i < args.length; ++i) {
				if (args[i].equals("-file") && i+2 >= args.length) {
					c_file = args[i+1];
				}
			}
		}
		
		//System.out.println(c_file);
		List<ArrayList<ArrayList<String>>> dat = new ArrayList<ArrayList<ArrayList<String>>>();

		dat = ReadFile(c_file,true);
		
		
		System.out.println("Start function calling");
		System.out.println(dat);
		for (ArrayList<ArrayList<String>> category : dat) {
			if (category.size()>0) {
				String xpress_type = "";
				System.out.println(category);
				if (category.get(0).get(0).equals("-file")) {
					System.out.println("This is a file name: "+category.get(0).get(1)+"");	
					xpress_type = category.get(0).get(2);					
				} else {
					System.out.println("Still need to do something with " + category.get(0).get(0));
				}
				List<TreeMap<String, String>> list = new ArrayList<TreeMap<String,String>>();
				for(Endpoint e : auth.getEndpoints()) {
						XPress xPress = new XPress(auth.getToken(), e.getHref());
						list.addAll(DataRead(xpress_type, xPress,category.subList(1,category.size()))); // should be a list += need to look up exact syntax
						System.out.println(list);
						for (Map<String,String> i : list) {
							System.out.println(i);
						}
						//XLeas_GetXLeas(xPress);
				}
				Data d = new Data(list);
				file_name_to_data.put(category.get(0).get(1),d);
				
//				for (ArrayList<String> i : category) {
//					if (iskeyword(i.get(0)) && !i.get(0).equals("-file")) {
//						System.out.println("Keyword detected: " + i.get(0));
//					} else {
//	//					for(Endpoint e : auth.getEndpoints()) {
//	//						XPress xPress = new XPress(auth.getToken(), e.getHref());
//	//						FunctCaller(i.get(1), xPress,xpress_type);
//	//	TODO					DataRead(i.get(1),xpress_type, xPress);
//	//						XLeas_GetXLeas(xPress);
//	//				    } 
//						//System.out.println("Full call looks like: X" + xpress_type + "Type var : xPress.getX" + xpress_type + "s().getData() (" + xpress_type + ")var.get"+i.get(1)+"()");
//		
//					}
//				}
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
		
		System.out.println("End function calling");		
		
		
		for (Map.Entry<String,Data> entry : file_name_to_data.entrySet()) {
			System.out.println("\n" + entry.getKey());
		}
		
				
		
			
		
		
		
		System.out.println("End Program");
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
	
	public static List<TreeMap<String,String>> DataRead(String data_type, XPress xPress, List<ArrayList<String>> commands) {
		List<TreeMap<String,String>> list = new ArrayList<TreeMap<String,String>>();
		if (ref_type.equals("Lea")) {
			for (String rid : refid) {
				switch (data_type) {
					case "Lea": {
						TreeMap<String,String> map = new TreeMap<String,String>();
// TODO remove when API server is back online						XLeaType lea = xPress.getXLea(rid).getData();
						for (ArrayList<String> com : commands) {
							Method m;
//							try {
//								m = XLeaType.class.getDeclaredMethod(com.get(1));
//								System.out.println(m.invoke(lea));
//								map.put(com.get(0),m.invoke(lea)); // this line should associate the data pulled from the RICONE server with the user's function name
								map.put(com.get(0),com.get(1));
//								System.out.println("executing ." + com.get(1) + "() on Lea with refid: " + rid); // TODO make this input to data map
//							} catch (NoSuchMethodException e) {
//								System.err.println("Couldnt find ." + com + "() as a callable method. Check the spelling?");
//								e.printStackTrace();
//							} catch (SecurityException e) {
//								e.printStackTrace();
//							} catch (IllegalAccessException e) {
//								e.printStackTrace();
//							} catch (IllegalArgumentException e) {
//								e.printStackTrace();
//							} catch (InvocationTargetException e) {
//								e.printStackTrace();
//							}
						}
						list.add(map);
						break;
					}
					case "School": {
						TreeMap<String,String> map = new TreeMap<String,String>();
//						// TODO remove when API server is back online						XLeaType lea = xPress.getXLea(rid).getData();
						for (ArrayList<String> com : commands) {
							Method m;
//							try {
//								m = XLeaType.class.getDeclaredMethod(com.get(1));
//								System.out.println(m.invoke(lea));
//								map.put(com.get(0),m.invoke(lea)); // this line should associate the data pulled from the RICONE server with the user's function name
								map.put(com.get(0),com.get(1));
//								System.out.println("executing ." + com.get(1) + "() on Lea with refid: " + rid); // TODO make this input to data map
//							} catch (NoSuchMethodException e) {
//								System.err.println("Couldnt find ." + com + "() as a callable method. Check the spelling?");
//								e.printStackTrace();
//							} catch (SecurityException e) {
//								e.printStackTrace();
//							} catch (IllegalAccessException e) {
//								e.printStackTrace();
//							} catch (IllegalArgumentException e) {
//								e.printStackTrace();
//							} catch (InvocationTargetException e) {
//								e.printStackTrace();
//							}
						}
						list.add(map);
						break;
					}
					default: {
						System.err.println("hitting default case. thats a problem...");
						break;
					}
				}
			}
		}
		if (ref_type.equals("School")) {
			
		}
		return list;
	}
	
	
	//Method designed to take the strings of function names from the previous methods and use them to call the RICONE API for the needed data
	public static void FunctCaller(String funct_name, XPress xPress, String type) {
		Map<String,String> ma = new TreeMap<>();
		System.out.println(funct_name + " of type " + type);
		type = "nothing"; //TODO Delete this line once we can successfully connect to the API server
		switch (type) {
			case "Lea": {
				if(xPress.getXLeas().getData() != null)
				{
					for (XLeaType var : xPress.getXLeas().getData())
					{	
						Method m;
						try {
							m = XLeaType.class.getDeclaredMethod(funct_name);
							System.out.println(m.invoke(var));
						} catch (NoSuchMethodException e) {
							System.err.println("Couldnt find ." + funct_name + "() as a callable method. Check the spelling?");
							e.printStackTrace();
						} catch (SecurityException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
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
							System.err.println("Couldnt find .get" + funct_name + "() as a callable method. Check the spelling?");
							e.printStackTrace();
						} catch (SecurityException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
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
							System.err.println("Couldnt find .get" + funct_name + "() as a callable method. Check the spelling?");
							e.printStackTrace();
						} catch (SecurityException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
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
							System.err.println("Couldnt find .get" + funct_name + "() as a callable method. Check the spelling?");
							e.printStackTrace();
						} catch (SecurityException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
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
							System.err.println("Couldnt find .get" + funct_name + "() as a callable method. Check the spelling?");
							e.printStackTrace();
						} catch (SecurityException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
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
							System.err.println("Couldnt find .get" + funct_name + "() as a callable method. Check the spelling?");
							e.printStackTrace();
						} catch (SecurityException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
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
							System.err.println("Couldnt find .get" + funct_name + "() as a callable method. Check the spelling?");
							e.printStackTrace();
						} catch (SecurityException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
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
							System.err.println("Couldnt find .get" + funct_name + "() as a callable method. Check the spelling?");
							e.printStackTrace();
						} catch (SecurityException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
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