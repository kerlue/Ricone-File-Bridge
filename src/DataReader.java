/*///////////////////////////////////////////////
 * Created By: Shamus Cardon
 * Date Created: 7/14/2016
 * Version: 1.4.1
 * Updated: 8/10/2016
*////////////////////////////////////////////////

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.xml.datatype.XMLGregorianCalendar;

import riconeapi.common.Authenticator;
import riconeapi.common.XPress;
import riconeapi.models.authentication.Endpoint;
import riconeapi.models.xpress.*;

public class DataReader {
   // private static String[] refid;   // Identifies the location the data is being pulled from
    private static String ref_type; // Should be either Lea or School
   // private static String[][] grade_nums; // optional param
    private static TreeMap<String,String[]> grade_assoc;
    private static long st_time;
    private static boolean print_enabled = true;
    
    
    public static void PopulateDataReader(String r_type, String[] rID, String[][] grades, Authenticator auth) {
    	st_time = System.nanoTime();
		
    	ref_type = r_type;

    	grade_assoc = new TreeMap<String,String[]>();
    	
		XPress xPress = new XPress(auth.getToken(), "https://10.6.11.20/api/requests/");
		
    	if (grades.length>0) {
    		for (int i=0; i < grades.length; ++i) {
    			boolean found = false;
    			XSchoolType school;
    			if ((school = xPress.getXSchool(rID[i]).getData()) != null) {
    				for (String grade : grades[i]) {
    	    			if (grade.trim().length()>0) {
    	    				found = true;
    	    				if (GradeChecker.GradeCheck("School",school,xPress,grades[i])) {
    		    	    		grade_assoc.put(rID[i], grades[i]);
    	    				}
    	    	    		break;
    	    			}
        			}
    			}
    			if (!found) {
//    				System.out.println(i);
//    				System.out.println(rID[i]);
//    				System.out.println("after");
//    				String[] t = new String[] {"2","3"};
//    				for (String s : t) {
//    					System.out.println("s " + s);
//    				}
    				grade_assoc.put(rID[i],null);
    			}
    		}
    	}
    }
    
    public static void PopulateDataReader(String r_type, String[] rID, String[][] grades, Authenticator auth, boolean print_en) {
    	PopulateDataReader(r_type,rID,grades,auth);
    	print_enabled = print_en;
    }
    
    public static ArrayList<Data> GenerateFiles(Configuration config, Authenticator auth) {
    	

		
		// Class to pull data from ric one api
		
		///TODO Search by grade functionality not working - Courses do not have grade information populated in API. blocked from enabling searchbygrade until grades are populated
		
    	PopulateDataReader(config.getFilterBy(),config.getFilterRefId(),config.getFilterGrades(),auth);
		
    	ArrayList<Data> file_list = new ArrayList<Data>();
    	
    	int maxi = config.getTextTitle().size();
		//int maxi = 2;
		
		for (int i=0; i < maxi; ++i) {
			System.out.println("File " + (i+1) + " Started");
			ArrayList<DataType> temp = new ArrayList<DataType>();
			
			temp = GetDataTypes(config.getColumnNames().get(i), config.getRequiredData().get(i));
			
			System.out.println("---------------------------------Finished Reading Data Requirements---------------------------------");
			
		    
			Data data = ReadIn(auth,temp,config.getTextTitle().get(i),i,maxi,st_time);
			file_list.add(data);
			
			System.out.println((i+1) + " files completed in " + ((float)(System.nanoTime()-st_time))/1000000000 + "s");
		}

    	
    	
    	return file_list;
    }
   
    private static ArrayList<DataType> GetDataTypes (String col_names, String req_data) {
    	ArrayList<DataType> full_list = new ArrayList<DataType>();
    	
    	String[] t_list1 = col_names.split(",");    	
    	String[] t_list2 = req_data.split(",");
    	    	
    	if (t_list1.length != t_list2.length) {
    		System.err.println("Lists are different sizes. That is a problem...");
    		System.err.println("List1 size: " + t_list1.length + " List2 size: " + t_list2.length);
    	}

		String data_category = t_list2[0].trim().split(" ")[0].substring(1,t_list2[0].trim().split(" ")[0].length()); // pull the data type info. i.e. Lea, School, etc.
		
		
		String temp = "";
		String[] st = t_list2[0].trim().split(" ");
		for (int j=1; j < st.length; ++j) {
			if (st[j].equals("-g") && j < st.length-1) {
				++j;
				temp += st[j] + " ";
			} else if (st[j].contains("-and")) {
				temp += st[j] + " ";				
			} else {
				temp += "get" + st[j] + " ";					
			}
		}
		temp = temp.trim();
		
		DataType dt = new DataType(data_category,t_list1[0].trim(),temp);
		full_list.add(dt);
    	
    	for (int i=1; i < t_list1.length; ++i) {
    		String s = t_list2[i].trim();
    		
    		if (s.equals("null")) {
    			System.err.println("cannot recognize " + t_list1[i].trim());
        		//DataType d = new DataType("",t_list1[i].trim(),"");
        		//full_list.add(d);
    		} else {
    			temp = "";
    			st = s.split(" ");
    			for (int j=0; j < st.length; ++j) {
    				if (st[j].equals("-g") && j < st.length-1) {
    					++j;
    					temp += st[j] + " ";
    				} else if (st[j].contains("-and")) {
    					temp += st[j] + " ";				
    				} else {
    					temp += "get" + st[j] + " ";					
    				}
    			}
    			temp = temp.trim();
    			DataType d = new DataType(data_category,t_list1[i].trim(), temp);
    			full_list.add(d);
    		}
    	}
    	
    	return full_list;
    }

	
    private static Data ReadIn(Authenticator auth, List<DataType> data_type_list, String file_name,int file_num, int num_files,long start_time) { // xPressType should be "Lea","School","Student","Staff",etc.
		List<ArrayList<DataType>> list = new ArrayList<ArrayList<DataType>>();
				
		int num_endpoints = auth.getEndpoints().size();
		int endpoint_num = 0;
		for(@SuppressWarnings("unused") Endpoint e : auth.getEndpoints()) {
			//XPress xPress = new XPress(auth.getToken(), e.getHref());
			XPress xPress = new XPress(auth.getToken(), "https://10.6.11.20/api/requests/");
			list.addAll(DataRead(xPress,data_type_list,endpoint_num,num_endpoints,file_num,num_files,start_time));
			//System.out.println(list);
			//XLeas_GetXLeas(xPress);
		}
		Data d = new Data(list,file_name);
		return d;
    }

