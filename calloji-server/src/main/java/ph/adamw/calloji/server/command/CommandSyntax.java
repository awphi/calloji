package ph.adamw.calloji.server.command;

import lombok.Getter;

public class CommandSyntax {
	private final String[] pattern;
	private final String fullString;

	@Getter
	private final String helpText;

	@Getter
	private final boolean isOnHelp;

	public CommandSyntax(String pattern, String helpText) {
		this(pattern, helpText, true);
	}

	public CommandSyntax(String pattern, String helpText, boolean isOnHelp) {
		this.helpText = helpText;
		this.isOnHelp = isOnHelp;

		this.fullString = pattern;

		if(pattern.isEmpty()) {
			this.pattern = new String[0];
		} else {
			this.pattern = pattern.split(" ");
		}
	}
	
	@Override
	public String toString() {
		return fullString;
	}

	public boolean syntaxMatches(String[] args) {
		if(pattern.length != args.length) {
			return false;
		}

		for(int i = 0; i < args.length; i ++) {
			if(pattern[i].startsWith("[")) {
				final CommandArgument argument = CommandArgument.fromPattern(pattern[i]);

				if (!argument.isSyntaxValid(args[i])) {
					return false;
				}
			} else {
				if (!args[i].equalsIgnoreCase(pattern[i])) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Matches raw arguments with raw strings expressed in the command syntax (i.e. not variable inputs [integer], [string] etc.)
	 * @param args Arguments to run comparison against
	 * @return int - How many raw string isValidNewStructure were present
	 */
	public int getArgumentMatches(String[] args) {
		int c = 0;

		for(int i = 0; i < args.length; i ++) {
			if(i >= pattern.length) {
				break;
			}


			if(!pattern[i].startsWith("[")) {
				if (args[i].equalsIgnoreCase(pattern[i])) {
					c ++;
				}
			}
		}

		return c;
	}

	public String isDataValid(String[] args) {
		for(int i = 0; i < args.length; i ++) {
			if(pattern[i].startsWith("[")) {
				final CommandArgument argument = CommandArgument.fromPattern(pattern[i]);

				if (argument.getObjectFromArg(args[i]) == null) {
					return argument.getInvalidDataString(args[i]);
				}
			} else {
				if(!args[i].equalsIgnoreCase(pattern[i])) {
					return "Didn't understand argument: " + args[i] + ", expected: " + pattern[i] + ".";
				}
			}
		}

		return null;
	}

	public Object[] stringArgsToObjects(String[] args) {
		final Object[] objs = new Object[args.length];

		for(int i = 0; i < args.length; i ++) {
			if(pattern[i].startsWith("[")) {
				final CommandArgument argument = CommandArgument.fromPattern(pattern[i]);
				objs[i] = argument.getObjectFromArg(args[i]);
			} else {
				// Raw string (i.e. for switcher commands like /admin eco, /admin region
				objs[i] = pattern[i];
			}
		}

		return objs;
	}

	public String toHumanString() {
		final StringBuilder sb = new StringBuilder();

		for(String i : pattern) {
			sb.append(" ");
			if(i.startsWith("[")) {
				sb.append("<" + CommandArgument.fromPattern(i).toHumanString() + ">");
			} else {
				sb.append(i);
			}
		}

		return sb.toString();
	}
}
