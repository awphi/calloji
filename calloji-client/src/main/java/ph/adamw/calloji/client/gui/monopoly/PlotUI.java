package ph.adamw.calloji.client.gui.monopoly;

import com.google.common.collect.ImmutableMap;
import javafx.geometry.Insets;
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
public class PlotUI extends StackPane {
    private final BoardUI board;
    private Text valueText;
    private Text nameText;

    public PlotUI(@Nullable BoardUI board) {
        this(board, true);
    }

    public PlotUI(@Nullable BoardUI board, boolean dynamic) {
        this.board = board;

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

    private Text generatePlotText(String x, String... classes) {
        final Text text = new Text(x);
        text.getStyleClass().addAll(classes);
        text.setTextAlignment(TextAlignment.CENTER);

        double width = BoardUI.WIDTH;

        if(board != null) {
            width = board.getColumnConstraints().get(0).getPrefWidth();
        }

        text.wrappingWidthProperty().setValue(width);
        return text;
    }

    public void load(Plot plot) {
        getStyleClass().addAll("border", "border-in", "plot");

        if(nameText == null) {
            final Text t = generatePlotText(plot.getName());
            add(t);
            StackPane.setAlignment(t, Pos.CENTER);
        } else {
            nameText.setText(plot.getName());
        }

        // Header
        if(COLOR_MAP.containsKey(plot.getType())) {
            final HBox top = new HBox();
            top.getStyleClass().addAll("border", "border-out", "plot-header");
            top.setStyle("-fx-background-color: #" + Integer.toHexString(COLOR_MAP.get(plot.getType()).hashCode()) + ";");
            top.setMaxHeight(10);
            StackPane.setAlignment(top, Pos.TOP_CENTER);

            getChildren().add(0, top);
        }

        if(plot instanceof PropertyPlot) {
            final PropertyPlot x = ((PropertyPlot) plot);
            final String c = x.getOwner() == null ? "" : "strikethrough";

            if(valueText == null) {
                final Text t = generatePlotText("£" + x.getValue() + ".00", "bold", c);
                add(t);
                StackPane.setAlignment(t, Pos.BOTTOM_CENTER);
            } else {
                valueText.setText("£" + x.getValue() + ".00");
            }
        }

        if(plot instanceof StreetPlot) {
            final StreetPlot s = (StreetPlot) plot;
            final HBox top = ((HBox) getChildren().get(0));

            top.getChildren().clear();

            for(int i = 0; i < s.getHouses(); i ++) {
                //TODO merge every 4 houses into a hotel
                final Pane pane = new Pane();
                pane.getStyleClass().addAll("border", "house");
                top.getChildren().add(pane);
            }
        }
    }

    public void add(Node i) {
        getChildren().add(i);
    }

    public void remove(Node i) {
        getChildren().remove(i);
    }
}
