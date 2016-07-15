/*////////////////////////////////////////////////
 * Created By: Shamus Cardon
 * Date Created: 7/14/2016
 * Version: 1.0.0
 * Updated: 7/15/2016
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
    
    
    public Data ReadIn(Authenticator auth, String xPressType, List<ArrayList<String>> command_list, String file_name) { // xPressType should be "Lea","School","Student","Staff",etc.
		List<TreeMap<String, String>> list = new ArrayList<TreeMap<String,String>>();
		for(Endpoint e : auth.getEndpoints()) {
			XPress xPress = new XPress(auth.getToken(), e.getHref());
			list.addAll(DataRead(xPressType, xPress,command_list));
			System.out.println(list);
			for (Map<String,String> i : list) {
				System.out.println(i);
			}
			//XLeas_GetXLeas(xPress);
		}
		Data d = new Data(list,file_name);
		return d;
    }
	
	public ArrayList<String> Split(String string_to_split) {
		ArrayList<String> t_list = new ArrayList<String>();
		return t_list;
	}
	
	public List<TreeMap<String,String>> DataRead(String data_type, XPress xPress, List<ArrayList<String>> commands) {
		List<TreeMap<String,String>> list = new ArrayList<TreeMap<String,String>>();
		if (ref_type.equals("Lea")) {
			for (String rid : refid) {
				switch (data_type) {
					case "Lea": {
						TreeMap<String,String> map = new TreeMap<String,String>();
 						XLeaType lea = xPress.getXLea(rid).getData();
						for (ArrayList<String> com : commands) {
							Method m;
							try {
								m = XLeaType.class.getDeclaredMethod(com.get(1));
								System.out.println(m.invoke(lea));
								map.put(com.get(0),(String)m.invoke(lea)); // this line should associate the data pulled from the RICONE server with the user's function name
							} catch (NoSuchMethodException e) {
								System.err.println("Couldnt find ." + com + "() as a callable method. Check the spelling?");
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						list.add(map);
						break;
					}
					case "School": {
						TreeMap<String,String> map = new TreeMap<String,String>();
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
								for (ArrayList<String> com : commands) {
									Method m;
									try {
										m = XSchoolType.class.getDeclaredMethod(com.get(1));
										System.out.println(m.invoke(var));
										map.put(com.get(0),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
									} catch (NoSuchMethodException e) {
										System.err.println("Couldnt find ." + com + "() as a callable method. Check the spelling?");
										e.printStackTrace();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							} else {
								System.out.println("here (no grade match)");
							}
						}
						if (!map.isEmpty()) {
							list.add(map);
						}
						break;
					}
					case "Calendar": {
						TreeMap<String,String> map = new TreeMap<String,String>();
						for (XCalendarType var : xPress.getXCalendarsByXLea(rid).getData()) { // loop through all calendars in the district
							for (ArrayList<String> com : commands) {
								Method m;
								try {
									m = XCalendarType.class.getDeclaredMethod(com.get(1));
									System.out.println(m.invoke(var));
									map.put(com.get(0),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
								} catch (NoSuchMethodException e) {
									System.err.println("Couldnt find ." + com + "() as a callable method. Check the spelling?");
									e.printStackTrace();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
						if (!map.isEmpty()) {
							list.add(map);
						}
						break;
					}
					case "Course": {
						TreeMap<String,String> map = new TreeMap<String,String>();
						for (XCourseType var : xPress.getXCoursesByXLea(rid).getData()) { // loop through all courses in the district
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
										m = XCourseType.class.getDeclaredMethod(com.get(1));
										System.out.println(m.invoke(var));
										map.put(com.get(0),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
									} catch (NoSuchMethodException e) {
										System.err.println("Couldnt find ." + com + "() as a callable method. Check the spelling?");
										e.printStackTrace();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							} else {
								System.out.println("here (no grade match)");
							}
						}
						if (!map.isEmpty()) {
							list.add(map);
						}
						break;
					}
					case "Roster": {
						TreeMap<String,String> map = new TreeMap<String,String>();
						for (XRosterType var : xPress.getXRostersByXLea(rid).getData()) { // loop through all rosters in the district
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
										m = XRosterType.class.getDeclaredMethod(com.get(1));
										System.out.println(m.invoke(var));
										map.put(com.get(0),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
									} catch (NoSuchMethodException e) {
										System.err.println("Couldnt find ." + com + "() as a callable method. Check the spelling?");
										e.printStackTrace();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							} else {
								System.out.println("here (no grade match)");
							}
						}
						if (!map.isEmpty()) {
							list.add(map);
						}
						break;
					}
					case "Staff": {
						TreeMap<String,String> map = new TreeMap<String,String>();
						for (XStaffType var : xPress.getXStaffsByXLea(rid).getData()) { // loop through all staffs in the district
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
										m = XStaffType.class.getDeclaredMethod(com.get(1));
										System.out.println(m.invoke(var));
										map.put(com.get(0),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
									} catch (NoSuchMethodException e) {
										System.err.println("Couldnt find ." + com + "() as a callable method. Check the spelling?");
										e.printStackTrace();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							} else {
								System.out.println("here (no grade match)");
							}
						}
						if (!map.isEmpty()) {
							list.add(map);
						}
						break;
					}
					case "Student": {
						TreeMap<String,String> map = new TreeMap<String,String>();
						for (XStudentType var : xPress.getXStudentsByXLea(rid).getData()) { // loop through all students in the district
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
										m = XStudentType.class.getDeclaredMethod(com.get(1));
										System.out.println(m.invoke(var));
										map.put(com.get(0),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
									} catch (NoSuchMethodException e) {
										System.err.println("Couldnt find ." + com + "() as a callable method. Check the spelling?");
										e.printStackTrace();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							} else {
								System.out.println("here (no grade match)");
							}
						}
						if (!map.isEmpty()) {
							list.add(map);
						}
						break;
					}
					case "Contact": {
						TreeMap<String,String> map = new TreeMap<String,String>();
						for (XContactType var : xPress.getXContactsByXLea(rid).getData()) { // loop through all contacts in the district
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
										m = XStudentType.class.getDeclaredMethod(com.get(1));
										System.out.println(m.invoke(var));
										map.put(com.get(0),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
									} catch (NoSuchMethodException e) {
										System.err.println("Couldnt find ." + com + "() as a callable method. Check the spelling?");
										e.printStackTrace();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							} else {
								System.out.println("here (no grade match)");
							}
						}
						if (!map.isEmpty()) {
							list.add(map);
						}
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
			for (String rid : refid) {
				switch (data_type) {
					case "Lea": {
						TreeMap<String,String> map = new TreeMap<String,String>();
 						for (XLeaType lea : xPress.getXLeasByXSchool(rid).getData()) {
							for (ArrayList<String> com : commands) {
								Method m;
								try {
									m = XLeaType.class.getDeclaredMethod(com.get(1));
									System.out.println(m.invoke(lea));
									map.put(com.get(0),(String)m.invoke(lea)); // this line should associate the data pulled from the RICONE server with the user's function name
								} catch (NoSuchMethodException e) {
									System.err.println("Couldnt find ." + com + "() as a callable method. Check the spelling?");
									e.printStackTrace();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
						list.add(map);
						break;
					}
					case "School": {
						TreeMap<String,String> map = new TreeMap<String,String>();
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
									m = XSchoolType.class.getDeclaredMethod(com.get(1));
									System.out.println(m.invoke(var));
									map.put(com.get(0),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
								} catch (NoSuchMethodException e) {
									System.err.println("Couldnt find ." + com + "() as a callable method. Check the spelling?");
									e.printStackTrace();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						} else {
							System.out.println("here (no grade match)");
						}
						if (!map.isEmpty()) {
							list.add(map);
						}
						break;
					}
					case "Calendar": {
						TreeMap<String,String> map = new TreeMap<String,String>();
						for (XCalendarType var : xPress.getXCalendarsByXSchool(rid).getData()) { // loop through all calendars in the district
							for (ArrayList<String> com : commands) {
								Method m;
								try {
									m = XCalendarType.class.getDeclaredMethod(com.get(1));
									System.out.println(m.invoke(var));
									map.put(com.get(0),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
								} catch (NoSuchMethodException e) {
									System.err.println("Couldnt find ." + com + "() as a callable method. Check the spelling?");
									e.printStackTrace();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
						if (!map.isEmpty()) {
							list.add(map);
						}
						break;
					}
					case "Course": {
						TreeMap<String,String> map = new TreeMap<String,String>();
						for (XCourseType var : xPress.getXCoursesByXSchool(rid).getData()) { // loop through all courses in the district
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
										m = XCourseType.class.getDeclaredMethod(com.get(1));
										System.out.println(m.invoke(var));
										map.put(com.get(0),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
									} catch (NoSuchMethodException e) {
										System.err.println("Couldnt find ." + com + "() as a callable method. Check the spelling?");
										e.printStackTrace();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							} else {
								System.out.println("here (no grade match)");
							}
						}
						if (!map.isEmpty()) {
							list.add(map);
						}
						break;
					}
					case "Roster": {
						TreeMap<String,String> map = new TreeMap<String,String>();
						for (XRosterType var : xPress.getXRostersByXSchool(rid).getData()) { // loop through all rosters in the district
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
										m = XRosterType.class.getDeclaredMethod(com.get(1));
										System.out.println(m.invoke(var));
										map.put(com.get(0),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
									} catch (NoSuchMethodException e) {
										System.err.println("Couldnt find ." + com + "() as a callable method. Check the spelling?");
										e.printStackTrace();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							} else {
								System.out.println("here (no grade match)");
							}
						}
						if (!map.isEmpty()) {
							list.add(map);
						}
						break;
					}
					case "Staff": {
						TreeMap<String,String> map = new TreeMap<String,String>();
						for (XStaffType var : xPress.getXStaffsByXSchool(rid).getData()) { // loop through all staffs in the district
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
										m = XStaffType.class.getDeclaredMethod(com.get(1));
										System.out.println(m.invoke(var));
										map.put(com.get(0),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
									} catch (NoSuchMethodException e) {
										System.err.println("Couldnt find ." + com + "() as a callable method. Check the spelling?");
										e.printStackTrace();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							} else {
								System.out.println("here (no grade match)");
							}
						}
						if (!map.isEmpty()) {
							list.add(map);
						}
						break;
					}
					case "Student": {
						TreeMap<String,String> map = new TreeMap<String,String>();
						for (XStudentType var : xPress.getXStudentsByXSchool(rid).getData()) { // loop through all students in the district
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
										m = XStudentType.class.getDeclaredMethod(com.get(1));
										System.out.println(m.invoke(var));
										map.put(com.get(0),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
									} catch (NoSuchMethodException e) {
										System.err.println("Couldnt find ." + com + "() as a callable method. Check the spelling?");
										e.printStackTrace();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							} else {
								System.out.println("here (no grade match)");
							}
						}
						if (!map.isEmpty()) {
							list.add(map);
						}
						break;
					}
					case "Contact": {
						TreeMap<String,String> map = new TreeMap<String,String>();
						for (XContactType var : xPress.getXContactsByXSchool(rid).getData()) { // loop through all contacts in the district
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
										m = XStudentType.class.getDeclaredMethod(com.get(1));
										System.out.println(m.invoke(var));
										map.put(com.get(0),(String)m.invoke(var)); // this line should associate the data pulled from the RICONE server with the user's function name
									} catch (NoSuchMethodException e) {
										System.err.println("Couldnt find ." + com + "() as a callable method. Check the spelling?");
										e.printStackTrace();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							} else {
								System.out.println("here (no grade match)");
							}
						}
						if (!map.isEmpty()) {
							list.add(map);
						}
						break;
					}
					default: {
						System.err.println("hitting default case. thats a problem...");
						break;
					}
				}
			}
		}
		return list;
	}
}
