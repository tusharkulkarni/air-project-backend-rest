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

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.codehaus.jettison.json.JSONArray;
import org.json.JSONObject;

import com.air.RecipeFinder;


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
@Path("/v1/")
public class V1_status {

	private static final String api_version = "00.01.00"; //version of the api

	/**
	 * This method sits at the root of the api.  It will return the name
	 * of this api.
	 * 
	 * @return String - Title of the api
	 * @throws Exception 
	 */
	@Path("/recipe")
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

	@Path("/recipe/{queryString}")	
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

	@Path("/rssFeeds/{disease}/{preferences}/{lifestyle}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getRssFeed(@PathParam("disease") String disease, @PathParam("preferences") String preferences, @PathParam("lifestyle") String lifestyle) throws Exception {
		//JSONObject jsonObj = getJsonObject(disease);
		

		System.out.println("preferences: "+preferences);
		HttpSolrServer solr = new HttpSolrServer("http://utkkefe93c38.tushk1990.koding.io:8983/solr/rssfeed/");		
		SolrQuery query = new SolrQuery();
		System.out.println("Initialized solr core...");
		String[] preferenceArray = preferences.split(",");
		
		StringBuilder sb = new StringBuilder();
		for (String string : preferenceArray) {
			sb.append(" OR title:"+string);
		}
		int activityLevel = Integer.parseInt(lifestyle);
		switch (activityLevel) {
		case 0:			
			sb.append(" OR (title:exercise)^3");
			break;
		case 1:		
			sb.append(" OR (title:exercise)^1.5");
			break;		
		case 2:			
			sb.append(" OR (title:* -title:exercise)^2");
			sb.append(" OR (title:exercise)^0.15");
			break;
		case 3:		
			sb.append(" OR (title:* -title:exercise)^3");
			sb.append(" OR (title:exercise)^0.15");
			break;	
		}
		sb.append(" OR (title:"+disease+")^2 OR (disease:"+disease+")^2");
		query.setQuery("*:*"+sb.toString());
		query.addFacetField("title");
		query.setFacet(true);
		query.setRows(100);
		System.out.println(query.toString());

		query.setStart(0);    
		//query.setFilterQueries("title:food");
		//query.set("defType", "edismax");
		System.out.println("Recieving response...");
		org.json.JSONArray feedArray = new org.json.JSONArray();
		org.json.JSONObject jsonObject = null;
		org.json.JSONObject returnObject = new JSONObject();


		try{
			QueryResponse response = solr.query(query);
			//System.out.println("facet Field info: " + response.getFacetFields());
			SolrDocumentList results = response.getResults();
			//System.out.println("results: " + response.toString());
			for (SolrDocument solrDocument : results) {
				jsonObject = new JSONObject();
				String title = solrDocument.get("title").toString();
				jsonObject.put("id",solrDocument.get("id"));
				jsonObject.put("title",title);
				for (String str : title.split(" ")) {
					for (FacetField.Count facetField : response.getFacetFields().get(0).getValues()) {	
						if(str.equalsIgnoreCase(facetField.getName())){
							jsonObject.put("facet",facetField.getName());							
							break;
						}
					}
				}
				jsonObject.put("link",solrDocument.get("link"));
				feedArray.put(jsonObject);
			}
			org.json.simple.JSONArray facetArray = new org.json.simple.JSONArray();
			org.json.simple.JSONObject facetObj = null;
			int count = 0;
			for (FacetField.Count facet : response.getFacetFields().get(0).getValues()) {	
				facetObj = new org.json.simple.JSONObject();				
				facetObj.put("title", facet.getName());
				facetArray.add(facetObj);
				if(count == 10)
					break;
				count++;
			}
			facetArray.add(new JSONObject().put("title", "All"));


			returnObject.put("facets", facetArray);
			returnObject.put("feed", feedArray);
			System.out.println(returnObject);

		}catch(Exception e){
			e.printStackTrace();

		}
		//org.json.JSONObject jsonObj=querySolr();
		/*try{
			jsonArray = ReadTest.rssFeedReader(disease);
		}catch(Exception e){
			e.printStackTrace();
		}*/
		querySolr();

		return returnObject.toString();
	}


