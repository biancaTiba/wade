package phobias;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONException;

import com.alibaba.fastjson.JSON;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import dbConnections.DatabaseManager;
import login.LoginManager;

@Path("/")
public class PhobiasContext {
	enum Activities {
		VISIT,
		WATCH,
		PRACTICE
	}
	
	public static String getActivityString(Activities activity, String language){
		switch (activity) {
		case VISIT:
			return language.equals("fr") ? "visiter" : "visit";
		case PRACTICE:
			return language.equals("fr") ? "pratique du sport" : "practice sport";
		case WATCH:
			return language.equals("fr") ? "regarder un film" : "watch a movie";
		default: return activity.toString();
		}
	}
	
	@GET
	@Path("findPhobias")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findPhobias(@QueryParam("activity") String activity, @QueryParam("subactivity") String subactivity, @QueryParam("friend") String friend, @QueryParam("token") String token, @QueryParam("lang") String lang) throws JSONException {
		
		if (token == null || !LoginManager.checkAuthToken(token)) {
    		return Response.status(401).build();
    	}
		
		if (activity == null || activity.equals("") || subactivity.equals("") || subactivity == null) {
			return Response.status(200).entity("").build();
		}
		
		String language = "en";
		if (lang != null && !lang.equals("")) {
			language = lang;
		}
        
		ArrayList<String> userFriendsPhobias = new ArrayList<>();
		if (friend != null && friend.equals("")) {
			userFriendsPhobias = getUserFriendPhobias(friend, activity, subactivity);
		}
		
		ArrayList<String> userPhobias = getUserPhobiasFromLocalDb(token, activity, subactivity);
		userPhobias.addAll(userFriendsPhobias);
		StringBuilder queryString = new StringBuilder();
		String s2 = "PREFIX  dbo: <http://dbpedia.org/ontology/>\n" +
                "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/>" +
                "PREFIX dbr: <http://dbpedia.org/resource/>" +
                "\n" +
                "SELECT ?title ?shortDesc " +
                "WHERE\n {";
		queryString.append(s2);
		for (int i=0; i<userPhobias.size()-1; i++) {
			s2 = phobiaTemplate(userPhobias.get(i), language);
			queryString.append(s2);
			queryString.append(" UNION ");
		}
		if (userPhobias.size() != 0) {
			queryString.append(phobiaTemplate(userPhobias.get(userPhobias.size()-1), language));
		}
		queryString.append("}");
		
		if(userPhobias.size() == 0) {
			return Response.status(200).entity(JSON.toJSONString("")).build();
		} 
		
		Query query = QueryFactory.create(queryString.toString());
        QueryExecution qExe = QueryExecutionFactory.sparqlService( "http://dbpedia.org/sparql", query );
        ResultSet results = qExe.execSelect();
        
        ArrayList<HashMap<String, String>> phobias = new ArrayList<HashMap<String, String>>();
        int index = 0;
		while (results.hasNext()) {
			QuerySolution result = results.next();
			String title = result.getLiteral("title").getString();
			String shortDesc = result.getLiteral("shortDesc").getString();
			HashMap<String, String> parsedPhobia = new HashMap<>();
			parsedPhobia.put("resource", userPhobias.get(index));
			parsedPhobia.put("title", title);
			parsedPhobia.put("shortDesc", shortDesc);
			phobias.add(parsedPhobia);			
			index++;
		}
        
		
        String jsonObj = JSON.toJSONString(phobias);
        System.out.println(jsonObj.toString()); 
		return Response.status(200).entity(jsonObj).build();
	}
	
	@GET
	@Path("getActivities")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getActivities(@QueryParam("token") String token, @QueryParam("lang") String lang) throws JSONException {
		
		if (token == null || !LoginManager.checkAuthToken(token)) {
    		return Response.status(401).build();
    	}
		
		String language = "en";
		if (lang != null && !lang.equals("")) {
			language = lang;
		}
		
        HashMap<String,String> activities = new HashMap<String,String>();
        activities.put(Activities.WATCH.toString(), getActivityString(Activities.WATCH, language));
		activities.put(Activities.VISIT.toString(), getActivityString(Activities.VISIT, language));
		activities.put(Activities.PRACTICE.toString(), getActivityString(Activities.PRACTICE, language));
		
        String jsonObj = JSON.toJSONString(activities);
        System.out.println(jsonObj.toString()); 
		return Response.status(200).entity(jsonObj).build();
	}
	
