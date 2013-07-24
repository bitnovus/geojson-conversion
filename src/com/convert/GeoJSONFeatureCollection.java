package com.convert;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class GeoJSONFeatureCollection extends JSONObject {
	private JSONArray features;

	public GeoJSONFeatureCollection() {
		super();
		features = new JSONArray();
		this.put("type", "FeatureCollection");
	}
	
	public void addFeature(JSONObject inputObject) {
		features.add(inputObject);
		this.put("features", features);
	}
	
	public void addCRS(String crs_name) {
		JSONObject crs =new JSONObject();
		crs.put("type", "name");
		JSONObject crs_obj = new JSONObject();
		crs_obj.put("name", crs_name);
		crs.put("properties", crs_obj);
		this.put("crs", crs);
	}
}
