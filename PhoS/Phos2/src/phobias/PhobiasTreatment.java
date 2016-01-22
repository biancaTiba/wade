package phobias;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alibaba.fastjson.JSON;

import login.LoginManager;

@Path("/context")
public class PhobiasTreatment {

	@GET
	@Path("/getTreatment")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTreatment(@QueryParam("phobia") String phobia, @QueryParam("token") String token, @QueryParam("lang") String lang) throws JSONException, IOException {
		
		if (token == null || !LoginManager.checkAuthToken(token)) {
    		return Response.status(401).build();
    	} else if (phobia == null) {
    		return Response.status(400).build();
    	}
		
		String language = "en";
		if (lang != null && !lang.equals("")) {
			language = lang;
		}
		
		String treatsString = language.equals("fr") ? "traitement" : "treatments";
		String tipsString = language.equals("fr") ? "pointes" : "tips" ;
		
		JSONArray treatmentsResults = getGoogleResults(phobia + treatsString);
		JSONArray tipsResults = getGoogleResults(phobia + tipsString);
		
		ArrayList<HashMap<String,String>> treatments = new ArrayList<>();
		for (int i=0; i<treatmentsResults.length(); i++) {
			JSONObject result = treatmentsResults.getJSONObject(i);
			HashMap<String, String> treatment = new HashMap<>();
			treatment.put("title", result.get("title").toString());
			treatment.put("url", result.get("url").toString());
			treatments.add(treatment);
		}
		
		ArrayList<HashMap<String,String>> tips = new ArrayList<>();
		for (int i=0; i<tipsResults.length(); i++) {
			JSONObject result = tipsResults.getJSONObject(i);
			HashMap<String, String> tip = new HashMap<>();
			tip.put("title", result.get("title").toString());
			tip.put("url", result.get("url").toString());
			tips.add(tip);
		}
		
		if (phobia.toLowerCase().contains("phobia")) {
			String phobiaPrefix = phobia.replace("phobia", "");
			
			HashMap<String, String> treatment = new HashMap<>();
			treatment.put("title", phobia + " treatments");
			treatment.put("url", "http://common-phobias.com/"+ phobiaPrefix +"/treatment.htm");
			treatments.add(treatment);
			
			HashMap<String, String> tip = new HashMap<>();
			tip.put("title", phobia + " tips");
			tip.put("url", "http://common-phobias.com/"+ phobiaPrefix +"/tips.htm");
			tips.add(tip);
		}
	
		HashMap<String,ArrayList<HashMap<String,String>>> suggestions = new HashMap<>();
		suggestions.put("treatments", treatments);
		suggestions.put("tips", tips);
		return Response.status(200).entity(JSON.toJSONString(suggestions)).build();
	}
	
	private static JSONArray getGoogleResults(String keyword) throws IOException{
		String google = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=";
	    String search = keyword;
	    String charset = "UTF-8";

	    URL url = new URL(google + URLEncoder.encode(search, charset));
	    
	    StringBuilder sb = new StringBuilder();
		URLConnection urlConn = null;
		InputStreamReader in = null;
		try {
			urlConn = url.openConnection();
			if (urlConn != null)
				urlConn.setReadTimeout(60 * 1000);
			if (urlConn != null && urlConn.getInputStream() != null) {
				in = new InputStreamReader(urlConn.getInputStream(),
						Charset.defaultCharset());
				BufferedReader bufferedReader = new BufferedReader(in);
				if (bufferedReader != null) {
					int cp;
					while ((cp = bufferedReader.read()) != -1) {
						sb.append((char) cp);
					}
					bufferedReader.close();
				}
			}
		in.close();
		} catch (Exception e) {
			throw new RuntimeException("Exception while calling URL", e);
		} 
		JSONObject myjson = new JSONObject(sb.toString());
		if (myjson.get("responseData") != null) {
			JSONObject responseData = myjson.getJSONObject("responseData");
			if (responseData.getJSONArray("results") != null) {
				JSONArray results = responseData.getJSONArray("results");
				return results;
			}
		}
		return null;
	}
}
