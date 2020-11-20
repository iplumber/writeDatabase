package writeDatabase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Calendar;

public class writeDatabase {
    //定义一个全局的天气对象

    public static void main(String args[]) {
        writeDatabase insertData = new writeDatabase();
        WeatherData currentWeather = new WeatherData();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,22);
        calendar.set(Calendar.MINUTE,00);
        calendar.set(Calendar.SECOND,00);
        Date date = calendar.getTime();

        String dateString = formatter.format(date);
        System.out.println("=========dateString4========");
        System.out.println(dateString);

//        parseWeatherData(); // return WeatherData;
        // timeToWrite();

        new Timer("timer - " + 1).scheduleAtFixedRate(new TimerTask() {
            int key = 0;
            @Override
            public void run() {
                System.out.println("-----------------------------------");
                System.out.println(formatter.format( new Date()) + " run ");
                System.out.println(key);
                key = key + 1;

                char[] weatherJsonString = crawlWeatherPage();  // return char[];
                String tempDate = formatter.format( new Date());

                Double temperature = Double.parseDouble(parseWeatherData(weatherJsonString,"temp"));
                Double temperatureFeelsLike = Double.parseDouble(parseWeatherData(weatherJsonString,"feels_like"));
                Double humidity = Double.parseDouble(parseWeatherData(weatherJsonString,"humidity"));
                String weatherDescription = parseWeatherData(weatherJsonString,"description");

                currentWeather.setDate(tempDate);
                currentWeather.setTemperature(temperature);
                currentWeather.setTemperatureFeelsLike(temperatureFeelsLike);
                currentWeather.setHumidity(humidity);
                currentWeather.setWeatherDescription(weatherDescription);
                currentWeather.setNumber(key);
                insertData.insert(currentWeather);
            }
        },date,5*60*1000);
    }

    public static char[] crawlWeatherPage() {
        final String url=  "http://api.openweathermap.org/data/2.5/weather?id=1796231&units=metric&lang=zh_cn&APPID=9b4dd646f6aa6a980c9553ac5748c40f";

        try {
            Document doc = Jsoup.connect(url).timeout(20000).ignoreContentType(true).get();
            String str = doc.text();
            char[] weatherJsonString = str.toCharArray();

            return weatherJsonString;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String parseWeatherData(char[] line, String keyword){
        StringBuffer name = new StringBuffer();
        String value = "";
        int iCount = 0;
        boolean flag = false;
        while(iCount < line.length) {
            char letter = line[iCount];
            if (letter != ',' && letter != ':' && letter != '{' && letter != '}' && letter != '"') {
                name.append(letter);
            } else {
                String str3 = name.toString();
                if(flag && str3.length() != 0) {
                    value = str3;
//                    flag = false;
                    return(value);
                }
                if(str3.equals(keyword)) {
                    flag = true;
                }
                name.setLength(0);
            }
            iCount++;
        }
        return(value);
    }

    public int insert(WeatherData currentWeather){
        Connection conn = getConn();
        int i = 0;
        String sql = "insert into hour_weather(weathertimestamp,temperature,temperaturefeelslike,humidity,weather,date,key) values(?,?,?,?,?,?,?)";
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            pstmt.setString(1, currentWeather.getDate());
            pstmt.setDouble(2, currentWeather.getTemperature());
            pstmt.setDouble(3, currentWeather.getTemperatureFeelsLike());
            pstmt.setDouble(4, currentWeather.getHumidity());
            pstmt.setString(5, currentWeather.getWeatherDescription());
            pstmt.setString(6, "6");
            pstmt.setInt(7, currentWeather.getNumber());
            i = pstmt.executeUpdate();
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

    public Connection getConn() {
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
