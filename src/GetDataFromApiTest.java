import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import riconeapi.common.Authenticator;
import riconeapi.common.XPress;
import riconeapi.models.authentication.Endpoint;
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
import riconeapi.models.xpress.XStaffPersonAssignmentType;
import riconeapi.models.xpress.XStaffReferenceType;
import riconeapi.models.xpress.XStaffType;
import riconeapi.models.xpress.XStudentType;
import riconeapi.models.xpress.XTelephoneType;

public class GetDataFromApiTest {
	

		static String refId = null;
		// These objects will store the results from the api into Json format.
		private static JSONObject schoolObj  = new JSONObject();
		private static JSONObject studentObj = new JSONObject();
		private static JSONObject rosterObj  = new JSONObject();
		private static JSONObject contactsObj= new JSONObject();
		private static JSONObject coursessObj= new JSONObject();
		private static JSONObject staffObj   = new JSONObject();
		private static JSONObject leaObj     = new JSONObject();
		
		// counter variables that will assign to  
		static int schoolObjCounter;
		static int studentObjCounter;
		static int rosterObjCounter;
		static int contactsObjCounter;
		static int coursessObjCounter;
		static int staffObjCounter;
		static int leaObjCounter;
		private static Configuration config;
		
		public GetDataFromApiTest(Configuration config) {
			this.config = config;
			
			Authenticator auth = new Authenticator(Authorizer.getURL(), Authorizer.getID(), Authorizer.getSecret()+"");
			
			for (Endpoint e : auth.getEndpoints())
			{
				XPress xPress = new XPress(auth.getToken(), e.getHref()+"");				
				getXLeas(xPress);				
			}
	    }

		private static void getXLeas(XPress xPress) {
			
			if(xPress.getXLeas().getData() != null)
			   
				for (XLeaType lea : xPress.getXLeas().getData())
				{						
					JSONObject tempObj = new JSONObject();					
					try {
						
						
						
						tempObj.put("refId" , lea.getRefId()+"");						
						tempObj.put("leaName" , lea.getLeaName()+"");
						tempObj.put("leaRefId" , lea.getLeaRefId()+"");
						tempObj.put("localId" , lea.getLocalId()+"");
						tempObj.put("ncesId" , lea.getNcesId()+"");
						tempObj.put("stateProvinceId" , lea.getStateProvinceId()+"");

						tempObj.put("addressType" , lea.getAddress().getAddressType()+"");
						tempObj.put("city" , lea.getAddress().getCity()+"");
						tempObj.put("line1" , lea.getAddress().getLine1()+"");
						tempObj.put("line2" , lea.getAddress().getLine2()+"");
						tempObj.put("countryCode" , lea.getAddress().getCountryCode()+"");
						tempObj.put("postalCode" , lea.getAddress().getPostalCode()+"");
						tempObj.put("stateProvince" , lea.getAddress().getStateProvince()+"");
						
						tempObj.put("number" , lea.getPhoneNumber().getNumber()+"");
						tempObj.put("phoneNumberType" , lea.getPhoneNumber().getPhoneNumberType()+"");
						tempObj.put("primaryIndicator" , lea.getPhoneNumber().isPrimaryIndicator()+"");
					
						for (XTelephoneType p : lea.getOtherPhoneNumbers().getPhoneNumber())
						{
							tempObj.put("other number" , p.getNumber()+"");
							tempObj.put("other phoneNumberType" , p.getPhoneNumberType()+"");
							tempObj.put("other primaryIndicator" , p.isPrimaryIndicator()+"");
						}
						
						leaObj.put(""+leaObjCounter++, tempObj );
						
						getXSchoolsByXLea(xPress, lea.getRefId()+"");
					
						
					} catch (JSONException e) {
						e.printStackTrace();
						
					}
				}
		}
		
