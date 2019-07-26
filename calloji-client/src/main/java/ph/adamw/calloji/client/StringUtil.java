package ph.adamw.calloji.client;

import javafx.scene.control.TextFormatter;

public class StringUtil {
    public static String formatSecondMinutes(int seconds) {
        return (Integer.toString(seconds / 60).length() == 1 ? "0" : "")
                + seconds / 60 + ":"
                + (Integer.toString(seconds % 60).length() == 1 ? "0" : "")
                + seconds % 60;
    }

    public static TextFormatter<Integer> INTEGER_FORMATTER = new TextFormatter<>(change -> {
        String newText = change.getControlNewText();

        if (newText.matches("-?([1-9][0-9]*)?")) {
            return change;
        }

        return null;
    });
}


