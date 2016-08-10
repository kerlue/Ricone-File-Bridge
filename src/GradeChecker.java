/*///////////////////////////////////////////////
 * Created By: Shamus Cardon
 * Date Created: 8/10/2016
 * Version: 1.4.1
 * Updated: 8/10/2016
*////////////////////////////////////////////////


import java.util.List;

import riconeapi.common.XPress;
import riconeapi.models.xpress.XContactType;
import riconeapi.models.xpress.XCourseType;
import riconeapi.models.xpress.XRosterType;
import riconeapi.models.xpress.XSchoolType;
import riconeapi.models.xpress.XStaffType;
import riconeapi.models.xpress.XStudentType;

public class GradeChecker {

	public static <T> boolean GradeCheck(String data_type, T var, XPress xPress, String[] grade_nums) {
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
					/*List<XCourseType> dat;
					if ((dat = xPress.getXCoursesByXRoster(((XRosterType) var).getRefId()).getData()) != null) {
						for (XCourseType temp : dat) { // get the course associated with the roster (needed to pull grade level information
							if (GradeCheck("Course",temp,xPress,grade_nums)) {
								return true;
							}
						}
						
					} */
					
					List<XStudentType> dat;
					if ((dat = xPress.getXStudentsByXRoster(((XRosterType) var).getRefId()).getData()) != null) {
						for (XStudentType temp : dat) { // get the course associated with the roster (needed to pull grade level information
							if (GradeCheck("Student",temp,xPress,grade_nums)) {
								return true;
							}
						}
						
					}
				}
				break;
			}
			case "Staff": {
				if (grade_nums != null) { // 
					List<XRosterType> dat;
					if ((dat = xPress.getXRostersByXStaff(((XStaffType) var).getRefId()).getData()) != null) {    										
						for (XRosterType roster : dat) {		
							if (GradeCheck("Roster",roster,xPress,grade_nums)) {
								return true;
							}
						} 
					}
				}
				break;
			}
			case "Student": {
				if (grade_nums != null) { // 
					String given_grade = ((XStudentType) var).getEnrollment().getGradeLevel();
					for (String desired_grade : grade_nums) { // nested for loops to check if the given grades fall into the grades this particular school offers
						if (desired_grade.equals(given_grade)) {
							return true;
						}
					}
				}
				break;
			}
			case "Contact": {
				if (grade_nums != null) { // 
					for (XStudentType student : xPress.getXStudentsByXContact(((XContactType) var).getRefId()).getData()) {										
						if (GradeCheck("Student",student,xPress,grade_nums)) {
							return true;
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
}
