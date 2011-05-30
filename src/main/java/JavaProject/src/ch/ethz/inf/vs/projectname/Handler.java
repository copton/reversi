package ch.ethz.inf.vs.projectname;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The class that handles HTTP calls on the created resources.
 * @author ch.ethz.inf.vs.wot.autowot.builders.java.JerseyJavaBuilder
 * @meta-author Simon Mayer, ETH Zurich; Claude Barthels, ETH Zurich
 *
 */
public class Handler {

	public static String getRoot(HashMap<String, Object> source) {
		System.out.println("A GET was called on the dynamic resource \"" + source + "\"");
		
		return "Hello World!";
	}

	public static List<String> loadTournament(HashMap<String, Object> source) {
		
		System.out.println("A reflexive call has been executed on the dynamic resource \"" + source + "\"");
		List<String> returnList = new ArrayList<String>();
		returnList.add("Subresource1");
		returnList.add("Subresource2");
		returnList.add("Subresource3");
		return returnList;
	 }

	public static String getTournaments(HashMap<String, Object> source) {
		System.out.println("A GET was called on the dynamic resource \"" + source + "\"");
		
		return "Hello World!";
	}

	public static void postTournaments(HashMap<String, Object> source, Object posterVar) {
		System.out.println("A POST was called on the dynamic resource with data: \"" + posterVar + "\"");
		
		for(String key : source.keySet()) System.out.println("\t\" + key + \": " + source.get(key));
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

	public static void postTournament(HashMap<String, Object> source, Object posterVar) {
		System.out.println("A POST was called on the dynamic resource with data: \"" + posterVar + "\"");
		
		for(String key : source.keySet()) System.out.println("\t\" + key + \": " + source.get(key));
	}

	public static List<String> loadPlayer(HashMap<String, Object> source) {
		
		System.out.println("A reflexive call has been executed on the dynamic resource \"" + source + "\"");
		List<String> returnList = new ArrayList<String>();
		returnList.add("Subresource1");
		returnList.add("Subresource2");
		returnList.add("Subresource3");
		return returnList;
	 }

	public static String getGame(HashMap<String, Object> source) {
		System.out.println("A GET was called on the dynamic resource \"" + source + "\"");
		
		return "Hello World!";
	}

	public static String getPlayer(HashMap<String, Object> source) {
		System.out.println("A GET was called on the dynamic resource \"" + source + "\"");
		
		return "Hello World!";
	}

	public static String getCurrentTurn(HashMap<String, Object> source) {
		System.out.println("A GET was called on the dynamic resource \"" + source + "\"");
		
		return "Hello World!";
	}

	public static List<String> loadTurn(HashMap<String, Object> source) {
		
		System.out.println("A reflexive call has been executed on the dynamic resource \"" + source + "\"");
		List<String> returnList = new ArrayList<String>();
		returnList.add("Subresource1");
		returnList.add("Subresource2");
		returnList.add("Subresource3");
		return returnList;
	 }

	public static String getTurns(HashMap<String, Object> source) {
		System.out.println("A GET was called on the dynamic resource \"" + source + "\"");
		
		return "Hello World!";
	}

	public static String getTurn(HashMap<String, Object> source) {
		System.out.println("A GET was called on the dynamic resource \"" + source + "\"");
		
		return "Hello World!";
	}

	
	
}
