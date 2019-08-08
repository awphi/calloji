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
        final MonoGame game = ServerRouter.getGame();
        final MonoPlayer player = game.getMonoPlayer(connection.getId());

        if((player != game.getCurrentTurnPlayer() || player != game.getBankruptee())) {
            return;
        }

        final HouseRequest request = JsonUtils.getObject(content, HouseRequest.class);
        final MonoStreetPlot monoPlot = (MonoStreetPlot) game.getMonoBoard().getMonoPlot(request.getPlot());
        final StreetPlot plot = monoPlot.getPlot();

        if(player == game.getCurrentTurnPlayer() && request.isBuild() && plot.canBuildHouse(player.getPlayer(), game.getMonoBoard().getBoard())) {
            monoPlot.buildHouse();
        } else if(!request.isBuild() && plot.canSellHouse(player.getPlayer(), game.getMonoBoard().getBoard())) {
            monoPlot.sellHouse();
        }
    }
}
