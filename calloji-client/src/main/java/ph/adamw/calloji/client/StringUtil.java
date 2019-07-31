package ph.adamw.calloji.client;

import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;

public class StringUtil {
    public static String formatSecondMinutes(int seconds) {
        return (Integer.toString(seconds / 60).length() == 1 ? "0" : "")
                + seconds / 60 + ":"
                + (Integer.toString(seconds % 60).length() == 1 ? "0" : "")
                + seconds % 60;
    }
}


