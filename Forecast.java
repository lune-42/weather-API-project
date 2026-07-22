
//this class is used to help create the 7 day forecast on the GUI
public class Forecast {
    private String day;
    private String weather;
    private double maxTemp;
    private double minTemp;

    public Forecast(String day, String weather,
                    double maxTemp, double minTemp) {

        this.day = day;
        this.weather = weather;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
    }

    public String getDay() {
        return day; //returns the day of the week
    }

    public String getWeather() {
        return weather; //returns the weather
    }

    public double getMaxTemp() {
        return maxTemp; //returns max temp
    }

    public double getMinTemp() {
        return minTemp; //returns min temp
    }
}
