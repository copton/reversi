package ch.ethz.inf.vs.projectname.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author ch.ethz.inf.vs.wot.autowot.builders.java.JerseyJavaBuilder
 *
 */

public class ResourceProtector {
	
	public static void protect(HttpServletRequest request, String[] authorizedHashes) {
		if(request.getHeader("Authorization") != null) {
			String loginData = request.getHeader("Authorization");
			if(loginData.startsWith("Basic") || loginData.startsWith("basic")) {
				String loginHash = loginData.substring(6);
				String loginHashMd5 = md5(loginHash);
				for(String hash : authorizedHashes) {
					if(hash.equalsIgnoreCase(loginHashMd5)) {
						return;
					}
				}
			}
		}
		
		throw new UnauthorizedException();
	}
	
	private static String md5(String data) {
		String base64 = data;
		MessageDigest digest;
		try {
			digest = java.security.MessageDigest.getInstance("MD5");
			digest.reset();
			digest.update(base64.getBytes());
		    byte[] messageDigest = digest.digest();
		    StringBuffer messageDigestHex = new StringBuffer();
		    for(byte b : messageDigest) {
		    	messageDigestHex.append(Integer.toHexString(0xFF & b));
		    }
		    return messageDigestHex.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "MD5 computation failed!";
	}
	
}
