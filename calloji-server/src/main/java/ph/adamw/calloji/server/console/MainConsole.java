package ph.adamw.calloji.server.console;

import lombok.extern.log4j.Log4j2;
import net.minecrell.terminalconsole.SimpleTerminalConsole;
import ph.adamw.calloji.server.ServerRouter;
import ph.adamw.calloji.server.console.command.Command;

import java.util.Arrays;

@Log4j2
public class MainConsole extends SimpleTerminalConsole {
    @Override
    protected boolean isRunning() {
        return true;
    }

    @Override
    protected void runCommand(String in) {
        final String[] split = in.split(" ");
        final String[] args = split.length > 1 ? Arrays.copyOfRange(split, 1, split.length) : new String[0];

        final Command cmd = ServerRouter.getParser().getCommand(split[0]);
        if(cmd != null) {
            cmd.accept(args);
        } else {
            log.info("Unrecognized command: " + in);
        }
    }

    @Override
    protected void shutdown() {
        System.exit(0);
    }
}
