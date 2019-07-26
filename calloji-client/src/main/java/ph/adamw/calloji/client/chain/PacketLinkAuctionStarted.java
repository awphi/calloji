package ph.adamw.calloji.client.chain;

import com.google.gson.JsonElement;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.client.gui.AuctionGuiController;
import ph.adamw.calloji.packet.PacketLinkBase;
import ph.adamw.calloji.packet.PacketLinkType;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;
import ph.adamw.calloji.util.JsonUtils;

@PacketLinkType(PacketType.AUCTION_START)
public class PacketLinkAuctionStarted extends PacketLinkBase {
    @Override
    public void handle(PacketType type, JsonElement content) {
        final PropertyPlot plot = JsonUtils.getObject(content, PropertyPlot.class);
        AuctionGuiController.open(Client.getStage(), plot);
    }
}
