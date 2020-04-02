package capitals;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RestcountriesConnector {

    private String urlStr = "http://restcountries.eu/rest/v2/capital/";

    public List<Capital> getCapitalsInfo(List<String> names){
        List<Capital> capitals = new ArrayList<>();
        for(String name : names){
            capitals.add(getInfo(name));
        }
        return capitals;
    }

    private Capital getInfo(String name){
        try {
            URL url = new URL(urlStr + name);
            try(InputStream is = url.openStream()) {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                JSONArray json = new JSONArray(br.readLine());
                String country = json.getJSONObject(0).getString("name");
                String currency = json.getJSONObject(0).getJSONArray("currencies").getJSONObject(0).getString("name");
                return new Capital(name, country, currency);
            }catch (IOException e){
                e.printStackTrace();
                return null;
            }
        }catch (MalformedURLException ex){
            ex.printStackTrace();
            return null;
        }
    }
}
