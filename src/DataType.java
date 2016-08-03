import java.util.ArrayList;

/*////////////////////////////////////////////////
 * Created By: Shamus Cardon
 * Date Created: 7/15/2016
 * Version: 1.1.0
 * Updated: 8/3/2016
*/////////////////////////////////////////////////

public class DataType {
	private String data_category;  // Lea, School, Student, etc.
	private String outside_name;   // user readable name (specified in config file)
	private String command_name;   // API command (specified in config file)
	private Object command_result; // result pulled from API
	
	DataType (String d_type, String out_name, String c_name) {
		this.data_category = d_type;
		this.outside_name = out_name;
		this.command_name = c_name;
		this.command_result = "";
	}
	
	DataType (String d_type, String out_name, String c_name, String result) {
		this.data_category = d_type;
		this.outside_name = out_name;
		this.command_name = c_name;
		this.command_result = result;
	}
	
	DataType (String d_type, String out_name, String c_name, Object result) {
		this.data_category = d_type;
		this.outside_name = out_name;
		this.command_name = c_name;
		this.command_result = result;
	}
	
	
	public String getDataCategory() {
		return data_category;
	}
	
	public String getOutsideName() {
		return outside_name;
	}
	
	public String getCommandName() {
		return command_name;
	}
	
	public Object getResult() {
		return this.command_result;
	}
	
	public ArrayList<String> getCommandArray() {
		ArrayList<String> temp = new ArrayList<String>();
		temp.add(outside_name);
		temp.add(command_name);
		return temp;
	}
	
	public void Print() {
		System.out.println("o_name: " + outside_name + " c_name: " + command_name);
	}
	
	public void Print2() {
		System.out.println("o_name: " + outside_name + " c_name: " + command_name + " c_result: " + command_result);
	}
}
