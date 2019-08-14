package ph.adamw.calloji.client.gui.monopoly;

import com.google.common.collect.ImmutableMap;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import lombok.extern.log4j.Log4j2;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.client.gui.GuiUtils;
import ph.adamw.calloji.packet.data.plot.Plot;
import ph.adamw.calloji.packet.data.plot.PlotType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;
import ph.adamw.calloji.packet.data.plot.StreetPlot;
import ph.adamw.calloji.util.GameConstants;

@Log4j2
public class PlotUI extends StackPane {
    private Label valueText = GuiUtils.buildStyledLabel("", "bold");
    private Label nameText = GuiUtils.buildStyledLabel("", "centred", "word-ellipsis");
    private Tooltip ownerTooltip = new Tooltip("Owner: -");
    private HBox header = new HBox();

    private static final Insets MARGIN_HOUSE = new Insets(0, 1, 0, 1);

    public PlotUI() {
        StackPane.setAlignment(valueText, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(nameText, Pos.CENTER);
        getChildren().addAll(valueText, nameText);

        nameText.maxHeightProperty().bind(heightProperty().multiply(0.5d));

        header.getStyleClass().addAll("border", "border-out", "plot-header");
        header.setAlignment(Pos.CENTER);
        header.maxHeightProperty().bind(heightProperty().multiply(0.2d));
        header.minHeightProperty().bind(heightProperty().multiply(0.2d));
        StackPane.setAlignment(header, Pos.TOP_CENTER);
    }

    static final ImmutableMap<PlotType, Color> COLOR_MAP = new ImmutableMap.Builder<PlotType, Color>()
            .put(PlotType.RED, Color.RED)
            .put(PlotType.BROWN, Color.BROWN)
            .put(PlotType.LIGHT_BLUE, Color.LIGHTBLUE)
            .put(PlotType.PINK, Color.PINK)
            .put(PlotType.ORANGE, Color.ORANGE)
            .put(PlotType.YELLOW, Color.YELLOW)
            .put(PlotType.GREEN, Color.GREEN)
            .put(PlotType.BLUE, Color.ROYALBLUE)
            .build();

    public void load(Plot plot) {
        getStyleClass().addAll("border", "border-in", "plot");
        nameText.setText(plot.getName());

        if(plot instanceof PropertyPlot) {
            valueText.setTooltip(ownerTooltip);

            final PropertyPlot x = ((PropertyPlot) plot);
            valueText.setText("£" + x.getValue() + ".00");
            valueText.setTextFill(Color.BLACK);

            if(x.getOwner() != null) {
                valueText.setTextFill(Color.GREEN);
                ownerTooltip.setStyle("-fx-text-fill: white;");
                ownerTooltip.setText("Owner: " + Client.getCache().getOtherPlayer(x.getOwner()).getNick());
            } else {
                valueText.setTextFill(Color.BLACK);
                ownerTooltip.setStyle("-fx-text-fill: red;");
                ownerTooltip.setText("Owner: -");
            }

            if(x.isMortgaged()) {
                valueText.setTextFill(Color.RED);
                valueText.setText(valueText.getText() + " (M)");
            }
        } else {
            valueText.setTooltip(null);
        }

        if(plot instanceof StreetPlot) {
            if(!getChildren().contains(header)) {
                getChildren().add(0, header);
            }

            header.setStyle("-fx-background-color: #" + Integer.toHexString(COLOR_MAP.get(plot.getType()).hashCode()) + ";");

            final StreetPlot s = (StreetPlot) plot;

            header.getChildren().clear();

            int houses = s.getHouses() % GameConstants.MAX_HOUSES;
            int hotels = s.getHouses() / GameConstants.MAX_HOUSES;

            for(int i = 0; i < houses + hotels; i ++) {
                final HBox house = new HBox();
                house.minHeightProperty().bind(header.heightProperty().multiply(0.8));
                house.maxHeightProperty().bind(header.heightProperty().multiply(0.8));

                String type = "house";

                if(hotels > 0) {
                    type = "hotel";
                    hotels --;
                }

                house.getStyleClass().addAll("border", "border-in", type);
                header.getChildren().add(house);
                HBox.setMargin(house, MARGIN_HOUSE);
            }
        } else {
            getChildren().remove(header);
        }
    }

    public void addChild(Node i) {
        getChildren().add(i);
    }

    public void removeChild(Node i) {
        getChildren().remove(i);
    }

    public void unload() {
        getStyleClass().clear();
        getChildren().clear();
    }
}
