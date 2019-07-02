package ph.adamw.calloji.client.gui.monopoly;

import com.google.common.collect.ImmutableMap;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.packet.data.plot.Plot;
import ph.adamw.calloji.packet.data.plot.PlotType;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;
import ph.adamw.calloji.packet.data.plot.StreetPlot;

import javax.annotation.Nullable;

@Slf4j
public class PlotUI extends BorderPane {
    private final BoardUI board;

    private final VBox bottomBox = new VBox();

    private final HBox centreBox = new HBox();

    public PlotUI(@Nullable BoardUI board) {
        this.board = board;

        centreBox.getStyleClass().add("centred");
        bottomBox.getStyleClass().add("centred");
        setBottom(bottomBox);
        setCenter(centreBox);
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

    private Text generatePlotText(String x, String... classes) {
        final Text mid = new Text(x);
        mid.getStyleClass().addAll(classes);
        mid.setTextAlignment(TextAlignment.CENTER);

        double width = BoardUI.WIDTH;

        if(board != null) {
            width = board.getColumnConstraints().get(0).getPrefWidth();
        }

        mid.wrappingWidthProperty().setValue(width);
        setAlignment(mid, Pos.CENTER);

        return mid;
    }

    public void load(Plot plot) {
        getStyleClass().addAll("border", "border-in", "plot");

        bottomBox.getChildren().clear();

        bottomBox.getChildren().add(generatePlotText(plot.getName()));

        // Header
        if(COLOR_MAP.containsKey(plot.getType())) {
            final HBox top = new HBox();
            top.getStyleClass().addAll("border", "border-out", "plot-header");
            top.setStyle("-fx-background-color: #" + Integer.toHexString(COLOR_MAP.get(plot.getType()).hashCode()) + ";");

            setTop(top);
        } else {
            switch (plot.getType()) {
                case COMMUNITY_CHEST: break;
            }
        }

        if(plot instanceof PropertyPlot) {
            final PropertyPlot x = ((PropertyPlot) plot);
            final String c = x.getOwner() == null ? "" : "strikethrough";
            bottomBox.getChildren().add(generatePlotText("£" + x.getValue(), "bold", c));
        }

        if(plot instanceof StreetPlot) {
            final StreetPlot s = (StreetPlot) plot;

            ((HBox) getTop()).getChildren().clear();

            for(int i = 0; i < s.getHouses(); i ++) {
                final Pane pane = new Pane();
                pane.getStyleClass().addAll("border", "house");
                ((HBox) getTop()).getChildren().add(pane);
            }
        }
    }

    public void addCentre(Node i) {
        centreBox.getChildren().add(i);
    }

    public void removeCentre(Node i) {
        centreBox.getChildren().remove(i);
    }
}
