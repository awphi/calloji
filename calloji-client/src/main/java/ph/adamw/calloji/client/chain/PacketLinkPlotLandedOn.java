package ph.adamw.calloji.client.chain;

import com.google.gson.JsonElement;
import javafx.application.Platform;
import lombok.extern.log4j.Log4j2;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.client.gui.BuyGuiController;
import ph.adamw.calloji.packet.PacketLinkBase;
import ph.adamw.calloji.packet.PacketLinkType;
import ph.adamw.calloji.packet.PacketType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;
import ph.adamw.calloji.util.JsonUtils;

import java.io.IOException;

@Log4j2
@PacketLinkType(PacketType.PLOT_LANDED_ON)
public class PacketLinkPlotLandedOn extends PacketLinkBase {
    @Override
    public void handle(PacketType type, JsonElement content) {
        final PropertyPlot plot = JsonUtils.getObject(content, PropertyPlot.class);

        Platform.runLater(() -> {
            try {
                BuyGuiController.open(Client.getStage(), plot);
            } catch (IOException e) {
                log.trace(e);
            }
        });
    }
}