	@Path("/rssFeeds/{disease}/{preferences}/{facet}/{lifestyle}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getRssFeed(@PathParam("disease") String disease, @PathParam("preferences") String preferences, @PathParam("facet") String facet, @PathParam("lifestyle") String lifestyle) throws Exception {
		System.out.println("preferences: "+preferences);
		String[] preferenceArray = preferences.split(",");
		HttpSolrServer solr = new HttpSolrServer("http://utkkefe93c38.tushk1990.koding.io:8983/solr/rssfeed/");		
		SolrQuery query = new SolrQuery();
		System.out.println("Initialized solr core...");
		
		StringBuilder sb = new StringBuilder();
		for (String string : preferenceArray) {
			sb.append(" OR title:"+string);
		}
		int activityLevel = Integer.parseInt(lifestyle);
		switch (activityLevel) {
		case 0:			
			sb.append(" OR (title:exercise)^3");
			break;
		case 1:		
			sb.append(" OR (title:exercise)^1.5");
			break;		
		case 2:			
			sb.append(" OR (title:* -title:exercise)^2");
			sb.append(" OR (title:exercise)^0.15");
			break;
		case 3:		
			sb.append(" OR (title:* -title:exercise)^3");
			sb.append(" OR (title:exercise)^0.15");
			break;	
		}
		sb.append(" OR (title:"+disease+")^1.5 OR (disease:"+disease+")^2");
		query.setQuery("*:*"+sb.toString());
		query.addFacetField("title");
		query.setFacet(true);
		query.setRows(100);
		query.setStart(0);  
		query.setFilterQueries("title:"+facet);		
		System.out.println("query.toString(): " + query.toString());
		

		System.out.println("Recieving response...");
		org.json.JSONArray feedArray = new org.json.JSONArray();
		org.json.JSONObject jsonObject = null;
		org.json.JSONObject returnObject = new JSONObject();		
		try{
			QueryResponse response = solr.query(query);
			SolrDocumentList results = response.getResults();
			for (SolrDocument solrDocument : results) {
				jsonObject = new JSONObject();				
				String title = solrDocument.get("title").toString();
				jsonObject.put("id",solrDocument.get("id"));
				jsonObject.put("title",title);
				for (String str : title.split(" ")) {
					for (FacetField.Count facetField : response.getFacetFields().get(0).getValues()) {	
						if(str.equalsIgnoreCase(facetField.getName())){
							jsonObject.put("facet",facetField.getName());							
							break;
						}
					}
				}
				jsonObject.put("link",solrDocument.get("link"));
				feedArray.put(jsonObject);
			}
			org.json.simple.JSONArray facetArray = new org.json.simple.JSONArray();
			org.json.simple.JSONObject facetObj = null;
			int count = 1;
			for (FacetField.Count facetField : response.getFacetFields().get(0).getValues()) {	
				facetObj = new org.json.simple.JSONObject();				
				facetObj.put("title", facetField.getName());
				facetArray.add(facetObj);
				if(count == 10)
					break;
				count++;
			}
			facetArray.add(new JSONObject().put("title", "All"));


			returnObject.put("facets", facetArray);
			returnObject.put("feed", feedArray);
			System.out.println(returnObject);

		}catch(Exception e){
			e.printStackTrace();

		}
		//org.json.JSONObject jsonObj=querySolr();
		/*try{
			jsonArray = ReadTest.rssFeedReader(disease);
		}catch(Exception e){
			e.printStackTrace();
		}*/
		querySolr();

		return returnObject.toString();
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

	public static void main(String[] args) {
		new V1_status().querySolr();
	}

	public org.json.JSONObject querySolr(){
		return null;
	}	
}