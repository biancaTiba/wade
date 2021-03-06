package login;
 
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.alibaba.fastjson.JSON;

import models.User;
 
@Path("/login")
public class Login { 
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response login(InputStream incomingData) {
		StringBuilder crunchifyBuilder = new StringBuilder();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
			String line = null;
			while ((line = in.readLine()) != null) {
				crunchifyBuilder.append(line);
			}
		} catch (Exception e) {
			System.out.println("Error Parsing: - ");
		}
		System.out.println("Data Received: " + crunchifyBuilder.toString());
		
		User object1 = JSON.parseObject(crunchifyBuilder.toString(), User.class);
		System.out.println(object1.getName());
		System.out.println(object1.getId());
		System.out.println(object1.getImageUrl());
		// return HTTP response 200 in case of success
		return Response.status(200).entity(crunchifyBuilder.toString()).build();
	}
	
}