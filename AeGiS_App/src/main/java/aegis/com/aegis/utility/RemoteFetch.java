package aegis.com.aegis.utility;

/**
 * Created by Lolo on 10/18/2015.
 */

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RemoteFetch {

    public static JSONObject getJSON(String city)
    {
        try {
            final String OPEN_WEATHER_MAP_API =
                    "http://api.openweathermap.org/data/2.5/weather?q="+city+"&units=metric&APPID=e9b9987ad7fa2d42ee47be19ca082a4e";
            URL url = new URL(OPEN_WEATHER_MAP_API);
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp = reader.readLine()) != null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            // This value will be 404 if the request was not
            // successful
            if (data.getInt("cod") != HttpURLConnection.HTTP_OK) {
                return null;
            }

            return data;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}