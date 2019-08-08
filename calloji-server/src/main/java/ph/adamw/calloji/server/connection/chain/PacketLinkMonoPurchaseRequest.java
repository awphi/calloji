package ph.adamw.calloji.server.connection.chain;

import com.google.gson.JsonElement;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.packet.PacketLinkType;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;
import ph.adamw.calloji.server.ServerRouter;
import ph.adamw.calloji.server.connection.ClientConnection;
import ph.adamw.calloji.server.monopoly.MonoGame;
import ph.adamw.calloji.server.monopoly.MonoPlayer;
import ph.adamw.calloji.util.JsonUtils;

@PacketLinkType(PacketType.PLOT_PURCHASED)
@Log4j2
public class PacketLinkMonoPurchaseRequest extends PacketLinkMonoTurnOnly {
    @Override
    public void handle(PacketType type, JsonElement content, MonoPlayer player) {
        final PropertyPlot plot = JsonUtils.getObject(content, PropertyPlot.class);

        final MonoGame game = ServerRouter.getGame();
        final PropertyPlot stoodOn = (PropertyPlot) game.getMonoBoard().getIndexedPlot(game.getCurrentTurnPlayer().getPlayer().boardPosition);

        if(stoodOn.equals(plot)) {
            game.getCurrentTurnPlayer().tryRemoveMoney(stoodOn.getValue());
            game.getCurrentTurnPlayer().addAsset(game.getMonoBoard().getMonoPlot(stoodOn));
        }
    }
}
