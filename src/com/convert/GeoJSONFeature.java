package com.convert;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GeoJSONFeature extends JSONObject {

	public void addProperties(JSONArray input) {
		this.put("properties", input);
	}
	
	public void addProperties(JSONObject input) {
		this.put("properties", input);
	}
	
	public void addGeometry(GeoJSONGeometry input) {
		this.put("geometry", input);
	}
	
	public GeoJSONFeature() {
		super();
		// TODO Auto-generated constructor stub
		this.put("type", "Feature");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
