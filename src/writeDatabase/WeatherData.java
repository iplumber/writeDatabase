package writeDatabase;

public class WeatherData {
    private String date;
    private Double temperature;
    private Double temperatureFeelsLike;
    private Double humidity;
    private String weatherDescription;
    private String weatherTimeStamp;
    private String weatherDate;
    private Integer number;

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getNumber() {
        return this.number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getTemperatureFeelsLike() {
        return temperatureFeelsLike;
    }

    public void setTemperatureFeelsLike(Double temperatureFeelsLike) {
        this.temperatureFeelsLike = temperatureFeelsLike;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

	@Override
	public String toString() {
		return "WeatherData [date=" + date + ", temperature=" + temperature + ", temperatureFeelsLike="
				+ temperatureFeelsLike + ", humidity=" + humidity + ", weatherDescription=" + weatherDescription
				+ ", weatherTimeStamp=" + weatherTimeStamp + ", weatherDate=" + weatherDate + ", number=" + number
				+ "]";
	}
    
    
}
