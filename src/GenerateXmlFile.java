
/**
 * @author      Schillaci "Dwayne" McInnis <dmcinnis@lhric.org>
 * @version     1.0
 * @since       Jul 7, 2016
 * Filename		GenerateXmlFile.java
 */


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class GenerateXmlFile {

	public GenerateXmlFile(File file, ArrayList<Data> data_list, Configuration config) {
		
		try {
			
		    GlobalUtilities.logInfo("Generating file...");
		    
		      
			for (Data data : data_list) {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
				Document doc = docBuilder.newDocument();
				StreamResult result = null;
				
			    result = new StreamResult(new File(file+File.separator+data.getFileName()+config.getExtension()));
				
			    Element rootElement = doc.createElement(data.getFileName());
			    doc.appendChild(rootElement);
			   
		        List<String> columnNameStringList = config.getColumnNames();
		        String columnNameString = "";
				
				for (int i=0; i < columnNameStringList.size(); ++i) {
					if (config.getTextTitle().get(i).equals(data.getFileName())) {
						columnNameString = columnNameStringList.get(i);
					}
				} 
				
				try {
					for (TreeMap<String,Object> m : data.getData()) {
						 try {
								
								Element elem = doc.createElement("DATA");
								rootElement.appendChild(elem);
								
								for(String columnName: columnNameString.split(",")){	
									Element required_data = doc.createElement(columnName);
									elem.appendChild(required_data);
									elem.appendChild(doc.createTextNode(String.valueOf(m.get(columnName)).replace("null", "")));
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
				} catch(Exception e){
					e.printStackTrace();
				}
			        	
				
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
		        Transformer transformer = transformerFactory.newTransformer();
		        DOMSource source = new DOMSource(doc);
		        transformer.transform(source, result);
			}
		    
		} catch (Exception e) {
			e.printStackTrace();
			GlobalUtilities.logError(""+e.getMessage());
		}
			
		GlobalUtilities.logInfo("Files generated successfully..");
	}

}
