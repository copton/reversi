package ch.ethz.inf.vs.projectname;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.UriBuilder;

import ch.ethz.inf.vs.projectname.commons.JerseyConstants;

/**
 * @author ch.ethz.inf.vs.wot.autowot.builders.java.JerseyJavaBuilder
 * @meta-author Simon Mayer, ETH Zurich; Claude Barthels, ETH Zurich
 */

public class JerseyMain {
	
	protected static String baseURI = JerseyConstants.BASE_URI;
	protected static int port = JerseyConstants.SERVER_PORT;
	protected static SelectorThread webServerThread;
	
    private static URI getURI() {
        return UriBuilder.fromUri(baseURI+"/").port(port).build();
    }

    protected static void startServer() throws IOException {
        final Map<String, String> initParams = new HashMap<String, String>();
        initParams.put("com.sun.jersey.config.property.packages", "ch.ethz.inf.vs.projectname.resources");
        System.out.println("Starting Grizzly Web Container...");
        webServerThread = GrizzlyWebContainerFactory.create(getURI(), initParams);     
    }
    
    protected static void stopServer() throws IOException, InstantiationException {
    	webServerThread.stopEndpoint();
    }
    
    public static void main(String[] args) throws IOException, InstantiationException {
		startServer();
		System.out.println(String.format("Server running at %s on port %s", baseURI, port));
		System.in.read();
		stopServer();
    }

////////////////////////// Stuff added for the reversi server
    public void myServerStarter(akka.actor.ActorRef reversiRef) throws IOException, InstantiationException {
	REVERSI = reversiRef;
    	startServer();

    }

    public static akka.actor.ActorRef REVERSI;
//////////////////////////////////////
    
    
}