	@GET
	@Path("getSubactivities")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSubactivities(@QueryParam("activity") String activity, @QueryParam("token") String token, @QueryParam("lang") String lang) throws JSONException {
		
		if (token == null || !LoginManager.checkAuthToken(token)) {
    		return Response.status(401).build();
    	}
		
		if (activity == null || activity.equals("")) {
			return Response.status(200).entity("").build();
		}
		
		String language = "en";
		if (lang != null && !lang.equals("")) {
			language = lang;
		}
		
        Activities a = Activities.valueOf(activity);
        String responseBody = "";
        switch(a) {
        case VISIT:
        	responseBody = getCities(language);
        	break;
		case PRACTICE:
			responseBody = getSports(language);
			break;
		case WATCH:
			responseBody = getMovies(language);
			break;
		default:break; 
        }
 
		return Response.status(200).entity(responseBody).build();
	}
	
	private String getMovies(String language){
		String queryString = "PREFIX dbp: <http://dbpedia.org/property/>\n" +
                "PREFIX  dct: <http://purl.org/dc/terms/>\n" +
                "PREFIX dbc: <http://dbpedia.org/resource/Category:>\n" +
                "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "\n" +
                "SELECT DISTINCT ?genre ?name (count(?movie) AS ?c) WHERE { "+
                "  ?genre dct:subject dbc:Film_genres.\n" +
                "  ?genre rdfs:label ?name.\n" +
                "  ?movie dbp:genre ?genre.\n" +
                "  FILTER (langMatches(lang(?name), \"" + language + "\")) \n" +
                " }GROUP BY ?genre ?name ORDER BY DESC(?c) LIMIT 40\n";
  
        Query query = QueryFactory.create(queryString);
        QueryExecution qExe = QueryExecutionFactory.sparqlService( "http://dbpedia.org/sparql", query );
        ResultSet results = qExe.execSelect();
        
        ArrayList<HashMap<String, String>> movies = new ArrayList<HashMap<String, String>>();
		while (results.hasNext()) {
			QuerySolution result = results.next();
			String name = result.getLiteral("name").getString();
			String genre = result.getResource("genre").getURI().toString();
			HashMap<String, String> parsedMovie = new HashMap<>();
			parsedMovie.put("name", name);
			parsedMovie.put("resource", genre);
			movies.add(parsedMovie);			
		}
		
        String jsonObj = JSON.toJSONString(movies);
		return jsonObj;
	  }
	
	private String getSports(String language){
		String queryString = "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n" +
                "PREFIX dbc: <http://dbpedia.org/resource/Category:>\n" +
                "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "\n" +
                "SELECT DISTINCT ?sportType ?sportName WHERE { "+
                "  ?sportType skos:broader dbc:Sports_by_type. \n" +
                "  ?sportType rdfs:label ?sportName.\n" +
                "  FILTER (langMatches(lang(?sportName), \"" + "en" + "\")) \n" +
                " }";
  
        Query query = QueryFactory.create(queryString);
        QueryExecution qExe = QueryExecutionFactory.sparqlService( "http://dbpedia.org/sparql", query );
        ResultSet results = qExe.execSelect();
        
        ArrayList<HashMap<String, String>> sports = new ArrayList<HashMap<String, String>>();
		while (results.hasNext()) {
			QuerySolution result = results.next();
			String sportName = result.getLiteral("sportName").getString();
			String sportType = result.getResource("sportType").getURI().toString();
			HashMap<String, String> parsedSport = new HashMap<>();
			parsedSport.put("name", sportName);
			parsedSport.put("resource", sportType);
			sports.add(parsedSport);			
		}
		
        String jsonObj = JSON.toJSONString(sports);
		return jsonObj;
	  }
	
	private String getCities(String language){
		String queryString = "PREFIX dct: <http://purl.org/dc/terms/>\n" +
                "PREFIX dbc: <http://dbpedia.org/resource/Category:>\n" +
                "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX dbp: <http://dbpedia.org/property/>\n" +
                "\n" +
                "SELECT DISTINCT ?citiesType ?citiesName WHERE { "+
                " ?citiesType dct:subject dbc:Cities_in_Romania. \n" +
                " ?citiesType rdfs:label ?citiesName. \n" + 
                "  FILTER (langMatches(lang(?citiesName), \"" + language + "\")) \n" +
                " }";
  
        Query query = QueryFactory.create(queryString);
        QueryExecution qExe = QueryExecutionFactory.sparqlService( "http://dbpedia.org/sparql", query );
        ResultSet results = qExe.execSelect();
        
        ArrayList<HashMap<String, String>> cities = new ArrayList<HashMap<String, String>>();
		while (results.hasNext()) {
			QuerySolution result = results.next();
			String citiesName = result.getLiteral("citiesName").getString();
			String citiesType = result.getResource("citiesType").getURI().toString();
			HashMap<String, String> parseCity = new HashMap<>();
			parseCity.put("name", citiesName);
			parseCity.put("resource", citiesType);
			cities.add(parseCity);			
		}
		
        String jsonObj = JSON.toJSONString(cities);
		return jsonObj;
	  }
	
