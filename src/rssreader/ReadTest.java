package rssreader;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Random;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import de.vogella.rss.model.Feed;
import de.vogella.rss.model.FeedMessage;
import de.vogella.rss.read.RSSFeedParser;

public class ReadTest {
	
	public static JSONArray shuffleJsonArray (JSONArray array) throws JSONException {
	    // Implementing Fisher�Yates shuffle
	        Random rnd = new Random();
	        rnd.setSeed(System.currentTimeMillis());
	        for (int i = array.length() - 1; i >= 0; i--)
	        {
	          int j = rnd.nextInt(i + 1);
	          // Simple swap
	          Object object = array.get(j);
	          array.put(j, array.get(i));
	          array.put(i, object);
	        }
	    return array;
	}

	
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static JSONArray rssFeedReader(String Disease) throws JSONException{
		JSONArray feedArray = new JSONArray();
		String[] RSSFeeds={"http://www.hopkinsmedicine.org/news/media/releases/?format=rss","https://www.nlm.nih.gov/medlineplus/feeds/topics/exerciseandphysicalfitness.xml","http://www.medpagetoday.com/rss/Headlines.xml","http://www.medpagetoday.com/rss/Blogs.xml","http://www.dailybreeze.com/section?template=RSS&profile=4000317&mime=xml"};
		String diabetes="http://diabetes.diabetesjournals.org/rss/mfr.xml";
		String heart="http://heart.bmj.com/rss/mfr.xml";
		String renal="http://www.renalandurologynews.com/pages/rss.aspx?sectionid=2334";
		int i =RSSFeeds.length;
		if (Disease.equalsIgnoreCase("diabetes")){
			RSSFeeds[i-1]=diabetes;
		}
		else if (Disease.equalsIgnoreCase("heart")){
			RSSFeeds[i-1]=heart;
				
		}
		else if (Disease.equalsIgnoreCase("renal")){
			RSSFeeds[i-1]=renal;
			
		}
		for( int j =0;j<=i-1;j++){
			System.out.println(RSSFeeds[j]);
		RSSFeedParser parser = new RSSFeedParser(RSSFeeds[j]);
		Feed feed = parser.readFeed();
		JSONObject object=null;
		//System.out.println(feed);
		try {
			for (FeedMessage message : feed.getMessages()) {
				//System.out.println(message);
				object = new JSONObject();
				object.put("title", message.getTitle());
				object.put("description", message.getDescription());
				object.put("link", message.getGuid());	      
				feedArray.put(object);
			}



		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		
		return shuffleJsonArray(feedArray);

	}


	public static void main(String[] args) throws JSONException {
		//System.out.println(getJsonArray(obj.getString("disease")).toString());

	}
} 
