import java.util.ArrayList;

public class DataType {
	private String outside_name; // Name of function specified by user
	private String result; // Value pulled from the API
	private String api_name; // Name of function for API to use
	private String data_type; // Type of data pulled from API (i.e. Lea, School, Student, etc.)

	DataType(String d_type, String o_name, String api_name) {
		this.data_type = d_type;
		this.outside_name = o_name;
		this.api_name = api_name;
		this.result = null;
	}

	DataType(String d_type, String o_name, String api_name, String res) {
		this.data_type = d_type;
		this.outside_name = o_name;
		this.api_name = api_name;
		this.result = res;
	}

	public String getDataCategory() {
		return data_type;
	}

	public ArrayList<String> getCommandArray() {
		return api_name;
	}

	public String getOutsideName() {
		return outside_name;
	}

	public String getResult() {
		return result;
	}

}
