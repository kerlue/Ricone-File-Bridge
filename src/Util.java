
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import org.springframework.http.HttpStatus;


public class Util
{
	/**
	 * Response handler to return HTTP Status Codes if an error with a request
	 * occurs
	 * 
	 * @param response
	 */
	public static void ResponseHandler(HttpStatus response)
	{
		HttpStatus httpStatusCode = response;

		if (httpStatusCode == HttpStatus.OK || httpStatusCode == HttpStatus.NOT_FOUND)
		{
			// System.out.println(httpStatusCode);
		}
		else
		{
			System.out.println("HttpCode: " + httpStatusCode.toString());
		}
	}
	 
	public static void disableSslVerification()
	{
		try
		{
			// Create a trust manager that does not validate certificate chains
			X509TrustManager[] trustAllCerts = new X509TrustManager[] { new X509TrustManager()
			{
				public X509Certificate[] getAcceptedIssuers()
				{
					return null;
				}

				public void checkClientTrusted(X509Certificate[] certs, String authType)
				{
				}

				public void checkServerTrusted(X509Certificate[] certs, String authType)
				{
				}
			} };

			// Install the all-trusting trust manager
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

			// Create all-trusting host name verifier
			HostnameVerifier allHostsValid = new HostnameVerifier()
			{
				public boolean verify(String hostname, SSLSession session)
				{
					return true;
				}
			};

			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		catch (KeyManagementException e)
		{
			e.printStackTrace();
		}
	}
}