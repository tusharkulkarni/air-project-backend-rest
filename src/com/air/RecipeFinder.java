package com.air;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;



public class RecipeFinder {

	/* protected static SpellDictionaryHashMap dictionary = null;
	    protected static SpellChecker spellChecker = null;

	    static {

	        try {

	            dictionary =
	                new SpellDictionaryHashMap(new
	                File("C:\\Users\\Saumitra\\workspace\\air2\\english.0"));
	        }
	        catch (IOException e) {

	            e.printStackTrace();
	        }
	        spellChecker = new SpellChecker(dictionary);
	    }

	    public static List getSuggestions(String word,
	        int threshold) {

	        return spellChecker.getSuggestions(word, threshold);
	    }
	 */

/*
	public static JSONObject getJsonObject(){
		FileReader filereader = null;
		BufferedReader br = null;
		String str=null;
		JSONObject object =null;
		StringBuffer sb = new StringBuffer();
		try{
			filereader = new FileReader("input.json");
			br = new BufferedReader(filereader);


			while(null != (str=br.readLine())){
				sb.append(str);
			}
			object = new JSONObject(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		//System.out.println(sb.toString());
		//System.out.println(object.toString());
		return object;

	}
	*/
	public static void main(String[] args) throws UnirestException, IOException, JSONException, ParseException  
	{
		/*System.out.println(getJsonObject().toString());
		System.out.println(RecipeFinder.trigger(getJsonObject()).toString());*/
		//System.out.println(RecipeFinder.getJsonObject().toString());
	}

