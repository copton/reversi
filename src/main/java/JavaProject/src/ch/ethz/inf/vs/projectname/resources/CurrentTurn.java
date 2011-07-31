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

@Path("/tournaments/{tournament}/{game}/currentturn")
public class CurrentTurn extends BaseResource {

	protected String[] authorizedHashesGet = null;
	protected String[] authorizedHashesPost = null;
	protected String[] authorizedHashesPut = null;
	protected String[] authorizedHashesDelete = null;
	
	public CurrentTurn() {
		allowGet = true;
		allowPost = false;
		allowPut = false;
		allowDelete = false;
	}
	String asdfturn = "turn0";
  
    @Override
	public void handleGetters() {
//		getterValue = ch.ethz.inf.vs.projectname.Handler.getCurrentTurn(getSource(), asdfturn).renderHtml(); // null

	}
    
    // By default resources handle GET requests
    @GET 
    @Produces("text/html")
    public String handleGetHTML(@Context HttpServletRequest request, @Context UriInfo uri) {
	String turn;
        try {
		turn = request.getCookies()[0].getValue();
	} catch (java.lang.NullPointerException e) {
		turn = "turnMinusOne";
	}
	asdfturn = turn;
	System.out.println("the cookie containts: " + turn);

        parseResourceInformation(request, uri);
        getterValue = ch.ethz.inf.vs.projectname.Handler.getCurrentTurn(getSource(), turn).renderHtml(); // null

        
        return getRepresentationHTML();
    }


    
    @Override
	protected String getterListItem() {
		this.handleGetters();

		log.info(getterName);
		log.info(getterValue);

		String returnString = "<div>" + getterValue + "</div>";

		if (!(getterDescription == null) && !(getterDescription.equalsIgnoreCase(""))) {
			returnString += " <span class = \"descriptor\">(" + getterDescription + ")</span>";
		}

		returnString += "</li>\n";

		return returnString;
	}


 	// By default resources handle GET requests
    @GET 
    @Produces("application/json")
    public String handleGetJSON(@Context HttpServletRequest request, @Context UriInfo uri) {
       String turn;
        try {
		turn = request.getCookies()[0].getValue();
	} catch (java.lang.NullPointerException e) {
		turn = "turnMinusOne";
	}
	asdfturn = turn;
	System.out.println("the cookie containts: " + turn); 
        parseResourceInformation(request, uri);
        getterValue = ch.ethz.inf.vs.projectname.Handler.getCurrentTurn(getSource(),turn).renderJson(); // null

        
        return getRepresentationJSON();
    }
    
    // By default resources handle GET requests
    @GET 
    @Produces("text/xml")
    public String handleGetXML(@Context HttpServletRequest request, @Context UriInfo uri) {
        String turn;
        try {
		turn = request.getCookies()[0].getValue();
	} catch (java.lang.NullPointerException e) {
		turn = "turn0";
	}
	asdfturn = turn;
	System.out.println("the cookie containts: " + turn);
        parseResourceInformation(request, uri);
      getterValue = ch.ethz.inf.vs.projectname.Handler.getCurrentTurn(getSource(),turn).renderXml(); // null

        
        return getRepresentationXML();
    }
    
    
    
    
    
    
    
}
