package writeDatabase;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class writeDatabase {
	public static void main(String args[]) {
		writeDatabase insertData = new writeDatabase();
		WeatherData currentWeather = new WeatherData();

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 9);
		calendar.set(Calendar.MINUTE, 00);
		calendar.set(Calendar.SECOND, 00);
		Date date = calendar.getTime();

		Map<String,String> city = new HashMap<>();
		city.put("changshu","7283386");
		city.put("shanghai","1796231");
		city.put("xiamen","1790645");
		city.put("fuzhou","1810821");

		new Timer("timer - " + 1).scheduleAtFixedRate(new TimerTask() {
			int key = 0;

			@Override
			public void run() {
				key = key + 1;
				System.out.println("-----------------------------------");
				for(String cityName : city.keySet()) {
					System.out.println("key:" + cityName + "\t value:" + city.get(cityName) );
					char[] weatherJsonString = crawlWeatherPage(city.get(cityName)); // return char[];
					if (weatherJsonString != null) {
						Double temperature = Double.parseDouble(parseWeatherData(weatherJsonString, "temp"));
						Double temperatureFeelsLike = Double.parseDouble(parseWeatherData(weatherJsonString, "feels_like"));
						Double humidity = Double.parseDouble(parseWeatherData(weatherJsonString, "humidity"));
						String weatherDescription = parseWeatherData(weatherJsonString, "description");

						currentWeather.setDate(formatter.format(new Date()));
						currentWeather.setTemperature(temperature);
						currentWeather.setTemperatureFeelsLike(temperatureFeelsLike);
						currentWeather.setHumidity(humidity);
						currentWeather.setWeatherDescription(weatherDescription);
						currentWeather.setNumber(key);

						try {
							insertData.insert(currentWeather,"hour_weather_" + cityName);
						} catch (SQLException | ClassNotFoundException e) {
							e.printStackTrace();
							System.out.println("Database issue! Cannot save weather data this time, will try later!");
						}
					} else {
						System.out.println("没取到weatherJsonString  key:" + key);
						System.out.println("Weather site issue! Cannot get the weather data this time, will try later!");
					}
				}
			}
		}, date, // 2000
				5 * 60 * 1000 // 1000
		);
	}

	public static char[] crawlWeatherPage(String cityId) {
		final String url = "http://api.openweathermap.org/data/2.5/weather?id="+cityId+"&units=metric&lang=zh_cn&APPID=0671b23de53376719895dbfa1d476939";

		try {
			Document doc = Jsoup.connect(url).timeout(15000).ignoreContentType(true).get();
			String str = doc.text();
			char[] weatherJsonString = str.toCharArray();
			return weatherJsonString;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String parseWeatherData(char[] line, String keyword) {
		StringBuffer name = new StringBuffer();
		String value = "";
		int iCount = 0;
		boolean flag = false;
		while (iCount < line.length) {
			char letter = line[iCount];
			if (letter != ',' && letter != ':' && letter != '{' && letter != '}' && letter != '"') {
				name.append(letter);
			} else {
				String str3 = name.toString();
				if (flag && str3.length() != 0) {
					value = str3;
					// flag = false;
					return (value);
				}
				if (str3.equals(keyword)) {
					flag = true;
				}
				name.setLength(0);
			}
			iCount++;
		}
		return (value);
	}
	
	public int insert(WeatherData currentWeather, String dataSheetName) throws SQLException, ClassNotFoundException {

		int i = 0;
		String sql = "insert into " + dataSheetName + "(weathertimestamp,temperature,temperaturefeelslike,humidity,weather,date,key) values(?,?,?,?,?,?,?)";
		try (
//				Connection conn = (Connection) DriverManager.getConnection(url, username, password);
				Connection conn = getconn();
				PreparedStatement pstmt = (PreparedStatement) conn.prepareStatement(sql)) {
			pstmt.setString(1, currentWeather.getDate());
			pstmt.setDouble(2, currentWeather.getTemperature());
			pstmt.setDouble(3, currentWeather.getTemperatureFeelsLike());
			pstmt.setDouble(4, currentWeather.getHumidity());
			pstmt.setString(5, currentWeather.getWeatherDescription());
			pstmt.setString(6, "6");
			pstmt.setInt(7, currentWeather.getNumber());
			i = pstmt.executeUpdate();
			return i;
		}
	}

	public Connection  getconn() {
		String driver = "org.postgresql.Driver";
		String url = "jdbc:postgresql://localhost:5433/weatherData";
		String username = "postgres";
		String password = "123";
		Connection conn = null;
		try {
			Class.forName(driver); // classLoader,加载对应驱动
			conn = (Connection) DriverManager.getConnection(url, username, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
}
