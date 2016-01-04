package login;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import models.User;

@Path("/user")
public class UserManager {

	@GET
	@Path("/userDetails")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserDetails(@QueryParam("token") String token) throws JSONException {

		if (token == null || !LoginManager.checkAuthToken(token)) {
			return Response.status(401).build();
		}

		String userId = LoginManager.getLoggedInUser(token);
		String resourceId;
		if (userId == null) {
			return Response.status(401).build();
		} else {
			if (DatabaseManager.checkIfUserExists(userId) == false) {
				return Response.status(401).build();
			} else {
				resourceId = DatabaseManager.getUserResourceId(userId);
				if (resourceId == null) {
					return Response.status(401).build();
				}
			}
		}

		QueryExecution qe = QueryExecutionFactory.sparqlService("http://localhost:3030/ds/query",
				"PREFIX foaf: <http://xmlns.com/foaf/0.1/>"
						+ "PREFIX phos: <http://www.semanticweb.org/tibabianca/ontologies/2016/0/phos#>"
						+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + "SELECT ?photo ?name ?email WHERE {"
						+ "<" + resourceId + "> foaf:depiction ?photo. \n" + " <" + resourceId
						+ "> foaf:firstName ?name. \n" + " <" + resourceId + "> rdfs:email ?email. \n" + " }");
		ResultSet results = qe.execSelect();
		String photo = null;
		String name = null;
		String email = null;
		if (results.hasNext()) {
			QuerySolution result = results.next();
			photo = result.getResource("photo").getURI().toString();
			name = result.getLiteral("name").getString();
			email = result.getLiteral("email").getString();
		}

		HashMap<String, String> parsedUser = new HashMap<>();
		parsedUser.put("photo", photo);
		parsedUser.put("name", name);
		parsedUser.put("email", email);

		String jsonObj = JSON.toJSONString(parsedUser);
		System.out.println(jsonObj.toString());
		return Response.status(200).entity(jsonObj).build();
	}

	@Path("/addFriend")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addFriend(@QueryParam("friendUsername") String friendUsername,
			@QueryParam("token") String token) {

		if (token == null || !LoginManager.checkAuthToken(token)) {
			return Response.status(401).build();
		} else if (friendUsername == null || friendUsername.equals("")) {
    		return Response.status(400).build();
		} else {
			String userFriendResourceId = DatabaseManager.getUserFriendResourceId(friendUsername);
			if (userFriendResourceId == null) {
				return Response.status(406).build();
			}
			DatabaseManager.addUserFriend(token, friendUsername, userFriendResourceId);
		}

		return Response.status(200).build();
	}

	@GET
	@Path("/friends")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserFriends(@QueryParam("token") String token) throws JSONException {
		if (token == null || !LoginManager.checkAuthToken(token)) {
			return Response.status(401).build();
		}

		String userId = LoginManager.getLoggedInUser(token);
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

		String queryString = "PREFIX phos: <http://www.semanticweb.org/tibabianca/ontologies/2016/0/phos#>\n"
				+ "PREFIX phosR:<http://www.semanticweb.org/tibabianca/ontologies/2016/0/phos/>\n"
				+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + "SELECT ?friendName ?friendImage ?friendEmail \n"
				+ "WHERE {\n" + "<%s> foaf:knows ?friend.\n" 
				+ "?friend rdfs:email ?friendEmail.\n"
				+ "?friend foaf:firstName ?friendName.\n"
				+ "OPTIONAL{?friend foaf:depiction ?friendImage.}" + "}";
		Query query = QueryFactory.create(String.format(queryString, resourceId));
		QueryExecution qExe = QueryExecutionFactory.sparqlService("http://localhost:3030/ds/query", query);
		ResultSet results = qExe.execSelect();
		ArrayList<HashMap<String, String>> friends = new ArrayList<HashMap<String, String>>();
		while (results.hasNext()) {
			QuerySolution result = results.next();
			String name = result.getLiteral("friendName").getString();
			String friendEmail = result.getLiteral("friendEmail").getString();
			String image = "";
			if (result.getResource("friendImage") != null) {
				image = result.getResource("friendImage").getURI().toString();
			}
			HashMap<String, String> friend = new HashMap<>();
			friend.put("name", name);
			friend.put("imageUrl", image);
			friend.put("email", friendEmail);
			friends.add(friend);
		}
		String jsonObj = JSON.toJSONString(friends);
		System.out.println(jsonObj.toString());
		return Response.status(200).entity(jsonObj).build();
	}

