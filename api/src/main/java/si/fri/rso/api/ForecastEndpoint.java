package si.fri.rso.api;

import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;
import com.kumuluz.ee.configuration.sources.FileConfigurationSource;
import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Path("forecast")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@ApplicationScoped
@ConfigBundle("test-service.config")
public class ForecastEndpoint {

    @ApplicationScoped
    static HashMap<String, Forecast> cachedForecasts = new HashMap<>();

    @Inject
    DelayService delayService;

    @GET
    @ApplicationScoped
    @Path("/cities/{city}")
    public Response get(@PathParam(value="city") String city){
        delayService.Delay();
        String coords = GetCoordinates(city);
        if(cachedForecasts.containsKey(city)
                && cachedForecasts.get(city).Timestamp.isAfter(Instant.now().minusSeconds(60*60))){
            return Response.ok(cachedForecasts.get(city)).build();
        }else{
            try {
                String jsonString = FromApi(coords);
                Forecast f = new Forecast();
                f.Parse(new JSONObject(jsonString));
                cachedForecasts.put(city, f);
                return Response.ok(f).build();
            } catch (Exception e) {
                cachedForecasts.remove(city);
                e.printStackTrace();
                return Response.serverError().build();
            }
        }
    }

    private String GetCoordinates(String city){
        HashMap<String, String> locMap = new HashMap<>();
        locMap.put("LJLJ", "46.05,14.50");
        locMap.put("EHAM", "52.37,04.89");
        locMap.put("LYBE", "44.78,20.44");
        locMap.put("EBBR", "50.84,04.35");
        locMap.put("LROP", "44.42,26.09");
        locMap.put("EKCH", "55.67,12.56");
        locMap.put("LDDU", "42.65,18.09");
        locMap.put("EDDL", "51.22,06.77");
        locMap.put("EDDF", "50.11,08.68");
        locMap.put("LSGG", "46.19,06.14");
        locMap.put("EDDH", "53.55,09.99");
        locMap.put("LTBA", "41.01,28.97");
        locMap.put("UKKK", "50.44,30.53");
        locMap.put("UUEE", "55.75,37.61");
        locMap.put("EDDM", "48.14,11.58");
        locMap.put("LFPG", "48.85,02.35");
        locMap.put("LYPG", "42.43,19.26");
        locMap.put("LKPR", "50.07,14.43");
        locMap.put("BKPR", "42.66,21.16");
        locMap.put("LYSA", "43.85,18.41");
        locMap.put("LWSK", "41.99,21.42");
        locMap.put("LBSF", "42.69,23.32");
        locMap.put("LATI", "41.32,19.81");
        locMap.put("LOWW", "48.20,16.37");
        locMap.put("EPWA", "52.23,21.01");
        locMap.put("LSZH", "47.36,08.54");
        return locMap.get(city);
    }

    private String FromApi(String coords) throws Exception{
        Optional<String> s = ConfigurationUtil.getInstance().get("kumuluzee.env.darksky");
        if(!s.isPresent()) throw new Exception("Missing Darksky API key");
        URL url = new URL("https://api.darksky.net/forecast/"+s.get()+"/"+coords+"?units=si");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        int status = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();
        return content.toString();
    }
}