    private static <T> Object SimpleCase(Class<T> classofmethod, String m_name, T var) {
    	Method m;
    	try {
			m = classofmethod.getMethod(m_name);							  	    							
			return m.invoke(var);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }
    
    private static <T,E> Object SimpleCase(Class<T> classofmethod, Class<E> classofvar, String high_name, String low_name, E var) {
    	Method m;
    	try {
			m = classofvar.getMethod(high_name);
			Object t_obj = (m.invoke(var));
			m = classofmethod.getMethod(low_name);							  	    							
			return m.invoke(t_obj);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }
    
    private static <T> Object SpecialCase(String[] t,int start_index, Class<T> clazz, T var, String data_type) {
    	return SpecialCaseMain(t,start_index,clazz,var,null,null,data_type);
    }
    
    private static <T,E> Object SpecialCase(String[] t,int start_index, Class<T> clazz, T var, Class<E> clazz2, E var2, String data_type) {
    	return SpecialCaseMain(t,start_index,clazz,var,clazz2,var2,data_type);
    }
    
    private static void null_case(String[] t) {
//    	for (String s : t) {
//    		System.out.print(s + " ");
//    	}
//    	System.out.println("\n\nnull_case\n\n");
//    	new Exception().printStackTrace();
//    	System.exit(1);
    }
    
    
	@SuppressWarnings("unchecked")
	private static <T,E> Object SpecialCaseMain(String[] t,int i, Class<T> clazz, T var, Class<E> clazz2, E var2, String data_type) {
		//for (int i=0; i < t.length; ++i) {
		if (t.length < i+1) {
			System.err.println("t is not large enough for that start index. t.length=" + t.length + " start_index=" + i);
			new Exception().printStackTrace();
			return null;
		}
			Method m;
			try {
				switch (data_type) { 
					case "Lea": {
						if (t[i].equals("getAddress")) {
							Object temp = SimpleCase(XOrganizationAddressType.class,clazz,t[i],t[i+1],var);
							 
							return temp;
						} else if (t[i].equals("getPhoneNumber")) {
							Object temp = SimpleCase(XTelephoneType.class,clazz,t[i],t[i+1],var);
							 
							return temp;
						} else if (t[i].equals("getOtherPhoneNumbers")) {
							String t_str = "";
							for (XTelephoneType v :  ((XLeaType) var).getOtherPhoneNumbers().getPhoneNumber()) {
								t_str += (String)SimpleCase(XTelephoneType.class,t[i+2],v) + ",";
							}
							 
							return t_str.substring(0, t_str.length()-1);
						} else {
							null_case(t);
						}
						break;
					}
					case "School": {
						if (t[i].equals("getOtherIds")) {
							String t_str = "";
							for (XOtherOrganizationIdType v :  ((XSchoolType) var).getOtherIds().getOtherId()) {
								t_str += (String)SimpleCase(XOtherOrganizationIdType.class,t[i+2],v) + ",";
							}
							return t_str.substring(0, t_str.length()-1);
						} else if (t[i].equals("getGradeLevels")) {
//							String temp_str = "\"";
							String temp_str = "";
							m = clazz.getMethod(t[i]);
							Object t_obj = (m.invoke(var));
							m = XGradeLevelListType.class.getMethod(t[i+1]);
							for (String t_s : (List<String>)m.invoke(t_obj)) {
								temp_str += t_s + ", ";
							}
							
//							if (temp_str.length() > 1) {
//								temp_str = temp_str.substring(0,(temp_str.length()-2)) + "\"";
//							} else {
//								temp_str = "";
//							}
//							System.out.println("temp_str " + temp_str);
							if (temp_str.length() > 1) {
								return temp_str.trim().substring(0,temp_str.trim().length()-1);
							} else {
								return "";
							}
						} else if (t[i].equals("getAddress")) {
							Object temp = SimpleCase(XOrganizationAddressType.class,clazz,t[i],t[i+1],var);
							 
							return temp;
						} else if (t[i].equals("getPhoneNumber")) {
							Object temp = SimpleCase(XTelephoneType.class,clazz,t[i],t[i+1],var);
							 
							return temp;
						} else if (t[i].equals("getOtherPhoneNumbers")) {
							String t_str = "";
							for (XTelephoneType v :  ((XSchoolType) var).getOtherPhoneNumbers().getPhoneNumber()) {
								t_str += (String)SimpleCase(XTelephoneType.class,t[i+2],v) + ",";
							}
							 
							return t_str.substring(0, t_str.length()-1);
						} else {
							null_case(t);
						}
						break;
					}
					case "Calendar": {
						if (t[i].equals("getSessions")) {
							String t_str = "";
							for (XSessionType v :  ((XCalendarType) var).getSessions().getSessionList()) {
								t_str += (String)SimpleCase(XSessionType.class,t[i+2],v) + ",";
							}
							return t_str.substring(0, t_str.length()-1);
						} else {
							null_case(t);
						}
						break;
					}
					case "Course": {
						if (t[i].equals("getOtherIds")) {
							String t_str = "";
							for (XOtherCourseIdType v :  ((XCourseType) var).getOtherIds().getOtherId()) {
								t_str += (String)SimpleCase(XOtherCourseIdType.class,t[i+2],v) + ",";
							}			 
							return t_str.substring(0, t_str.length()-1);
						} else if (t[i].equals("getApplicableEducationLevels")) {
							String t_str = "";
							for (String v :  ((XCourseType) var).getApplicableEducationLevels().getApplicableEducationLevel()) {
								t_str += v + ",";
							}
							return t_str.substring(0, t_str.length()-1);
						} else {
							null_case(t);
						}
						break;
					}
					case "Roster": {
						if (t[i].equals("getMeetingTimes")) {
							String t_str = "";
							if(t[i+2].equals("getClassMeetingDays")) {
								for (XMeetingTimeType v :  ((XRosterType) var).getMeetingTimes().getMeetingTime()) {
									XDayListType v2 = v.getClassMeetingDays();
									t_str += (String)SimpleCase(XDayListType.class,t[i+3],v2) + ",";
								}
							} else {
								for (XMeetingTimeType v :  ((XRosterType) var).getMeetingTimes().getMeetingTime()) {
									t_str += (String)SimpleCase(XMeetingTimeType.class,t[i+2],v) + ",";
								}
							}
							return t_str.substring(0, t_str.length()-1);
						} else if (t[i].equals("-staff")) {
							String str = (String)XPersonReferenceType.class.getMethod("getRefId").invoke(XStaffReferenceType.class.getMethod("getStaffPersonReference").invoke(clazz.getMethod("getPrimaryStaff").invoke(var)));
							String str2 = (String)clazz2.getMethod("getRefId").invoke(var2);
							if (str2.equals(str)) {
								if (t[i+1].equals("getStaffPersonReference")) {
									XPersonReferenceType t_person = ((XRosterType) var).getPrimaryStaff().getStaffPersonReference();
									m = XPersonReferenceType.class.getMethod(t[i+2]);
									return m.invoke(t_person);
								} else {
									XStaffReferenceType t_person = (XStaffReferenceType)clazz.getMethod("getPrimaryStaff").invoke(var);
									m = XStaffReferenceType.class.getMethod(t[i+1]);
									return m.invoke(t_person);
								}
							} else {
								Boolean found = false;
								for (XStaffReferenceType st : ((XRosterType) var).getOtherStaffs().getOtherStaff()) {
									if (str2.equals(st.getStaffPersonReference().getRefId())) {
										if (t[i+1].equals("getStaffPersonReference")) {
											m = XPersonReferenceType.class.getMethod(t[i+2]);
			    							return (String)m.invoke(st.getStaffPersonReference());
										} else {
											m = XStaffReferenceType.class.getMethod(t[i+1]);
			    							return (String)m.invoke(st);
										}
									}
								}
								if (!found) {
									return null;
								}
							}
						} else if (t[i].equals("getStudents")) {
							if (var2 != null) {
								String str2 = (String)clazz2.getMethod("getRefId").invoke(var2);
								boolean found = false;
								for (XPersonReferenceType st : ((XRosterType) var).getStudents().getStudentReference()) {
									if (str2.equals(st.getRefId())) {
										m = XPersonReferenceType.class.getMethod(t[i+2]);
		    							return m.invoke(st);
									}
								}
								if (!found) {
									return null;
								}
							} else {
								String t_str = "";
								for (XPersonReferenceType v : ((XRosterType) var).getStudents().getStudentReference()) {
									t_str += (String)SimpleCase(XPersonReferenceType.class,t[i+2],v) + ",";
								}
								return t_str.substring(0, t_str.length()-1);
							}
						} else {
							null_case(t);
						}
						break;
					}
					case "Staff": {
						if (t[i].equals("getName")) {
							Object temp = SimpleCase(XPersonNameType.class,clazz,t[i],t[i+1],var);
							 
							return temp;
						} else if (t[i].equals("getOtherIds")) {
							String t_str = "";
							for (XOtherPersonIdType v :  ((XStaffType) var).getOtherIds().getOtherId()) {
								t_str += (String)SimpleCase(XOtherPersonIdType.class,t[i+2],v) + ",";
							}
							 
							return t_str.substring(0, t_str.length()-1);
						} else if (t[i].equals("getEmail")) {
							Object temp = SimpleCase(XEmailType.class,clazz,t[i],t[i+1],var);
							 
							return temp;
						} else if (t[i].equals("getPrimaryAssignment")) {
							Object temp = SimpleCase(XStaffPersonAssignmentType.class,clazz,t[i],t[i+1],var);
							 
							return temp;
						} else if (t[i].equals("getOtherAssignments")) {
							String t_str = "";
							for (XStaffPersonAssignmentType v :  ((XStaffType) var).getOtherAssignments().getStaffPersonAssignment()) {
								t_str += (String)SimpleCase(XStaffPersonAssignmentType.class,t[i+2],v) + ",";
							}
							 
							return t_str.substring(0,t_str.length()-1);
						} else {
							null_case(t);
						}
						break;
					}
					case "Student": {
						if (t[i].equals("getName")) {
							Object temp = SimpleCase(XPersonNameType.class,clazz,t[i],t[i+1],var);
							 
							return temp;
						} else if (t[i].equals("getOtherNames")) {
							String t_str = "";
							for (XPersonNameType v :  ((XStudentType) var).getOtherNames().getName()) {
								t_str += (String)SimpleCase(XPersonNameType.class,t[i+2],v) + ",";
							}
							 
							return t_str.substring(0, t_str.length()-1);
						} else if (t[i].equals("getOtherIds")) {
							String t_str = "";
							for (XOtherPersonIdType v :  ((XStudentType) var).getOtherIds().getOtherId()) {
								t_str += (String)SimpleCase(XOtherPersonIdType.class,t[i+2],v) + ",";
							}
							 
							return t_str.substring(0, t_str.length()-1);
						} else if (t[i].equals("getAddress")) {
							Object temp = SimpleCase(XPersonAddressType.class,clazz,t[i],t[i+1],var);
							 
							return temp;
						} else if (t[i].equals("getPhoneNumber")) {
							Object temp = SimpleCase(XTelephoneType.class,clazz,t[i],t[i+1],var);
							 
							return temp;
						} else if (t[i].equals("getOtherPhoneNumbers")) {
							String t_str = "";
							for (XTelephoneType v :  ((XStudentType) var).getOtherPhoneNumbers().getPhoneNumber()) {
								t_str += (String)SimpleCase(XTelephoneType.class,t[i+2],v) + ",";
							}
							 
							return t_str.substring(0, t_str.length()-1);
						} else if (t[i].equals("getEmail")) {
							Object temp = SimpleCase(XEmailType.class,clazz,t[i],t[i+1],var);
							 
							return temp;
						} else if (t[i].equals("getDemographics")) {
							if (t[i+1].equals("getRaces")) {
								String t_str = "";
								for (XRaceType v : ((XStudentType) var).getDemographics().getRaces().getRace()) {
									t_str += (String)SimpleCase(XRaceType.class,t[i+3],v) + ",";
								}
								 
								return t_str.substring(0, t_str.length()-1);
							} else {
								
							}
							
						} else if (t[i].equals("getEnrollment")) {
							Object temp = SimpleCase(XEnrollmentType.class,clazz,t[i],t[i+1],var);
							 
							return temp;
						} else if (t[i].equals("getStudentContacts")) {
							if (t[i+1].equals("getContactPersonRefId")) {
								String t_str = "";
								for (String v :  ((XStudentType) var).getStudentContacts().getContactPersonRefId()) {
									t_str += (String)SimpleCase(String.class,t[i+2],v) + ",";
								}
								return t_str.substring(0, t_str.length()-1);
							} else if (t[i+1].equals("getXContact")) {
								String t_str = "";
								for (XContactType v : ((XStudentType) var).getStudentContacts().getXContact()) {
									t_str += SpecialCase(t, 2, XContactType.class, v, "Contact");
								}
								return t_str;
							}
						} else {
							null_case(t);
						}
						break;
					}
					case "Contact": {
						if (t[i].equals("getName")) {
							Object temp = SimpleCase(XPersonNameType.class,clazz,t[i],t[i+1],var);
							 
							return temp;
						} else if (t[i].equals("getAddress")) {
							Object temp = SimpleCase(XPersonAddressType.class,clazz,t[i],t[i+1],var);
							 
							return temp;
						} else if (t[i].equals("getPhoneNumber")) {
							Object temp = SimpleCase(XTelephoneType.class,clazz,t[i],t[i+1],var);
							 
							return temp;
						} else if (t[i].equals("getOtherPhoneNumbers")) {
							String t_str = "";
							for (XTelephoneType v :  ((XContactType) var).getOtherPhoneNumbers().getPhoneNumber()) {
								t_str += (String)SimpleCase(XTelephoneType.class,t[i+2],v) + ",";
							}
							 
							return t_str.substring(0, t_str.length()-1);
						} else if (t[i].equals("getEmail")) {
							Object temp = SimpleCase(XEmailType.class,clazz,t[i],t[i+1],var);
							 
							return temp;
						} else {
							null_case(t);
						}
						break;
					}
					default: {
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		//}
    	return null;
    }
    
    private static <T> String DefVal(String data_type, String command, T var) {
    	switch (data_type) {
    	case "Roster" : {
    		if (command.equals("getSchoolYear")) {
    			XMLGregorianCalendar temp = ((XRosterType) var).getSchoolYear();
				return Integer.toString(temp.getYear()-1);
	    	}
    		break;
    	}
    	default: {
    		break;
    	}
    	}
    	return null;
    }
    
    private static <T> ArrayList<DataType> DRead(Class<T> clazz, ArrayList<ArrayList<String>> commands, T var, String data_type) {
    	ArrayList<DataType> data_point = new ArrayList<DataType>();
    	for (ArrayList<String> com : commands) {
			Method m;
			try {
				String tm;
				if (com.get(1).split(" ").length > 1) {
					String[] t = com.get(1).split(" ");
					DataType d = new DataType(data_type,com.get(0),com.get(1),SpecialCase(t,0,clazz,var,data_type)); // this line should associate the data pulled from the RICONE server with the user's function name
					data_point.add(d);
				} else if ((tm = DefVal(data_type,com.get(1),var)) != null) {
					DataType d = new DataType(data_type,com.get(0),com.get(1),tm); // this line should associate the data pulled from the RICONE server with the user's function name
					data_point.add(d);
				} else {
					m = clazz.getMethod(com.get(1));
					 
					DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
					data_point.add(d);
				}
			} catch (NoSuchMethodException e) {
				System.err.println("399 - Couldnt find ." + com.get(1) + "() as a callable method. Check the spelling?");
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (!data_point.isEmpty()) {
			return data_point;
		}
		
    	return null;    	
    }
    
	private static <T,E> ArrayList<DataType> DRead(Class<T> clazz, Class<E> clazz2, ArrayList<ArrayList<String>> commands, T var, E var2, String data_type) {
    	ArrayList<DataType> data_point = new ArrayList<DataType>();
    	for (ArrayList<String> com : commands) {
			Method m;
			try {
				String tm;
				if (com.get(1).split(" ").length > 1) {
					String[] t = com.get(1).split(" ");
					if (t[0].contains("-and")) {
						String t_str = "";
						for (int j=0; j < t.length; ++j) {
							String temp;
							boolean done = false;
							while (!t[j].contains("-and")) {
								++j;
								if (j >= t.length) {
									done = true;
									break;
								}
							}
							if (done) {
								break;
							}
							if ((temp = (String)SpecialCase(t,j+1,clazz,var,clazz2,var2,data_type)) != null) {
								t_str += temp;
							} else {
								m = XRosterType.class.getMethod(t[j+1]);
								try {
									t_str += m.invoke(var);
								} catch (ClassCastException e) {
									System.err.println("420 - Couldnt recognize " + t[j+1]);	
								}
							}
							t_str += "_";
						}
						DataType d = new DataType(data_type,com.get(0),com.get(1),t_str.substring(0,t_str.length()-1)); // this line should associate the data pulled from the RICONE server with the user's function name
						data_point.add(d);
					} else {
						DataType d = new DataType(data_type,com.get(0),com.get(1),SpecialCase(t,0,clazz,var,clazz2,var2,data_type)); // this line should associate the data pulled from the RICONE server with the user's function name
						data_point.add(d);
					}
				} else if ((tm = DefVal(data_type,com.get(1),var)) != null) {
					DataType d = new DataType(data_type,com.get(0),com.get(1),tm); // this line should associate the data pulled from the RICONE server with the user's function name
					data_point.add(d);
				} else {
					m = XRosterType.class.getMethod(com.get(1));
					try {
						DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
						data_point.add(d);
					} catch (ClassCastException e) {
						DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
						data_point.add(d);
					}
				}
			} catch (NoSuchMethodException e) {
				System.err.println("454 - Couldnt find ." + com.get(1) + "() as a callable method. Check the spelling?");
				e.printStackTrace();
				System.exit(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (!data_point.isEmpty()) {
			return data_point;
		}
		
    	return null;    	
    }
    
   
    
    private static void PrintPercent(int num, int size, int refid_size, int refid_num, int num_endpoints, int endpoint_num, int num_files, int file_num) {
    	
//		float time_elapsed_so_far = ((float)(System.nanoTime()-file_start_time))/1000000000;
//		float percent_complete = ((float)num*100/size/refid_size+(float)refid_num*100/refid_size);
//		float estimated_time_remaining = time_elapsed_so_far/percent_complete; //  (((float)(System.nanoTime()-file_start_time))/1000000000)/((float)num*100/size/refid_size+(float)refid_num*100/refid_size)
//		float total_time_for_this_specific_file = time_elapsed_so_far+estimated_time_remaining; // (((float)(System.nanoTime()-file_start_time))/1000000000)+((((float)(System.nanoTime()-file_start_time))/1000000000)/((float)num*100/size/refid_size+(float)refid_num*100/refid_size))
//		float total_time_for_all_files = total_time_for_this_specific_file*(num_files-file_num+1)+estimated_time_remaining;
//
    
    	if (print_enabled) {
    		float per_complete = ((float)num*100/size/refid_size/num_endpoints/num_files+(float)refid_num*100/refid_size/num_endpoints/num_files+(float)endpoint_num*100/num_endpoints/num_files+(float)file_num*100/num_files);
	    	System.out.printf("File %s/%s Completed %.2f%% (total: %.2f%%).", file_num+1,num_files,((float)num*100/size/refid_size+(float)refid_num*100/refid_size),per_complete);
			System.out.println();
    	}
    }
    
    private static List<ArrayList<DataType>> DataRead(XPress xPress,List<DataType> data_highlevel_list, int endpoint_num, int num_endpoints, int file_num, int num_files, long start_time) {
    	int starting_num = 1;
    	List<ArrayList<DataType>> list = new ArrayList<ArrayList<DataType>>();
    	String data_type;
    	data_type = data_highlevel_list.get(0).getDataCategory();

    	ArrayList<ArrayList<String>> commands = new ArrayList<ArrayList<String>>();
    	for (DataType d : data_highlevel_list) {
    		commands.add(d.getCommandArray());
    	}
    	
    	
    	if (ref_type.equals("Lea")) {
    		int refid_size = grade_assoc.size();
    		int refid_num = 0;
    		for (String rid : grade_assoc.keySet()) {
    			switch (data_type) {
    				case "Lea": {
    					XLeaType var = xPress.getXLea(rid).getData();
    					list.add(DRead(XLeaType.class, commands, var, data_type));
    					break;
    				}
    				case "School": {
    					if (xPress.getXSchoolsByXLea(rid).getData() != null) {
    						int size = xPress.getXSchoolsByXLea(rid).getData().size();
    						int num = starting_num;
    						for (XSchoolType var : xPress.getXSchoolsByXLea(rid).getData()) { // loop through all schools in the district
    							PrintPercent(num,size,refid_size,refid_num,num_endpoints,endpoint_num,num_files,file_num);
		    					++num;	 
		    					boolean grade_match = GradeChecker.GradeCheck(data_type,var,xPress,grade_assoc.get(rid));
	    						if (grade_match) {
	    	    					list.add(DRead(XSchoolType.class, commands, var, data_type));
	    						}
	    					}
    					}
    					break;
    				}
    				case "Calendar": {
    					if (xPress.getXCalendarsByXLea(rid).getData() != null) {
	    					int size = xPress.getXCalendarsByXLea(rid).getData().size();
							int num = starting_num;
	    					for (XCalendarType var : xPress.getXCalendarsByXLea(rid).getData()) { // loop through all calendars in the district
	    						PrintPercent(num,size,refid_size,refid_num,num_endpoints,endpoint_num,num_files,file_num);
		    					++num;
	    						list.add(DRead(XCalendarType.class, commands, var, data_type));
	    					}
    					}
    					break;
    				}
    				case "Course": {
    					if (xPress.getXCoursesByXLea(rid).getData() != null) {
							int size = xPress.getXCoursesByXLea(rid).getData().size();
							int num = starting_num;
							for (XCourseType var : xPress.getXCoursesByXLea(rid).getData()) { // loop through all courses in the district
								PrintPercent(num,size,refid_size,refid_num,num_endpoints,endpoint_num,num_files,file_num);
		    					++num;
		    					boolean grade_match = GradeChecker.GradeCheck(data_type,var,xPress,grade_assoc.get(rid));
								if (grade_match) {
			    					list.add(DRead(XCourseType.class, commands, var, data_type));
								}
							}
    					}
    					break;
    				}
    				case "Roster": {
    					if (xPress.getXRostersByXLea(rid).getData() != null) {
    						int size = xPress.getXRostersByXLea(rid).getData().size();
    						int num = starting_num;
    						for (XRosterType var : xPress.getXRostersByXLea(rid).getData()) { // loop through all rosters in the district
	    						if (num%5 == 0) {
	    							PrintPercent(num,size,refid_size,refid_num,num_endpoints,endpoint_num,num_files,file_num);
	    						}
	    						++num;	    						
	    						boolean grade_match = GradeChecker.GradeCheck(data_type,var,xPress,grade_assoc.get(rid));
	    						if (grade_match) {
	    							if (commands.get(0).get(1).contains("-and")) {
	    								if (commands.get(0).get(1).contains("-andSTAFF")) {
	    									List<XStaffType> dat;
											if ((dat = xPress.getXStaffsByXRoster(var.getRefId()).getData()) != null) {
	    										for (XStaffType var2 : dat) {
	    											ArrayList<DataType> t_ls = DRead(XRosterType.class, XStaffType.class, commands, var, var2, data_type);
			    	    	    					if (t_ls != null) {
			    	    	    						list.add(t_ls);
			    	    	    					}
	    										}
	    									}
	    								} else if (commands.get(0).get(1).contains("-andSTUDENT")) {
	    									List<XStudentType> dat;
											if ((dat = xPress.getXStudentsByXRoster(var.getRefId()).getData()) != null) {
	    										for (XStudentType var2 : dat) {
	    											ArrayList<DataType> t_ls = DRead(XRosterType.class, XStudentType.class, commands, var, var2, data_type);
			    	    	    					if (t_ls != null) {
			    	    	    						list.add(t_ls);
			    	    	    					}
	    										}
	    									}
	    								} else {
	    									System.err.println("-and command not recognized. Exact command: " + commands.get(0).get(1));
	    								}
	    							} else {
	    								list.add(DRead(XRosterType.class, commands, var, data_type));
	    							}
	    						}
	    					}
    					}
    					break;
    				}
    				case "Staff": {
    					if (xPress.getXStaffsByXLea(rid).getData() != null) {
	    					int size = xPress.getXStaffsByXLea(rid).getData().size();
							int num = starting_num;
							for (XStaffType var : xPress.getXStaffsByXLea(rid).getData()) { // loop through all staffs in the district
	    						PrintPercent(num,size,refid_size,refid_num,num_endpoints,endpoint_num,num_files,file_num);
		    					++num;	 
		    					boolean grade_match = GradeChecker.GradeCheck(data_type,var,xPress,grade_assoc.get(rid));
		    					if (grade_match) {
	    	    					list.add(DRead(XStaffType.class, commands, var, data_type));
	    						}
	    					}
    					}
    					break;
    				}
    				case "Student": {
    					if (xPress.getXStudentsByXLea(rid).getData() != null) {
	    					int size = xPress.getXStudentsByXLea(rid).getData().size();
							int num = starting_num;
							for (XStudentType var : xPress.getXStudentsByXLea(rid).getData()) { // loop through all students in the district
	    						if (num%5==0) {
									PrintPercent(num,size,refid_size,refid_num,num_endpoints,endpoint_num,num_files,file_num);
								}
		    					++num;	
		    					boolean grade_match = GradeChecker.GradeCheck(data_type,var,xPress,grade_assoc.get(rid));
	    						if (grade_match) {
	    	    					list.add(DRead(XStudentType.class, commands, var, data_type));
	    						}
	    					}
    					}
    					break;
    				}
    				case "Contact": {
    					if (xPress.getXContactsByXLea(rid).getData() != null) { 
	    					int size = xPress.getXContactsByXLea(rid).getData().size();
							int num = starting_num;
							for (XContactType var : xPress.getXContactsByXLea(rid).getData()) { // loop through all contacts in the district
	    						PrintPercent(num,size,refid_size,refid_num,num_endpoints,endpoint_num,num_files,file_num);
		    					++num;
		    					boolean grade_match = GradeChecker.GradeCheck(data_type,var,xPress,grade_assoc.get(rid));
	    						if (grade_match) {
	    	    					list.add(DRead(XContactType.class, commands, var, data_type));
	    						}
	    					}
    					}
    					break;
    				}
    				default: {
    					System.err.println("hitting default case. thats a problem... Type is: " + data_type);
    					break;
    				}
    			}
        		++refid_num;
    		}
    	}
    	if (ref_type.equals("School")) {
    		int refid_size = grade_assoc.size();
    		int refid_num = 0;
    		for (String rid : grade_assoc.keySet()) {
    			switch (data_type) {
    				case "Lea": {
    					if (xPress.getXLeasByXSchool(rid).getData() != null) {
	    					int size = xPress.getXLeasByXSchool(rid).getData().size();
							int num = starting_num;
							for (XLeaType var : xPress.getXLeasByXSchool(rid).getData()) {
	    						PrintPercent(num,size,refid_size,refid_num,num_endpoints,endpoint_num,num_files,file_num);
		    					++num;
	    						list.add(DRead(XLeaType.class, commands, var, data_type));
	    					}
    					}
    					break;
    				}
    				case "School": {
    					if (xPress.getXSchool(rid).getData() != null) {
	    					XSchoolType var = xPress.getXSchool(rid).getData();
	    					boolean grade_match = GradeChecker.GradeCheck(data_type,var,xPress,grade_assoc.get(rid));
	    					if (grade_match) {
	        					list.add(DRead(XSchoolType.class, commands, var, data_type));
	    					}
    					}
    					break;
    				}
    				case "Calendar": {
    					if (xPress.getXCalendarsByXSchool(rid).getData() != null) {
	    					int size = xPress.getXCalendarsByXSchool(rid).getData().size();
							int num = 0;
							for (XCalendarType var : xPress.getXCalendarsByXSchool(rid).getData()) { // loop through all calendars in the district
	    						PrintPercent(num,size,refid_size,refid_num,num_endpoints,endpoint_num,num_files,file_num);
		    					++num;	
	    						list.add(DRead(XCalendarType.class, commands, var, data_type));
	    					}
    					}
    					break;
    				}
    				case "Course": {
    					if (xPress.getXCoursesByXSchool(rid).getData() != null) {
	    					int size = xPress.getXCoursesByXSchool(rid).getData().size();
							int num = starting_num;
							for (XCourseType var : xPress.getXCoursesByXSchool(rid).getData()) { // loop through all courses in the district
	    						PrintPercent(num,size,refid_size,refid_num,num_endpoints,endpoint_num,num_files,file_num);
		    					++num;
		    					boolean grade_match = GradeChecker.GradeCheck(data_type,var,xPress,grade_assoc.get(rid));
	    						if (grade_match) {
	    							list.add(DRead(XCourseType.class, commands, var, data_type));
	    						}
	    					}
    					}
    					break;
    				}
    				case "Roster": {
    					if (xPress.getXRostersByXSchool(rid).getData() != null) {
    						int size = xPress.getXRostersByXSchool(rid).getData().size();
    						int num = starting_num;
    						for (XRosterType var : xPress.getXRostersByXSchool(rid).getData()) { // loop through all rosters in the district
	    						if (num%5 == 0) {
	    							PrintPercent(num,size,refid_size,refid_num,num_endpoints,endpoint_num,num_files,file_num);
	    						}
	    						++num;	    						
	    						boolean grade_match = GradeChecker.GradeCheck(data_type,var,xPress,grade_assoc.get(rid));
	    						if (grade_match) {
	    							if (commands.get(0).get(1).contains("-and")) {
	    								if (commands.get(0).get(1).contains("-andSTAFF")) {
	    									List<XStaffType> dat;
											if ((dat = xPress.getXStaffsByXRoster(var.getRefId()).getData()) != null) {
	    										for (XStaffType var2 : dat) {
	    											ArrayList<DataType> t_ls = DRead(XRosterType.class, XStaffType.class, commands, var, var2, data_type);
			    	    	    					if (t_ls != null) {
			    	    	    						list.add(t_ls);
			    	    	    					}
	    										}
	    									}
	    								} else if (commands.get(0).get(1).contains("-andSTUDENT")) {
	    									List<XStudentType> dat;
											if ((dat = xPress.getXStudentsByXRoster(var.getRefId()).getData()) != null) {
	    										for (XStudentType var2 : dat) {
	    											ArrayList<DataType> t_ls = DRead(XRosterType.class, XStudentType.class, commands, var, var2, data_type);
			    	    	    					if (t_ls != null) {
			    	    	    						list.add(t_ls);
			    	    	    					}
	    										}
	    									}
	    								} else {
	    									System.err.println("-and command not recognized. Exact command: " + commands.get(0).get(1));
	    								}
	    							} else {
	    								list.add(DRead(XRosterType.class, commands, var, data_type));
	    							}
	    						}
	    					}
    					}
    					break;
    					}
    				case "Staff": {
    					if (xPress.getXStaffsByXSchool(rid).getData() != null) {
	    					int size = xPress.getXStaffsByXSchool(rid).getData().size();
							int num = starting_num;
							for (XStaffType var : xPress.getXStaffsByXSchool(rid).getData()) { // loop through all staffs in the district
	    						PrintPercent(num,size,refid_size,refid_num,num_endpoints,endpoint_num,num_files,file_num);
		    					++num;
		    					boolean grade_match = GradeChecker.GradeCheck(data_type,var,xPress,grade_assoc.get(rid));
	    						if (grade_match) {
	    							list.add(DRead(XStaffType.class, commands, var, data_type));
	    						}
	    					}
    					}
    					break;
    				}
    				case "Student": {
    					if (xPress.getXStudentsByXSchool(rid).getData() != null) {
	    					int size = xPress.getXStudentsByXSchool(rid).getData().size();
							int num = starting_num;
							for (XStudentType var : xPress.getXStudentsByXSchool(rid).getData()) { // loop through all students in the district
	    						PrintPercent(num,size,refid_size,refid_num,num_endpoints,endpoint_num,num_files,file_num);
		    					++num;
		    					boolean grade_match = GradeChecker.GradeCheck(data_type,var,xPress,grade_assoc.get(rid));
	    						if (grade_match) {
	    							list.add(DRead(XStudentType.class, commands, var, data_type));
	    						}
	    					}
    					}
    					break;
    				}
    				case "Contact": {
    					if (xPress.getXContactsByXSchool(rid).getData() != null) {
	    					int size = xPress.getXContactsByXSchool(rid).getData().size();
							int num = starting_num;
							for (XContactType var : xPress.getXContactsByXSchool(rid).getData()) { // loop through all contacts in the district
	    						PrintPercent(num,size,refid_size,refid_num,num_endpoints,endpoint_num,num_files,file_num);
		    					++num;
		    					boolean grade_match = GradeChecker.GradeCheck(data_type,var,xPress,grade_assoc.get(rid));
	    						if (grade_match) {
	    							list.add(DRead(XContactType.class, commands, var, data_type));
	    						}
	    					}
    					}
    					break;
    				}
    				default: {
    					System.err.println("hitting default case. thats a problem... Type is: " + data_type);
    					break;
    				}
    			}
        		++refid_num;
    		}
    	}
    	return list;
    }
}
