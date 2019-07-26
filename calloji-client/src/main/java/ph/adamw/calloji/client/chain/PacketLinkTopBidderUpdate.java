package ph.adamw.calloji.client.chain;

import com.google.gson.JsonElement;
import ph.adamw.calloji.client.gui.AuctionGuiController;
import ph.adamw.calloji.packet.PacketLinkBase;
import ph.adamw.calloji.packet.PacketLinkType;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.BidUpdate;
import ph.adamw.calloji.util.JsonUtils;

@PacketLinkType(PacketType.TOP_BIDDER_UPDATE)
public class PacketLinkTopBidderUpdate extends PacketLinkBase {
    @Override
    public void handle(PacketType type, JsonElement content) {
        final BidUpdate update = JsonUtils.getObject(content, BidUpdate.class);
        AuctionGuiController.getActiveGui().onBidUpdate(update);
    }
}
