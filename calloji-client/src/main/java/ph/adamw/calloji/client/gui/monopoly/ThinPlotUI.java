package ph.adamw.calloji.client.gui.monopoly;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import ph.adamw.calloji.packet.data.plot.Plot;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;


public class ThinPlotUI extends HBox {
    private final static Insets PADDING = new Insets(5, 10, 5, 10);
    private final static Insets LABEL_MARGIN = new Insets(0, 0, 0, 10);

    public ThinPlotUI(Plot plot) {
        setAlignment(Pos.CENTER_LEFT);
        setPadding(PADDING);
        setMaxHeight(25);

        final Rectangle header = new Rectangle();
        header.setArcHeight(2);
        header.setArcWidth(2);
        header.setHeight(10);
        header.setWidth(20);
        header.setStroke(Color.BLACK);
        header.setFill(PlotUI.COLOR_MAP.get(plot.getType()));
        getChildren().add(header);

        final Label label = new Label();
        label.setText(plot.getName());
        getChildren().add(label);
        HBox.setMargin(label, LABEL_MARGIN);

        if(plot instanceof PropertyPlot) {
            final PropertyPlot prop = (PropertyPlot) plot;
            final Label label2 = new Label();
            label2.setText("£" + prop.getValue());
            label2.getStyleClass().add("bold");
            getChildren().add(label2);
            HBox.setMargin(label2, LABEL_MARGIN);
        }
    }
}
