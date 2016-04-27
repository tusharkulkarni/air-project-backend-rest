package com.air;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
//import org.python.core.packagecache.SysPackageManager;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class JsonParser {
	//////////////////////////////////////////////
	public static JSONArray jsonparser(ArrayList<String> al,ArrayList<HashMap<String,Float>> hmal, JSONArray recipes, String disease) throws IOException, ParseException, JSONException, UnirestException {
		// TODO Auto-generated method stub
		//Calendar calendar = Calendar.getInstance();
		//String timestamp= calendar.getTime().toString().replaceAll("\\s+:","").replaceAll(":","");
		//String title;
		//int amount;

		//FileReader filereader = new FileReader("air_Sat Apr 16 141546 EDT 2016.json");
		//BufferedReader br = new BufferedReader(filereader);
		//JSONParser parser = new JSONParser();
		//JSONArray a = (JSONArray) parser.parse(filereader);
		//JSONObject object = new JSONObject(br.readLine());
		//JSONObject getObject = object.getJSONObject("JObjects");
		//JSONArray getArray = object.getJSONArray("results");
		//JSONArray getAyyay =recipes;
		//FileWriter filewriter = new FileWriter(disease+"_"+timestamp+".json");
		/*
		for(int i = 0; i < getArray.length(); i++){
		JSONObject objects = getArray.getJSONObject(i);
		HttpResponse<JsonNode> response = Unirest.get("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/14992/information?includeNutrition=true")
				.header("X-Mashape-Key", "EzPVzV3OIImshUC16NzTUL4Q9zE5p1fS8uIjsnHrZlqZDU38BN")
				.asJson();


		System.out.println(objects.get("id").toString());

		}*/
		System.out.println("*****************************inside JSONParser****************************");
		System.out.println(al);
		JSONArray retMainArray = new JSONArray();

		for(int j=0;j<al.size();j++){

			JSONObject returningObj = new JSONObject(); 	

			boolean accept=true;
			float total=0,count=0;
			HttpResponse<JsonNode> response = Unirest.get("https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/"+al.get(j)+"/information?includeNutrition=true")
					.header("X-Mashape-Key", "q8OegLa2iqmshvbsYklWvajeDcqZp1e63fSjsnmhLIw9xoIlw1")
					.asJson();
			//System.out.println(response.getBody());
			JSONObject rec = new JSONObject(response.getBody().toString());
			String rec_title= rec.getString("title");
			String spoon_url=rec.getString("spoonacularSourceUrl");
			//System.out.println(rec.toString());
			JSONObject nutrition = rec.getJSONObject("nutrition");
			JSONArray  nutrients = nutrition.getJSONArray("nutrients");
			HashMap<String,Float> hm=new HashMap<String,Float>();

			//System.out.println(al.get(j));
			JSONArray returningNutArray= new JSONArray();
			for(int i = 0; i < nutrients.length(); i++){
				JSONObject nutrient = nutrients.getJSONObject(i);
				//System.out.println(nutrient.get("title").toString()+"\t"+nutrient.get("amount").toString());
				hm.put(nutrient.get("title").toString().toLowerCase(), Float.valueOf((nutrient.get("amount").toString())));
				String nutName=nutrient.get("title").toString().toLowerCase();
				//System.out.println(nutName);
				if(nutName.equals("calories") || nutName.equals("protein") ||nutName.equals("carbohydrates") ||nutName.equals("fat"))
				{
					returningNutArray.put(nutrients.getJSONObject(i));
				}

			}
			//System.out.println(returningNutArray);


			//System.out.println("hm : " + hmal.get(0).toString());
			for( Map.Entry<String, Float> needs:hmal.get(0).entrySet() )
			{	total++;		
			if(hm.containsKey(needs.getKey()))				
			{
				//System.out.println("needs.getKey : " + needs.getKey());
				//System.out.println("recipe "+hm.get(needs.getKey()).toString()+" xml "+needs.getValue().toString());
				if(hm.get(needs.getKey())<needs.getValue())
					count++;
				//accept=false;
			}
			//else accept=false;
			else count+=0.2;
			}
			for( Map.Entry<String, Float> excess:hmal.get(1).entrySet() )
			{	total++;
			if(hm.containsKey(excess.getKey()))
			{
				//System.out.println("recipe "+hm.get(excess.getKey()).toString()+" xml "+excess.getValue().toString());
				if(hm.get(excess.getKey())>excess.getValue())
					//accept=false;
					count+=1.3;
			}

			}
			//System.out.println((String.valueOf(count/total)));
			if( (count/total) > 0.20)accept=false;
			if(accept==true)
			{
				//filewriter.write(String.valueOf(al.get(j))+" "+rec_title.toString()+" "+spoon_url+"\n");
				returningObj.put("title", rec_title);
				returningObj.put("url", spoon_url);
				returningObj.put("readyInMinutes", rec.getLong("readyInMinutes"));
				returningObj.put("vegetarian", rec.getBoolean("vegetarian"));
				returningObj.put("vegan", rec.getBoolean("vegan"));
				returningObj.put("recipeId", rec.getLong("id"));
				returningObj.put("imageUrl", rec.getString("image"));
				returningObj.put("nutrients", returningNutArray);

				retMainArray.put(returningObj);
				/*for(int i = 0; i < nutrients.length(); i++){
					JSONObject nutrient = nutrients.getJSONObject(i);
					//System.out.println(nutrient.get("title").toString().toLowerCase()+"\n");
				}*/
			}
		}
		System.out.println(retMainArray);
		//filewriter.close();


		Random rnd = new Random();
		rnd.setSeed(System.currentTimeMillis());
		for (int i = retMainArray.length() - 1; i >= 0; i--)
		{
			int j = rnd.nextInt(i + 1);
			// Simple swap
			Object object = retMainArray.get(j);
			retMainArray.put(j, retMainArray.get(i));
			retMainArray.put(i, object);
		}


		return retMainArray;

	}

}
