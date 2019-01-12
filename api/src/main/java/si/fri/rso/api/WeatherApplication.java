package si.fri.rso.api;


import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("v1")
public class WeatherApplication  extends Application {
    public WeatherApplication() {
        super();
    }
}
