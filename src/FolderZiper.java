
/**
 * @author      Schillaci "Dwayne" McInnis <dmcinnis@lhric.org>
 * @version     1.0
 * @since       Jul 7, 2016
 * Filename		FolderZipper.java
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FolderZiper {


static public boolean zipFolder(String srcFolder, String destZipFile) {
    ZipOutputStream zip = null;
    FileOutputStream fileWriter = null;
    
    try{
    	GlobalUtilities.logInfo("Zipping in progress..");
    
    	fileWriter = new FileOutputStream(destZipFile);
        zip = new ZipOutputStream(fileWriter);
        
	    addFolderToZip("", srcFolder, zip);
	    
	    zip.flush();
	    zip.close();
	    
    }catch(Exception e){
		  GlobalUtilities.logError("Failed to zip file..");
		  return false;
	 }
    GlobalUtilities.logInfo("Zipping complete..");
    return true;
  }

  static private boolean addFileToZip(String path, String srcFile, ZipOutputStream zip) throws Exception{
	  
		  File folder = new File(srcFile);
		    if (folder.isDirectory()) {
		      addFolderToZip(path, srcFile, zip);
		    } 
		    else {
		      byte[] buf = new byte[1024];
		      int len;
		      FileInputStream in = new FileInputStream(srcFile);
		      zip.putNextEntry(new ZipEntry( folder.getName()));
		      while ((len = in.read(buf)) > 0) {
		        zip.write(buf, 0, len);
		      }
		    }
	   
	  return true;

  }

  static private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip)
      throws Exception {
	  
    File folder = new File(srcFolder);

    for (String fileName : folder.list()) {
      if (path.equals("")) {
        addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
      } else {
        addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip);
      }
    }
  }
}