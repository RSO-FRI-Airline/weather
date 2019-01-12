package si.fri.rso.api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Forecast {
    public Instant Timestamp;

    public HashMap<String, Report> reports;

    public void Parse(JSONObject obj) throws Exception{
        this.Timestamp = Instant.now();
        JSONArray daily = obj.getJSONObject("daily").getJSONArray("data");
        reports = new HashMap<>();
        for(int i = 0; i < daily.length(); i++){
            JSONObject o = daily.getJSONObject(i);
            Report r = new Report();
            r.summary = o.getString("summary");
            double h = Math.round(o.getDouble("temperatureHigh"));
            double l = Math.round(o.getDouble("temperatureLow"));
            r.temperatures = String.format("%.0f°C - %.0f°C", l, h);

            Date date = new java.util.Date(o.getInt("sunriseTime")*1000L);
            SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd.MM.yyyy");
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+1"));
            String formattedDate = sdf.format(date);

            reports.put(formattedDate, r);
        }
    }

    private static class Report{
        public String summary, temperatures;
    }
}
