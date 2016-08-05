/*///////////////////////////////////////////////
 * Created By: Shamus Cardon
 * Date Created: 7/14/2016
 * Version: 1.4.1
 * Updated: 8/3/2016
*////////////////////////////////////////////////

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import riconeapi.common.Authenticator;
import riconeapi.common.XPress;
import riconeapi.models.authentication.Endpoint;
import riconeapi.models.xpress.*;

public class DataReader {
    private static String[] refid;   // Identifies the location the data is being pulled from
    private static String ref_type; // Should be either Lea or School
    private static String[] grade_nums; // optional param
    private static long st_time;
    private static boolean print_enabled = true;
    
    
    public static void PopulateDataReader(String r_type, String[] rID, String[] grades) {
    	st_time = System.nanoTime();
		
    	refid = rID;
    	ref_type = r_type;
    	boolean gr = false;
    	if (grades.length>0) {
    		for (String grade : grades) {
    			if (grade.trim().length()>0) {
    	    		grade_nums = grades;
    	    		gr = true;
    	    		break;
    			}
    		}
    	} 
    	if (!gr) {
    		grade_nums = null;
    	}
    }
    
    public static void PopulateDataReader(String r_type, String[] rID, String[] grades, boolean print_en) {
    	st_time = System.nanoTime();
		
    	refid = rID;
    	ref_type = r_type;
    	boolean gr = false;
    	if (grades.length>0) {
    		for (String grade : grades) {
    			if (grade.trim().length()>0) {
    	    		grade_nums = grades;
    	    		gr = true;
    	    		break;
    			}
    		}
    	} 
    	if (!gr) {
    		grade_nums = null;
    	}
    	print_enabled = print_en;
    }
    
