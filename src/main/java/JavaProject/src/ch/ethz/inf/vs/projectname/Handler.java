package ch.ethz.inf.vs.projectname;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import ch.ethz.inf.vs.projectname.JerseyMain;

import messages.*;
import akka.actor.ActorRef;


/**
 * The class that handles HTTP calls on the created resources.
 * @author ch.ethz.inf.vs.wot.autowot.builders.java.JerseyJavaBuilder
 * @meta-author Simon Mayer, ETH Zurich; Claude Barthels, ETH Zurich
 *
 */
public class Handler {

	private static ActorRef actorRefRequester(String actorName) {
		Object o = JerseyMain.REVERSI.sendRequestReply(new WebRequestActor(actorName));
		return (ActorRef)o;
	}

	public static String getRoot(HashMap<String, Object> source) {
		System.out.println("A GET was called on the dynamic resource \"" + source + "\"");
		Object o = JerseyMain.REVERSI.sendRequestReply(new WebGetRoot());
		return (String)o;
	}

	public static List<String> loadTournament(HashMap<String, Object> source) {
		System.out.println("A reflexive call has been executed on the dynamic resource \"" + source + "\"");
		Object o = JerseyMain.REVERSI.sendRequestReply(new WebLoadTournamentCollection());
		StringTokenizer st = new StringTokenizer( (String)o, "\n");	
		List<String> returnList = new ArrayList<String>();
		while(st.hasMoreTokens()) {
			returnList.add(st.nextToken());
		}
		return returnList;
	 }

	public static String getTournaments(HashMap<String, Object> source) {
		System.out.println("A GET was called on the dynamic resource \"" + source + "\"");
		Object o = JerseyMain.REVERSI.sendRequestReply(new WebGetTournaments());
		return (String)o;
	}

	public static void postTournamentsUpload(HashMap<String, Object> source, Object posterVar) {
		System.out.println("A POST was called on the dynamic resource with data: \"" + posterVar + "\"");
//		{External Call}
		for(String key : source.keySet()) System.out.println("\t\" + key + \": " + source.get(key));
	}

	public static List<String> loadGame(HashMap<String, Object> source) {
		
		System.out.println("A reflexive call has been executed on the dynamic resource \"" + source + "\"");
		System.out.println(source.toString());		
		
		String argument = source.get("tournament").toString();
		argument = argument.substring(1, argument.length()-1);

		ActorRef actor = actorRefRequester("/tournaments/" + argument);

		Object o = actor.sendRequestReply(new WebLoadGameCollection());
		StringTokenizer st = new StringTokenizer( (String)o, "\n");	
		List<String> returnList = new ArrayList<String>();
		while(st.hasMoreTokens()) {
			returnList.add(st.nextToken());
		}
		return returnList;
	 }

	public static String getTournament(HashMap<String, Object> source) {
		System.out.println("A GET was called on the dynamic resource \"" + source + "\"");
		
		String argument = source.get("tournament").toString();
		argument = argument.substring(1, argument.length()-1);

		ActorRef actor = actorRefRequester("/tournaments/" + argument);
		Object o = actor.sendRequestReply(new WebGetTournament());
		
		return (String)o;
	}

	public static void putTournament(HashMap<String, Object> source, Object putterVar) {
		System.out.println("A PUT was called on the dynamic resource \"" + source + "\" with data: \"" + putterVar + "\"");
//		{External Call}
	}

	public static void deleteTournament(HashMap<String, Object> source) {
		System.out.println("A DELETE was called on the dynamic resource \"" + source + "\"");
//		{External Call}
	}

	public static List<String> loadTurn(HashMap<String, Object> source) {
	
		System.out.println("loadTurn called");		
		System.out.println("A reflexive call has been executed on the dynamic resource \"" + source + "\"");
		
		String argument = source.get("tournament").toString();
		argument = argument.substring(1, argument.length()-1);

		String argument2 = source.get("game").toString();
		argument2 = argument2.substring(1, argument2.length()-1);

		ActorRef actor = actorRefRequester("/tournaments/" + argument +"/"+ argument2);

		Object o = actor.sendRequestReply(new WebLoadTurnCollection());
		StringTokenizer st = new StringTokenizer( (String)o, "\n");	
		List<String> returnList = new ArrayList<String>();
		while(st.hasMoreTokens()) {
			returnList.add(st.nextToken());
		}
		return returnList;
	 }

	public static String getGame(HashMap<String, Object> source) {		
		System.out.println("A GET was called on the dynamic resource \"" + source + "\"");
	
		String argument = source.get("tournament").toString();
		argument = argument.substring(1, argument.length()-1);

		String argument2 = source.get("game").toString();
		argument2 = argument2.substring(1, argument2.length()-1);

		ActorRef actor = actorRefRequester("/tournaments/" + argument +"/"+ argument2);
		Object o = actor.sendRequestReply(new WebGetGame());
		
		return (String)o;
	}

	public static String getTurn(HashMap<String, Object> source) {
		System.out.println("A GET was called on the dynamic resource \"" + source + "\"");

		String argument = source.get("tournament").toString();
		argument = argument.substring(1, argument.length()-1);

		String argument2 = source.get("game").toString();
		argument2 = argument2.substring(1, argument2.length()-1);

		String argument3= source.get("turn").toString();
		argument3 = argument3.substring(1, argument3.length()-1);


		ActorRef actor = actorRefRequester("/tournaments/" + argument +"/"+ argument2);
		Object o = actor.sendRequestReply(new WebGetTurn(argument3));
		
		return (String)o;
	}

	public static String getCurrentTurn(HashMap<String, Object> source) {
		System.out.println("A GET was called on the dynamic resource \"" + source + "\"");

		String argument = source.get("tournament").toString();
		argument = argument.substring(1, argument.length()-1);

		String argument2 = source.get("game").toString();
		argument2 = argument2.substring(1, argument2.length()-1);		

		ActorRef actor = actorRefRequester("/tournaments/" + argument +"/"+ argument2);
		Object o = actor.sendRequestReply(new WebGetCurrentTurn());
		return (String)o;
	}

	
	
}
