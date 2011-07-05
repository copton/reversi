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

	public static RootReply getRoot(HashMap<String, Object> source) {
		System.out.println("A GET was called on the dynamic resource \"" + source + "\"");
		Object o = JerseyMain.REVERSI.sendRequestReply(new WebGetRoot());
		return ((RootReply)o);
	}

	public static List<String> loadTournament(HashMap<String, Object> source) {
		System.out.println("A reflexive call has been executed on the dynamic resource \"" + source + "\"");
		Object o = JerseyMain.REVERSI.sendRequestReply(new WebLoadTournamentCollection());

		return (List<String>)o;
	 }

	public static TournamentsReply getTournaments(HashMap<String, Object> source) {
		System.out.println("A GET was called on the dynamic resource \"" + source + "\"");
		Object o = JerseyMain.REVERSI.sendRequestReply(new WebGetTournaments());
		return (TournamentsReply)o;
	}
	//TODO
	public static void postTournaments(HashMap<String, Object> source, Object posterVar) {
		System.out.println("A POST was called on the dynamic resource with data: \"" + posterVar + "\"");
		
		for(String key : source.keySet()) System.out.println("\t\" + key + \": " + source.get(key));
	}

	public static List<String> loadGame(HashMap<String, Object> source) {
		
		System.out.println("A reflexive call has been executed on the dynamic resource \"" + source + "\"");
		System.out.println(source.toString());		
		
		String argument = source.get("tournament").toString();
		argument = argument.substring(1, argument.length()-1);

		ActorRef actor = actorRefRequester("/tournaments/" + argument);

		Object o = actor.sendRequestReply(new WebLoadGameCollection());
		
		return (List<String>)o;
	 }

	public static TournamentReply getTournament(HashMap<String, Object> source) {
		System.out.println("A GET was called on the dynamic resource \"" + source + "\"");
		
		String argument = source.get("tournament").toString();
		argument = argument.substring(1, argument.length()-1);

		ActorRef actor = actorRefRequester("/tournaments/" + argument);
		Object o = actor.sendRequestReply(new WebGetTournament());
		
		return (TournamentReply)o;
	}
	
	public static void postTournament(HashMap<String, Object> source, Object posterVar) {
		System.out.println("A POST was called on the dynamic resource with data: \"" + posterVar + "\"");

		String argument = source.get("tournament").toString();
		argument = argument.substring(1, argument.length()-1);

		JerseyMain.REVERSI.sendOneWay(new WebPostForTournament(argument, posterVar.toString()));

		for(String key : source.keySet()) System.out.println("\t\" + key + \": " + source.get(key));
	}
	//TODO
	public static List<String> loadPlayer(HashMap<String, Object> source) {
		
		System.out.println("A reflexive call has been executed on the dynamic resource \"" + source + "\"");
	
		String argument = source.get("tournament").toString();
		argument = argument.substring(1, argument.length()-1);

		String argument2 = source.get("game").toString();
		argument2 = argument2.substring(1, argument2.length()-1);

		ActorRef actor = actorRefRequester("/tournaments/" + argument + "/" + argument2);

		Object o = actor.sendRequestReply(new WebLoadPlayerCollection());
		
		return (List<String>)o;
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

	public static PlayerReply getPlayer(HashMap<String, Object> source) {
		System.out.println("A GET was called on the dynamic resource \"" + source + "\"");
		
		String argument = source.get("tournament").toString();
		argument = argument.substring(1, argument.length()-1);

		String argument2 = source.get("game").toString();
		argument2 = argument2.substring(1, argument2.length()-1);

		String argument3 = source.get("player").toString();
		argument3 = argument3.substring(1, argument3.length()-1);

		ActorRef actor = actorRefRequester("/tournaments/" + argument + "/" + argument2);

		Object o = actor.sendRequestReply(new WebGetPlayer(argument3));

		return (PlayerReply)o;
	}

	public static CurrentTurnReply getCurrentTurn(HashMap<String, Object> source, String currentTurn) {
		System.out.println("A GET was called on the dynamic resource \"" + source + "\"");

		String argument = source.get("tournament").toString();
		argument = argument.substring(1, argument.length()-1);

		String argument2 = source.get("game").toString();
		argument2 = argument2.substring(1, argument2.length()-1);		

		ActorRef actor = actorRefRequester("/tournaments/" + argument +"/"+ argument2);
		Object o = actor.sendRequestReply(new WebGetCurrentTurn(currentTurn));
		return (CurrentTurnReply)o;
	}

	public static List<String> loadTurn(HashMap<String, Object> source) {
		
		System.out.println("A reflexive call has been executed on the dynamic resource \"" + source + "\"");

		String argument = source.get("tournament").toString();
		argument = argument.substring(1, argument.length()-1);

		String argument2 = source.get("game").toString();
		argument2 = argument2.substring(1, argument2.length()-1);

		ActorRef actor = actorRefRequester("/tournaments/" + argument + "/" + argument2);

		Object o = actor.sendRequestReply(new WebLoadTurnCollection());
		
		return (List<String>)o;
	 }
	//TODO
	public static String getTurns(HashMap<String, Object> source) {
		System.out.println("A GET was called on the dynamic resource \"" + source + "\"");
		
		return "Hello World!";
	}
	//TODO
	public static TurnReply getTurn(HashMap<String, Object> source) {
		System.out.println("A GET was called on the dynamic resource \"" + source + "\"");
		
		String argument = source.get("tournament").toString();
		argument = argument.substring(1, argument.length()-1);

		String argument2 = source.get("game").toString();
		argument2 = argument2.substring(1, argument2.length()-1);

		String argument3 = source.get("turn").toString();
		argument3 = argument3.substring(1, argument3.length()-1);

		ActorRef actor = actorRefRequester("/tournaments/" + argument + "/" + argument2);

		Object o = actor.sendRequestReply(new WebGetTurn(argument3));

		return (TurnReply)o;
	}

	
	
}