	public static JSONArray trigger(JSONObject object) throws UnirestException, IOException, JSONException, ParseException {


		//JSONObject object = getJsonObject();

		System.out.println("recieved object : " + object.toString());
		Calendar calendar = Calendar.getInstance();
		String timestamp= calendar.getTime().toString().replaceAll("\\s+:","").replaceAll(":","");
		String cuisine=object.getJSONObject("query").getString("cuisine").replaceAll(" ", "+").replaceAll(",", "%2C");
		String excludeIngredients=object.getJSONObject("query").getString("excludeIngredients").replaceAll(" ", "+").replaceAll(",", "%2C");
		String includeIngredients=object.getJSONObject("query").getString("includeIngredients").replaceAll(" ", "+").replaceAll(",", "%2C");
		String intolerances=object.getJSONObject("query").getString("intolerances").replaceAll(" ", "+").replaceAll(",", "%2C");
		String maxCalories=object.getJSONObject("query").getString("maxCalories");
		String maxCarbs=object.getJSONObject("query").getString("maxCarbs");
		String maxFat=object.getJSONObject("query").getString("maxFat");
		String maxProtein=object.getJSONObject("query").getString("maxProtein");
		String minCalories=object.getJSONObject("query").getString("minCalories");
		String minCarbs=object.getJSONObject("query").getString("minCarbs");
		String minFat=object.getJSONObject("query").getString("minFat");
		String minProtein=object.getJSONObject("query").getString("minProtein");
		String query=object.getJSONObject("query").getString("querywords").replaceAll(" ", "+").replaceAll(",", "%2C");
		String type="".replaceAll(" ", "+");
		String disease=object.getString("disease");
		//System.out.println(disease);


		if (query.length()>1){
			query=query.substring(1);
		}
		/*
	ZoneId zoneIdParis = ZoneId.of( "America/New_York" );
	LocalDateTime dateTime = LocalDateTime.now( zoneIdParis);
	String temp=dateTime.toString().split("T")[1];
	System.out.println(temp);
		 */

		@SuppressWarnings("deprecation")
		int hours= new Date().getHours();
		System.out.println("hours:  " + hours);

		if (hours>=5&&hours<=10){
			type=type+"breakfast";
		}else if (hours>10&&hours<=12){
			type=type+"brunch";
		}else if (hours>12&&hours<=15 || hours>18&&hours<=23){
			type=type+"main+course";
		}
		else if (hours>23||hours<5 || hours>15&&hours<=18 ) {
			type = type+"snacks";
		}
		System.out.println();

	/*	double weight = Integer.parseInt(object.getString("weight"));
		String unit_weight=object.getString("weightIN");
		if(unit_weight.equals("pounds")){
			weight=weight/2.2;
		}

		double height = 0.0;
		String unit_height=object.getString("heightIN");
		if (unit_height.equals("ft"))
		{
			height=height/2.2;
		}*/


		/*
	double bmi = 0.0;

	bmi = ((weight * 703)/(height * height));




	System.out.println("BMI VALUES");
	System.out.println("Underweight: Under 18.5");
	System.out.println("Normal: 18.5-24.9 ");
	System.out.println("Overweight: 25-29.9");
	System.out.println("Obese: 30 or over");
		 */
		//////////////////////////////////////////////
		double weight = Double.parseDouble(object.getString("weight"));
		String unit_weight=object.getString("weightIN");
		if(unit_weight.equals("pounds")){
			weight=weight*0.45;
		}
		System.out.println(weight);
		double height = Double.parseDouble(object.getString("height"));
		String unit_height=object.getString("heightIN");
		System.out.println(height);

		if (unit_height.equals("ft"))
		{
			height=height*0.025;
		}

		System.out.println(height);

		double bmi = 0.0;

		bmi = ((weight)/(height * height));

		int age=Integer.parseInt(object.getString("age"));
		String Gender=object.getString("gender");
		double[] maleActivities={1,1.11,1.26,1.48}; 
		double[] femaleActivities={1,1.12,1.27,1.45};
		//String[] PhysicalActivity={"Sedentary (no exercise)","Low active (walks about 2 miles daily at 3-4 mph)","Active (walks about 7 miles daily at 3-4 mph)","Very active (walks about 17 miles daily at 3-4 mph)"};
		double calories=0.0;
		double userMaxCal = Double.parseDouble(maxCalories);
		if (Gender.equals("male"))
		{
			calories=662-(6.91*age)+(maleActivities[Integer.parseInt(object.getString("activityLevel"))]*15.91*weight)+(539.6*height);
		}
		else{
			calories=354-(6.91*age)+(femaleActivities[Integer.parseInt(object.getString("activityLevel"))]*9.36*weight)+(726*height);
		}

		if(userMaxCal>calories){
		//	maxCalories=String.valueOf(calories);
		}



		//List<String> words= new ArrayList<String>();

		//words= getSuggestions("Olrves", 2);
		//System.out.println(words.toString());
		////////////////////////////////////////////////////////////////////		


		//original recipes
		//String url = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/searchComplex?cuisine="+cuisine+"&excludeIngredients="+excludeIngredients+"&includeIngredients="+includeIngredients+"&intolerances="+intolerances+"&limitLicense=false&maxCalories="+maxCalories+"+&maxCarbs="+maxCarbs+"&maxFat="+maxFat+"&maxProtein="+maxProtein+"&minCalories="+minCalories+"&minCarbs="+minCarbs+"&minFat="+minFat+"&minProtein="+minProtein+"&number=100&offset=0&query="+query+"&ranking=1&type="+type;
		String url = "https://spoonacular-recipe-food-nutrition-v1.p.mashape.com/recipes/searchComplex?cuisine="+cuisine+"&fillIngredients=false";
		if(intolerances!=""){
		url+="&intolerances="+intolerances;
		}
		if(includeIngredients!=""){
			url+="&includeIngredients="+includeIngredients;
		}else{
			url+="&includeIngredients=salt";
		}
		url+="&limitLicense=false&maxCalories="+maxCalories+"&maxCarbs="+maxCarbs+"&maxFat="+maxFat+"&maxProtein="+maxProtein+"&minCalories="+minCalories+"&minCarbs="+minCarbs+"&minFat="+minFat+"&minProtein="+minProtein+"&number=100&offset=0&query="+query+"&ranking=1"+"&type="+type;

		//System.out.println("url : " + url);


		HttpResponse<String> response = Unirest.get(url)
				//EzPVzV3OIImshUC16NzTUL4Q9zE5p1fS8uIjsnHrZlqZDU38BN
				
				.header("X-Mashape-Key", "q8OegLa2iqmshvbsYklWvajeDcqZp1e63fSjsnmhLIw9xoIlw1")
				.asString();
		System.out.println("**********************************Just before printing url********************************");
		System.out.println("url : " + url);
		System.out.println("response.getBody() : " + response.getBody());
		System.out.println("**********************************Just after printing url********************************");

		//String filename="air_"+timestamp+".json";
		//FileWriter filewriter = new FileWriter(filename);

		//filewriter.write(response.getBody().toString());
		//filewriter.write(response.getBody().toString());
		//filewriter.close();
		//FileReader filereader = new FileReader(filename);
		//BufferedReader br = new BufferedReader(filereader);
		//System.out.println(br.readLine());
		//JSONObject a = new JSONObject(br.readLine());
		//System.out.println(a);
		//JSONArray getArray = a.getJSONArray("results");
		ArrayList<String> al=new ArrayList<>();
		JSONObject res = new JSONObject(response.getBody().toString());
		org.json.JSONArray  recipes = res.getJSONArray("results");
		System.out.println(recipes.toString());
		for(int i = 0; i < recipes.length(); i++){
			JSONObject ids = recipes.getJSONObject(i);
			//System.out.println(ids.get("id").toString());
			al.add(ids.get("id").toString());

		}
		ArrayList<HashMap<String,Float>> xmlresult = new ArrayList<HashMap<String,Float>>();
		System.out.println("*****************************before xmlParser****************************");
		XMLParser xmlParse = new XMLParser();
		xmlresult= xmlParse.xmlparser(disease);
		System.out.println("*****************************after xmlParser****************************");
		//System.out.println(xmlresult.get(0).toString());
		//System.out.println(xmlresult.get(1).toString());
		//System.out.println(response.getBody());
		System.out.println("al :" +  al);
		System.out.println("xmlResult : "+xmlresult);
		return(JsonParser.jsonparser(al,xmlresult,recipes,disease));

	}


}
