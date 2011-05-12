package ch.ethz.inf.vs.projectname;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import ch.ethz.inf.vs.projectname.JerseyMain;

import messages.*;

/**
 * The class that handles HTTP calls on the created resources.
 * @author ch.ethz.inf.vs.wot.autowot.builders.java.JerseyJavaBuilder
 * @meta-author Simon Mayer, ETH Zurich; Claude Barthels, ETH Zurich
 *
 */
public class Handler {

	public static String getRoot(HashMap<String, Object> source) {
		System.out.println("A GET was called on the dynamic resource \"" + source + "\"");
		Object o = JerseyMain.REVERSI.sendRequestReply(new WebGetRoot());
		return (String)o;
	}

	public static List<String> loadTournament(HashMap<String, Object> source) {
		System.out.println("A reflexive call has been executed on the dynamic resource \"" + source + "\"");
		Object o = JerseyMain.REVERSI.sendRequestReply(new WebGetTournaments());
		StringTokenizer st = new StringTokenizer( (String)o, "\n");	
		List<String> returnList = new ArrayList<String>();
		while(st.hasMoreTokens()) {
			returnList.add(st.nextToken());
		}
		return returnList;
	}

	public static String tournamentsGetter(HashMap<String, Object> source) {
		System.out.println("A GET was called on the dynamic resource \"" + source + "\"");
		return "Hello World!";
	}

	public static List<String> loadGame(HashMap<String, Object> source) {
		System.out.println("A reflexive call has been executed on the dynamic resource \"" + source + "\"");
		List<String> returnList = new ArrayList<String>();
		returnList.add("Subresource1");
		returnList.add("Subresource2");
		returnList.add("Subresource3");
		return returnList;
	}

	public static String getTournament(HashMap<String, Object> source) {
		System.out.println("A GET was called on the dynamic resource \"" + source + "\"");
		return "Hello World!";
	}

	public static String getGame(HashMap<String, Object> source) {
		System.out.println("A GET was called on the dynamic resource \"" + source + "\"");
		return "Hello World!";
	}

	
	
}
