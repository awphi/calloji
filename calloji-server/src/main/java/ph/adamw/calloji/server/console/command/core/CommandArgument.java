package ph.adamw.calloji.server.console.command.core;

import lombok.extern.log4j.Log4j2;

import java.util.HashMap;

@Log4j2
public abstract class CommandArgument<T> {
	private final static HashMap<String, CommandArgument> registry = new HashMap<>();

	static {
		register(new CommandArgument<Integer>("[integer]") {
			@Override
			public String getInvalidDataString(String arg) {
				return arg + " is not an integer.";
			}

			@Override
			public boolean isSyntaxValid(String arg) {
				return arg.matches("[1-9][0-9]*");
			}

			@Override
			public Integer getObjectFromArg(String arg) {
				return Integer.valueOf(arg);
			}

			@Override
			public String toHumanString() {
				return "integer";
			}
		});

		register(new CommandArgument<Double>("[double]") {
			@Override
			public String getInvalidDataString(String arg) {
				return arg + " is not a rational number.";
			}

			@Override
			public boolean isSyntaxValid(String arg) {
				return arg.matches("[1-9][0-9]*(.[0-9]+)?");
			}

			@Override
			public Double getObjectFromArg(String arg) {
				return Double.valueOf(arg);
			}

			@Override
			public String toHumanString() {
				return "decimal";
			}
		});

		register(new CommandArgument<String>("[string]") {
			@Override
			public String getInvalidDataString(String arg) {
				return "Didn't recognise " + arg + " as a string";
			}

			@Override
			public boolean isSyntaxValid(String arg) {
				return !arg.isEmpty();
			}

			@Override
			public String getObjectFromArg(String arg) {
				return arg;
			}

			@Override
			public String toHumanString() {
				return "string";
			}
		});
	}

	public static void register(CommandArgument<?> e) {
		registry.put(e.pattern, e);
	}

	public static CommandArgument fromPattern(String pattern) {
		return registry.get(pattern);
	}

	private final String pattern;

	protected CommandArgument(String pattern) {
		this.pattern = pattern;
	}

	public abstract String getInvalidDataString(String arg);

	public abstract boolean isSyntaxValid(String arg);

	public abstract T getObjectFromArg(String arg);

	public abstract String toHumanString();
}
