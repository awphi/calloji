package ph.adamw.calloji.server.console.command;

import lombok.extern.log4j.Log4j2;
import ph.adamw.calloji.server.ServerRouter;

@Log4j2
public class CommandRigDice extends Command {
    public CommandRigDice() {
        super("rigdice", new CommandSyntax[] {
                new CommandSyntax("[integer]")
        });
    }

    @Override
    public void handle(Object[] args) {
        final Integer inp = (Integer) args[0];
        ServerRouter.getGame().rigDice(inp);
        log.info("Next dice roll rigged to roll a " + inp + ".");
    }
}
