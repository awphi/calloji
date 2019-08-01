package ph.adamw.calloji.server.console.command;

public class CommandSyntax {
	private final String[] pattern;
	private final String fullString;

	public CommandSyntax(String pattern) {
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
}
