package io.flexwork.web.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SharedController {

    @GetMapping("/timezones")
    public List<ZoneInfo> getTimezones() {
        return getTimeZoneList("GMT");
    }

    private static String calculateOffset(int rawOffset) {
        if (rawOffset == 0) {
            return "+00:00";
        }
        long hours = TimeUnit.MILLISECONDS.toHours(rawOffset);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(rawOffset);
        minutes = Math.abs(minutes - TimeUnit.HOURS.toMinutes(hours));

        return String.format("%+03d:%02d", hours, Math.abs(minutes));
    }

    private static List<ZoneInfo> getTimeZoneList(String base) {
        String[] availableZoneIds = TimeZone.getAvailableIDs();
        List<ZoneInfo> result = new ArrayList<>(availableZoneIds.length);

        for (String zoneId : availableZoneIds) {
            TimeZone curTimeZone = TimeZone.getTimeZone(zoneId);
            String offset = calculateOffset(curTimeZone.getRawOffset());
            result.add(new ZoneInfo(zoneId, String.format("(%s%s)", base, offset)));
        }
        Collections.sort(result);
        return result;
    }

    public record ZoneInfo(String zoneId, String offset) implements Comparable<ZoneInfo> {
        @Override
        public int compareTo(ZoneInfo o) {
            return this.offset.compareTo(o.offset);
        }
    }
}
