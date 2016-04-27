package com.air;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
public class XMLParser {

	public  ArrayList<HashMap<String,Float>> xmlparser(String disease) {
		// TODO Auto-generated method stub
		ArrayList<HashMap<String,Float>>al=new 	ArrayList<HashMap<String,Float>>();
		ClassLoader classLoader = getClass().getClassLoader();
		String filename="";
		//String filename = "/home/tushar/workspace/mars/com.tutorial.rest/target/com.tutorial.rest-0.0.1-SNAPSHOT/WEB-INF/classes/";
		try {
			
			if(disease.equals("renal"))filename+="renal.xml";
			else if(disease.equals("heart"))filename+="heart.xml";
			else filename+="diabetes.xml";
			//else return null;
			File file = new File(classLoader.getResource(filename).getFile()); 
			//File file = new File(filename);
			SAXBuilder saxBuilder = new SAXBuilder();
			HashMap<String,Float> needsn=new HashMap<String,Float>();
			HashMap<String,Float> excessn=new HashMap<String,Float>();
			Document document = saxBuilder.build(file);
			

			System.out.println("Disease :" + document.getRootElement().getAttribute("name"));

			Element diseaseElement = document.getRootElement();
			Element needsNutrient = diseaseElement.getChild("needsNutrient");
			//System.out.println("----------------------------");

			//System.out.println("\nCurrent Element :" + needsNutrient.getName());
			List<Element> nutrientList = needsNutrient.getChildren("Nutrient");
			for (int temp = 0; temp < nutrientList.size(); temp++) {
				float quantity;
				Element nutrient = nutrientList.get(temp);
				String name=nutrient.getChild("name").getText();
				try{
				quantity = Float.valueOf(nutrient.getChild("value").getText());
				}
				catch(Exception e)
				{
				quantity=1;
				}
				
				//System.out.println("Name of Nutrient : " + name);
				//System.out.println("Quantity : " + String.valueOf(quantity));
				needsn.put(name.toLowerCase(), quantity);
			}
			
			Element excessNutrient = diseaseElement.getChild("excessNutrient");
			//System.out.println("----------------------------");

			//System.out.println("\nCurrent Element :" + excessNutrient.getName());
			nutrientList = excessNutrient.getChildren("Nutrient");
			for (int temp = 0; temp < nutrientList.size(); temp++) {
				float quantity;
				Element nutrient = nutrientList.get(temp);
				String name=nutrient.getChild("name").getText();
				try{
				quantity = Float.valueOf(nutrient.getChild("value").getText());
				}
				catch(Exception e)
				{
					quantity=200;
				}
				//System.out.println("Name of Nutrient : " + name);
				//System.out.println("Quantity : " + String.valueOf(quantity));
				excessn.put(name.toLowerCase(), quantity);
				
			}
			al.add(needsn);
			al.add(excessn);
			
			
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return al;
	}

}
