package com.convert;

import java.io.BufferedReader;
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
		// TODO Auto-generated method stub
		FileReader fileIn;
		String jsonText = "";

		try {
			fileIn = new FileReader("examples/tmobile_demo_data/20130608125914_208.54.90.246_51b31c92e3c2f.json");
			jsonText = readFile(fileIn);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
			Set coll = jsobj.entrySet();
			for (Object temp : coll) {
				System.out.println(temp.toString());
			}

			for (Object temp : metrics) {
				JSONObject tempjsobj = (JSONObject) temp;

				if (tempjsobj.containsKey("longitude") && tempjsobj.containsKey("latitude")) {
					JSONArray tempArray = new JSONArray();
					tempArray.add(tempjsobj.get("latitude"));
					tempArray.add(tempjsobj.get("longitude"));

					tempCoordinates.add(tempArray);

					//System.out.println(tempjsobj.toString());
				}
			}
			GeoJSONGeometry geometry = new GeoJSONGeometry("LineString", tempCoordinates);
			feature.addGeometry(geometry);

			JSONObject aggregated = new JSONObject();
			StringBuilder sb = new StringBuilder();

			for (Object temp : tests) {
				JSONObject tempJSObj = (JSONObject) temp;
				String testType = (String) tempJSObj.get("type");
				if (testType.equals("JHTTPGETMT") || testType.equals("JHTTPPOSTMT")) {
					sb.append("Target").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("target")).append(INTERNAL_DELIMITER);
					sb.append("bytes_sec").append(INTERNAL_DESIGNATOR).append(Integer.parseInt((String) tempJSObj.get("bytes_sec"))*0.000008).append(INTERNAL_DELIMITER);
					sb.append("success").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("success")).append(INTERNAL_DELIMITER);
					sb.append("transfer_bytes").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("transfer_bytes")).append(INTERNAL_DELIMITER);
					sb.append("transfer_time").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("transfer_time"));
				}
				else if (testType.equals("JUDPLATENCY")) {
					sb.append("Target").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("target")).append(INTERNAL_DELIMITER);
					sb.append("success").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("success")).append(INTERNAL_DELIMITER);
					sb.append("rtt_avg").append(INTERNAL_DESIGNATOR).append(Integer.parseInt((String) tempJSObj.get("rtt_avg"))/1000).append(INTERNAL_DELIMITER);
					sb.append("rtt_min").append(INTERNAL_DESIGNATOR).append(Integer.parseInt((String) tempJSObj.get("rtt_min"))/1000).append(INTERNAL_DELIMITER);
					sb.append("rtt_max").append(INTERNAL_DESIGNATOR).append(Integer.parseInt((String) tempJSObj.get("rtt_max"))/1000).append(INTERNAL_DELIMITER);
					sb.append("received_packets").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("received_packets")).append(INTERNAL_DELIMITER);
					sb.append("lost_packets").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("lost_packets")).append(INTERNAL_DELIMITER);
				}
				else {
					for (Object temp2 : tempJSObj.keySet()) {
						aggregated.put(temp2, tempJSObj.get(temp2));
					}
				}
				aggregated.put(testType, sb.toString());
			}

			for (Object temp : metrics) {
				JSONObject tempJSObj = (JSONObject) temp;
				String testType = (String) tempJSObj.get("type");
				if (testType.equals("gsm_cell_location")) {
					sb.append("gsm_signal_strength").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("gsm_signal_strength")).append(INTERNAL_DELIMITER);
				}
				else if (testType.equals("cdma_cell_location")) {
					sb.append("dbm").append(INTERNAL_DESIGNATOR).append(tempJSObj.get("dbm")).append(INTERNAL_DELIMITER);
				}
				else {
					for (Object temp2 : tempJSObj.keySet()) {
						aggregated.put(temp2, tempJSObj.get(temp2));
					}
				}
				aggregated.put(testType, sb.toString());
			}

			feature.addProperties(aggregated);

			System.out.println(feature);
		}
		catch(ParseException pe){
			System.out.println("position: " + pe.getPosition());
			System.out.println(pe);
		}
	}

}
