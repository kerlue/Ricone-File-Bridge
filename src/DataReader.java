/*////////////////////////////////////////////////
 * Created By: Shamus Cardon
 * Date Created: 7/14/2016
 * Version: 1.1.0
 * Updated: 7/25/2016
*/////////////////////////////////////////////////

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import riconeapi.common.Authenticator;
import riconeapi.common.XPress;
import riconeapi.models.authentication.Endpoint;
import riconeapi.models.xpress.*;

public class DataReader {
    private static String[] refid;   // Identifies the location the data is being pulled from
    private static String ref_type; // Should be either Lea or School
    private static String[] grade_nums; // optional param
    
    
    DataReader(String r_type, String[] rID, String[] grades) {
    	refid = rID;
    	ref_type = r_type;
    	grade_nums = grades;
    }
    
    DataReader(String r_type, String[] rID) {
    	refid = rID;
    	ref_type = r_type;
    	grade_nums = null;
    }
   
    public ArrayList<DataType> GetDataTypes (String col_names, String req_data) {
    	ArrayList<DataType> full_list = new ArrayList<DataType>();
    	
    	String[] t_list1 = col_names.split(",");    	
    	String[] t_list2 = req_data.split(",");
    	    	
    	if (t_list1.length != t_list2.length) {
    		System.err.println("Lists are different sizes. That is a problem...");
    		System.err.println("List1 size: " + t_list1.length + " List2 size: " + t_list2.length);
    	}

		String data_category = t_list2[0].trim().split(" ")[0].substring(1,t_list2[0].trim().split(" ")[0].length()); // pull the data type info. i.e. Lea, School, etc.
		
		DataType dt = new DataType(data_category,t_list1[0].trim(),"get" + t_list2[0].trim().split(" ")[1]);
		full_list.add(dt);
    	
    	for (int i=1; i < t_list1.length; ++i) {
    		String s = t_list2[i].trim();
    		
    		if (s.equals("null")) {
    			System.err.println("cannot recognize " + t_list1[i].trim());
        		//DataType d = new DataType("",t_list1[i].trim(),"");
        		//full_list.add(d);
    		} else {
    			String temp = "";
    			for (String st : s.split(" ")) {
    				temp += "get" + st + " ";
    			}
    			temp = temp.trim();
    			DataType d = new DataType(data_category,t_list1[i].trim(), temp);
            	full_list.add(d);
    		}
    	}
    	
    	return full_list;
    }
	
	public ArrayList<String> Split(String string_to_split) {
		ArrayList<String> t_list = new ArrayList<String>();
		String[] s = string_to_split.trim().split(",");
		for (String i : s) {
			System.out.println(i.trim());
		}
		
		System.out.print("\n");
		return t_list;
	}
	
