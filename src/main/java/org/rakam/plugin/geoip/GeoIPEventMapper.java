package org.rakam.plugin.geoip;


import com.google.inject.Inject;
import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;
import com.maxmind.geoip.timeZone;
import org.rakam.plugin.CollectionMapperPlugin;
import org.rakam.util.json.JsonObject;

import java.io.IOException;

/**
 * Created by buremba on 26/05/14.
 */
public class GeoIPEventMapper implements CollectionMapperPlugin {
    LookupService lookup;

    @Inject
    public GeoIPEventMapper() throws IOException {
//        lookup = new LookupService(ServiceStarter.conf.getString("geoip_location"), LookupService.GEOIP_MEMORY_CACHE );
    }

    @Override
    public boolean map(JsonObject event) {
        String IP = event.getString("ip");
        if (IP != null) {
            Location l1 = lookup.getLocation(IP);
            event.put("country", l1.countryName);
            event.put("country code", l1.countryCode);
            event.put("region", l1.region);
            event.put("city", l1.city);
            event.put("latitude", l1.latitude);
            event.put("longitude", l1.longitude);
            event.put("timezone", timeZone.timeZoneByCountryAndRegion(l1.countryCode, l1.region));
        }
        return true;
    }
}
