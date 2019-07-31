package ph.adamw.calloji.client.gui.monopoly;

import com.google.common.collect.ImmutableMap;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.client.gui.GuiUtils;
import ph.adamw.calloji.packet.data.plot.Plot;
import ph.adamw.calloji.packet.data.plot.PlotType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;
import ph.adamw.calloji.packet.data.plot.StreetPlot;

import javax.annotation.Nullable;

@Slf4j
public class PlotUI extends StackPane {
    private final BoardUI board;
    private Label valueText = GuiUtils.buildStyledLabel("", "bold");
    private Label nameText = GuiUtils.buildStyledLabel("");
    private Tooltip ownerTooltip = new Tooltip("Owner: -");

    public PlotUI(@Nullable BoardUI board) {
        this(board, true);
    }

    public PlotUI(@Nullable BoardUI board, boolean dynamic) {
        this.board = board;

        StackPane.setAlignment(valueText, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(nameText, Pos.CENTER);
        getChildren().addAll(valueText, nameText);

        nameText.setTooltip(ownerTooltip);

        if(!dynamic) {
            setMinWidth(BoardUI.WIDTH);
            setMinHeight(BoardUI.WIDTH);
        }
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

        // Header
        if(COLOR_MAP.containsKey(plot.getType())) {
            final HBox top = new HBox();
            top.getStyleClass().addAll("border", "border-out", "plot-header");
            top.setAlignment(Pos.CENTER);
            top.setStyle("-fx-background-color: #" + Integer.toHexString(COLOR_MAP.get(plot.getType()).hashCode()) + ";");
            top.setMaxHeight(10);
            StackPane.setAlignment(top, Pos.TOP_CENTER);

            getChildren().add(0, top);
        }

        if(plot instanceof PropertyPlot) {
            final PropertyPlot x = ((PropertyPlot) plot);
            valueText.setText("£" + x.getValue() + ".00");
            valueText.setTextFill(Color.BLACK);

            if(x.getOwner() != null) {
                valueText.getStyleClass().add("strikethrough");
                ownerTooltip.setStyle("-fx-text-fill: white;");
                ownerTooltip.setText("Owner: " + Client.getCache().getCachedPlayer(x.getOwner()).getNick());
            } else {
                valueText.getStyleClass().remove("strikethrough");
                ownerTooltip.setStyle("-fx-text-fill: red;");
                ownerTooltip.setText("Owner: -");
            }

            if(x.isMortgaged()) {
                valueText.setTextFill(Color.RED);
                valueText.setText(valueText.getText() + " (M)");
            }
        }

        if(plot instanceof StreetPlot) {
            final StreetPlot s = (StreetPlot) plot;
            final HBox top = ((HBox) getChildren().get(0));

            top.getChildren().clear();

            int houses = s.getHouses() % 5;
            int hotels = s.getHouses() / 5;

            for(int i = 0; i < houses + hotels; i ++) {
                final Pane pane = new Pane();
                String type = "house";

                if(hotels > 0) {
                    type = "hotel";
                    hotels --;
                }

                pane.getStyleClass().addAll("border", type);
                top.getChildren().add(pane);
            }
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