		public static void getXSchoolsByXLea(XPress xPress, String refId)
	    {
			
	    	if(xPress.getXSchoolsByXLea(refId).getData() != null)
	    	   
	    		for(XSchoolType school : xPress.getXSchoolsByXLea(refId).getData())
		        {
	    			JSONObject tempObj = new JSONObject();
		        	try {
		        		
						tempObj.put("refId" , school.getRefId()+"");
						tempObj.put("leaRefId" , school.getLeaRefId()+"");
			            tempObj.put("localId" , school.getLocalId()+"");
			            tempObj.put("stateProvinceId" , school.getStateProvinceId()+"");
			           
			            for(XOtherOrganizationIdType id : school.getOtherIds().getOtherId())
			            {
			                tempObj.put("otherId id" , id.getId()+"");
			                tempObj.put("type" , id.getType()+"");
			            }
			      
			            tempObj.put("schoolName" , school.getSchoolName()+"");
			         
			            for(String gl : school.getGradeLevels().getGradeLevel())
			            {
			                tempObj.put("gradeLevel" , gl);
			            }
			          
			            tempObj.put("addressType" , school.getAddress().getAddressType()+"");
			            tempObj.put("city" , school.getAddress().getCity()+"");
			            tempObj.put("line1" , school.getAddress().getLine1()+"");
			            tempObj.put("line2" , school.getAddress().getLine2()+"");
			            tempObj.put("countryCode" , school.getAddress().getCountryCode()+"");
			            tempObj.put("postalCode" , school.getAddress().getPostalCode()+"");
			            tempObj.put("stateProvince" , school.getAddress().getStateProvince()+"");
			        
			            tempObj.put("number" , school.getPhoneNumber().getNumber()+"");
			            tempObj.put("phoneNumberType" , school.getPhoneNumber().getPhoneNumberType()+"");
			            tempObj.put("primaryIndicator" , school.getPhoneNumber().isPrimaryIndicator()+"");
			       
			            for(XTelephoneType p : school.getOtherPhoneNumbers().getPhoneNumber())
			            {
			                tempObj.put("otherPhoneNumbers number" , p.getNumber()+"");
			                tempObj.put("phoneNumberType" , p.getPhoneNumberType()+"");
			                tempObj.put("primaryIndicator" , p.isPrimaryIndicator()+"");
			            }
			            
			          //#######  Check filter options ########//
						if(config.getFilterList() != null){
							
							for(String str : config.getFilterList().split(",")){

								Pattern MY_PATTERN = Pattern.compile(".*");
								Matcher m = MY_PATTERN.matcher(".*");
								while (m.find()) {
								    String s = m.group(0);
								    System.out.println("--->>"+ s);
								}
								

								/*str = str.replace("=", "").replaceAll(" ", ",").trim();
								String[] obj = str.split(",");
								System.out.println("*******************"+str);
								if(obj[0].trim().toLowerCase().matches("xschool")){
									
									if(tempObj.getString(obj[1].trim()).toLowerCase().matches(obj[2].trim().toLowerCase())){
										
									}
								}*/
							
							}
						}else{
							
						}
			            
			            schoolObj.put(schoolObjCounter +"", tempObj );
			            schoolObjCounter++;
			            
			            getXStaffsByXSchool(xPress, school.getRefId()+"");
			            //getXStudentsByXSchool(xPress, school.getRefId()+"");
			            getXContactsByXSchool(xPress, school.getRefId()+"");
			            getXRostersByXSchool(xPress, school.getRefId()+"" );
				            
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		            
		        }  
	    	}
	    	
		public static void getXStaffsByXSchool(XPress xPress, String refId)
	    {
			
			
	    	if(xPress.getXStaffsByXSchool(refId).getData() != null)
	    	{
	    		for(XStaffType s : xPress.getXStaffsByXSchool(refId).getData())
	            {
	    			JSONObject tempObj = new JSONObject();
	    			
		        	try {
		        		tempObj.put("refId" , s.getRefId()+"");
		             
		                tempObj.put("type" , s.getName().getType()+"");
		                tempObj.put("prefix" , s.getName().getPrefix()+"");
		                tempObj.put("familyName" , s.getName().getFamilyName()+"");
		                tempObj.put("givenName" , s.getName().getGivenName()+"");
		                tempObj.put("middleName" , s.getName().getMiddleName()+"");
		                tempObj.put("suffix" , s.getName().getSuffix()+"");
		             
		                tempObj.put("localId" , s.getLocalId()+"");
		                tempObj.put("stateProvinceId" , s.getStateProvinceId()+"");
		          
		                for(XOtherPersonIdType id : s.getOtherIds().getOtherId())
		                {
		                    tempObj.put("id" , id.getId()+"");
		                    tempObj.put("type" , id.getType()+"");
		                }
		           
		                tempObj.put("sex" , s.getSex()+"");
		        
		                tempObj.put("emailType" , s.getEmail().getEmailType()+"");
		                tempObj.put("emailAddress" , s.getEmail().getEmailAddress()+"");
		           
		                tempObj.put("leaRefId" , s.getPrimaryAssignment().getLeaRefId()+"");
		                tempObj.put("schoolRefId" , s.getPrimaryAssignment().getSchoolRefId()+"");
		                tempObj.put("jobFunction" , s.getPrimaryAssignment().getJobFunction()+"");
		              
		                for(XStaffPersonAssignmentType pa : s.getOtherAssignments().getStaffPersonAssignment())
		                {
		                    tempObj.put("leaRefId" , pa.getLeaRefId()+"");
		                    tempObj.put("schoolRefId" , pa.getSchoolRefId()+"");
		                    tempObj.put("jobFunction" , pa.getJobFunction()+"");
		                }
		                
		                
		                staffObj.put(staffObjCounter +"", tempObj );
		                staffObjCounter++;
		                
		               
		        	}catch(Exception e){
		        		
		        	}
	            	
	            }
	    	}
	    }
		
	    public static void getXStudentsByXSchool(XPress xPress, String refId) 
	    {
	    	
	    	if(xPress.getXStudentsByXSchool(refId).getData() != null)
	    	{
	    		for(XStudentType s : xPress.getXStudentsByXSchool(refId).getData())
	            {
                    JSONObject tempObj = new JSONObject();
	    			
		        	try {
		        		tempObj.put("refId" , s.getRefId()+"");
		                 
		                tempObj.put("type" , s.getName().getType()+"");
		                tempObj.put("prefix" , s.getName().getPrefix()+"");
		                tempObj.put("familyName" , s.getName().getFamilyName()+"");
		                tempObj.put("givenName" , s.getName().getGivenName()+"");
		                tempObj.put("middleName" , s.getName().getMiddleName()+"");
		                tempObj.put("suffix" , s.getName().getSuffix()+"");
		               
		                
		                for(XPersonNameType n : s.getOtherNames().getName())
		                {
		                    tempObj.put("type" , n.getType()+"");
		                    tempObj.put("prefix" , n.getPrefix()+"");
		                    tempObj.put("familyName" , n.getFamilyName()+"");
		                    tempObj.put("givenName" , n.getGivenName()+"");
		                    tempObj.put("middleName" , n.getMiddleName()+"");
		                    tempObj.put("suffix" , n.getSuffix()+"");
		                }
		            
		                for(XOtherPersonIdType id : s.getOtherIds().getOtherId())
		                {
		                    tempObj.put("id" , id.getId()+"");
		                    tempObj.put("type" , id.getType()+"");
		                }
		                
		                tempObj.put("addressType" , s.getAddress().getAddressType()+"");
		                tempObj.put("city" , s.getAddress().getCity()+"");
		                tempObj.put("line1" , s.getAddress().getLine1()+"");
		                tempObj.put("line2" , s.getAddress().getLine2()+"");
		                tempObj.put("countryCode" , s.getAddress().getCountryCode()+"");
		                tempObj.put("postalCode" , s.getAddress().getPostalCode()+"");
		                tempObj.put("stateProvince" , s.getAddress().getStateProvince()+"");
		                tempObj.put("number" , s.getPhoneNumber().getNumber()+"");
		                tempObj.put("phoneNumberType" , s.getPhoneNumber().getPhoneNumberType()+"");
		                tempObj.put("primaryIndicator" , s.getPhoneNumber().isPrimaryIndicator()+"");
		                
		                tempObj.put("number" , s.getPhoneNumber().getNumber()+"");
		                tempObj.put("phoneNumberType" , s.getPhoneNumber().getPhoneNumberType()+"");
		                tempObj.put("primaryIndicator" , s.getPhoneNumber().isPrimaryIndicator()+"");
		              
		                
		                for(XTelephoneType p : s.getOtherPhoneNumbers().getPhoneNumber())
		                {
		                    tempObj.put("otherPhoneNumbers number" , p.getNumber()+"");
		                    tempObj.put("phoneNumberType" , p.getPhoneNumberType()+"");
		                    tempObj.put("primaryIndicator" , p.isPrimaryIndicator()+"");
		                }
		                
		               
		        
		                tempObj.put("emailType" , s.getEmail().getEmailType()+"");
		                tempObj.put("emailAddress" , s.getEmail().getEmailAddress()+"");
		              
		                
		                for(XEmailType e : s.getOtherEmails().getEmail())
		                {
		                    tempObj.put("emailType" , e.getEmailType()+"");
		                    tempObj.put("emailAddress" , e.getEmailAddress()+"");
		                }
		                
		              
		                for(XRaceType r : s.getDemographics().getRaces().getRace())
		                {
		                    tempObj.put("race" , r.getRace()+"");
		                }
		                
		                tempObj.put("hispanicLatinoEthnicity" , s.getDemographics().isHispanicLatinoEthnicity()+"");
		                tempObj.put("sex" , s.getDemographics().getSex()+"");
		                tempObj.put("birthDate" , s.getDemographics().getBirthDate()+"");
		                tempObj.put("countryOfBirth" , s.getDemographics().getCountryOfBirth()+"");
		                tempObj.put("usCitizenshipStatus" , s.getDemographics().getUsCitizenshipStatus()+"");
		                
		                tempObj.put("leaRefId" , s.getEnrollment().getLeaRefId()+"");
		                tempObj.put("schoolRefId" , s.getEnrollment().getSchoolRefId()+"");
		                tempObj.put("studentSchoolAssociationRefId" , s.getEnrollment().getStudentSchoolAssociationRefId()+"");
		                tempObj.put("responsibleSchoolType" , s.getEnrollment().getResponsibleSchoolType()+"");
		                tempObj.put("membershipType" , s.getEnrollment().getMembershipType()+"");
		                tempObj.put("entryDate" , s.getEnrollment().getEntryDate()+"");
		                tempObj.put("exitDate" , s.getEnrollment().getExitDate()+"");
		                tempObj.put("homeRoomNumber" , s.getEnrollment().getHomeRoomNumber()+"");
		              
		                tempObj.put("refId" , s.getEnrollment().getHomeRoomTeacher().getRefId()+"");
		                tempObj.put("localId" , s.getEnrollment().getHomeRoomTeacher().getLocalId()+"");
		                tempObj.put("givenName" , s.getEnrollment().getHomeRoomTeacher().getGivenName()+"");
		                tempObj.put("familyName" , s.getEnrollment().getHomeRoomTeacher().getFamilyName()+"");
		            
		                tempObj.put("gradeLevel" , s.getEnrollment().getGradeLevel()+"");
		                tempObj.put("projectedGraduationYear" , s.getEnrollment().getProjectedGraduationYear()+"");
		               
		                tempObj.put("refId" , s.getEnrollment().getCounselor().getRefId()+"");
		                tempObj.put("localId" , s.getEnrollment().getCounselor().getLocalId()+"");
		                tempObj.put("givenName" , s.getEnrollment().getCounselor().getGivenName()+"");
		                tempObj.put("familyName" , s.getEnrollment().getCounselor().getFamilyName()+"");
		               
		                
		                for(XEnrollmentType e : s.getOtherEnrollments().getEnrollment())
		                {  
		                    tempObj.put("leaRefId" , e.getLeaRefId()+"");
		                    tempObj.put("schoolRefId" , e.getSchoolRefId()+"");
		                    tempObj.put("studentSchoolAssociationRefId" , e.getStudentSchoolAssociationRefId()+"");
		                    tempObj.put("responsibleSchoolType" , e.getResponsibleSchoolType()+"");
		                    tempObj.put("membershipType" , e.getMembershipType()+"");
		                    tempObj.put("entryDate" , e.getEntryDate()+"");
		                    tempObj.put("exitDate" , e.getExitDate()+"");
		                    tempObj.put("homeRoomNumber" , e.getHomeRoomNumber()+"");
		                   
		                    tempObj.put("refId" , e.getHomeRoomTeacher().getRefId()+"");
		                    tempObj.put("localId" , e.getHomeRoomTeacher().getLocalId()+"");
		                    tempObj.put("givenName" , e.getHomeRoomTeacher().getGivenName()+"");
		                    tempObj.put("familyName" , e.getHomeRoomTeacher().getFamilyName()+"");
		                    
		                    tempObj.put("gradeLevel" , e.getGradeLevel()+"");
		                    tempObj.put("projectedGraduationYear" , e.getProjectedGraduationYear()+"");
		                   
		                    tempObj.put("refId" , e.getCounselor().getRefId()+"");
		                    tempObj.put("localId" , e.getCounselor().getLocalId()+"");
		                    tempObj.put("givenName" , e.getCounselor().getGivenName()+"");
		                    tempObj.put("familyName" , e.getCounselor().getFamilyName()+"");
		                  
		                }
		                
		                
		                tempObj.put("cumulativeWeightedGpa" , s.getAcademicSummary().getCumulativeWeightedGpa()+"");
		                tempObj.put("termWeightedGpa" , s.getAcademicSummary().getTermWeightedGpa()+"");
		                tempObj.put("classRank" , s.getAcademicSummary().getClassRank()+"");
		               
		                
		                for(String contactRefid : s.getStudentContacts().getContactPersonRefId())
		                {
		                    tempObj.put("contactPersonRefId" , contactRefid);
		                }
		                for(XContactType c : s.getStudentContacts().getXContact())
		                {
		            
		                    tempObj.put("type" , c.getName().getType()+"");
		                    tempObj.put("prefix" , c.getName().getPrefix()+"");
		                    tempObj.put("familyName" , c.getName().getFamilyName()+"");
		                    tempObj.put("givenName" , c.getName().getGivenName()+"");
		                    tempObj.put("middleName" , c.getName().getMiddleName()+"");
		                    tempObj.put("suffix" , c.getName().getSuffix()+"");
		                 
		                    for(XPersonNameType n : c.getOtherNames().getName())
		                    {
		                        tempObj.put("type" , n.getType()+"");
		                        tempObj.put("prefix" , n.getPrefix()+"");
		                        tempObj.put("familyName" , n.getFamilyName()+"");
		                        tempObj.put("givenName" , n.getGivenName()+"");
		                        tempObj.put("middleName" , n.getMiddleName()+"");
		                        tempObj.put("suffix" , n.getSuffix()+"");
		                    }
		                    tempObj.put("localId" , c.getLocalId()+"");
		                    for(XOtherPersonIdType id : c.getOtherIds().getOtherId())
		                    {
		                        tempObj.put("id" , id.getId()+"");
		                        tempObj.put("type" , id.getType()+"");
		                    }
		                 
		                    tempObj.put("addressType" , c.getAddress().getAddressType()+"");
		                    tempObj.put("city" , c.getAddress().getCity()+"");
		                    tempObj.put("line1" , c.getAddress().getLine1()+"");
		                    tempObj.put("line2" , c.getAddress().getLine2()+"");
		                    tempObj.put("countryCode" , c.getAddress().getCountryCode()+"");
		                    tempObj.put("postalCode" , c.getAddress().getPostalCode()+"");
		                    tempObj.put("stateProvince" , c.getAddress().getStateProvince()+"");
		                    tempObj.put("number" , c.getPhoneNumber().getNumber()+"");
		                    tempObj.put("phoneNumberType" , c.getPhoneNumber().getPhoneNumberType()+"");
		                    tempObj.put("primaryIndicator" , c.getPhoneNumber().isPrimaryIndicator()+"");
		             
		                    tempObj.put("number" , c.getPhoneNumber().getNumber()+"");
		                    tempObj.put("phoneNumberType" , c.getPhoneNumber().getPhoneNumberType()+"");
		                    tempObj.put("primaryIndicator" , c.getPhoneNumber().isPrimaryIndicator()+"");
		                    
		                    for(XTelephoneType p : c.getOtherPhoneNumbers().getPhoneNumber())
		                    {
		                        tempObj.put("otherPhoneNumbers number" , p.getNumber()+"");
		                        tempObj.put("phoneNumberType" , p.getPhoneNumberType()+"");
		                        tempObj.put("primaryIndicator" , p.isPrimaryIndicator()+"");
		                    }


		                    tempObj.put("emailType" , c.getEmail().getEmailType()+"");
		                    tempObj.put("emailAddress" , c.getEmail().getEmailAddress()+"");
		                 
		                    for(XEmailType e : c.getOtherEmails().getEmail())
		                    {
		                        tempObj.put("emailType" , e.getEmailType()+"");
		                        tempObj.put("emailAddress" , e.getEmailAddress()+"");
		                    }
		                    tempObj.put("" , c.getSex()+"");
		                    tempObj.put("" , c.getEmployerType()+"");
		                    for(XContactStudentRelationshipType csr : c.getRelationships().getRelationship())
		                    {
		                        tempObj.put("studentRefId" , csr.getStudentRefId()+"");
		                        tempObj.put("relationshipCode" , csr.getRelationshipCode()+"");
		                        tempObj.put("restrictions" , csr.getRestrictions()+"");
		                        tempObj.put("livesWith" , csr.isLivesWith()+"");
		                        tempObj.put("primaryContactIndicator" , csr.isPrimaryContactIndicator()+"");
		                        tempObj.put("emergencyContactIndicator" , csr.isEmergencyContactIndicator()+"");
		                        tempObj.put("financialResponsibilityIndicator" , csr.isFinancialResponsibilityIndicator()+"");
		                        tempObj.put("custodialIndicator" , csr.isCustodialIndicator()+"");
		                        tempObj.put("communicationsIndicator" , csr.isCommunicationsIndicator()+"");
		                        tempObj.put("contactSequence" , csr.getContactSequence()+"");
		                    }
		                }
		                for(XLanguageType l : s.getLanguages().getLanguage())
		                {
		                    tempObj.put("type" , l.getType()+"");
		                    tempObj.put("code" , l.getCode()+"");
		                }
		                
		                //System.out.println(x);
		                studentObj.put(studentObjCounter++ +"", tempObj );
		             
		        	}catch(Exception e){
		        		
		        	}
		        	
	            	
	            }
	    	}
	    }
	    
	    public static void getXRostersByXSchool(XPress xPress, String refId){
	    	
	    	if(xPress.getXRostersByXSchool(refId).getData() != null)
	    	{
				for (XRosterType r : xPress.getXRostersByXSchool(refId).getData())
				{

					JSONObject tempObj = new JSONObject();
	    			
		        	try {
		        		tempObj.put("refId" , r.getRefId()+"");
						tempObj.put("courseRefId" , r.getCourseRefId()+"");
						tempObj.put("courseTitle" , r.getCourseTitle()+"");
						tempObj.put("sectionRefId" , r.getSectionRefId()+"");
						tempObj.put("subject" , r.getSubject()+"");
						tempObj.put("schoolRefId" , r.getSchoolRefId()+"");
						tempObj.put("schoolSectionId" , r.getSchoolSectionId()+"");
						tempObj.put("schoolYear" , r.getSchoolYear()+"");
						tempObj.put("sessionCode" , r.getSessionCode()+"");
						tempObj.put("schoolCalendarRefId" , r.getSchoolCalendarRefId()+"");
						
						for (XMeetingTimeType mt : r.getMeetingTimes().getMeetingTime())
						{
							tempObj.put("timeTableDay" , mt.getTimeTableDay()+"");

							tempObj.put("bellScheduleDay" , mt.getClassMeetingDays().getBellScheduleDay()+"");
							tempObj.put("timeTablePeriod" , mt.getTimeTablePeriod()+"");
							tempObj.put("roomNumber" , mt.getRoomNumber()+"");
							tempObj.put("classBeginningTime" , mt.getClassBeginningTime()+"");
							tempObj.put("classEndingTime" , mt.getClassEndingTime()+"");
						}
						
						StringBuilder studentRefIds = new StringBuilder();
						for (XPersonReferenceType student : r.getStudents().getStudentReference())
						{
							studentRefIds.append(student.getRefId()+",");
							
							//tempObj.put("localId" , student.getLocalId()+"");
							//tempObj.put("givenName" , student.getGivenName()+"");
							//tempObj.put("familyName" , student.getFamilyName()+"");
						}
						tempObj.put("students refId" , studentRefIds.toString()+"");
						
						
						tempObj.put("primaryStaff refId" , r.getPrimaryStaff().getStaffPersonReference().getRefId()+"");
						tempObj.put("primaryStaff localId" , r.getPrimaryStaff().getStaffPersonReference().getLocalId()+"");
						tempObj.put("primaryStaff givenName" , r.getPrimaryStaff().getStaffPersonReference().getGivenName()+"");
						tempObj.put("primaryStaff familyName" , r.getPrimaryStaff().getStaffPersonReference().getFamilyName()+"");
						tempObj.put("primaryStaff teacherOfRecord" , r.getPrimaryStaff().isTeacherOfRecord()+"");
						tempObj.put("primaryStaff percentResponsible" , r.getPrimaryStaff().getPercentResponsible()+"");
					    
						
						if(r.getOtherStaffs().getOtherStaff().size()>0)
							for (XStaffReferenceType staff : r.getOtherStaffs().getOtherStaff())
							{							
								tempObj.put("otherStaff refId" , staff.getStaffPersonReference().getRefId()+"");
								tempObj.put("otherStaff localId" , staff.getStaffPersonReference().getLocalId()+"");
								tempObj.put("otherStaff givenName" , staff.getStaffPersonReference().getGivenName()+"");
								tempObj.put("otherStaff familyName" , staff.getStaffPersonReference().getFamilyName()+"");
								tempObj.put("otherStaff teacherOfRecord" , staff.isTeacherOfRecord()+"");
								tempObj.put("otherStaff percentResponsible" , staff.getPercentResponsible()+"");
								 
							}
						
						else{
							tempObj.put("otherStaff refId" , "null");
							tempObj.put("otherStaff localId" , "null");
							tempObj.put("otherStaff givenName" , "null");
							tempObj.put("otherStaff familyName" , "null");
							tempObj.put("otherStaff teacherOfRecord" , "null");
							tempObj.put("otherStaff percentResponsible" , "null");
						}

						rosterObj.put(rosterObjCounter++ +"", tempObj );
						
		        	}catch(Exception e){
		        		e.printStackTrace();
		        	}
				}
	    	}
	  
	    }

	    public static void getXContactsByXSchool(XPress xPress,String refId)
	    {
	    	
	    	if(xPress.getXContactsByXSchool(refId).getData() != null)
	    	{
	    		for(XContactType c : xPress.getXContactsByXSchool(refId).getData())
	            {
	    			JSONObject tempObj = new JSONObject();
	    			
	    			try {
	    				tempObj.put("refId" , c.getRefId()+"");
		                tempObj.put("type" , c.getName().getType()+"");
		                tempObj.put("prefix" , c.getName().getPrefix()+"");
		                tempObj.put("familyName" , c.getName().getFamilyName()+"");
		                tempObj.put("givenName" , c.getName().getGivenName()+"");
		                tempObj.put("middleName" , c.getName().getMiddleName()+"");
		                tempObj.put("suffix" , c.getName().getSuffix()+"");
		                for(XPersonNameType n : c.getOtherNames().getName())
		                {
		                    tempObj.put("type" , n.getType()+"");
		                    tempObj.put("prefix" , n.getPrefix()+"");
		                    tempObj.put("familyName" , n.getFamilyName()+"");
		                    tempObj.put("givenName" , n.getGivenName()+"");
		                    tempObj.put("middleName" , n.getMiddleName()+"");
		                    tempObj.put("suffix" , n.getSuffix()+"");
		                }

		                tempObj.put("localId" , c.getLocalId()+"");
		                for(XOtherPersonIdType id : c.getOtherIds().getOtherId())
		                {
		                    tempObj.put("id" , id.getId()+"");
		                    tempObj.put("type" , id.getType()+"");
		                }
		                tempObj.put("addressType" , c.getAddress().getAddressType()+"");
		                tempObj.put("city" , c.getAddress().getCity()+"");
		                tempObj.put("line1" , c.getAddress().getLine1()+"");
		                tempObj.put("line2" , c.getAddress().getLine2()+"");
		                tempObj.put("countryCode" , c.getAddress().getCountryCode()+"");
		                tempObj.put("postalCode" , c.getAddress().getPostalCode()+"");
		                tempObj.put("stateProvince" , c.getAddress().getStateProvince()+"");
		                tempObj.put("number" , c.getPhoneNumber().getNumber()+"");
		                tempObj.put("phoneNumberType" , c.getPhoneNumber().getPhoneNumberType()+"");
		                tempObj.put("primaryIndicator" , c.getPhoneNumber().isPrimaryIndicator()+"");
		                tempObj.put("number" , c.getPhoneNumber().getNumber()+"");
		                tempObj.put("phoneNumberType" , c.getPhoneNumber().getPhoneNumberType()+"");
		                tempObj.put("primaryIndicator" , c.getPhoneNumber().isPrimaryIndicator()+"");
		                for(XTelephoneType p : c.getOtherPhoneNumbers().getPhoneNumber())
		                {
		                    tempObj.put("otherPhoneNumbers number" , p.getNumber()+"");
		                    tempObj.put("phoneNumberType" , p.getPhoneNumberType()+"");
		                    tempObj.put("primaryIndicator" , p.isPrimaryIndicator()+"");
		                }
		                tempObj.put("emailType" , c.getEmail().getEmailType()+"");
		                tempObj.put("emailAddress" , c.getEmail().getEmailAddress()+"");
		                for(XEmailType e : c.getOtherEmails().getEmail())
		                {
		                    tempObj.put("emailType" , e.getEmailType()+"");
		                    tempObj.put("emailAddress" , e.getEmailAddress()+"");
		                }
		                tempObj.put("sex" , c.getSex()+"");
		                tempObj.put("employerType" , c.getEmployerType()+"");
		                for(XContactStudentRelationshipType csr : c.getRelationships().getRelationship())
		                {
		                    tempObj.put("studentRefId" , csr.getStudentRefId()+"");
		                    tempObj.put("relationshipCode" , csr.getRelationshipCode()+"");
		                    tempObj.put("restrictions" , csr.getRestrictions()+"");
		                    tempObj.put("livesWith" , csr.isLivesWith()+"");
		                    tempObj.put("primaryContactIndicator" , csr.isPrimaryContactIndicator()+"");
		                    tempObj.put("emergencyContactIndicator" , csr.isEmergencyContactIndicator()+"");
		                    tempObj.put("financialResponsibilityIndicator" , csr.isFinancialResponsibilityIndicator()+"");
		                    tempObj.put("custodialIndicator" , csr.isCustodialIndicator()+"");
		                    tempObj.put("communicationsIndicator" , csr.isCommunicationsIndicator()+"");
		                    tempObj.put("contactSequence" , csr.getContactSequence()+"");
		                }
		                
		                contactsObj.put(contactsObjCounter++ +"", tempObj );
		                
	    			}catch(Exception e){
	    				
	    			}
	            	
	            }
	    	}
	    }
	    
	    public static void getXCoursesByXSchool(XPress xPress,String refId)
	    {
	    	
	    	if(xPress.getXCoursesByXSchool(refId).getData() != null)
	    	{
	    		for (XCourseType course : xPress.getXCoursesByXSchool(refId).getData())
	            {
	    			
	    			JSONObject tempObj = new JSONObject();
	    	          
				    try {
				    	tempObj.put("refId" , course.getRefId()+"");
		                tempObj.put("schoolRefId" , course.getSchoolRefId()+"");
		                tempObj.put("schoolCourseId" , course.getSchoolCourseId()+"");
		                tempObj.put("leaCourseId" , course.getLeaCourseId()+"");
		    			
		                for(XOtherCourseIdType id : course.getOtherIds().getOtherId())
		                {
		                    tempObj.put("otherId id" , id.getId()+"");
		                    tempObj.put("type" , id.getType()+"");
		                }
		    			
		                tempObj.put("courseTitle" , course.getCourseTitle()+"");
		                tempObj.put("description" , course.getDescription()+"");
		                tempObj.put("subject" , course.getSubject()+"");
		    			
		                for(String ael : course.getApplicableEducationLevels().getApplicableEducationLevel())
		                {
		                    tempObj.put("applicableEducationLevel" , ael);
		                }
		    			
		                tempObj.put("scedCourseCode" , course.getScedCourseCode()+"");
		                tempObj.put("scedCourseLevelCode" , course.getScedCourseLevelCode()+"");
		                tempObj.put("scedCourseSubjectAreaCode" , course.getScedCourseSubjectAreaCode()+"");

		                coursessObj.put(coursessObjCounter++ +"", tempObj );
		                
				    }catch(Exception e){
				    	
				    }
	            }
	    	}
	    }
	    	    
		public JSONObject getSchoolResult() {
			return schoolObj;
		}
		public JSONObject getStudentResult() {
			return studentObj;
		}
		public JSONObject getRosterResult() {
			return rosterObj;
		}
		public JSONObject getContactsResult() {
			return contactsObj;
		}
		public JSONObject getCourcesResult() {
			return coursessObj;
		}
		public JSONObject getStaffResult() {
			return staffObj;
		}
		public JSONObject getLeaResult() {
			return leaObj;
		}
		
 
}
