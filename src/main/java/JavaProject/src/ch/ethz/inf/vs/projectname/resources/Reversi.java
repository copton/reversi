package ch.ethz.inf.vs.projectname.resources;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import javax.servlet.http.HttpServletRequest;

import ch.ethz.inf.vs.projectname.security.ResourceProtector;

/**
 * @author ch.ethz.inf.vs.wot.autowot.builders.java.JerseyJavaBuilder
 * @meta-author Simon Mayer, ETH Zurich; Claude Barthels, ETH Zurich
 */

@Path("/reversi")
public class Reversi extends BaseResource {

	protected String[] authorizedHashes = null;
	
	public Reversi() {
		allowGet = true;
		allowPost = false;
		allowPut = false;
		allowDelete = false;
	}
    
    @Override
	public void handleGetters() {
		getterValue = ch.ethz.inf.vs.projectname.Handler.getTournaments(getSource()); // null

	}
    
    // By default resources handle GET requests
    @GET 
    @Produces("text/html")
    public String handleGetHTML(@Context HttpServletRequest request, @Context UriInfo uri) {
        
        parseResourceInformation(request, uri);
        
        handleGetters();
        getterValue = ch.ethz.inf.vs.projectname.Handler.getTournaments(getSource()); // null

        
        return getRepresentationHTML();
    }
    
 	// By default resources handle GET requests
    @GET 
    @Produces("application/json")
    public String handleGetJSON(@Context HttpServletRequest request, @Context UriInfo uri) {
        
        parseResourceInformation(request, uri);
        
        handleGetters();
        getterValue = ch.ethz.inf.vs.projectname.Handler.getTournaments(getSource()); // null

        
        return getRepresentationJSON();
    }
    
    
    
    
    
    
    
}
