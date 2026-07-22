import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

//retrieve data from API
//display data to the user
public class WeatherApp {
    //fetch weather data from given location
    public static JSONObject getWeatherData(String locationName) {
        JSONArray locationData = getLocationData(locationName);

        // extract latitude and longitude data
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        //build API request URL with location coordinates
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude +
                "&longitude=" + longitude +
                "&hourly=temperature_2m,weather_code,wind_speed_10m,relative_humidity_2m" +
                "&daily=weather_code,temperature_2m_max,temperature_2m_min" +
                "&forecast_days=7" +
                "&wind_speed_unit=mph";

        try{
            // call API and get response
            HttpURLConnection conn = fetchApiResponse(urlString);

            //check for response status
            //200 - means that the connection was a success
            if(conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }

            //store resulting json data
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while(scanner.hasNext()){
                resultJson.append(scanner.nextLine());
            }

            //close scanner
            scanner.close();

            //close url connection
            conn.disconnect();

            //parse theough our data
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            //retrieve hourly data
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            //retrieve daily data
            JSONObject daily = (JSONObject) resultJsonObj.get("daily");

            //get forecast arrays
            JSONArray forecastDates = (JSONArray) daily.get("time");
            JSONArray forecastMaxTemps = (JSONArray) daily.get("temperature_2m_max");
            JSONArray forecastMinTemps = (JSONArray) daily.get("temperature_2m_min");
            JSONArray forecastWeatherCodes = (JSONArray) daily.get("weather_code");

            //create an array
            ArrayList<Forecast> forecastList = new ArrayList<>();

            for (int i = 0; i < forecastDates.size(); i++){
                Forecast forecast = new Forecast(
                        getDayName((String) forecastDates.get(i)),
                        convertWeatherCode((long) forecastWeatherCodes.get(i)),
                (double) forecastMaxTemps.get(i),
                        (double) forecastMinTemps.get(i));

                forecastList.add(forecast);
            }

            for (Forecast forecast : forecastList) {
                System.out.println(
                        forecast.getDay() + " | " +                  //print the day of the week
                                forecast.getMaxTemp() + "°C | " +    //print max temp
                                forecast.getMinTemp() + "°C | " +    //print min temp
                                forecast.getWeather()                //print the weather
                );
            }


            //get index of our current hour
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            //get temperature
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            //get weather code
            JSONArray weathercode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weathercode.get(index));

            //get humidity
            JSONArray relevantHumidity = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) relevantHumidity.get(index);

            //get windspeed
            JSONArray windspeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windspeed = (double) windspeedData.get(index);

            //build the weather json data object
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);
            weatherData.put("forecast", forecastList);

            return weatherData;

        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    //retrieves geographic coordinates for given location name
    public static JSONArray getLocationData(String locationName) {
        //replace any spaces with a +
        locationName = locationName.replaceAll(" ", "+");

        //build API url with location parameters
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" + locationName
                + "&count=10&language=en&format=json";

        try{
            //call api and get a response
            HttpURLConnection conn = fetchApiResponse(urlString);

            //check response status
            //200 means successful
            if(conn.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }else{
                //store API results
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                //read and store the resulting json data into our string building
                while(scanner.hasNext()){
                    resultJson.append(scanner.nextLine());
                }

                //close scanner
                scanner.close();

                //close url connection
                conn.disconnect();

                //parse the JSON string into a JSON obj
                JSONParser parser = new JSONParser();
                JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                //get the list of location data the API generated from the location name
                JSONArray locationData = (JSONArray) resultJsonObj.get("results");
                return locationData;
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        //couldn't find location
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString){
        try {
            //attempt to create connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //set request method to get
            conn.setRequestMethod("GET");

            //connect to API
            conn.connect();
            return conn;
        }catch(IOException e){
            e.printStackTrace();
        }

        //could not make connection
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();

        //iterate through the time list and see which one matches our current time
        for(int i=0; i < timeList.size(); i++){
            String time = (String) timeList.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                //return the index
                return i;
            }
        }

        return 0;
    }

    public static String getCurrentTime(){
        //get current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        //format fate is to be 2026-07-1100:00 (this is how it is read in API)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        //format and print the current date and time
        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;

    }

    //make weather code readable
    private static String convertWeatherCode(long weathercode){
        String weatherCondition = "";
        if(weathercode == 0L){
            weatherCondition = "Clear";
        }else if(weathercode <= 3L && weathercode > 0L){
            weatherCondition = "Cloudy";
        }else if((weathercode >= 51L && weathercode <= 67L)
            || (weathercode >= 80L && weathercode <= 99L)){

            //rain
            weatherCondition = "Rain";
        }else if(weathercode >= 71L && weathercode <= 77L){

            //snow
            weatherCondition = "Snow";
        }
        return weatherCondition;
    }

    //change the dates to say the days of the week
    private static String getDayName(String date){

        LocalDate localDate = LocalDate.parse(date);

        return localDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
    }
}
