package login;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import com.alibaba.fastjson.JSON;

import dbConnections.DatabaseManager;
import models.User;
 
@Path("/login")
public class LoginManager {
	
	public static HashMap<String, String> tokens = new HashMap<String, String>();
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response login(InputStream incomingData) {
		StringBuilder builder = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
			while ((line = in.readLine()) != null) {
				builder.append(line);
			}
		} catch (Exception e) {
			System.out.println("Error Parsing: - ");
		}
		
		User user = JSON.parseObject(builder.toString(), User.class);
		HashMap<String, String> responseToken = new HashMap<String, String>();
		String generateAuthToken = this.generateAuthToken();
		responseToken.put("token", generateAuthToken);
		JSONObject jsonObject = new JSONObject (responseToken);
		tokens.put(user.getId(), generateAuthToken);
		DatabaseManager.addUser(user);
		return Response.status(200).entity(jsonObject.toString()).build();
	}
	
	private String generateAuthToken(){
		UUID key = UUID.randomUUID();
		return key.toString();
	}
	
	public static boolean checkAuthToken(String token) {
		return tokens.containsValue(token);
	} 
	
	public static String getLoggedInUser(String token) {
		for (Entry<String, String> entry :tokens.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			if (value.equals(token)) {
				return key;
			}
		}
		return null;
	}
}