	@GET
	@Path("/getAnimals")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAnimals(@QueryParam("token") String token, @QueryParam("lang") String lang) throws JSONException {

		if (token == null || !LoginManager.checkAuthToken(token)) {
			return Response.status(401).build();
		}

		String language = "en";
		if (lang != null && !lang.equals("")) {
			language = lang;
		}
		
		String userId = LoginManager.getLoggedInUser(token);
		String resourceId;
		if (userId == null) {
			return Response.status(401).build();
		} else {
			if (DatabaseManager.checkIfUserExists(userId) == false) {
				return Response.status(401).build();
			} else {
				resourceId = DatabaseManager.getUserResourceId(userId);
				if (resourceId == null) {
					return Response.status(401).build();
				}
			}
		}
		// http://dbpedia.org/page/Dog
		// http://dbpedia.org/page/Cat
		// http://dbpedia.org/page/Fish
		// http://dbpedia.org/page/Bird
		// http://dbpedia.org/page/Arachnid
		// http://dbpedia.org/page/Insect
		// http://dbpedia.org/page/Squirrel
		// http://dbpedia.org/page/Mouse

		String prefix = "http://dbpedia.org/page/";
		HashMap<String, String> animalsMap = new HashMap<>();
		String[] animals = { "Dog", "Cat", "Fish", "Bird", "Arachnid", "Insect", "Squirrel", "Mouse" };
		String[] animals_FR = { "Chien", "Chat", "Poisson", "Oiseau", "Arachnid", "Insecte", "Ecureuil", "Souris" };
		for (int i = 0; i < animals.length; i++) {
			if (language.equals("fr")) {
				animalsMap.put(prefix + animals[i], animals_FR[i]);
			} else {
				animalsMap.put(prefix + animals[i], animals[i]);
			}
		}

		String jsonObj = JSON.toJSONString(animalsMap);
		System.out.println(jsonObj.toString());
		return Response.status(200).entity(jsonObj).build();
	}

	@Path("/saveUserData")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response saveUserData(@QueryParam("gender") String gender,
			@QueryParam("animal") String animal, @QueryParam("hasChildren") Boolean hasChildren,
			@QueryParam("token") String token) throws ParseException {

		if (token == null || !LoginManager.checkAuthToken(token)) {
			return Response.status(401).build();
		} else if (gender == null || gender.equals("") || animal.equals("") || hasChildren.equals("") || animal == null || hasChildren == null) {
			return Response.status(400).build();
		} else {
			User user = new User();
			//SimpleDateFormat dateF = new SimpleDateFormat("dd-mm-yyyy");
			//Date date = dateF.parse(birthDate);
			//user.birthdate = date;
			user.gender = gender;
			user.animal = animal;
			user.hasChildren = hasChildren;
			DatabaseManager.updateUser(user, token);
		}
		return Response.status(200).build();
	}

	@GET
	@Path("/getUserData")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserData(@QueryParam("token") String token) throws JSONException {

		if (token == null || !LoginManager.checkAuthToken(token)) {
			return Response.status(401).build();
		}

		String userId = LoginManager.getLoggedInUser(token);
		String resourceId;
		if (userId == null) {
			return Response.status(401).build();
		} else {
			if (DatabaseManager.checkIfUserExists(userId) == false) {
				return Response.status(401).build();
			} else {
				resourceId = DatabaseManager.getUserResourceId(userId);
				if (resourceId == null) {
					return Response.status(401).build();
				}
			}
		}
		User user = DatabaseManager.getUserData(resourceId);
		HashMap<String, String> parsedUser = new HashMap<>();
		parsedUser.put("gender", user.getGender());
		parsedUser.put("hasChildren", user.getHasChildren().toString());
		parsedUser.put("animal", user.getAnimal());

		String jsonObj = JSON.toJSONString(parsedUser);
		System.out.println(jsonObj.toString());
		return Response.status(200).entity(jsonObj).build();
	}
}
