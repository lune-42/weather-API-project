import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;


public class WeatherAppGUI extends JFrame {
    private boolean darkMode = false;
    private JSONObject weatherData;
    private JTextField searchTextField;
    private JLabel temperatureText;
    private JLabel weatherConditionDesc;
    private JLabel humidityText;
    private JLabel windspeedText;
    private JLabel forecastTitle;
    private JLabel humidityImage;
    private JLabel windspeedImage;
    private JLabel[] forecastDayLabels = new JLabel[7]; //days of the week
    private JLabel[] forecastImageLabels = new JLabel[7]; //forecast images
    private JLabel[] forecastTempLabels = new JLabel[7]; //temperature

    public WeatherAppGUI(){
        //set up the GUI and add a title
        super( "weather dashboard");

        //configure GUI to end program process when it is closed
        setDefaultCloseOperation((EXIT_ON_CLOSE));

        //set the size of the GUI(in pixels)
        setSize(450, 870);

        //load the GUI to the centre of the screen
        setLocationRelativeTo(null);

        //make the layout manager null to manually position the components inside the GUI
        setLayout(null);

        //prevent resizing of the GUI
        setResizable(false);

        addGuiComponents();

        //load last city upon opening
        String lastCity = Settings.loadLastCity();

        //if there is nothing inside the text file
        if(!lastCity.isEmpty()){
            searchTextField.setText(lastCity);
        }

        //set background to white
        getContentPane().setBackground(Color.WHITE);
    }
    private void addGuiComponents(){


        //add in the days of the week
        for (int i = 0; i < 7; i++){

            int y = 620 + (i * 30);

            forecastDayLabels[i] = new JLabel("Day");
            forecastDayLabels[i].setBounds(20, y, 45, 25);
            add(forecastDayLabels[i]);

            forecastImageLabels[i] = new JLabel();
            forecastImageLabels[i].setBounds(80, y, 20, 20);
            add(forecastImageLabels[i]);

            forecastTempLabels[i] = new JLabel("--° / --°");
            forecastTempLabels[i].setBounds(130, y, 130, 25);
            add(forecastTempLabels[i]);

        }

        //the search field
        searchTextField = new JTextField();

        //setting the location and size of the component
        searchTextField.setBounds(15, 15, 351, 45);

        //changing the font style and size
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));

        //add the search button
        add(searchTextField);

        //add a dark-mode button
        JButton darkModeButton = new JButton("\uD83C\uDF19");
        darkModeButton.setBounds(375, 65, 47, 45);
        add(darkModeButton);

        darkModeButton.addActionListener(e -> {

            darkMode = !darkMode;

            //changes the text color based on if dark mode is active
            Color textColor = darkMode ? Color.WHITE : Color.BLACK;
            temperatureText.setForeground(textColor);
            weatherConditionDesc.setForeground(textColor);
            humidityText.setForeground(textColor);
            windspeedText.setForeground(textColor);
            forecastTitle.setForeground(textColor);

            for(int i = 0; i < 7; i++){
                forecastDayLabels[i].setForeground(textColor);
                forecastTempLabels[i].setForeground(textColor);
            }
            if(darkMode){
                getContentPane().setBackground(new Color(35, 35, 35));

                //alters the search button text
                searchTextField.setBackground(new Color(55, 55, 55));
                searchTextField.setForeground(Color.WHITE);
                searchTextField.setCaretColor(Color.WHITE);
                darkModeButton.setText("☀");
                humidityImage.setIcon(loadImage("src/Assets/weatherapp_images/output-onlinepngtools.png"));
                windspeedImage.setIcon(loadImage("src/Assets/weatherapp_images/image-2.png"));

            }else{
                getContentPane().setBackground(Color.WHITE);
                searchTextField.setBackground(Color.WHITE);
                searchTextField.setForeground(Color.BLACK);
                searchTextField.setCaretColor(Color.BLACK);
                darkModeButton.setText("\uD83C\uDF19");
                humidityImage.setIcon(loadImage("src/Assets/weatherapp_images/humidity.png"));
                windspeedImage.setIcon(loadImage("src/Assets/weatherapp_images/windspeed.png"));
            }
        });

        //weather image
        JLabel weatherConditionImage = new JLabel(loadImage("src/Assets/weatherapp_images/cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        //temperature text
        temperatureText = new JLabel( "10 C");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font( "Dialog", Font.BOLD, 48));

        //center the text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        //weather condition description
        weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0, 405, 450, 35);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment((SwingConstants.CENTER));
        add(weatherConditionDesc);

        // humidity image
        humidityImage = new JLabel(loadImage("src/Assets/weatherapp_images/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        // humidity text
        humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        // windspeed image
        windspeedImage = new JLabel(loadImage("src/Assets/weatherapp_images/windspeed.png"));
        windspeedImage.setBounds(220, 500, 74, 66);
        add(windspeedImage);

        //windspeed text
        windspeedText = new JLabel("<html><b>windspeed</b> 9mph</html>");
        windspeedText.setBounds(310, 500, 85, 55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 15));
        add(windspeedText);

        //search button
        JButton searchButton = new JButton(loadImage("src/Assets/weatherapp_images/search.png"));

        //enables the enter key for the search button
        searchTextField.addActionListener(e -> searchButton.doClick());

        //add the 7-day forecast text
        forecastTitle = new JLabel("7-Day Forecast");
        forecastTitle.setBounds(15, 580, 200, 30);
        forecastTitle.setFont(new Font("Dialog", Font.BOLD, 20));
        add(forecastTitle);

        //change the cursor to a hand cursor when hovering over this button
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

               // get location from user
                String userInput = searchTextField.getText();

                //saves the city to the text file
                Settings.saveLastCity(userInput);

                //validate innput - remove whitespace
                if(userInput.replaceAll("\\s", "").length() <= 0){
                    return;
                }

                //retrieve weather data
                weatherData = WeatherApp.getWeatherData(userInput);

                //retrieve data from the forecast array
                ArrayList<Forecast> forecastList = (ArrayList<Forecast>) weatherData.get("forecast");

                System.out.println("Forecast size: " + forecastList.size());

                //update weather image
                String weatherCondition = (String) weatherData.get("weather_condition");

                for (int i = 0; i < forecastList.size(); i++){

                    Forecast forecast = forecastList.get(i);

                    forecastDayLabels[i].setText(forecast.getDay());

                    forecastTempLabels[i].setText(
                      "H:" + Math.round(forecast.getMaxTemp()) + "° L: " +
                      Math.round(forecast.getMinTemp()) + "°"
                    );

                //update weather image based on the conditions
                switch(forecast.getWeather()) {
                    case "Clear":
                        forecastImageLabels[i].setIcon(loadScaledImage("src/Assets/weatherapp_images/clear.png", 20, 20));
                        break;
                    case "Cloudy":
                        forecastImageLabels[i].setIcon(loadScaledImage("src/Assets/weatherapp_images/cloudy.png", 20, 20));
                        break;
                    case "Rain":
                        forecastImageLabels[i].setIcon(loadScaledImage("src/Assets/weatherapp_images/rain.png",20, 20));
                        break;
                    case "Snow":
                        forecastImageLabels[i].setIcon(loadScaledImage("src/Assets/weatherapp_images/snow.png",20, 20));
                        break;
                    }
                }

                //update temperature text
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " C");

                //update wetaher condition text
                weatherConditionDesc.setText(weatherCondition);

                //update humidity text
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                //update windspeed text
                double windspeed = (double) weatherData.get("windspeed");
                windspeedText.setText("<html><b>windspeed</b> " + windspeed + "mph</html>");

            }
        });
        add(searchButton);

        String lastCity = Settings.loadLastCity();

        if (lastCity != null && !lastCity.isBlank()){
            searchTextField.setText(lastCity);
            searchButton.doClick();
        }

    }



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
    //used to create images in the GUI components
     private ImageIcon loadImage(String resourcePath) {
        try{

         //reading the image file from the path given
         BufferedImage image = ImageIO.read(new File(resourcePath));

         //returns an image icon so that the component can render it
         return new ImageIcon(image);
     }catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("Could not find resource");
        return null;
    }

    //scales the image down to fit the smaller label size for the 7 day forecast
    private ImageIcon loadScaledImage(String resourcePath, int width, int height){
        ImageIcon icon = loadImage(resourcePath);

        Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);

        return new ImageIcon(scaled);
    }
}
