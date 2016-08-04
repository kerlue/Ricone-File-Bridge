import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class GenerateJSONFile {
	
		public GenerateJSONFile(File file, ArrayList<Data> data_list, Configuration config) {
			
			try {
				
			    GlobalUtilities.logInfo("Generating file...");
			    
			      
				for (Data data : data_list) {
					
					JSONObject obj = new JSONObject();
					
					FileWriter writer = new FileWriter(file+File.separator+data.getFileName()+config.getExtension());

			        List<String> columnNameStringList = config.getColumnNames();
			        String columnNameString = "";
					
					for (int i=0; i < columnNameStringList.size(); ++i) {
						if (config.getTextTitle().get(i).equals(data.getFileName())) {
							columnNameString = columnNameStringList.get(i);
						}
					} 
					
					try {
						int i =0;
						for (TreeMap<String,Object> m : data.getData()) {
							 try {
									
								 JSONObject tempObj = new JSONObject();
									
									for(String columnName: columnNameString.split(",")){	
									  tempObj.append(columnName, String.valueOf(m.get(columnName)).replace("null", ""));										
									}
									obj.put(data.getFileName()+"_"+i++,tempObj );
								} catch (Exception e) {
									e.printStackTrace();
								}
							 
							 
							}
					} catch(Exception e){
						e.printStackTrace();
					}
				        	
					
					writer.append(obj.toString());				
					writer.flush();
					writer.close();		
				}
			    
			} catch (Exception e) {
				e.printStackTrace();
				GlobalUtilities.logError(""+e.getMessage());
			}
				
			GlobalUtilities.logInfo("Files generated successfully..");
		}
}
