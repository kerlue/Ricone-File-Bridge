
/**
 * @author      Schillaci "Dwayne" McInnis <dmcinnis@lhric.org>
 * @version     1.0
 * @since       Jul 7, 2016
 * Filename		Configuration.java
 */


public abstract class Configuration {
	
	 protected String zipMode;
	 protected String outputSchema;
	 protected String outputExport;
	 protected String outputPath;
	 protected String sftpPort;
	 protected String sftpUsername;
	 protected String sftpPassword;
	 protected String outputFolderTitle;
	 protected boolean zipEnabled;
	 final String CONF_FILE = "config.xml";
	

	
	/**
	 * @param args void.
	 * @param Returns export type. eg CSV, XML or JSON.
	 */
	public String getOutputSchema() {
		return this.outputSchema;
	}

	/**
	 * @param args void.
	 * @param Returns output export type. eg local, sftp.
	 */
	public String getOutputExport() {
		return this.outputExport;
	}
	
	/**
	 * @param args void.
	 * @param Returns output path ex c://user.
	 */
	public String getOutputPath() {
		return this.outputPath;
	}

	/**
	 * @param args void.
	 * @param Returns SFTP Port.
	 */
	public String getSftpPort() {
		return this.sftpPort;
	}

	/**
	 * @param args void.
	 * @param Returns SFTP Username.
	 */
	public String getSftpUsername() {
		return this.sftpUsername;
	}
	
	/**
	 * @param args void.
	 * @param Returns SFTP password.
	 */

	public String getSftpPassord() {
		return this.sftpPassword;
	}

	/**
	 * @param args void.
	 * @param Returns a boolean value to for zip mode (true or false).
	 */
	
	public boolean isZipEnabled() {
		return this.zipEnabled;
	}
	
	/**
	 * @param args void.
	 * @param Returns zip mode  (Binary or ASCII) eg .
	 */
	
	public String getZipMode() {
		return this.zipMode;
	}


	/**
	 * @param args void.
	 * @param Returns zip title from configuration settings .
	 */
	
	public String getOutputFolderTitle() {
		return this.outputFolderTitle;
	}

	 
	
}
