import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.json.JSONObject;

public class GenerateCsvFile{

public GenerateCsvFile(File file, ArrayList<Data> data_list, Configuration config) {
	
	
	GlobalUtilities.logInfo("Generating file...");
	try {
		for (Data data : data_list) {
			//#######  Create empty files ########//
			FileWriter writer = new FileWriter(file+File.separator+data.getFileName()+config.getExtension() );
			ArrayList<String> fields = new ArrayList<String>();			
		
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
							writer.append("\""+String.valueOf(m.get(field)).replace("null", "")+"\"");
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


}
