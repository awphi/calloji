package ph.adamw.calloji.client;

public class StringUtil {
    public static String formatSecondMinutes(int seconds) {
        return (Integer.toString(seconds / 60).length() == 1 ? "0" : "")
                + seconds / 60 + ":"
                + (Integer.toString(seconds % 60).length() == 1 ? "0" : "")
                + seconds % 60;
    }
}


