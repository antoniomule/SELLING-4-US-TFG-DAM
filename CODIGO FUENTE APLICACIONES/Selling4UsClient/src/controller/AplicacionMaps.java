/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class AplicacionMaps {

    
    static double latitud;
    static double longitud;
    
    public static void main(String[] args) throws MalformedURLException, IOException {
        
               pedirLongitudLatitud();
                String nearbyPlacesResponse = sendGetRequest("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + latitud + "," + longitud + "&radius=1500000&type=locality&key=AIzaSyCqc-QCcLCaFXzXgbU8AhpLqAbyrac0RcQ");
                String[] cities = parseCities(nearbyPlacesResponse);

               
                    // Imprime las ciudades cercanas
                    for (String city : cities) {
                        System.out.println("Ciudad: "+city);
                    }
                 
            }


    private static String sendGetRequest(String url) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        }
        return null;
    }

    
    
    static void pedirLongitudLatitud() throws UnsupportedEncodingException, MalformedURLException, IOException {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=Andalucia&key=AIzaSyCqc-QCcLCaFXzXgbU8AhpLqAbyrac0RcQ";

        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestMethod("GET");
        int status = con.getResponseCode();

        if (status == 200) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            JSONObject response = new JSONObject(content.toString());
            if (response.getString("status").equals("OK")) {
                JSONObject location = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                latitud = location.getDouble("lat");
                longitud = location.getDouble("lng");
            }
        }
    }
    
    private static String[] parseCities(String nearbyPlacesResponse) {
    JSONObject response = new JSONObject(nearbyPlacesResponse);
    JSONArray results = response.getJSONArray("results");
        System.out.println(results.toString());
    int numCities = results.length();
    String[] cities = new String[numCities];

    for (int i = 0; i < numCities; i++) {
        JSONObject result = results.getJSONObject(i);
        String cityName = result.getString("name");
        cities[i] = cityName;
    }
    return cities;
}
}
