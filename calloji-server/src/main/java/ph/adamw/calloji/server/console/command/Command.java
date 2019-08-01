package ph.adamw.calloji.server.console.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@AllArgsConstructor
@Log4j2
public abstract class Command {
    @Getter
    private final String base;

    @Getter
    private final CommandSyntax[] syntaxes;

    private CommandSyntax getValidSyntaxPattern(String[] args) {
        for(CommandSyntax i : syntaxes) {
            if(i.syntaxMatches(args)) {
                return i;
            }
        }

        return null;
    }

    public void accept(String[] args) {
        // Zero-arg commands
        if(syntaxes == null || syntaxes.length == 0) {
            handle(new Object[0]);
            return;
        }

        final CommandSyntax syntax = getValidSyntaxPattern(args);

        if(syntax == null) {
            log.debug("Invalid syntax!");
            return;
        }

        final String isDataValid = syntax.isDataValid(args);

        if(isDataValid != null) {
            log.info("Invalid Input! " + isDataValid);
            return;
        }

        handle(syntax.stringArgsToObjects(args));
    }

    public abstract void handle(Object[] args);
}
