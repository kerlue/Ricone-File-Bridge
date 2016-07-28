/*////////////////////////////////////////////////
 * Created By: Shamus Cardon
 * Date Created: 7/11/2016
 * Version: 0.2
 * Updated: 7/11/2016
*/////////////////////////////////////////////////

final class Authorizer {
	static final String authUrl= "https://auth.ricone.org/login";        
    static final String clientId= "RICOneFileBridge";
    static final String clientSecret = "redacted";
    static final String providerId = "RICOneFileBridge";
    static final int navigationPageSize = 1;
    
    public Authorizer() {
    	
    }
    
    static String getURL() {
    	return authUrl;
    }
    
    static String getID() {
    	return clientId;
    }
    
    static String getSecret() {
    	return clientSecret;
    }
    
    static String getProviderID() {
    	return providerId;
    }
    
    static int getPageSize() {
    	return navigationPageSize;
    }    
}
