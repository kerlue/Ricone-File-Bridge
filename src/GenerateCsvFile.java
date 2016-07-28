import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.json.JSONObject;

public class GenerateCsvFile{
/*
public GenerateCsvFile(Configuration config, File file, GetDataFromApiTest data) {
		
		GlobalUtilities.logInfo("Generating file...");
		
		try {
			
			for(int i=0; i < config.getTextTitle().size(); i++){
				//#######  Create empty files ########//
				FileWriter writer = new FileWriter(file+File.separator+config.getTextTitle()
						.get(i)+"."+config.getOutputSchema());
				
				
				//#######  Create column names for each files ########//
				String columnNameStringList = config.getColumnNames().get(i);
				
				for(String columnName: columnNameStringList.split(",")){		
					System.out.println(columnName);
					writer.append(columnName);
				    writer.append(',');
				}
				writer.append('\n');
				
				
		        String requiredDataList = config.getRequiredData().get(i);
		        
				
				
				//#######  Save xLea data for each column ########//
				
				
				if(requiredDataList.toLowerCase().contains("xleas")){
					String[] fields = splitRequiredDataToList(requiredDataList); 
					
					try{
						for(int j=0; j < data.getLeaResult().length(); j++){
							JSONObject temp_data = new JSONObject(data.getLeaResult().get(j+"").toString());

							
							
							    for(String field: fields){
							    	writer.append(temp_data.getString(field.trim()).replace("null", ""));
									writer.append(',');
								 }
							    writer.append('\n');
							  } 
						
						   }catch(Exception e){
								 e.printStackTrace();
						   }
					
					} 
				
				//#######  Save xSchool data for each column ########//
				if(requiredDataList.toLowerCase().contains("xschool")){
                    String[] fields = splitRequiredDataToList(requiredDataList); 
					
					try{
						for(int j=0; j < data.getSchoolResult().length(); j++){
							JSONObject temp_data = new JSONObject(data.getSchoolResult().get(j+"").toString());

							    for(String field: fields){
							    	writer.append(temp_data.getString(field.trim()).replace("null", ""));
									writer.append(',');
								 }
							    writer.append('\n');
							  } 
						   }catch(Exception e){
								 e.printStackTrace();
						   }
					
					} 
				
				
				//#######  Save xRoster data for each column ########//
				if(requiredDataList.toLowerCase().contains("xroster")){
					String[] fields = splitRequiredDataToList(requiredDataList); 
					
					
					try{
						for(int j=0; j < data.getRosterResult().length(); j++){
							JSONObject temp_data = new JSONObject(data.getRosterResult().get(j+"").toString());
							  
							if(requiredDataList.toString().contains("students refId")){
								//#######  Config requires all students for roster ########//
								
								 String studentsId = temp_data.getString("students refId");								 
								 
								 for(String studentId: studentsId.split(",")){
									 									 
									 for(String field: fields){
										 if(field.contains("students refId")){
											 writer.append(studentId);
										 }else{
											 writer.append(temp_data.getString(field.trim()).replace("null", ""));
										 }
										 writer.append(',');									 
									 }
									 writer.append('\n');
								 }
								 
							}else{
								for(String field: fields){
									
									writer.append(temp_data.getString(field.trim()).replace("null", ""));
									writer.append(',');										
								 }
								 writer.append('\n');
							  }	
							}
							 
						   }catch(Exception e){
							   e.printStackTrace();
						   }
					} 
				 
				//#######  Save staff data for each column ########//
				
				if(requiredDataList.toLowerCase().contains("xstaff")){
                    String[] fields = splitRequiredDataToList(requiredDataList); 
                    
					try{
						for(int j=0; j < data.getStaffResult().length(); j++){
							JSONObject temp_data = new JSONObject(data.getStaffResult().get(j+"").toString());
                            
							    for(String field: fields){
							    	writer.append(temp_data.getString(field.trim()).replace("null", ""));
							    	
									writer.append(',');
								 }
							    writer.append('\n');
							  } 
						   }catch(Exception e){
								 e.printStackTrace();
						   }
					
					} 
				
				//#######  Save student data for each column ########//
				if(requiredDataList.toLowerCase().contains("xstudent")){
                    String[] fields = splitRequiredDataToList(requiredDataList); 
                    
					try{
						for(int j=0; j < data.getStudentResult().length(); j++){
							JSONObject temp_data = new JSONObject(data.getStudentResult().get(j+"").toString());
                            
							    for(String field: fields){
							    	writer.append(temp_data.getString(field.trim()).replace("null", ""));						    	
									writer.append(',');
								 }
							    writer.append('\n');
							  } 
						   }catch(Exception e){
								 e.printStackTrace();
						   }
					
					} 
			
				writer.append('\n');				
				writer.flush();
			    writer.close();

			}	
		} catch (IOException e) {
			e.printStackTrace();
			GlobalUtilities.logError(""+e.getMessage());
		}
		
		GlobalUtilities.logInfo("Files generated successfully..");
		// TODO Auto-generated method stub
		
	
	}
*/

public GenerateCsvFile(File file, ArrayList<Data> data_list, Configuration config) {
	
	
	GlobalUtilities.logInfo("Generating file...");
	try {
		for (Data data : data_list) {
			//#######  Create empty files ########//
			FileWriter writer = new FileWriter(file+File.separator+data.getFileName()+"." + config.getOutputSchema());
			ArrayList<String> fields = new ArrayList<String>();			
			//#######  Create column names for each files ########//			
//			for(String columnName: data.getColumnNames()){					
//				writer.append(columnName);
//			    writer.append(',');
//			    fields.add(columnName);
//			}
			
			List<String> columnNameStringList = config.getColumnNames();
			
			String columnNameString = "";
			
			for (int i=0; i < columnNameStringList.size(); ++i) {
				if (config.getTextTitle().get(i).equals(data.getFileName())) {
					columnNameString = columnNameStringList.get(i);
				}
			}
			for(String columnName: columnNameString.split(",")){					
				writer.append(columnName);
			    writer.append(',');
			    fields.add(columnName);
			}
			writer.append('\n');
			
			System.out.println("fields " + fields);
			writer.append('\n');
			
			
			try {
				for (TreeMap<String,Object> m : data.getData()) {
					for(String field: fields){
						try {
							//writer.append(m.get(field));
							writer.append(String.valueOf(m.get(field)).replace("null", ""));
							writer.append(',');
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					writer.append('\n');
				} 					
			} catch(Exception e){
				e.printStackTrace();
			}
		        
			writer.append('\n');				
			writer.flush();
			writer.close();			
		}
	} catch (IOException e) {
		e.printStackTrace();
		GlobalUtilities.logError(""+e.getMessage());
	}
		
	GlobalUtilities.logInfo("Files generated successfully..");
	// TODO Auto-generated method stub
	

}


	private String[] splitRequiredDataToList(String requiredDataList) {
		
		String[] data = requiredDataList.trim()
				.replaceAll("x[lL]eas", "")
				.replaceAll("x[sS]chool", "")
				.replaceAll("x[rR]oster", "")
				.replaceAll("x[sS]taff", "")
				.replaceAll("x[sS]tudent", "")
				.split(",");
		
		return data;
	}

}
