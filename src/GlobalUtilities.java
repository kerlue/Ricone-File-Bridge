/**
 * @author      Schillaci "Dwayne" McInnis <dmcinnis@lhric.org>
 * @version     1.0
 * @since       Jul 7, 2016
 * Filename		GlobalUtilities.java
 */


import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class GlobalUtilities {
    private static Logger LOGGER = Logger.getLogger("File Bridge");
	private static boolean shouldLog;
	final static String LOCAL="local";
   
	
	public static void enableLogging(boolean val) {
		 shouldLog = val;
		 FileHandler fh; 
		 
		 try {  
		        // This block configure the logger with handler and formatter  
		        fh = new FileHandler("log.log");  
		        LOGGER.addHandler(fh);
		        SimpleFormatter formatter = new SimpleFormatter();  
		        fh.setFormatter(formatter);  

		    } catch (SecurityException e) {  
		        e.printStackTrace();  
		    } catch (IOException e) {  
		        e.printStackTrace();  
		    }  
	}

	public static void logInfo(String msg) {
		if(shouldLog)
			LOGGER.info(msg);
	}
	public static void logWarning(String msg) {
		if(shouldLog)
			LOGGER.warning(msg);
	}

	public static void logError(String msg) {
		if(shouldLog)
			LOGGER.severe(msg);
	}

}
