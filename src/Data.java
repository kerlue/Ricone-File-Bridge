/*////////////////////////////////////////////////
 * Created By: Shamus Cardon
 * Date Created: 7/12/2016
 * Version: 1.0.0
 * Updated: 7/15/2016
*/////////////////////////////////////////////////

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Data {
	private List<TreeMap<String, String>> l; // each map represents 1 object (Lea, School, Student, Staff, etc.) list represents all objects present
	private String file_name;
	
	Data(List<ArrayList<DataType>> list, String f_name) {
		for (ArrayList<DataType> i : list) {
			TreeMap<String,String> m= new TreeMap<String,String>();
			for (DataType d_t : i) {
				m.put(d_t.getOutsideName(),d_t.getResult());
			}
			this.l.add(m);
		}
		this.file_name = f_name;
	}
	
	List<String> getAllValues(String val) { // returns a list of all values associated with a certain category
		List<String> ls = new ArrayList<String>();
		for (Map<String,String> map : l) {
			ls.add(map.get(val));
		}
		return ls;
	}

	List<TreeMap<String,String>> getData() {
		return l;
	}
	
	String getFileName() {
		return file_name;
	}
	
	void Print() {
		System.out.println("File Name: " + file_name + "\nData: " + l);
	}
}