	private ArrayList<String> getUserPhobiasFromLocalDb(String token, String activity, String subactivity) {
		String userId = LoginManager.getLoggedInUser(token);
		String subqueryChildren = "";
		if (userHasChild(userId)) {
			subqueryChildren = queryChildPhobias();
		}
		String resourceId;
		if (userId == null) {
			return null;
		} else {
			if (DatabaseManager.checkIfUserExists(userId) == false) {
				return null;
			} else {
				resourceId = DatabaseManager.getUserResourceId(userId);
				if (resourceId == null) {
					return null;
				}
			}
		}
		String subquery = phobiaTemplateForActivityType(activity, subactivity) + subqueryChildren;
		String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX dbo: <http://dbpedia.org/ontology/>\n" + "PREFIX dbr: <http://dbpedia.org/resource/>\n"
				+ "PREFIX dbc: <http://dbpedia.org/resource/Category:> \n"
				+ "PREFIX dbr: <http://dbpedia.org/resource/> \n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + "PREFIX db: <http://dbpedia.org/>\n"
				+ "SELECT (str(?p) AS ?phobiaName)  \n" + "WHERE {\n"
				+ "<%s> <http://www.semanticweb.org/tibabianca/ontologies/2016/0/phos#hasPhobia> ?p.\n"
				+ subquery 
				+ "}";
		Query query = QueryFactory.create(String.format(queryString, resourceId));
		QueryExecution qExe = QueryExecutionFactory.sparqlService("http://localhost:3030/ds/query", query);
		ResultSet results = qExe.execSelect();
		ArrayList<String> userPhobias = new ArrayList<String>();

		while (results.hasNext()) {
			String phobiaName = results.next().getLiteral("phobiaName").getString();
			System.out.println(phobiaName);
			userPhobias.add(phobiaName);
		}
		return userPhobias;
	}
	
	public static String phobiaTemplate(String phobiaName, String language) {
		String template = "{"
		+ "<%s> rdfs:label ?title.\n"
		+ "<%s> rdfs:comment ?shortDesc.\n"
		+ " FILTER (langMatches( lang(?title),  \"" + language + "\" ) && langMatches( lang(?shortDesc),  \"" + language + "\" ))\n"
				+ "}";
		return String.format(template, phobiaName, phobiaName, phobiaName, phobiaName);
	}
	
	private static String phobiaTemplateForActivityType(String activity, String subactivity){
		
		Activities a = Activities.valueOf(activity);
        String responseBody = "";
        switch(a) {
        case VISIT:
        	String firstPart = "";
        	if (visitTypeIsMountain(subactivity)) {
        		firstPart = "{?p <http://www.semanticweb.org/tibabianca/ontologies/2016/0/phos#isAssociatedWith> dbo:Mountain. \n}";
        	} else if (visitTypeIsPort(subactivity)) {
        		firstPart = "{?p <http://www.semanticweb.org/tibabianca/ontologies/2016/0/phos#isAssociatedWith> dbo:Beach. \n}";
        	}
        	responseBody = firstPart + "UNION {"
        			+ "?p <http://www.semanticweb.org/tibabianca/ontologies/2016/0/phos#activityType> db:Place."
        			+ "}";
        	break;
		case PRACTICE:
			responseBody = "{ ?p <http://www.semanticweb.org/tibabianca/ontologies/2016/0/phos#activityType> dbc:" + subactivity + ". }\n"
					+ "UNION {"
					+ "?p <http://www.semanticweb.org/tibabianca/ontologies/2016/0/phos#activityType> dbc:Sports_by_type."
					+ "}";
			break;
		case WATCH:
			responseBody = "{?p <http://www.semanticweb.org/tibabianca/ontologies/2016/0/phos#movieType> dbr:" + subactivity + ". }\n"
					+ "UNION {"
					+ "?p <http://www.semanticweb.org/tibabianca/ontologies/2016/0/phos#activityType> dbc:Film_genre"
					+ "}";
			break;
		default:break;
        }
		return responseBody;
	}
	
