package com.convert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class GeoConvert {
	public static final String INTERNAL_DELIMITER = " | ";
	public static final String INTERNAL_DESIGNATOR = "=";

	private static GeoJSONFeature getFeature(String jsonText) {
		JSONParser parser = new JSONParser();

		try{
			JSONObject jsobj = (JSONObject) parser.parse(jsonText);
			GeoJSONFeature feature = new GeoJSONFeature();
			JSONArray tempCoordinates = new JSONArray();

			String datetime = (String) jsobj.get("datetime");
			String _sourceip = (String) jsobj.get("_sourceip");
			String sim_operator_code = (String) jsobj.get("sim_operator_code");
			String app_version_name = (String) jsobj.get("app_version_name");
			String submission_type = (String) jsobj.get("submission_type");

			JSONArray metrics = (JSONArray) jsobj.get("metrics");
			JSONArray tests = (JSONArray) jsobj.get("tests");
			JSONArray conditions = (JSONArray) jsobj.get("conditions");

			for (Object temp : metrics) {
				JSONObject tempjsobj = (JSONObject) temp;

				if (tempjsobj.containsKey("longitude") && tempjsobj.containsKey("latitude")) {
					JSONArray tempArray = new JSONArray();
					tempArray.add(Double.parseDouble((String) tempjsobj.get("longitude")));
					tempArray.add(Double.parseDouble((String) tempjsobj.get("latitude")));

					tempCoordinates.add(tempArray);
				}
			}
			GeoJSONGeometry geometry = new GeoJSONGeometry("MultiPoint", tempCoordinates);
			feature.addGeometry(geometry);

			JSONObject aggregated = new JSONObject();
			StringBuilder sb = new StringBuilder();

			int getCount = 0;
			int postCount = 0;
			int latencyCount = 0;

			aggregated.put("datetime", datetime);
			aggregated.put("sourceip", _sourceip);
			aggregated.put("sim_operator_code", sim_operator_code);
			aggregated.put("app_version_name", app_version_name);
			aggregated.put("submission_type", submission_type);

			for (Object temp : tests) {
				sb = new StringBuilder();
				JSONObject tempJSObj = (JSONObject) temp;
				String testType = (String) tempJSObj.get("type");

				if (testType.equals("JHTTPGETMT")) {
					sb.append("target").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("target")).append(INTERNAL_DELIMITER);
					sb.append("bytes_sec").append(INTERNAL_DESIGNATOR).append(Integer.parseInt((String) tempJSObj.get("bytes_sec"))*0.000008).append(INTERNAL_DELIMITER);
					sb.append("success").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("success")).append(INTERNAL_DELIMITER);
					sb.append("transfer_bytes").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("transfer_bytes")).append(INTERNAL_DELIMITER);
					sb.append("transfer_time").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("transfer_time"));
					aggregated.put(testType + " (" + (++getCount) + ")", sb.toString());
				}
				else if (testType.equals("JHTTPPOSTMT")) {
					sb.append("target").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("target")).append(INTERNAL_DELIMITER);
					sb.append("bytes_sec").append(INTERNAL_DESIGNATOR).append(Integer.parseInt((String) tempJSObj.get("bytes_sec"))*0.000008).append(INTERNAL_DELIMITER);
					sb.append("success").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("success")).append(INTERNAL_DELIMITER);
					sb.append("transfer_bytes").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("transfer_bytes")).append(INTERNAL_DELIMITER);
					sb.append("transfer_time").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("transfer_time"));
					aggregated.put(testType + " (" + (++postCount) + ")", sb.toString());
				}
				else if (testType.equals("JUDPLATENCY")) {
					sb.append("target").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("target")).append(INTERNAL_DELIMITER);
					sb.append("success").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("success")).append(INTERNAL_DELIMITER);
					sb.append("rtt_avg").append(INTERNAL_DESIGNATOR).append(Integer.parseInt((String) tempJSObj.get("rtt_avg"))/1000).append(INTERNAL_DELIMITER);
					sb.append("rtt_min").append(INTERNAL_DESIGNATOR).append(Integer.parseInt((String) tempJSObj.get("rtt_min"))/1000).append(INTERNAL_DELIMITER);
					sb.append("rtt_max").append(INTERNAL_DESIGNATOR).append(Integer.parseInt((String) tempJSObj.get("rtt_max"))/1000).append(INTERNAL_DELIMITER);
					sb.append("received_packets").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("received_packets")).append(INTERNAL_DELIMITER);
					sb.append("lost_packets").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("lost_packets"));
					aggregated.put(testType + " (" + (++latencyCount) + ")", sb.toString());
				}
			}

			int gsmCount = 0;
			int cdmaCount = 0;

			for (Object temp : metrics) {
				sb = new StringBuilder();
				JSONObject tempJSObj = (JSONObject) temp;
				String testType = (String) tempJSObj.get("type");
				if (testType.equals("gsm_cell_location")) {
					sb.append("gsm_signal_strength").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("signal_strength"));
					aggregated.put(testType + " (" + (++gsmCount) + ")", sb.toString());
				}
				else if (testType.equals("cdma_cell_location")) {
					sb.append("dbm").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("dbm"));
					aggregated.put(testType + " (" + (++cdmaCount) + ")", sb.toString());
				}
			}

			feature.addProperties(aggregated);

			return feature;
		}
		catch(ParseException pe){
			System.out.println("position: " + pe.getPosition());
			System.out.println(pe);
		}
		return null; // should never get here
	}

	private static String readFile(FileReader fileIn) {
		BufferedReader in = new BufferedReader(fileIn);
		StringBuilder sb = new StringBuilder();
		try {
			String temp = in.readLine();
			while (temp != null) {
				sb.append(temp);
				temp = in.readLine();
			}
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}

	/**
	 *  { "type": "Feature",
	 *    "geometry": {
	 *        "type": "LineString",
	 *        "coordinates": [
	 *           [102.0, 0.0], [103.0, 1.0], [104.0, 0.0], [105.0, 1.0]
	 *           ]
	 *       }
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		FileReader fileIn;
		String jsonText = "";

		try {
			File folder = new File("examples/tmobile_demo_data");
			File[] fileList = folder.listFiles();
			GeoJSONFeatureCollection collection = new GeoJSONFeatureCollection();

			for (int i = 0; i < fileList.length; i++) {
				if (fileList[i].isFile()) {
					fileIn = new FileReader(fileList[i]);
					jsonText = readFile(fileIn);
					GeoJSONFeature feature = getFeature(jsonText);
					collection.addFeature(feature);
				}
			}
			collection.addCRS("urn:ogc:def:crs:OGC:1.3:CRS84");
			System.out.println(collection.toJSONString());

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

}
