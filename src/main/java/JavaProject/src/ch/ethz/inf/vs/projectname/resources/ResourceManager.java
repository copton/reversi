package ch.ethz.inf.vs.projectname.resources;

import java.io.File;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.sun.jersey.spi.resource.Singleton;

@Singleton
@Path("/webresources")
public class ResourceManager {
	
	@GET
	@Path("/images/{size}/{picturename}")
	@Produces("images/*")
	public Response getImage(@PathParam("size") String size, @PathParam("picturename") String picturename) {
		File f = new File("webresources/images/" + size + "/" + picturename);
		
		if (f.exists()) {
			String mt = "images/*";
			return Response.ok(f, mt).build();
		}
		return null;
	}
	
	@GET
	@Path("/stylesheets/{scriptname}")
	@Produces("text/css")
	public Response getScrip(@PathParam("scriptname") String scriptname) {
		File f = new File("webresources/stylesheets/" + scriptname);
		
		if (f.exists()) {
			String mt = "text/css";
			return Response.ok(f, mt).build();
		}
		return null;
	}
}
