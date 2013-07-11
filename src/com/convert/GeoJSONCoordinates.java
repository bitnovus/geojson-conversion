package com.convert;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GeoJSONCoordinates extends JSONObject {

	public GeoJSONCoordinates(JSONArray inputArray) {
		super();
		this.put("coordinates", inputArray);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
