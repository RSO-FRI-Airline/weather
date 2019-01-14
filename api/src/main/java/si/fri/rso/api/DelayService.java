package si.fri.rso.api;

import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;
import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.kumuluz.ee.discovery.utils.Etcd2Utils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;

@ApplicationScoped
@ConfigBundle("rest-config")
public class DelayService {

    public void Delay(){
        Optional<Integer> s = ConfigurationUtil.getInstance().getInteger("rest-config.delay");
        if(s.isPresent()){
            try {
                Thread.sleep(s.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
