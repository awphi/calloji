package ph.adamw.calloji.server.connection.chain;

import com.google.gson.JsonElement;
import ph.adamw.calloji.packet.PacketLinkType;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.HouseRequest;
import ph.adamw.calloji.packet.data.plot.StreetPlot;
import ph.adamw.calloji.server.ServerRouter;
import ph.adamw.calloji.server.connection.ClientConnection;
import ph.adamw.calloji.server.monopoly.MonoGame;
import ph.adamw.calloji.server.monopoly.MonoPlayer;
import ph.adamw.calloji.server.monopoly.MonoStreetPlot;
import ph.adamw.calloji.util.JsonUtils;

@PacketLinkType(PacketType.HOUSE_REQUEST)
public class PacketLinkMonoHouseRequest extends PacketLinkMono {
    @Override
    public void handle(PacketType type, JsonElement content, ClientConnection connection) {
        final HouseRequest request = JsonUtils.getObject(content, HouseRequest.class);

        final MonoGame game = ServerRouter.getGame();
        final MonoStreetPlot monoPlot = (MonoStreetPlot) game.getMonoBoard().getMonoPlot(request.getPlot());
        final StreetPlot plot = monoPlot.getPlot();
        final MonoPlayer player = game.getMonoPlayer(connection.getId());

        final boolean hasMonopoly = player.getPlayer().hasMonopolyOf(plot.getType(), game.getMonoBoard().getBoard());
        final boolean canAffordHouse = player.getPlayer().getBalance() >= plot.getBuildCost();
        final boolean isBuiltOn = plot.isBuiltOn();

        if(request.isBuild()) {
            if(hasMonopoly && canAffordHouse && game.getMonoBoard().getBoard().canConstructOn(plot, true)) {
                monoPlot.buildHouses(1);
            }
        } else {
            if(hasMonopoly && isBuiltOn && game.getMonoBoard().getBoard().canConstructOn(plot, false)) {
                monoPlot.sellHouses(1);
            }
        }
    }
}