    public Data ReadIn(Authenticator auth, List<DataType> data_type_list, String file_name) { // xPressType should be "Lea","School","Student","Staff",etc.
		List<ArrayList<DataType>> list = new ArrayList<ArrayList<DataType>>();
				
		for(Endpoint e : auth.getEndpoints()) {
			XPress xPress = new XPress(auth.getToken(), e.getHref());
			list.addAll(DataRead(xPress,data_type_list));
			//System.out.println(list);
			//XLeas_GetXLeas(xPress);
		}
		Data d = new Data(list,file_name);
		return d;
    }
    
    
    public List<ArrayList<DataType>> DataRead(XPress xPress,List<DataType> data_highlevel_list) {
    	List<ArrayList<DataType>> list = new ArrayList<ArrayList<DataType>>();
    	String data_type;
    	data_type = data_highlevel_list.get(0).getDataCategory();
    	
    	ArrayList<ArrayList<String>> commands = new ArrayList<ArrayList<String>>();
    	for (DataType d : data_highlevel_list) {
    		commands.add(d.getCommandArray());
    	}
    	
    	if (ref_type.equals("Lea")) {
    		for (String rid : refid) {
    			switch (data_type) {
    				case "Lea": {
    					ArrayList<DataType> data_point = new ArrayList<DataType>();
    					XLeaType var = xPress.getXLea(rid).getData();
    					for (ArrayList<String> com : commands) {
    						Method m;
    						try {
    							if (com.get(1).split(" ").length > 1) {
    								String[] t = com.get(1).split(" ");
    								if (t[0].equals("getAddress")) {
    									m = XLeaType.class.getMethod(t[0]);
    	    							Object t_obj = (m.invoke(var));
    	    							m = XOrganizationAddressType.class.getMethod(t[1]);
    	    							  	    							
    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    							data_point.add(d);
    								} else if (t[0].equals("getName")) {
    									m = XLeaType.class.getMethod(t[0]);
    	    							Object t_obj = (m.invoke(var));
    	    							m = XPersonNameType.class.getMethod(t[1]);
    	    							
    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    							data_point.add(d);
    								} else if (t[0].equals("getEmail")) {
    									m = XLeaType.class.getMethod(t[0]);
    	    							Object t_obj = (m.invoke(var));
    	    							m = XEmailType.class.getMethod(t[1]);
    	    							
    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    							data_point.add(d);
    								} else {
    									System.err.println("Couldnt recognize " + t[0]);
    								}
    							} else {
	    							m = XLeaType.class.getMethod(com.get(1));
	    							 
	    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
	    							data_point.add(d);
    							}
    						} catch (NoSuchMethodException e) {
    							System.err.println("Couldnt find ." + com.get(1) + "() as a callable method. Check the spelling?");
    							e.printStackTrace();
    						} catch (Exception e) {
    							e.printStackTrace();
    						}
    					}
    					list.add(data_point);
    					break;
    				}
    				case "School": {
    					for (XSchoolType var : xPress.getXSchoolsByXLea(rid).getData()) { // loop through all schools in the district
        					ArrayList<DataType> data_point = new ArrayList<DataType>();
    						boolean grade_match = false;
    						if (grade_nums != null) { // TODO Still need to test to make sure this grade matches correctly
    							for (String desired_grade : grade_nums) { // nested for loops to check if the given grades fall into the grades this particular school offers
    								if (!grade_match) {
    									for (String given_grade : var.getGradeLevels().getGradeLevel()) {
    										if (desired_grade.equals(given_grade)) {
    											grade_match = true;
    											System.out.println("breaking");
    											break;
    										}
    									}
    								}
    							}
    						} else {
    							grade_match = true;
    						}
    						if (grade_match) {
    							for (ArrayList<String> com : commands) {
    								Method m;
    								try {
    									if (com.get(1).split(" ").length > 1) {
    	    								String[] t = com.get(1).split(" ");
    	    								if (t[0].equals("getAddress")) {
    	    									m = XSchoolType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XOrganizationAddressType.class.getMethod(t[1]);
    	    	    							  	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else if (t[0].equals("getName")) {
    	    									m = XSchoolType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XPersonNameType.class.getMethod(t[1]);
    	    	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else if (t[0].equals("getEmail")) {
    	    									m = XSchoolType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XEmailType.class.getMethod(t[1]);
    	    	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else {
    	    									System.err.println("Couldnt recognize " + t[0]);
    	    								}
    	    							} else {
    		    							m = XSchoolType.class.getMethod(com.get(1));
    		    							 
    		    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
    		    							data_point.add(d);
    	    							}
    								} catch (NoSuchMethodException e) {
    									System.err.println("Couldnt find ." + com.get(1) + "() as a callable method. Check the spelling?");
    									e.printStackTrace();
    								} catch (Exception e) {
    									e.printStackTrace();
    								}
    							}
    	    					if (!data_point.isEmpty()) {
    	    						list.add(data_point);
    	    					}
    						} else {
    							System.out.println("here (no grade match)");
    						}
    					}
    					break;
    				}
    				case "Calendar": {
    					for (XCalendarType var : xPress.getXCalendarsByXLea(rid).getData()) { // loop through all calendars in the district
        					ArrayList<DataType> data_point = new ArrayList<DataType>();
        					for (ArrayList<String> com : commands) {
    							Method m;
    							try {
    								if (com.get(1).split(" ").length > 1) {
	    								String[] t = com.get(1).split(" ");
	    								if (t[0].equals("getAddress")) {
	    									m = XCalendarType.class.getMethod(t[0]);
	    	    							Object t_obj = (m.invoke(var));
	    	    							m = XOrganizationAddressType.class.getMethod(t[1]);
	    	    							  	    							
	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
	    	    							data_point.add(d);
	    								} else if (t[0].equals("getName")) {
	    									m = XCalendarType.class.getMethod(t[0]);
	    	    							Object t_obj = (m.invoke(var));
	    	    							m = XPersonNameType.class.getMethod(t[1]);
	    	    							
	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
	    	    							data_point.add(d);
	    								} else if (t[0].equals("getEmail")) {
	    									m = XCalendarType.class.getMethod(t[0]);
	    	    							Object t_obj = (m.invoke(var));
	    	    							m = XEmailType.class.getMethod(t[1]);
	    	    							
	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
	    	    							data_point.add(d);
	    								} else {
	    									System.err.println("Couldnt recognize " + t[0]);
	    								}
	    							} else {
		    							m = XCalendarType.class.getMethod(com.get(1));
		    							 
		    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
		    							data_point.add(d);
	    							}
    							} catch (NoSuchMethodException e) {
    								System.err.println("Couldnt find ." + com.get(1) + "() as a callable method. Check the spelling?");
    								e.printStackTrace();
    							} catch (Exception e) {
    								e.printStackTrace();
    							}
    						}
        					if (!data_point.isEmpty()) {
        						list.add(data_point);
        					}
    					}
    					break;
    				}
    				case "Course": {
    					for (XCourseType var : xPress.getXCoursesByXLea(rid).getData()) { // loop through all courses in the district
        					ArrayList<DataType> data_point = new ArrayList<DataType>();
        					boolean grade_match = false;
    						if (grade_nums != null) { // TODO Still need to test to make sure this grade matches correctly
    							for (String desired_grade : grade_nums) { // nested for loops to check if the given grades fall into the grades this particular school offers
    								if (!grade_match) {
    									for (String given_grade : var.getApplicableEducationLevels().getApplicableEducationLevel()) {
    										if (desired_grade.equals(given_grade)) {
    											grade_match = true;
    											System.out.println("breaking");
    											break;
    										}
    									}
    								}
    							}
    						} else {
    							grade_match = true;
    						}
    						if (grade_match) {
    							for (ArrayList<String> com : commands) {
    								Method m;
    								try {
    									if (com.get(1).split(" ").length > 1) {
    	    								String[] t = com.get(1).split(" ");
    	    								if (t[0].equals("getAddress")) {
    	    									m = XCourseType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XOrganizationAddressType.class.getMethod(t[1]);
    	    	    							  	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else if (t[0].equals("getName")) {
    	    									m = XCourseType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XPersonNameType.class.getMethod(t[1]);
    	    	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else if (t[0].equals("getEmail")) {
    	    									m = XCourseType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XEmailType.class.getMethod(t[1]);
    	    	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else {
    	    									System.err.println("Couldnt recognize " + t[0]);
    	    								}
    	    							} else {
    		    							m = XCourseType.class.getMethod(com.get(1));
    		    							 
    		    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
    		    							data_point.add(d);
    	    							}
    								} catch (NoSuchMethodException e) {
    									System.err.println("Couldnt find ." + com.get(1) + "() as a callable method. Check the spelling?");
    									e.printStackTrace();
    								} catch (Exception e) {
    									e.printStackTrace();
    								}
    							}
    						} else {
    							System.out.println("here (no grade match)");
    						}
        					if (!data_point.isEmpty()) {
        						list.add(data_point);
        					}
    					}
    					break;
    				}
    				case "Roster": {
    					for (XRosterType var : xPress.getXRostersByXLea(rid).getData()) { // loop through all rosters in the district
        					ArrayList<DataType> data_point = new ArrayList<DataType>();boolean grade_match = false;
    						if (grade_nums != null) { // TODO Still need to test to make sure this grade matches correctly
    							for (String desired_grade : grade_nums) { // nested for loops to check if the given grades fall into the grades this particular school offers
    								if (!grade_match) {
    									for (XCourseType temp : xPress.getXCoursesByXRoster(var.getRefId()).getData()) { // get the course associated with the roster (needed to pull grade level information
    										if (!grade_match) {
    											for (String given_grade : temp.getApplicableEducationLevels().getApplicableEducationLevel()) {
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
    						else {
    							grade_match = true;
    						}
    						
    						
    						if (grade_match) {
    							for (ArrayList<String> com : commands) {
    								Method m;
    								try {
    									if (com.get(1).split(" ").length > 1) {
    	    								String[] t = com.get(1).split(" ");
    	    								if (t[0].equals("getAddress")) {
    	    									m = XRosterType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XOrganizationAddressType.class.getMethod(t[1]);
    	    	    							  	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else if (t[0].equals("getName")) {
    	    									m = XRosterType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XPersonNameType.class.getMethod(t[1]);
    	    	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else if (t[0].equals("getEmail")) {
    	    									m = XRosterType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XEmailType.class.getMethod(t[1]);
    	    	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else {
    	    									System.err.println("Couldnt recognize " + t[0]);
    	    								}
    	    							} else {
    		    							m = XRosterType.class.getMethod(com.get(1));
    		    							System.out.println(m.invoke(var));
    		    							try {
    		    								DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
        		    							data_point.add(d);
    		    							} catch (ClassCastException e) {
    		    								DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
        		    							data_point.add(d);
    		    							}
    	    							}
    								} catch (NoSuchMethodException e) {
    									System.err.println("Couldnt find ." + com.get(1) + "() as a callable method. Check the spelling?");
    									e.printStackTrace();
    								} catch (Exception e) {
    									e.printStackTrace();
    								}
    							}
    						} else {
    							System.out.println("here (no grade match)");
    						}
        					if (!data_point.isEmpty()) {
        						list.add(data_point);
        					}
    					}
    					break;
    				}
    				case "Staff": {
    					for (XStaffType var : xPress.getXStaffsByXLea(rid).getData()) { // loop through all staffs in the district
        					ArrayList<DataType> data_point = new ArrayList<DataType>();
    						boolean grade_match = false;
    						if (grade_nums != null) { // TODO Still need to test to make sure this grade matches correctly
    							for (String desired_grade : grade_nums) { // nested for loops to check if the given grades fall into the grades this particular school offers
    								if (!grade_match) {
    									for (XRosterType roster : xPress.getXRostersByXStaff(var.getRefId()).getData()) {		
    										if (!grade_match) {
    											for (XCourseType temp : xPress.getXCoursesByXRoster(roster.getRefId()).getData()) { // get the course associated with the roster (needed to pull grade level information
    												if (!grade_match) {
    													for (String given_grade : temp.getApplicableEducationLevels().getApplicableEducationLevel()) {
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
    							}
    						} else {
    							grade_match = true;
    						}
    						if (grade_match) {
    							for (ArrayList<String> com : commands) {
    								Method m;
    								try {
    									if (com.get(1).split(" ").length > 1) {
    	    								String[] t = com.get(1).split(" ");
    	    								if (t[0].equals("getAddress")) {
    	    									m = XStaffType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XOrganizationAddressType.class.getMethod(t[1]);
    	    	    							  	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else if (t[0].equals("getName")) {
    	    									m = XStaffType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XPersonNameType.class.getMethod(t[1]);
    	    	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else if (t[0].equals("getEmail")) {
    	    									m = XStaffType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XEmailType.class.getMethod(t[1]);
    	    	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else {
    	    									System.err.println("Couldnt recognize " + t[0]);
    	    								}
    	    							} else {
    		    							m = XStaffType.class.getMethod(com.get(1));
    		    							 
    		    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
    		    							data_point.add(d);
    	    							}
    								} catch (NoSuchMethodException e) {
    									System.err.println("Couldnt find ." + com.get(1) + "() as a callable method. Check the spelling?");
    									e.printStackTrace();
    								} catch (Exception e) {
    									e.printStackTrace();
    								}
    							}
    						} else {
    							System.out.println("here (no grade match)");
    						}
        					if (!data_point.isEmpty()) {
        						list.add(data_point);
        					}
    					}
    					break;
    				}
    				case "Student": {
    					for (XStudentType var : xPress.getXStudentsByXLea(rid).getData()) { // loop through all students in the district
    						ArrayList<DataType> data_point = new ArrayList<DataType>();
        					boolean grade_match = false;
    						if (grade_nums != null) { // TODO Still need to test to make sure this grade matches correctly
    							for (String desired_grade : grade_nums) { // nested for loops to check if the given grades fall into the grades this particular school offers
    								if (!grade_match) {
    									for (XRosterType roster : xPress.getXRostersByXStudent(var.getRefId()).getData()) {		
    										if (!grade_match) {
    											for (XCourseType temp : xPress.getXCoursesByXRoster(roster.getRefId()).getData()) { // get the course associated with the roster (needed to pull grade level information
    												if (!grade_match) {
    													for (String given_grade : temp.getApplicableEducationLevels().getApplicableEducationLevel()) {
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
    							}
    						} else {
    							grade_match = true;
    						}
    						if (grade_match) {
    							for (ArrayList<String> com : commands) {
    								Method m;
    								try {
    									if (com.get(1).split(" ").length > 1) {
    	    								String[] t = com.get(1).split(" ");
    	    								if (t[0].equals("getAddress")) {
    	    									m = XStudentType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XOrganizationAddressType.class.getMethod(t[1]);
    	    	    							  	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else if (t[0].equals("getName")) {
    	    									m = XStudentType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XPersonNameType.class.getMethod(t[1]);
    	    	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else if (t[0].equals("getEmail")) {
    	    									m = XStudentType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XEmailType.class.getMethod(t[1]);
    	    	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else {
    	    									System.err.println("Couldnt recognize " + t[0]);
    	    								}
    	    							} else {
    		    							m = XStudentType.class.getMethod(com.get(1));
    		    							 
    		    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
    		    							data_point.add(d);
    	    							}
    								} catch (NoSuchMethodException e) {
    									System.err.println("Couldnt find ." + com.get(1) + "() as a callable method. Check the spelling?");
    									e.printStackTrace();
    								} catch (Exception e) {
    									e.printStackTrace();
    								}
    							}
    						} else {
    							System.out.println("here (no grade match)");
    						}
        					if (!data_point.isEmpty()) {
        						list.add(data_point);
        					}
    					}
    					break;
    				}
    				case "Contact": {
    					for (XContactType var : xPress.getXContactsByXLea(rid).getData()) { // loop through all contacts in the district
    						ArrayList<DataType> data_point = new ArrayList<DataType>();
        					boolean grade_match = false;
    						if (grade_nums != null) { // TODO Still need to test to make sure this grade matches correctly
    							for (String desired_grade : grade_nums) { // nested for loops to check if the given grades fall into the grades this particular school offers
    								if (!grade_match) {
    									for (XStudentType student : xPress.getXStudentsByXContact(var.getRefId()).getData()) {										
    										if (!grade_match) {
    											for (XRosterType roster : xPress.getXRostersByXStudent(student.getRefId()).getData()) {		
    												if (!grade_match) {
    													for (XCourseType temp : xPress.getXCoursesByXRoster(roster.getRefId()).getData()) { // get the course associated with the roster (needed to pull grade level information
    														if (!grade_match) {
    															for (String given_grade : temp.getApplicableEducationLevels().getApplicableEducationLevel()) {
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
    									}
    								}
    							}
    						} else {
    							grade_match = true;
    						}
    						if (grade_match) {
    							for (ArrayList<String> com : commands) {
    								Method m;
    								try {
    									if (com.get(1).split(" ").length > 1) {
    	    								String[] t = com.get(1).split(" ");
    	    								if (t[0].equals("getAddress")) {
    	    									m = XStudentType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XOrganizationAddressType.class.getMethod(t[1]);
    	    	    							  	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else if (t[0].equals("getName")) {
    	    									m = XStudentType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XPersonNameType.class.getMethod(t[1]);
    	    	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else if (t[0].equals("getEmail")) {
    	    									m = XStudentType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XEmailType.class.getMethod(t[1]);
    	    	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else {
    	    									System.err.println("Couldnt recognize " + t[0]);
    	    								}
    	    							} else {
    		    							m = XStudentType.class.getMethod(com.get(1));
    		    							 
    		    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
    		    							data_point.add(d);
    	    							}
    								} catch (NoSuchMethodException e) {
    									System.err.println("Couldnt find ." + com.get(1) + "() as a callable method. Check the spelling?");
    									e.printStackTrace();
    								} catch (Exception e) {
    									e.printStackTrace();
    								}
    							}
    						} else {
    							System.out.println("here (no grade match)");
    						}
        					if (!data_point.isEmpty()) {
        						list.add(data_point);
        					}
    					}
    					break;
    				}
    				default: {
    					System.err.println("hitting default case. thats a problem... Type is: " + data_type);
    					break;
    				}
    			}
    		}
    	}
    	if (ref_type.equals("School")) {
    		for (String rid : refid) {
    			switch (data_type) {
    				case "Lea": {
    					for (XLeaType var : xPress.getXLeasByXSchool(rid).getData()) {
    						ArrayList<DataType> data_point = new ArrayList<DataType>();
        					for (ArrayList<String> com : commands) {
    							Method m;
    							try {
    								if (com.get(1).split(" ").length > 1) {
	    								String[] t = com.get(1).split(" ");
	    								if (t[0].equals("getAddress")) {
	    									m = XLeaType.class.getMethod(t[0]);
	    	    							Object t_obj = (m.invoke(var));
	    	    							m = XOrganizationAddressType.class.getMethod(t[1]);
	    	    							  	    							
	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
	    	    							data_point.add(d);
	    								} else if (t[0].equals("getName")) {
	    									m = XLeaType.class.getMethod(t[0]);
	    	    							Object t_obj = (m.invoke(var));
	    	    							m = XPersonNameType.class.getMethod(t[1]);
	    	    							
	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
	    	    							data_point.add(d);
	    								} else if (t[0].equals("getEmail")) {
	    									m = XLeaType.class.getMethod(t[0]);
	    	    							Object t_obj = (m.invoke(var));
	    	    							m = XEmailType.class.getMethod(t[1]);
	    	    							
	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
	    	    							data_point.add(d);
	    								} else {
	    									System.err.println("Couldnt recognize " + t[0]);
	    								}
	    							} else {
		    							m = XLeaType.class.getMethod(com.get(1));
		    							 
		    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
		    							data_point.add(d);
	    							}
    							} catch (NoSuchMethodException e) {
    								System.err.println("Couldnt find ." + com.get(1) + "() as a callable method. Check the spelling?");
    								e.printStackTrace();
    							} catch (Exception e) {
    								e.printStackTrace();
    							}
    						}
        					if (!data_point.isEmpty()) {
        						list.add(data_point);
        					}
    					}
    					break;
    				}
    				case "School": {
    					ArrayList<DataType> data_point = new ArrayList<DataType>();
    					XSchoolType var = xPress.getXSchool(rid).getData();
    					boolean grade_match = false;
    					if (grade_nums != null) { // TODO Still need to test to make sure this grade matches correctly
    						for (String desired_grade : grade_nums) { // nested for loops to check if the given grades fall into the grades this particular school offers
    							if (!grade_match) {
    								for (String given_grade : var.getGradeLevels().getGradeLevel()) {
    									if (desired_grade.equals(given_grade)) {
    										grade_match = true;
    										System.out.println("breaking");
    										break;
    									}
    								}
    							}
    						}
    					} else {
    						grade_match = true;
    					}
    					if (grade_match) {
    						for (ArrayList<String> com : commands) {
    							Method m;
    							try {
    								if (com.get(1).split(" ").length > 1) {
	    								String[] t = com.get(1).split(" ");
	    								if (t[0].equals("getAddress")) {
	    									m = XSchoolType.class.getMethod(t[0]);
	    	    							Object t_obj = (m.invoke(var));
	    	    							m = XOrganizationAddressType.class.getMethod(t[1]);
	    	    							  	    							
	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
	    	    							data_point.add(d);
	    								} else if (t[0].equals("getName")) {
	    									m = XSchoolType.class.getMethod(t[0]);
	    	    							Object t_obj = (m.invoke(var));
	    	    							m = XPersonNameType.class.getMethod(t[1]);
	    	    							
	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
	    	    							data_point.add(d);
	    								} else if (t[0].equals("getEmail")) {
	    									m = XSchoolType.class.getMethod(t[0]);
	    	    							Object t_obj = (m.invoke(var));
	    	    							m = XEmailType.class.getMethod(t[1]);
	    	    							
	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
	    	    							data_point.add(d);
	    								} else {
	    									System.err.println("Couldnt recognize " + t[0]);
	    								}
	    							} else {
		    							m = XSchoolType.class.getMethod(com.get(1));
		    							 
		    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
		    							data_point.add(d);
	    							}
    							} catch (NoSuchMethodException e) {
    								System.err.println("Couldnt find ." + com.get(1) + "() as a callable method. Check the spelling?");
    								e.printStackTrace();
    							} catch (Exception e) {
    								e.printStackTrace();
    							}
    						}
    					} else {
    						System.out.println("here (no grade match)");
    					}
    					if (!data_point.isEmpty()) {
    						list.add(data_point);
    					}
    					break;
    				}
    				case "Calendar": {
    					for (XCalendarType var : xPress.getXCalendarsByXSchool(rid).getData()) { // loop through all calendars in the district
    						ArrayList<DataType> data_point = new ArrayList<DataType>();
        					for (ArrayList<String> com : commands) {
    							Method m;
    							try {
    								if (com.get(1).split(" ").length > 1) {
	    								String[] t = com.get(1).split(" ");
	    								if (t[0].equals("getAddress")) {
	    									m = XCalendarType.class.getMethod(t[0]);
	    	    							Object t_obj = (m.invoke(var));
	    	    							m = XOrganizationAddressType.class.getMethod(t[1]);
	    	    							  	    							
	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
	    	    							data_point.add(d);
	    								} else if (t[0].equals("getName")) {
	    									m = XCalendarType.class.getMethod(t[0]);
	    	    							Object t_obj = (m.invoke(var));
	    	    							m = XPersonNameType.class.getMethod(t[1]);
	    	    							
	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
	    	    							data_point.add(d);
	    								} else if (t[0].equals("getEmail")) {
	    									m = XCalendarType.class.getMethod(t[0]);
	    	    							Object t_obj = (m.invoke(var));
	    	    							m = XEmailType.class.getMethod(t[1]);
	    	    							
	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
	    	    							data_point.add(d);
	    								} else {
	    									System.err.println("Couldnt recognize " + t[0]);
	    								}
	    							} else {
		    							m = XCalendarType.class.getMethod(com.get(1));
		    							 
		    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
		    							data_point.add(d);
	    							}
    							} catch (NoSuchMethodException e) {
    								System.err.println("Couldnt find ." + com.get(1) + "() as a callable method. Check the spelling?");
    								e.printStackTrace();
    							} catch (Exception e) {
    								e.printStackTrace();
    							}
    						}
        					if (!data_point.isEmpty()) {
        						list.add(data_point);
        					}
    					}
    					break;
    				}
    				case "Course": {
    					for (XCourseType var : xPress.getXCoursesByXSchool(rid).getData()) { // loop through all courses in the district
    						ArrayList<DataType> data_point = new ArrayList<DataType>();
        					boolean grade_match = false;
    						if (grade_nums != null) { // TODO Still need to test to make sure this grade matches correctly
    							for (String desired_grade : grade_nums) { // nested for loops to check if the given grades fall into the grades this particular school offers
    								if (!grade_match) {
    									for (String given_grade : var.getApplicableEducationLevels().getApplicableEducationLevel()) {
    										if (desired_grade.equals(given_grade)) {
    											grade_match = true;
    											System.out.println("breaking");
    											break;
    										}
    									}
    								}
    							}
    						} else {
    							grade_match = true;
    						}
    						if (grade_match) {
    							for (ArrayList<String> com : commands) {
    								Method m;
    								try {
    									if (com.get(1).split(" ").length > 1) {
    	    								String[] t = com.get(1).split(" ");
    	    								if (t[0].equals("getAddress")) {
    	    									m = XCourseType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XOrganizationAddressType.class.getMethod(t[1]);
    	    	    							  	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else if (t[0].equals("getName")) {
    	    									m = XCourseType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XPersonNameType.class.getMethod(t[1]);
    	    	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else if (t[0].equals("getEmail")) {
    	    									m = XCourseType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XEmailType.class.getMethod(t[1]);
    	    	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else {
    	    									System.err.println("Couldnt recognize " + t[0]);
    	    								}
    	    							} else {
    		    							m = XCourseType.class.getMethod(com.get(1));
    		    							 
    		    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
    		    							data_point.add(d);
    	    							}
    								} catch (NoSuchMethodException e) {
    									System.err.println("Couldnt find ." + com.get(1) + "() as a callable method. Check the spelling?");
    									e.printStackTrace();
    								} catch (Exception e) {
    									e.printStackTrace();
    								}
    							}
    						} else {
    							System.out.println("here (no grade match)");
    						}
        					if (!data_point.isEmpty()) {
        						list.add(data_point);
        					}
    					}
    					break;
    				}
    				case "Roster": {
    					for (XRosterType var : xPress.getXRostersByXSchool(rid).getData()) { // loop through all rosters in the district
    						ArrayList<DataType> data_point = new ArrayList<DataType>();
        					boolean grade_match = false;
    						if (grade_nums != null) { // TODO Still need to test to make sure this grade matches correctly
    							for (String desired_grade : grade_nums) { // nested for loops to check if the given grades fall into the grades this particular school offers
    								if (!grade_match) {
    									for (XCourseType temp : xPress.getXCoursesByXRoster(var.getRefId()).getData()) { // get the course associated with the roster (needed to pull grade level information
    										if (!grade_match) {
    											for (String given_grade : temp.getApplicableEducationLevels().getApplicableEducationLevel()) {
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
    						} else {
    							grade_match = true;
    						}
    						if (grade_match) {
    							for (ArrayList<String> com : commands) {
    								Method m;
    								try {
    									if (com.get(1).split(" ").length > 1) {
    	    								String[] t = com.get(1).split(" ");
    	    								if (t[0].equals("getAddress")) {
    	    									m = XRosterType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XOrganizationAddressType.class.getMethod(t[1]);
    	    	    							  	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else if (t[0].equals("getName")) {
    	    									m = XRosterType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XPersonNameType.class.getMethod(t[1]);
    	    	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else if (t[0].equals("getEmail")) {
    	    									m = XRosterType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XEmailType.class.getMethod(t[1]);
    	    	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else {
    	    									System.err.println("Couldnt recognize " + t[0]);
    	    								}
    	    							} else {
    		    							m = XRosterType.class.getMethod(com.get(1));
    		    							 
    		    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
    		    							data_point.add(d);
    	    							}
    								} catch (NoSuchMethodException e) {
    									System.err.println("Couldnt find ." + com.get(1) + "() as a callable method. Check the spelling?");
    									e.printStackTrace();
    								} catch (Exception e) {
    									e.printStackTrace();
    								}
    							}
    						} else {
    							System.out.println("here (no grade match)");
    						}
        					if (!data_point.isEmpty()) {
        						list.add(data_point);
        					}
    					}
    					break;
    				}
    				case "Staff": {
    					for (XStaffType var : xPress.getXStaffsByXSchool(rid).getData()) { // loop through all staffs in the district
    						ArrayList<DataType> data_point = new ArrayList<DataType>();
        					boolean grade_match = false;
    						if (grade_nums != null) { // TODO Still need to test to make sure this grade matches correctly
    							for (String desired_grade : grade_nums) { // nested for loops to check if the given grades fall into the grades this particular school offers
    								if (!grade_match) {
    									for (XRosterType roster : xPress.getXRostersByXStaff(var.getRefId()).getData()) {		
    										if (!grade_match) {
    											for (XCourseType temp : xPress.getXCoursesByXRoster(roster.getRefId()).getData()) { // get the course associated with the roster (needed to pull grade level information
    												if (!grade_match) {
    													for (String given_grade : temp.getApplicableEducationLevels().getApplicableEducationLevel()) {
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
    							}
    						} else {
    							grade_match = true;
    						}
    						if (grade_match) {
    							for (ArrayList<String> com : commands) {
    								Method m;
    								try {
    									if (com.get(1).split(" ").length > 1) {
    	    								String[] t = com.get(1).split(" ");
    	    								if (t[0].equals("getAddress")) {
    	    									m = XStaffType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XOrganizationAddressType.class.getMethod(t[1]);
    	    	    							  	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else if (t[0].equals("getName")) {
    	    									m = XStaffType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XPersonNameType.class.getMethod(t[1]);
    	    	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else if (t[0].equals("getEmail")) {
    	    									m = XStaffType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XEmailType.class.getMethod(t[1]);
    	    	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else {
    	    									System.err.println("Couldnt recognize " + t[0]);
    	    								}
    	    							} else {
    		    							m = XStaffType.class.getMethod(com.get(1));
    		    							 
    		    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
    		    							data_point.add(d);
    	    							}
    								} catch (NoSuchMethodException e) {
    									System.err.println("Couldnt find ." + com.get(1) + "() as a callable method. Check the spelling?");
    									e.printStackTrace();
    								} catch (Exception e) {
    									e.printStackTrace();
    								}
    							}
    						} else {
    							System.out.println("here (no grade match)");
    						}
        					if (!data_point.isEmpty()) {
        						list.add(data_point);
        					}
    					}
    					break;
    				}
    				case "Student": {
    					for (XStudentType var : xPress.getXStudentsByXSchool(rid).getData()) { // loop through all students in the district
    						ArrayList<DataType> data_point = new ArrayList<DataType>();
        					boolean grade_match = false;
    						if (grade_nums != null) { // TODO Still need to test to make sure this grade matches correctly
    							for (String desired_grade : grade_nums) { // nested for loops to check if the given grades fall into the grades this particular school offers
    								if (!grade_match) {
    									for (XRosterType roster : xPress.getXRostersByXStudent(var.getRefId()).getData()) {		
    										if (!grade_match) {
    											for (XCourseType temp : xPress.getXCoursesByXRoster(roster.getRefId()).getData()) { // get the course associated with the roster (needed to pull grade level information
    												if (!grade_match) {
    													for (String given_grade : temp.getApplicableEducationLevels().getApplicableEducationLevel()) {
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
    							}
    						} else {
    							grade_match = true;
    						}
    						if (grade_match) {
    							for (ArrayList<String> com : commands) {
    								Method m;
    								try {
    									if (com.get(1).split(" ").length > 1) {
    	    								String[] t = com.get(1).split(" ");
    	    								if (t[0].equals("getAddress")) {
    	    									m = XStudentType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XOrganizationAddressType.class.getMethod(t[1]);
    	    	    							  	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else if (t[0].equals("getName")) {
    	    									m = XStudentType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XPersonNameType.class.getMethod(t[1]);
    	    	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else if (t[0].equals("getEmail")) {
    	    									m = XStudentType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XEmailType.class.getMethod(t[1]);
    	    	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else {
    	    									System.err.println("Couldnt recognize " + t[0]);
    	    								}
    	    							} else {
    		    							m = XStudentType.class.getMethod(com.get(1));
    		    							 
    		    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
    		    							data_point.add(d);
    	    							}
    								} catch (NoSuchMethodException e) {
    									System.err.println("Couldnt find ." + com.get(1) + "() as a callable method. Check the spelling?");
    									e.printStackTrace();
    								} catch (Exception e) {
    									e.printStackTrace();
    								}
    							}
    						} else {
    							System.out.println("here (no grade match)");
    						}
        					if (!data_point.isEmpty()) {
        						list.add(data_point);
        					}
    					}
    					break;
    				}
    				case "Contact": {
    					for (XContactType var : xPress.getXContactsByXSchool(rid).getData()) { // loop through all contacts in the district
    						ArrayList<DataType> data_point = new ArrayList<DataType>();
        					boolean grade_match = false;
    						if (grade_nums != null) { // TODO Still need to test to make sure this grade matches correctly
    							for (String desired_grade : grade_nums) { // nested for loops to check if the given grades fall into the grades this particular school offers
    								if (!grade_match) {
    									for (XStudentType student : xPress.getXStudentsByXContact(var.getRefId()).getData()) {										
    										if (!grade_match) {
    											for (XRosterType roster : xPress.getXRostersByXStudent(student.getRefId()).getData()) {		
    												if (!grade_match) {
    													for (XCourseType temp : xPress.getXCoursesByXRoster(roster.getRefId()).getData()) { // get the course associated with the roster (needed to pull grade level information
    														if (!grade_match) {
    															for (String given_grade : temp.getApplicableEducationLevels().getApplicableEducationLevel()) {
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
    									}
    								}
    							}
    						} else {
    							grade_match = true;
    						}
    						if (grade_match) {
    							for (ArrayList<String> com : commands) {
    								Method m;
    								try {
    									if (com.get(1).split(" ").length > 1) {
    	    								String[] t = com.get(1).split(" ");
    	    								if (t[0].equals("getAddress")) {
    	    									m = XStudentType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XOrganizationAddressType.class.getMethod(t[1]);
    	    	    							  	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else if (t[0].equals("getName")) {
    	    									m = XStudentType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XPersonNameType.class.getMethod(t[1]);
    	    	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else if (t[0].equals("getEmail")) {
    	    									m = XStudentType.class.getMethod(t[0]);
    	    	    							Object t_obj = (m.invoke(var));
    	    	    							m = XEmailType.class.getMethod(t[1]);
    	    	    							
    	    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    	    							data_point.add(d);
    	    								} else {
    	    									System.err.println("Couldnt recognize " + t[0]);
    	    								}
    	    							} else {
    		    							m = XStudentType.class.getMethod(com.get(1));
    		    							 
    		    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
    		    							data_point.add(d);
    	    							}
    								} catch (NoSuchMethodException e) {
    									System.err.println("Couldnt find ." + com.get(1) + "() as a callable method. Check the spelling?");
    									e.printStackTrace();
    								} catch (Exception e) {
    									e.printStackTrace();
    								}
    							}
    						} else {
    							System.out.println("here (no grade match)");
    						}
        					if (!data_point.isEmpty()) {
        						list.add(data_point);
        					}
    					}
    					break;
    				}
    				default: {
    					System.err.println("hitting default case. thats a problem... Type is: " + data_type);
    					break;
    				}
    			}
    		}
    	}
    	return list;
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
}