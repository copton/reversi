package ch.ethz.inf.vs.projectname;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.ethz.inf.vs.projectname.JerseyMain;
import messages.WebTest;

/**
 * The class that handles HTTP calls on the created resources.
 * @author ch.ethz.inf.vs.wot.autowot.builders.java.JerseyJavaBuilder
 * @meta-author Simon Mayer, ETH Zurich; Claude Barthels, ETH Zurich
 *
 */
public class Handler {


	public static String getTournaments(HashMap<String, Object> source) {
		System.out.println("A GET was called on the dynamic resource \"" + source + "\"");
		Object o = JerseyMain.REVERSI.sendRequestReply(new WebTest());
		return (String)o;
	}

	
	
}
