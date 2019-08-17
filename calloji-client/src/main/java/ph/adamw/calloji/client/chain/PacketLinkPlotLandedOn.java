package ph.adamw.calloji.client.chain;

import com.google.gson.JsonElement;
import javafx.application.Platform;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.client.gui.BuyGuiController;
import ph.adamw.calloji.packet.PacketLinkBase;
import ph.adamw.calloji.packet.PacketLinkType;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;
import ph.adamw.calloji.util.JsonUtils;

@PacketLinkType(PacketType.PLOT_LANDED_ON)
public class PacketLinkPlotLandedOn extends PacketLinkBase {
    @Override
    public void handle(PacketType type, JsonElement content) {
        final PropertyPlot plot = JsonUtils.getObject(content, PropertyPlot.class);

        Platform.runLater(() -> BuyGuiController.open(Client.getStage(), plot,
                Client.getGui().getBoardUI().getPlotWidth(), Client.getGui().getBoardUI().getPlotHeight()));
    }
}
