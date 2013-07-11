package com.convert;

import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GeoJSONGeometry extends JSONObject {

	public GeoJSONGeometry(String type, JSONArray input) {
		super();
		this.put("type", type);
		GeoJSONCoordinates coords = new GeoJSONCoordinates(input);
		this.putAll(coords);
	}
	
	public GeoJSONGeometry(String type, GeoJSONCoordinates input) {
		super();
		this.put("type", type);
		this.putAll(input);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
