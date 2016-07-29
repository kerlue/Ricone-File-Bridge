/*////////////////////////////////////////////////
 * Created By: Shamus Cardon
 * Date Created: 7/12/2016
 * Version: 1.1.0
 * Updated: 7/25/2016
*/////////////////////////////////////////////////

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public class Data {
	private List<TreeMap<String, Object>> l; // each map represents 1 object (Lea, School, Student, Staff, etc.) list represents all objects present
	private String file_name;
	
	Data(List<ArrayList<DataType>> list, String f_name) {
		this.l = new ArrayList<TreeMap<String,Object>>();
		for (ArrayList<DataType> i : list) {
			if (i.size() > 0) {
				TreeMap<String,Object> m= new TreeMap<String,Object>();
				for (DataType d_t : i) {
					m.put(d_t.getOutsideName(),d_t.getResult());
				}
				this.l.add(m);
			}
		}
		this.file_name = f_name;
	}
	
	List<Object> getAllValues(String val) { // returns a list of all values associated with a certain category
		List<Object> ls = new ArrayList<Object>();
		for (Map<String,Object> map : l) {
			ls.add(map.get(val));
		}
		return ls;
	}

	List<TreeMap<String,Object>> getData() {
		Set<TreeMap<String,Object>> temp_set = new LinkedHashSet<>(l); // ensure duplicate values are removed
		return new ArrayList<TreeMap<String,Object>>(temp_set);
	}
	
	String getFileName() {
		return file_name;
	}
	
	ArrayList<String> getColumnNames() {
		ArrayList<String> temp = new ArrayList<String>();
		temp.addAll(l.get(0).keySet());
		return temp;
	}
	
	void Print() {
		System.out.println("File Name: " + file_name + "\nData: " + l);
	}
}