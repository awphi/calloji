package ph.adamw.calloji.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LoggerUtils {
	public static void establishLevels(String[] args, Logger log) {
		if(args.length > 0) {
			final List<String> arg = Arrays.asList(args);

			if(arg.contains("--stacktrace")) {
				log.setLevel(Level.FINEST);
				return;
			}

			if(arg.contains("--debug")) {
				log.setLevel(Level.FINE);
			}
		}
	}

	public static void bindToConsole(Logger log) {
		Handler handler = new ConsoleHandler();
		handler.setLevel(Level.ALL);
		handler.setFormatter(new DateFormatter());

		log.addHandler(handler);
	}

	private static class DateFormatter extends Formatter {
		// Create a DateFormat to format the logger timestamp.
		private static final DateFormat df = new SimpleDateFormat("hh:mm:ss.SS");

		public String format(LogRecord record) {
			StringBuilder builder = new StringBuilder(1000);
			builder.append("[").append(record.getLevel()).append("] ");
			builder.append(df.format(new Date(record.getMillis()))).append(" => ");
			builder.append("[").append(record.getSourceClassName()).append(".");
			builder.append(record.getSourceMethodName()).append("] - ");
			builder.append(formatMessage(record));
			builder.append("\n");
			return builder.toString();
		}
	}
}