    public static ArrayList<Data> GenerateFiles(Configuration config, Authenticator auth) {
    	

		
		// Class to pull data from ric one api
		
		///TODO Search by grade functionality not working - Courses do not have grade information populated in API. blocked from enabling searchbygrade until grades are populated
		
    	
    	
    	ArrayList<Data> file_list = new ArrayList<Data>();
    	
    	int maxi = config.getTextTitle().size();
//		int maxi = 2;
		
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
			} else if (st[j].equals("-andSTAFF")) {
				temp += st[j] + " ";				
			} else if (st[j].equals("-andSTUDENT")) {
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
    				} else if (st[j].equals("-andSTAFF")) {
    					temp += st[j] + " ";				
    				} else if (st[j].equals("-andSTUDENT")) {
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
    
    
	private static <T,E> Object SpecialCaseMain(String[] t,int i, Class<T> clazz, T var, Class<E> clazz2, E var2, String data_type) { // TODO make this method...
		//for (int i=0; i < t.length; ++i) {
		if (t.length <= i+1) {
			System.err.println("t is not large enough for that start index. t.length=" + t.length + " start_index=" + i);
			return null;
		}
			Method m;
			try {
				switch (data_type) {
					case "Lea": {
						if (t[i].equals("getAddress")) {
							Object temp = SimpleCase(XOrganizationAddressType.class,clazz,t[i],t[i+1],var);
							++i;
							return temp;
						} else if (t[i].equals("getPhoneNumber")) {
							Object temp = SimpleCase(XTelephoneType.class,clazz,t[i],t[i+1],var);
							++i;
							return temp;
						} else {
						}
						break;
					}
					case "School": {
						if (t[i].equals("getAddress")) {
							Object temp = SimpleCase(XOrganizationAddressType.class,clazz,t[i],t[i+1],var);
							++i;
							return temp;
						} else if (t[i].equals("getPhoneNumber")) {
							Object temp = SimpleCase(XTelephoneType.class,clazz,t[i],t[i+1],var);
							++i;
							return temp;
						} else if (t[i].equals("getGradeLevels")) {
							String temp_str = "\"";
							m = clazz.getMethod(t[i]);
							Object t_obj = (m.invoke(var));
							m = XGradeLevelListType.class.getMethod(t[i+1]);
							for (String t_s : (List<String>)m.invoke(t_obj)) {
								temp_str += t_s + ", ";
							}
							
							if (temp_str.length() > 1) {
								temp_str = temp_str.substring(0,(temp_str.length()-2)) + "\"";
							} else {
								temp_str = "";
							}
							
							return temp_str;
						}else {
						}
						break;
					}
					case "Calendar": {
						break;
					}
					case "Course": {
						break;
					}
					case "Roster": {
						if (t[i].equals("getStaffPersonReference") || t[i].equals("-staff")) {
							String str = (String)XPersonReferenceType.class.getMethod("getRefId").invoke(XStaffReferenceType.class.getMethod("getStaffPersonReference").invoke(clazz.getMethod("getPrimaryStaff").invoke(var)));
							String str2 = (String)clazz2.getMethod("getRefId").invoke(var2);
							if (str2.equals(str)) {
								if (t[i].equals("getStaffPersonReference")) {
									XPersonReferenceType t_person = (XPersonReferenceType)XStaffReferenceType.class.getMethod("getStaffPersonReference").invoke(clazz.getMethod("getPrimaryStaff").invoke(var));
									m = XPersonReferenceType.class.getMethod(t[i+1]);
									return m.invoke(t_person);
								}
								if (t[i].equals("-staff")) {
									XStaffReferenceType t_person = (XStaffReferenceType)XStaffReferenceType.class.getMethod("getStaffPersonReference").invoke(clazz.getMethod("getPrimaryStaff").invoke(var));
									m = XStaffReferenceType.class.getMethod(t[i+1]);
									return m.invoke(t_person);
								}
							} else {
								Boolean found = false;
								for (XStaffReferenceType st : (List<XStaffReferenceType>)XStaffReferenceListType.class.getMethod("getOtherStaff").invoke(clazz.getMethod("getOtherStaffs").invoke(var))) {
									if (str2.equals(st.getStaffPersonReference().getRefId())) {
										if (t[i].equals("getStaffPersonReference")) {
											m = XPersonReferenceType.class.getMethod(t[i+1]);
			    							return (String)m.invoke(st.getStaffPersonReference());
										}
										if (t[i].equals("-staff")) {
											m = XStaffReferenceType.class.getMethod(t[i+1]);
			    							return (String)m.invoke(st);
										}
									}
								}
								if (!found) {
									return null;
								}
							}
						} else if (t[i].equals("-student")) {
							String str2 = (String)clazz2.getMethod("getRefId").invoke(var2);
							boolean found = false;
							for (XPersonReferenceType st : (List<XPersonReferenceType>)XStudentReferenceListType.class.getMethod("getStudentReference").invoke(clazz.getMethod("getStudents").invoke(var))) {
								if (str2.equals(st.getRefId())) {
									m = XPersonReferenceType.class.getMethod(t[i+1]);
	    							return m.invoke(st);
								}
							}
							if (!found) {
								return null;
							}
						} else {
						}
						break;
					}
					case "Staff": {
						if (t[i].equals("getName")) {
							Object temp = SimpleCase(XPersonNameType.class,clazz,t[i],t[i+1],var);
							++i;
							return temp;
						} else if (t[i].equals("getEmail")) {
							Object temp = SimpleCase(XEmailType.class,clazz,t[i],t[i+1],var);
							++i;
							return temp;
						} else {
						}
						break;
					}
					case "Student": {
						if (t[i].equals("getName")) {
							Object temp = SimpleCase(XPersonNameType.class,clazz,t[i],t[i+1],var);
							++i;
							return temp;
						} else if (t[i].equals("getAddress")) {
							Object temp = SimpleCase(XPersonAddressType.class,clazz,t[i],t[i+1],var);
							++i;
							return temp;
						} else if (t[i].equals("getPhoneNumber")) {
							Object temp = SimpleCase(XTelephoneType.class,clazz,t[i],t[i+1],var);
							++i;
							return temp;
						} else if (t[i].equals("getEmail")) {
							Object temp = SimpleCase(XEmailType.class,clazz,t[i],t[i+1],var);
							++i;
							return temp;
						} else if (t[i].equals("getEnrollment")) {
							Object temp = SimpleCase(XEnrollmentType.class,clazz,t[i],t[i+1],var);
							++i;
							return temp;
						} else {
						}
						break;
					}
					case "Contact": {
						if (t[i].equals("getName")) {
							Object temp = SimpleCase(XPersonNameType.class,clazz,t[i],t[i+1],var);
							++i;
							return temp;
						} else if (t[i].equals("getAddress")) {
							Object temp = SimpleCase(XPersonAddressType.class,clazz,t[i],t[i+1],var);
							++i;
							return temp;
						} else if (t[i].equals("getPhoneNumber")) {
							Object temp = SimpleCase(XTelephoneType.class,clazz,t[i],t[i+1],var);
							++i;
							return temp;
						} else if (t[i].equals("getEmail")) {
							Object temp = SimpleCase(XEmailType.class,clazz,t[i],t[i+1],var);
							++i;
							return temp;
						} else {
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
    
    
    private static <T> ArrayList<DataType> DRead(Class<T> clazz, ArrayList<ArrayList<String>> commands, T var, String data_type) {
    	ArrayList<DataType> data_point = new ArrayList<DataType>();
    	for (ArrayList<String> com : commands) {
			Method m;
			try {
				if (com.get(1).split(" ").length > 1) {
					String[] t = com.get(1).split(" ");
					DataType d = new DataType(data_type,com.get(0),com.get(1),SpecialCase(t,0,clazz,var,data_type)); // this line should associate the data pulled from the RICONE server with the user's function name
					data_point.add(d);
				} else {
					m = clazz.getMethod(com.get(1));
					 
					DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
					data_point.add(d);
				}
			} catch (NoSuchMethodException e) {
				System.err.println("160 - Couldnt find ." + com.get(1) + "() as a callable method. Check the spelling?");
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
				if (com.get(1).split(" ").length > 1) {
					String[] t = com.get(1).split(" ");
					if (t[0].contains("-and")) {
						String t_str = "";
						for (int j=1; j < t.length; ++j) {
							String temp;
							if ((temp = (String)SpecialCase(t,j,clazz,var,clazz2,var2,data_type)) != null) {
								t_str += temp;
								++j;
							} else {
								m = XRosterType.class.getMethod(t[j]);
								try {
									t_str += m.invoke(var);
								} catch (ClassCastException e) {
									System.err.println("420 - Couldnt recognize " + t[j]);	
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
				System.err.println("607 - Couldnt find ." + com.get(1) + "() as a callable method. Check the spelling?");
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
    
    
    private static <T> boolean GradeCheck(String data_type, T var, XPress xPress) {
    	if (grade_nums == null) {
    		return true;
    	}
    	switch (data_type) {
			case "Lea": {
				break;
			}
			case "School": {
				if (grade_nums != null) {
					for (String desired_grade : grade_nums) { // nested for loops to check if the given grades fall into the grades this particular school offers
						for (String given_grade : ((XSchoolType) var).getGradeLevels().getGradeLevel()) {
							if (desired_grade.equals(given_grade)) {
								return true;
							}
						}
					}
				}
				break;
			}
			case "Calendar": {
				break;
			}
			case "Course": {
					if (grade_nums != null) { // 
						for (String desired_grade : grade_nums) { // nested for loops to check if the given grades fall into the grades this particular school offers
							for (String given_grade : ((XCourseType) var).getApplicableEducationLevels().getApplicableEducationLevel()) {
								if (desired_grade.equals(given_grade)) {
									return true;
								}
							}
						}
					}
				break;
			}
			case "Roster": {
				if (grade_nums != null) { // 
					for (String desired_grade : grade_nums) { // nested for loops to check if the given grades fall into the grades this particular school offers
						List<XCourseType> dat;
							if ((dat = xPress.getXCoursesByXRoster(((XRosterType) var).getRefId()).getData()) != null) {
								for (XCourseType temp : dat) { // get the course associated with the roster (needed to pull grade level information
									for (String given_grade : temp.getApplicableEducationLevels().getApplicableEducationLevel()) {
										if (desired_grade.equals(given_grade)) {
											return true;
										}
									}
								}
							}
						} 
				}
				break;
			}
			case "Staff": {
				if (grade_nums != null) { // 
					for (String desired_grade : grade_nums) { // nested for loops to check if the given grades fall into the grades this particular school offers
						List<XRosterType> dat;
						if ((dat = xPress.getXRostersByXStaff(((XStaffType) var).getRefId()).getData()) != null) {    										
							for (XRosterType roster : dat) {		
								List<XCourseType> dat2;
								if ((dat2 = xPress.getXCoursesByXRoster(roster.getRefId()).getData()) != null) {
									for (XCourseType temp : dat2) { // get the course associated with the roster (needed to pull grade level information
										for (String given_grade : temp.getApplicableEducationLevels().getApplicableEducationLevel()) {
											if (desired_grade.equals(given_grade)) {
												return true;
											}
										}
									}
								}
							} 
						}
					}
				}
				break;
			}
			case "Student": {
				if (grade_nums != null) { // 
					for (String desired_grade : grade_nums) { // nested for loops to check if the given grades fall into the grades this particular school offers
							/*List<XRosterType> dat;
							if ((dat = xPress.getXRostersByXStudent(var.getRefId()).getData()) != null) {
								for (XRosterType roster : dat) {		
									if (!grade_match) {
										List<XCourseType> dat2;
										if ((dat2=xPress.getXCoursesByXRoster(roster.getRefId()).getData()) != null) {
										for (XCourseType temp : dat2) { // get the course associated with the roster (needed to pull grade level information
											System.out.println(temp);
											if (!grade_match) {
												for (String given_grade : temp.getApplicableEducationLevels().getApplicableEducationLevel()) {
													System.out.println(given_grade);
													if (desired_grade.equals(given_grade)) {
														grade_match = true;
														System.out.println("breaking");
														break;
													}
												}
											}
										}
										}
									}
								}
							}*/
								
						String given_grade = ((XStudentType) var).getEnrollment().getGradeLevel();
						if (desired_grade.equals(given_grade)) {
							return true;
						}
					}
				}
				break;
			}
			case "Contact": {
			if (grade_nums != null) { // 
				for (String desired_grade : grade_nums) { // nested for loops to check if the given grades fall into the grades this particular school offers
					for (XStudentType student : xPress.getXStudentsByXContact(((XContactType) var).getRefId()).getData()) {										
						for (XRosterType roster : xPress.getXRostersByXStudent(student.getRefId()).getData()) {		
							for (XCourseType temp : xPress.getXCoursesByXRoster(roster.getRefId()).getData()) { // get the course associated with the roster (needed to pull grade level information
								for (String given_grade : temp.getApplicableEducationLevels().getApplicableEducationLevel()) {
									if (desired_grade.equals(given_grade)) {
										return true;											}
										}
									}
								}
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
    	return false;
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
    		int refid_size = refid.length;
    		int refid_num = 0;
    		for (String rid : refid) {
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
		    					boolean grade_match = GradeCheck(data_type,var,xPress);
	    						if (grade_match) {
	    	    					list.add(DRead(XSchoolType.class, commands, var, data_type));
	    						}
	    					}
    					}
    					break;
    				}
    				case "Calendar": {
    					int size = xPress.getXCalendarsByXLea(rid).getData().size();
						int num = starting_num;
    					for (XCalendarType var : xPress.getXCalendarsByXLea(rid).getData()) { // loop through all calendars in the district
    						PrintPercent(num,size,refid_size,refid_num,num_endpoints,endpoint_num,num_files,file_num);
	    					++num;
    						list.add(DRead(XCalendarType.class, commands, var, data_type));
    					}
    					break;
    				}
    				case "Course": {
    					int size = xPress.getXCoursesByXLea(rid).getData().size();
						int num = starting_num;
						for (XCourseType var : xPress.getXCoursesByXLea(rid).getData()) { // loop through all courses in the district
    						PrintPercent(num,size,refid_size,refid_num,num_endpoints,endpoint_num,num_files,file_num);
	    					++num;
	    					boolean grade_match = GradeCheck(data_type,var,xPress);
    						if (grade_match) {
    	    					list.add(DRead(XCourseType.class, commands, var, data_type));
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
	    						boolean grade_match = GradeCheck(data_type,var,xPress);
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
    					int size = xPress.getXStaffsByXLea(rid).getData().size();
						int num = starting_num;
						for (XStaffType var : xPress.getXStaffsByXLea(rid).getData()) { // loop through all staffs in the district
    						PrintPercent(num,size,refid_size,refid_num,num_endpoints,endpoint_num,num_files,file_num);
	    					++num;	 
	    					boolean grade_match = GradeCheck(data_type,var,xPress);
	    					if (grade_match) {
    	    					list.add(DRead(XStaffType.class, commands, var, data_type));
    						}
    					}
    					break;
    				}
    				case "Student": {
    					int size = xPress.getXStudentsByXLea(rid).getData().size();
						int num = starting_num;
						for (XStudentType var : xPress.getXStudentsByXLea(rid).getData()) { // loop through all students in the district
    						if (num%5==0) {
								PrintPercent(num,size,refid_size,refid_num,num_endpoints,endpoint_num,num_files,file_num);
							}
	    					++num;	
	    					boolean grade_match = GradeCheck(data_type,var,xPress);
    						if (grade_match) {
    	    					list.add(DRead(XStudentType.class, commands, var, data_type));
    						}
    					}
    					break;
    				}
    				case "Contact": {
    					int size = xPress.getXContactsByXLea(rid).getData().size();
						int num = starting_num;
						for (XContactType var : xPress.getXContactsByXLea(rid).getData()) { // loop through all contacts in the district
    						PrintPercent(num,size,refid_size,refid_num,num_endpoints,endpoint_num,num_files,file_num);
	    					++num;
	    					boolean grade_match = GradeCheck(data_type,var,xPress);
    						if (grade_match) {
    	    					list.add(DRead(XContactType.class, commands, var, data_type));
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
    		int refid_size = refid.length;
    		int refid_num = 0;
    		for (String rid : refid) {
    			switch (data_type) {
    				case "Lea": {
    					int size = xPress.getXLeasByXSchool(rid).getData().size();
						int num = starting_num;
						for (XLeaType var : xPress.getXLeasByXSchool(rid).getData()) {
    						PrintPercent(num,size,refid_size,refid_num,num_endpoints,endpoint_num,num_files,file_num);
	    					++num;
    						list.add(DRead(XLeaType.class, commands, var, data_type));
    					}
    					break;
    				}
    				case "School": {
    					XSchoolType var = xPress.getXSchool(rid).getData();
    					boolean grade_match = GradeCheck(data_type,var,xPress);
    					if (grade_match) {
        					list.add(DRead(XSchoolType.class, commands, var, data_type));
    					}
    					break;
    				}
    				case "Calendar": {
    					int size = xPress.getXCalendarsByXSchool(rid).getData().size();
						int num = 0;
						for (XCalendarType var : xPress.getXCalendarsByXSchool(rid).getData()) { // loop through all calendars in the district
    						PrintPercent(num,size,refid_size,refid_num,num_endpoints,endpoint_num,num_files,file_num);
	    					++num;	
    						list.add(DRead(XCalendarType.class, commands, var, data_type));
    					}
    					break;
    				}
    				case "Course": {
    					int size = xPress.getXCoursesByXSchool(rid).getData().size();
						int num = starting_num;
						for (XCourseType var : xPress.getXCoursesByXSchool(rid).getData()) { // loop through all courses in the district
    						PrintPercent(num,size,refid_size,refid_num,num_endpoints,endpoint_num,num_files,file_num);
	    					++num;
	    					boolean grade_match = GradeCheck(data_type,var,xPress);
    						if (grade_match) {
    							list.add(DRead(XCourseType.class, commands, var, data_type));
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
	    						boolean grade_match = GradeCheck(data_type,var,xPress);
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
    					int size = xPress.getXStaffsByXSchool(rid).getData().size();
						int num = starting_num;
						for (XStaffType var : xPress.getXStaffsByXSchool(rid).getData()) { // loop through all staffs in the district
    						PrintPercent(num,size,refid_size,refid_num,num_endpoints,endpoint_num,num_files,file_num);
	    					++num;
	    					boolean grade_match = GradeCheck(data_type,var,xPress);
    						if (grade_match) {
    							list.add(DRead(XStaffType.class, commands, var, data_type));
    						}
    					}
    					break;
    				}
    				case "Student": {
    					int size = xPress.getXStudentsByXSchool(rid).getData().size();
						int num = starting_num;
						for (XStudentType var : xPress.getXStudentsByXSchool(rid).getData()) { // loop through all students in the district
    						PrintPercent(num,size,refid_size,refid_num,num_endpoints,endpoint_num,num_files,file_num);
	    					++num;
	    					boolean grade_match = GradeCheck(data_type,var,xPress);
    						if (grade_match) {
    							list.add(DRead(XStudentType.class, commands, var, data_type));
    						}
    					}
    					break;
    				}
    				case "Contact": {
    					int size = xPress.getXContactsByXSchool(rid).getData().size();
						int num = starting_num;
						for (XContactType var : xPress.getXContactsByXSchool(rid).getData()) { // loop through all contacts in the district
    						PrintPercent(num,size,refid_size,refid_num,num_endpoints,endpoint_num,num_files,file_num);
	    					++num;
	    					boolean grade_match = GradeCheck(data_type,var,xPress);
    						if (grade_match) {
    							list.add(DRead(XContactType.class, commands, var, data_type));
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
