package ph.adamw.calloji.client;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class StringUtil {
    final static SimpleDateFormat minuteSecondsFormat = new SimpleDateFormat("mm:ss");

    static {
        minuteSecondsFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public static String formatSecondMinutes(int seconds) {
        return minuteSecondsFormat.format(new Date(seconds));
    }
}


