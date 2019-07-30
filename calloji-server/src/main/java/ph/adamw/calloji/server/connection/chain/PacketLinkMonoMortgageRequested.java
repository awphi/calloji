package ph.adamw.calloji.server.connection.chain;

import com.google.gson.JsonElement;
import ph.adamw.calloji.packet.PacketLinkType;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;
import ph.adamw.calloji.server.ServerRouter;
import ph.adamw.calloji.server.connection.ClientConnection;
import ph.adamw.calloji.server.monopoly.MonoGame;
import ph.adamw.calloji.server.monopoly.MonoPropertyPlot;
import ph.adamw.calloji.util.JsonUtils;

@PacketLinkType(PacketType.MORTGAGE_REQUEST)
public class PacketLinkMonoMortgageRequested extends PacketLinkMono {
    @Override
    public void handle(PacketType type, JsonElement content, ClientConnection connection) {
        final MonoGame game = ServerRouter.getGame();
        final PropertyPlot plot = JsonUtils.getObject(content, PropertyPlot.class);

        final MonoPropertyPlot monoPlot = game.getMonoBoard().getMonoPlot(plot);

        if(!monoPlot.getPlot().isClean()) {
            monoPlot.mortgage();
        } else if(monoPlot.getPlot().isMortgaged() && game.getMonoPlayer(connection.getId()).getPlayer().getBalance() >= monoPlot.getPlot().getUnmortgageCost()) {
            monoPlot.unmortgage();
        }
    }
}
