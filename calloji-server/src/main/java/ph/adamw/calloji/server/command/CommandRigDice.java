package ph.adamw.calloji.server.command;

import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.server.ServerRouter;

@Slf4j
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
