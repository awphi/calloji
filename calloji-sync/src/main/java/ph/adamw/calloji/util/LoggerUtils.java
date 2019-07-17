package ph.adamw.calloji.util;

import java.util.Arrays;
import java.util.List;

public class LoggerUtils {
	public static void setFormatting() {
		setProperty("showDateTime", "true");
		setProperty("dateTimeFormat", "[dd/MM/yy HH:mm:ss]");
		setProperty("levelInBrackets", "true");
	}

	public static void init(String[] args) {
		setFormatting();
		setProperty("defaultLogLevel", "info");
		establishLevels(args);
	}

	public static void establishLevels(String[] args) {
		final List<String> list = Arrays.asList(args);
		if(list.contains("--debug")) {
			setProperty("defaultLogLevel", "debug");
		}

		if(list.contains("--stacktrace")) {
			setProperty("defaultLogLevel", "trace");
		}
	}

	public static void setProperty(String x, String y) {
		System.setProperty("org.slf4j.simpleLogger." + x, y);
	}
}