	private static Boolean visitTypeIsPort(String place){
		String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
				+ "PREFIX dbc: <http://dbpedia.org/resource/Category:> \n"
				+ "PREFIX dct: <http://purl.org/dc/terms/> \n"
				+ "ASK"
				+ "WHERE"
				+ " {"
				+ "	?city dct:subject dbc:Port_cities_and_towns_in_Romania ."
				+ "?city rdfs:label ?name ."
				+ "FILTER (regex(?name, '" + place + "'))}";
		
        Query query = QueryFactory.create(queryString); //s2 = the query above
        QueryExecution qExe = QueryExecutionFactory.sparqlService( "http://dbpedia.org/sparql", query );
        
        Boolean isPort = qExe.execAsk();
        return isPort;
	}
	
	private static Boolean visitTypeIsMountain(String place){
		String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
				+ "PREFIX dbc: <http://dbpedia.org/resource/Category:> \n"
				+ "PREFIX dct: <http://purl.org/dc/terms/> \n"
				+ "PREFIX dbp: <http://dbpedia.org/property/> \n"
				+ "SELECT ?elevation \n"
				+ "WHERE {"
				+ "?city dbp:elevationM ?elevation."
				+ "?city dct:subject dbc:Cities_in_Romania."
				+ "?city rdfs:label ?name."
				+ "FILTER (regex(?name, '" + place + "'))}";
  
        Query query = QueryFactory.create(queryString); //s2 = the query above
        QueryExecution qExe = QueryExecutionFactory.sparqlService( "http://dbpedia.org/sparql", query );
        ResultSet results = qExe.execSelect();
        if(results.hasNext()) {
        	Integer elevation = results.next().getLiteral("elevation").getInt();
        	if (elevation > 200) {
        		return true;
        	}
        }
        return false;
	}
	
	private ArrayList<String> getUserFriendPhobias(String friendEmail, String activity, String subactivity) {
		String friendResId = DatabaseManager.getUserFriendResourceId(friendEmail);
		String subqueryChildren = "";
		if (userHasChild(friendResId)) {
			subqueryChildren = queryChildPhobias();
		}
		String subquery = phobiaTemplateForActivityType(activity, subactivity) + subqueryChildren;
		String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "PREFIX dbc: <http://dbpedia.org/resource/Category:> \n"
				+ "PREFIX dbr: <http://dbpedia.org/resource/> \n"
				+ "PREFIX dbo: <http://dbpedia.org/ontology/>\n" + "PREFIX dbr: <http://dbpedia.org/resource/>\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + "PREFIX db: <http://dbpedia.org/>\n"
				+ "SELECT (str(?p) AS ?phobiaName)  \n" + "WHERE {\n"
				+ "?friend rdfs:email \"" + friendEmail + "@gmail.com\". " 
				+ "?friend <http://www.semanticweb.org/tibabianca/ontologies/2016/0/phos#hasPhobia> ?p.\n" 
				+ subquery
				+ "}";
		Query query = QueryFactory.create(queryString);
		QueryExecution qExe = QueryExecutionFactory.sparqlService("http://localhost:3030/ds/query", query);
		ResultSet results = qExe.execSelect();
		ArrayList<String> userFriendPhobias = new ArrayList<String>();

		while (results.hasNext()) {
			String phobiaName = results.next().getLiteral("phobiaName").getString();
			System.out.println(phobiaName);
			userFriendPhobias.add(phobiaName);
		}
		
		return userFriendPhobias;
	}
	
	private static Boolean userHasChild(String userResourceId){
		String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
				+ "PREFIX dbc: <http://dbpedia.org/resource/Category:> \n"
				+ "PREFIX dct: <http://purl.org/dc/terms/> \n"
				+ "ASK"
				+ "WHERE"
				+ " {"
				+ "	<" + userResourceId + "> <http://www.semanticweb.org/tibabianca/ontologies/2016/0/phos#hasChildren> true.}";
		
        Query query = QueryFactory.create(queryString); //s2 = the query above
        QueryExecution qExe = QueryExecutionFactory.sparqlService( "http://dbpedia.org/sparql", query );
        
        Boolean hasChildren = qExe.execAsk();
        return hasChildren;
	}
	
	private static String queryChildPhobias() {
		String query = "UNION {"
				+ "?p <http://www.semanticweb.org/tibabianca/ontologies/2016/0/phos#boundWith> dbo:Child."
				+ "}";
		return query;
	}
}

