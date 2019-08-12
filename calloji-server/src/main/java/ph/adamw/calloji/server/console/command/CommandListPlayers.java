package ph.adamw.calloji.server.console.command;

import lombok.extern.log4j.Log4j2;
import ph.adamw.calloji.server.ServerRouter;
import ph.adamw.calloji.server.console.command.core.Command;
import ph.adamw.calloji.server.console.command.core.CommandSyntax;
import ph.adamw.calloji.server.monopoly.MonoPlayer;
import ph.adamw.calloji.util.JsonUtils;

@Log4j2
public class CommandListPlayers extends Command {
    public CommandListPlayers() {
        super("list", new CommandSyntax[] {
                new CommandSyntax("")
        });
    }

    @Override
    public void handle(Object[] args) {
        log.info(" --- Player List --- ");
        for(MonoPlayer i : ServerRouter.getGame().getPlayers()) {
            log.info(i.getConnectionNick() + ": " + i.getConnectionId() + " -> " + JsonUtils.getJsonElement(i.getPlayer()).toString());
        }
        log.info(" ------ ");
    }
}
