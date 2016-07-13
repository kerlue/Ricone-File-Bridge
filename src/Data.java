/*////////////////////////////////////////////////
 * Created By: Shamus Cardon
 * Date Created: 7/12/2016
 * Version: 0.3.0
 * Updated: 7/13/2016
*/////////////////////////////////////////////////

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Data {
	private List<TreeMap<String, String>> l; // each map represents 1 object (Lea, School, Student, Staff, etc.) listy represents all objects present
	
	Data(List<TreeMap<String, String>> list) {
		this.l = list;
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
}