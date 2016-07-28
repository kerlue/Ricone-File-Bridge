/*////////////////////////////////////////////////
 * Created By: Shamus Cardon
 * Date Created: 7/14/2016
 * Version: 1.1.0
 * Updated: 7/26/2016
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
		
		
		String temp = "";
		String[] st = t_list2[0].trim().split(" ");
		for (int j=1; j < st.length; ++j) {
			if (st[j].equals("-g") && j < st.length-1) {
				++j;
				temp += st[j] + " ";
			} else if (st[j].equals("-andSTAFF")) {
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
			//XPress xPress = new XPress(auth.getToken(), e.getHref());
			XPress xPress = new XPress(auth.getToken(), "https://10.6.11.20/api/requests/");
			list.addAll(DataRead(xPress,data_type_list));
			//System.out.println(list);
			//XLeas_GetXLeas(xPress);
		}
		Data d = new Data(list,file_name);
		return d;
    }
    
    private <T> ArrayList<DataType> DRead(Class<T> clazz, ArrayList<ArrayList<String>> commands, T var, String data_type) {
    	ArrayList<DataType> data_point = new ArrayList<DataType>();
    	for (ArrayList<String> com : commands) {
			Method m;
			try {
				if (com.get(1).split(" ").length > 1) {
					String[] t = com.get(1).split(" ");
					if (t[0].equals("getAddress")) {
						m = clazz.getMethod(t[0]);
						Object t_obj = (m.invoke(var));
						m = XOrganizationAddressType.class.getMethod(t[1]);
						  	    							
						DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
						data_point.add(d);
					} else if (t[0].equals("getName")) {
						m = clazz.getMethod(t[0]);
						Object t_obj = (m.invoke(var));
						m = XPersonNameType.class.getMethod(t[1]);
						
						DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
						data_point.add(d);
					} else if (t[0].equals("getEmail")) {
						m = clazz.getMethod(t[0]);
						Object t_obj = (m.invoke(var));
						m = XEmailType.class.getMethod(t[1]);
						
						DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_obj)); // this line should associate the data pulled from the RICONE server with the user's function name
						data_point.add(d);
					} else {
						System.err.println("151 - Couldnt recognize " + t[0]);
					}
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
    
    private <T,E> ArrayList<DataType> DRead(Class<T> clazz, Class<E> clazz2, ArrayList<ArrayList<String>> commands, T var, E var2, String data_type) {
    	ArrayList<DataType> data_point = new ArrayList<DataType>();
    	for (ArrayList<String> com : commands) {
			Method m;
			try {
				if (com.get(1).split(" ").length > 1) {
					String[] t = com.get(1).split(" ");
					if (t[0].equals("-andSTAFF")) {
						String t_str = "";
						for (int j=1; j < t.length; ++j) {
							if (t[j].equals("getAddress")) {
								m = XRosterType.class.getMethod(t[j]);
    							Object t_obj = (m.invoke(var));
    							m = XOrganizationAddressType.class.getMethod(t[j+1]);
    							  	    							
    							t_str += m.invoke(t_obj);
							} else if (t[j].equals("getName")) {
								m = XRosterType.class.getMethod(t[j]);
    							Object t_obj = (m.invoke(var));
    							m = XPersonNameType.class.getMethod(t[j+1]);    							
    							t_str += m.invoke(t_obj);
    							
							} else if (t[j].equals("getEmail")) {
								m = XRosterType.class.getMethod(t[j]);
    							Object t_obj = (m.invoke(var));
    							m = XEmailType.class.getMethod(t[j+1]);
    							
    							t_str += m.invoke(t_obj);
							} else if (t[j].equals("getStaffPersonReference")) {
								//String str = (String)Object.class.getMethod(getRefId).invoke(Object.class.getMethod(getStaffPersonReference).invoke(Object.class.getMethod(getPrimaryStaff).invoke(var)));
								String str = (String)XPersonReferenceType.class.getMethod("getRefId").invoke(XStaffReferenceType.class.getMethod("getStaffPersonReference").invoke(clazz.getMethod("getPrimaryStaff").invoke(var)));
								String str2 = (String)clazz2.getMethod("getRefId").invoke(var2);
								if (str2.equals(str)) {
									XPersonReferenceType t_person = (XPersonReferenceType)XStaffReferenceType.class.getMethod("getStaffPersonReference").invoke(clazz.getMethod("getPrimaryStaff").invoke(var));
	    							m = XPersonReferenceType.class.getMethod(t[j+1]);
	    							
	    							t_str += m.invoke(t_person);
								} else {
									for (XStaffReferenceType st : (List<XStaffReferenceType>)XStaffReferenceListType.class.getMethod("getOtherStaff").invoke(clazz.getMethod("getOtherStaffs").invoke(var))) {
										if (str2.equals(st.getStaffPersonReference().getRefId())) {
											m = XPersonReferenceType.class.getMethod(t[j+1]);
	    	    																		
	    	    							t_str += (String)m.invoke(st);
											break;
										}
									}
								}
								
							} else {
								m = XRosterType.class.getMethod(t[j]);
								try {
									t_str += m.invoke(var);
								} catch (ClassCastException e) {
									try {
										t_str += m.invoke(var);
									} catch (Exception ex) {
										System.err.println("222 - Couldnt recognize " + t[j]);										
									}
								}
							}
							t_str += "_";
						}
						DataType d = new DataType(data_type,com.get(0),com.get(1),t_str.substring(0,t_str.length()-1)); // this line should associate the data pulled from the RICONE server with the user's function name
						data_point.add(d);
						
					} else {
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
						} else if (t[0].equals("getStaffPersonReference")) {
							String str = (String)XPersonReferenceType.class.getMethod("getRefId").invoke(XStaffReferenceType.class.getMethod("getStaffPersonReference").invoke(clazz.getMethod("getPrimaryStaff").invoke(var)));
							String str2 = (String)clazz2.getMethod("getRefId").invoke(var2);
							if (str2.equals(str)) {
								XPersonReferenceType t_person = (XPersonReferenceType)XStaffReferenceType.class.getMethod("getStaffPersonReference").invoke(clazz.getMethod("getPrimaryStaff").invoke(var));
    							m = XPersonReferenceType.class.getMethod(t[1]);
    							
    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_person)); // this line should associate the data pulled from the RICONE server with the user's function name
    							data_point.add(d);
							} else {
								for (XStaffReferenceType st : (List<XStaffReferenceType>)XStaffReferenceListType.class.getMethod("getOtherStaff").invoke(clazz.getMethod("getOtherStaffs").invoke(var))) { // TODO I dont think this actually works. Check for mismatch between staffpersonreferencetype and personreferencetype
									if (str2.equals(st.getStaffPersonReference().getRefId())) {
										m = XPersonReferenceType.class.getMethod(t[1]);
    	    																	
    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),(String)m.invoke(st)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    							data_point.add(d);
										break;
									}
								}
							}
							
						} else if (t[0].equals("-staff")) {
							String str = (String)XPersonReferenceType.class.getMethod("getRefId").invoke(XStaffReferenceType.class.getMethod("getStaffPersonReference").invoke(clazz.getMethod("getPrimaryStaff").invoke(var)));
							String str2 = (String)clazz2.getMethod("getRefId").invoke(var2);
							if (str2.equals(str)) {
								XStaffReferenceType t_person = (XStaffReferenceType)clazz.getMethod("getPrimaryStaff").invoke(var);
    							m = XStaffReferenceType.class.getMethod(t[1]);
    							
    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_person)); // this line should associate the data pulled from the RICONE server with the user's function name
    							data_point.add(d);
							} else {
								for (XStaffReferenceType st : (List<XStaffReferenceType>)XStaffReferenceListType.class.getMethod("getOtherStaff").invoke(clazz.getMethod("getOtherStaffs").invoke(var))) {
									if (str2.equals(st.getStaffPersonReference().getRefId())) {
										m = XStaffReferenceType.class.getMethod(t[1]);
    	    																	
    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(st)); // this line should associate the data pulled from the RICONE server with the user's function name
    	    							data_point.add(d);
										break;
									}
								}
							}
						} else {
							System.err.println("274 - Couldnt recognize " + t[0]);
						}
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
				System.err.println("297 - Couldnt find ." + com.get(1) + "() as a callable method. Check the spelling?");
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
    					XLeaType var = xPress.getXLea(rid).getData();
    					list.add(DRead(XLeaType.class, commands, var, data_type));
    					break;
    				}
    				case "School": {
    					if (xPress.getXSchoolsByXLea(rid).getData() != null) {
	    					for (XSchoolType var : xPress.getXSchoolsByXLea(rid).getData()) { // loop through all schools in the district
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
	    	    					list.add(DRead(XSchoolType.class, commands, var, data_type));
	    						} else {
	    							System.out.println("here (no grade match)");
	    						}
	    					}
    					}
    					break;
    				}
    				case "Calendar": {
    					for (XCalendarType var : xPress.getXCalendarsByXLea(rid).getData()) { // loop through all calendars in the district
        					list.add(DRead(XCalendarType.class, commands, var, data_type));
    					}
    					break;
    				}
    				case "Course": {
    					for (XCourseType var : xPress.getXCoursesByXLea(rid).getData()) { // loop through all courses in the district
        					boolean grade_match = false;
    						if (grade_nums != null) { // 
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

    	    					list.add(DRead(XCourseType.class, commands, var, data_type));
    						} else {
    							System.out.println("here (no grade match)");
    						}
    					}
    					break;
    				}
    				case "Roster": {
    					for (XRosterType var : xPress.getXRostersByXLea(rid).getData()) { // loop through all rosters in the district
        					boolean grade_match = false;
    						if (grade_nums != null) { // 
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
    						
    						if (commands.get(0).get(1).contains("-and")) {
    							if (grade_match) {
    								if (commands.get(0).get(1).contains("-andSTAFF")) {
    									if (xPress.getXStaffsByXRoster(var.getRefId()).getData() != null) {
		    								for (XStaffType staff : xPress.getXStaffsByXRoster(var.getRefId()).getData()) {
		    	    	    					list.add(DRead(XRosterType.class, XStaffType.class, commands, var, staff, data_type));
		    								}
    									}
    								}
    							}
    							/*
    							if (commands.get(0).get(1).contains("-andSTAFF")) {
	    							for (XStaffType staff : xPress.getXStaffsByXRoster(var.getRefId()).getData()) {
	    								ArrayList<DataType> data_point = new ArrayList<DataType>();
	    	    						if (grade_match) {
	    	    							//TODO here!!
	    	    							for (ArrayList<String> com : commands) {
	    	    								Method m;
	    	    								try {
	    	    									if (com.get(1).split(" ").length > 1) {
	    	    										String[] t = com.get(1).split(" ");
	    	    										if (t[0].equals("-andSTAFF")) {
	    	    											String t_str = "";
	    	    											for (int j=1; j < t.length; ++j) {
	    	    												if (t[j].equals("getAddress")) {
	    	    													m = XRosterType.class.getMethod(t[j]);
	    	    					    							Object t_obj = (m.invoke(var));
	    	    					    							m = XOrganizationAddressType.class.getMethod(t[j+1]);
	    	    					    							  	    							
	    	    					    							t_str += m.invoke(t_obj);
	    	    												} else if (t[j].equals("getName")) {
	    	    													m = XRosterType.class.getMethod(t[j]);
	    	    					    							Object t_obj = (m.invoke(var));
	    	    					    							m = XPersonNameType.class.getMethod(t[j+1]);
	    	    					    							
	    	    					    							t_str += m.invoke(t_obj);
	    	    												} else if (t[j].equals("getEmail")) {
	    	    													m = XRosterType.class.getMethod(t[j]);
	    	    					    							Object t_obj = (m.invoke(var));
	    	    					    							m = XEmailType.class.getMethod(t[j+1]);
	    	    					    							
	    	    					    							t_str += m.invoke(t_obj);
	    	    												} else if (t[j].equals("getStaffPersonReference")) {
	    	    								
	    	    													if (staff.getRefId().equals(var.getPrimaryStaff().getStaffPersonReference().getRefId())) {
	    	    														XPersonReferenceType t_person = var.getPrimaryStaff().getStaffPersonReference();
	    	    						    							m = XPersonReferenceType.class.getMethod(t[j+1]);
	    	    						    							
	    	    						    							t_str += m.invoke(t_person);
	    	    													} else {
	    	    														for (XStaffReferenceType st : var.getOtherStaffs().getOtherStaff()) {
	    	    															if (staff.getRefId().equals(st.getStaffPersonReference().getRefId())) {
	    	    																m = XPersonReferenceType.class.getMethod(t[j+1]);
	    	    						    	    							
	    	    																
	    	    						    	    							t_str += m.invoke(st);
	    	    																break;
	    	    															}
	    	    														}
	    	    													}
	    	    													
	    	    												} else {
	    	    													System.err.println("Couldnt recognize " + t[j]);
	    	    												}
	    	    												t_str += "_";
	    	    											}
	    	    											DataType d = new DataType(data_type,com.get(0),com.get(1),t_str.substring(0,t_str.length()-1)); // this line should associate the data pulled from the RICONE server with the user's function name
	    	    											data_point.add(d);
	    	    											
	    	    										} else {
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
	    	    											} else if (t[0].equals("getStaffPersonReference")) { 
	    	    							
	    	    												if (staff.getRefId().equals(var.getPrimaryStaff().getStaffPersonReference().getRefId())) {
	    	    													XPersonReferenceType t_person = var.getPrimaryStaff().getStaffPersonReference();
	    	    					    							m = XPersonReferenceType.class.getMethod(t[1]);
	    	    					    							
	    	    					    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(t_person)); // this line should associate the data pulled from the RICONE server with the user's function name
	    	    					    							data_point.add(d);
	    	    												} else {
	    	    													for (XStaffReferenceType st : var.getOtherStaffs().getOtherStaff()) {
	    	    														if (staff.getRefId().equals(st.getStaffPersonReference().getRefId())) {
	    	    															m = XPersonReferenceType.class.getMethod(t[1]);
	    	    					    	    							
	    	    					    	    							DataType d = new DataType(data_type,com.get(0),com.get(1),m.invoke(st)); // this line should associate the data pulled from the RICONE server with the user's function name
	    	    					    	    							data_point.add(d);
	    	    															break;
	    	    														}
	    	    													}
	    	    												}
	    	    												
	    	    											} else {
	    	    												System.err.println("Couldnt recognize " + t[0]);
	    	    											}
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
    							}*/
    						} else {    							
    	    					list.add(DRead(XRosterType.class, commands, var, data_type));
    						}
    						
    					}
    					break;
    				}
    				case "Staff": {
    					for (XStaffType var : xPress.getXStaffsByXLea(rid).getData()) { // loop through all staffs in the district
        					boolean grade_match = false;
    						if (grade_nums != null) { // 
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
    	    					list.add(DRead(XStaffType.class, commands, var, data_type));
    						} else {
    							System.out.println("here (no grade match)");
    						}
    					}
    					break;
    				}
    				case "Student": {
    					for (XStudentType var : xPress.getXStudentsByXLea(rid).getData()) { // loop through all students in the district
    						boolean grade_match = false;
    						if (grade_nums != null) { // 
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
    	    					list.add(DRead(XStudentType.class, commands, var, data_type));
    						} else {
    							System.out.println("here (no grade match)");
    						}
    					}
    					break;
    				}
    				case "Contact": {
    					for (XContactType var : xPress.getXContactsByXLea(rid).getData()) { // loop through all contacts in the district
    						boolean grade_match = false;
    						if (grade_nums != null) { // 
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
    	    					list.add(DRead(XContactType.class, commands, var, data_type));
    						} else {
    							System.out.println("here (no grade match)");
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
        					list.add(DRead(XLeaType.class, commands, var, data_type));
    					}
    					break;
    				}
    				case "School": {
    					XSchoolType var = xPress.getXSchool(rid).getData();
    					boolean grade_match = false;
    					if (grade_nums != null) { // 
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
        					list.add(DRead(XSchoolType.class, commands, var, data_type));
    					} else {
    						System.out.println("here (no grade match)");
    					}
    					break;
    				}
    				case "Calendar": {
    					for (XCalendarType var : xPress.getXCalendarsByXSchool(rid).getData()) { // loop through all calendars in the district
    						list.add(DRead(XCalendarType.class, commands, var, data_type));
    					}
    					break;
    				}
    				case "Course": {
    					for (XCourseType var : xPress.getXCoursesByXSchool(rid).getData()) { // loop through all courses in the district
    						boolean grade_match = false;
    						if (grade_nums != null) { // 
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
    							list.add(DRead(XCourseType.class, commands, var, data_type));
    						} else {
    							System.out.println("here (no grade match)");
    						}
    					}
    					break;
    				}
    				case "Roster": {
    					for (XRosterType var : xPress.getXRostersByXSchool(rid).getData()) { // loop through all rosters in the district
    						ArrayList<DataType> data_point = new ArrayList<DataType>();
        					boolean grade_match = false;
    						if (grade_nums != null) { // 
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
    						boolean grade_match = false;
    						if (grade_nums != null) { // 
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
    							list.add(DRead(XStaffType.class, commands, var, data_type));
    						} else {
    							System.out.println("here (no grade match)");
    						}
    					}
    					break;
    				}
    				case "Student": {
    					for (XStudentType var : xPress.getXStudentsByXSchool(rid).getData()) { // loop through all students in the district
    						boolean grade_match = false;
    						if (grade_nums != null) { // 
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
    							list.add(DRead(XStudentType.class, commands, var, data_type));
    						} else {
    							System.out.println("here (no grade match)");
    						}
    					}
    					break;
    				}
    				case "Contact": {
    					for (XContactType var : xPress.getXContactsByXSchool(rid).getData()) { // loop through all contacts in the district
    						boolean grade_match = false;
    						if (grade_nums != null) { // 
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
    							list.add(DRead(XContactType.class, commands, var, data_type));
    						} else {
    							System.out.println("here (no grade match)");
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
    }
