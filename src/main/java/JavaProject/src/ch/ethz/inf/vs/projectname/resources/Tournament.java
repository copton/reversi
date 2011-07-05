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

@Path("/tournaments/{tournament}")
public class Tournament extends BaseResource {

	protected String[] authorizedHashesGet = null;
	protected String[] authorizedHashesPost = {"b13f262e6f498cf29ec1deca39aca5a"};
	protected String[] authorizedHashesPut = null;
	protected String[] authorizedHashesDelete = null;
	
	public Tournament() {
		allowGet = true;
		allowPost = true;
		allowPut = false;
		allowDelete = false;
	}
    
    @Override
	public void handleGetters() {
		getterValue = ch.ethz.inf.vs.projectname.Handler.getTournament(getSource()).renderHtml(); // null

	}
    
    // By default resources handle GET requests
    @GET 
    @Produces("text/html")
    public String handleGetHTML(@Context HttpServletRequest request, @Context UriInfo uri) {
        
        parseResourceInformation(request, uri);
        getterValue = ch.ethz.inf.vs.projectname.Handler.getTournament(getSource()).renderHtml(); // null

        
        return getRepresentationHTML();
    }
    
 	// By default resources handle GET requests
    @GET 
    @Produces("application/json")
    public String handleGetJSON(@Context HttpServletRequest request, @Context UriInfo uri) {
        
        parseResourceInformation(request, uri);
//        getterValue = ch.ethz.inf.vs.projectname.Handler.getTournament(getSource()); // null

        
        return getRepresentationJSON();
    }
    
    // By default resources handle GET requests
    @GET 
    @Produces("text/xml")
    public String handleGetXML(@Context HttpServletRequest request, @Context UriInfo uri) {
        
        parseResourceInformation(request, uri);
//        getterValue = ch.ethz.inf.vs.projectname.Handler.getTournament(getSource()); // null

        
        return getRepresentationXML();
    }
    
    @POST 
	@Produces("text/html") 
	public String handlePostHTML(@Context HttpServletRequest request, @Context UriInfo uri, @FormParam("value") String posterVar) { 
		ResourceProtector.protect(request, authorizedHashesPost);
		parseResourceInformation(request, uri);
		// No Poster Description Given
		ch.ethz.inf.vs.projectname.Handler.postTournament(getSource(), posterVar);
 
		return getRepresentationHTML();
	}

    

	@Override
	protected String getterListItem() {
		this.handleGetters();

		log.info(getterName);
		log.info(getterValue);

		getterValue = getterValue.replace("{{RessourceUri}}", resourceURI);
		String returnString = "<li><div>" + getterValue + "</div>";

		if (!(getterDescription == null) && !(getterDescription.equalsIgnoreCase(""))) {
			returnString += " <span class = \"descriptor\">(" + getterDescription + ")</span>";
		}

		returnString += "</li>\n";

		return returnString;
	}

	
	@POST 
	@Produces("application/json") 
	public String handlePostJSON(@Context HttpServletRequest request, @Context UriInfo uri, @FormParam("value") String posterVar) { 
		ResourceProtector.protect(request, authorizedHashesPost);		parseResourceInformation(request, uri);
		// No Poster Description Given
		ch.ethz.inf.vs.projectname.Handler.postTournament(getSource(), posterVar);
 
		return getRepresentationJSON();
	} 
	
	@POST 
	@Produces("text/xml") 
	public String handlePostXML(@Context HttpServletRequest request, @Context UriInfo uri, @FormParam("value") String posterVar) { 
		ResourceProtector.protect(request, authorizedHashesPost);		parseResourceInformation(request, uri);
		// No Poster Description Given
		ch.ethz.inf.vs.projectname.Handler.postTournament(getSource(), posterVar);
 
		return getRepresentationXML();
	} 
	
	

    
    
    
    
    
}
