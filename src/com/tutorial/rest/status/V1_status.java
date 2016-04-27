package com.tutorial.rest.status;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Random;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONArray;
import org.json.JSONObject;

import com.air.RecipeFinder;

import rssreader.ReadTest;


/**
 * This is the root path for our restful api service
 * In the web.xml file, we specified that /api/* need to be in the URL to
 * get to this class.
 * 
 * We are versioning the class in the URL path.  This is the first version v1.
 * Example how to get to the root of this api resource:
 * http://localhost:7001/com.youtube.rest/api/v1/status
 * 
 * @author 308tube
 *
 */
@Path("/v1/recipe/")
public class V1_status {

	private static final String api_version = "00.01.00"; //version of the api

	/**
	 * This method sits at the root of the api.  It will return the name
	 * of this api.
	 * 
	 * @return String - Title of the api
	 * @throws Exception 
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String returnTitle() throws Exception {
		JSONArray jsonArray=null;
		JSONObject jo = getJsonObject(null);

		try{
			org.json.JSONArray jArray = RecipeFinder.trigger(jo);
			jsonArray = new JSONArray(jArray.toString());

		}catch(Exception e){
			e.printStackTrace();
		}
		return jsonArray.toString();
	}
	
	

	private JSONObject getJsonObject(String queryString) throws org.json.JSONException{
		System.out.println("Query String : " + queryString);
		String queryStringSplit[] = queryString.split("&");
		System.out.println("Splitting query");
		JSONObject jsonObj = new JSONObject();
		JSONObject queryObj = new JSONObject();
		for (String string : queryStringSplit) {
			System.out.println(string);
			String insertArray[] = string.split("=");
			String key = insertArray[0];
			String value = insertArray[1];			
			if(value.equalsIgnoreCase("undefined")){
				value="";
			}
			if(string.contains("cuisine") || string.contains("excludeIngredients") ||
					string.contains("includeIngredients") || string.contains("maxCalories") ||
					string.contains("maxCarbs") || string.contains("maxFat") ||
					string.contains("maxProtein") || string.contains("minCalories") ||
					string.contains("minCarbs") || string.contains("minFat") ||
					string.contains("minProtein") || string.contains("querywords")||
					string.contains("intolerances")){
				queryObj.put(key, value);				
			}else{
				jsonObj.put(key, value);
			}

		}
		queryObj.put("type", "");
		jsonObj.put("query", queryObj);

		System.out.println("Json : "+ jsonObj);
		
		return jsonObj;
	}

	/**
	 * This method will return the version number of the api
	 * Note: this is nested one down from the root.  You will need to add version
	 * into the URL path.
	 * 
	 * Example:
	 * http://localhost:7001/com.youtube.rest/api/v1/status/version
	 * 
	 * @return String - version number of the api
	 */
	@Path("/version")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String returnVersion() {
		return "<p>Version:</p>" + api_version;
	}

	@Path("/{queryString}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getRecipes(@PathParam("queryString") String queryString) throws Exception {
		JSONObject jsonObj = getJsonObject(queryString);
		System.out.println(queryString);
		JSONArray jsonArray=null;
		try{
			//org.json.JSONArray jArray = RecipeFinder.trigger(jsonObj);
			org.json.JSONArray jArray = getJsonArray();
			jsonArray = new JSONArray(jArray.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//Random to be deleted
		//************random starts************
		Random rnd = new Random();
		for (int i = jsonArray.length() - 1; i >= 0; i--)
		{
			int j = rnd.nextInt(i + 1);
			// Simple swap
			Object object = jsonArray.get(j);
			jsonArray.put(j, jsonArray.get(i));
			jsonArray.put(i, object);
		}
		//************random ends************
		System.out.println("*********************Final syso start********************");
		System.out.println(jsonArray.toString());
		System.out.println("*********************Final syso end********************");
		
		return jsonArray.toString();
	}
	
	@Path("/rssFeeds/{disease}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getRssFeed(@PathParam("disease") String disease) throws Exception {
		//JSONObject jsonObj = getJsonObject(disease);
		org.json.JSONArray jsonArray=null;
		try{
			jsonArray = ReadTest.rssFeedReader(disease);
		}catch(Exception e){
			e.printStackTrace();
		}
		return jsonArray.toString();
	}



	public org.json.JSONArray getJsonArray(){
		System.out.println("*******Reading input file**********");
		//new code starts
		ClassLoader classLoader = getClass().getClassLoader();
		String filename="input.json";
		File file = new File(classLoader.getResource(filename).getFile()); 
		// new code ends
		FileReader filereader = null;
		BufferedReader br = null;
		String str=null;
		org.json.JSONArray object =null;
		StringBuffer sb = new StringBuffer();
		try{
			//original code
			//filereader = new FileReader("input.json");
			//new code
			filereader = new FileReader(file);
			System.out.println("*******input file read successfully**********");
			br = new BufferedReader(filereader);


			while(null != (str=br.readLine())){
				sb.append(str);
			}
			object = new org.json.JSONArray(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		//System.out.println(sb.toString());
		//System.out.println(object.toString());
		return object;

	}
	
	


	
}