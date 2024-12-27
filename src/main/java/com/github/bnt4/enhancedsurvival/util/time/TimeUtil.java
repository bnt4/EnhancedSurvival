package com.github.bnt4.enhancedsurvival.util.time;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    /**
     * Returns the given milliseconds in hours, minutes and seconds (5h 3min 32s).
     * If a unit is 0, it won't be visible (5h 0min 32s -> 5h 32s).
     *
     * @param millis milliseconds to be converted
     * @return hours, minutes and seconds (5h 3min 32s)
     */
    public static String formatDurationSeconds(long millis) {
        if (millis <= 0) return "0s";
        if (millis < 60_000) return millis / 1000 + "s";

        long hours = millis / 3600000;
        long minutes = millis % 3600000 / 60000;
        long seconds = millis / 1000 % 60;

        return hours == 0
                ? minutes + (seconds != 0 ? "min " + seconds + "s" : "min")
                : (minutes == 0
                ? hours + (seconds != 0 ? "h " + seconds + "s" : "h")
                : hours + "h " + minutes + (seconds != 0 ? "min " + seconds + "s" : "min"));
    }

    public static String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
    }

}
