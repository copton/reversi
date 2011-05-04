package ch.ethz.inf.vs.projectname.security;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * 
 * @author ch.ethz.inf.vs.wot.autowot.builders.java.JerseyJavaBuilder
 *
 */

public class UnauthorizedException extends WebApplicationException {

	private static final long serialVersionUID = 1L;

	public UnauthorizedException() {
		super(Response.status(Status.UNAUTHORIZED).header("WWW-Authenticate", "Basic realm=\"AutoWoT Secure Area\"").build());
	}
}
