package ch.ethz.inf.vs.projectname.commons;

import java.util.logging.Level;

public class JerseyConstants {
	/**
	 * The loglevel to use for general logging, set when launching the driver
	 */
	public static Level LOGLEVEL = Level.SEVERE;	
	
	// The server port on which the SPOTs are going to be available
	public static String BASE_URI = "http://localhost";
	public static Integer SERVER_PORT = 8080;
	
		
	// Paths to external resources, RESOURCES_DIRECTORY_PATH is set when launching the driver
	public static String RESOURCES_DIRECTORY_PATH = "resources";
	public static final String RESOURCES_CONFIGFILE_PATH = "config/configuration.xml";
	public static final String RESOURCES_STYLESHEET_PATH = "/webresources/scripts/main.css";
	public static final String RESOURCES_HTML_PATH = "webresources/html";	
	public static final String RESOURCES_FEEDS_PATH = "/feeds";
}