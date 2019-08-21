package ph.adamw.calloji.client.gui.monopoly;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import ph.adamw.calloji.client.gui.GuiUtils;
import ph.adamw.calloji.packet.data.plot.Plot;
import ph.adamw.calloji.packet.data.plot.PropertyPlot;


public class ThinPlotUI extends HBox {
    private final static Insets PADDING = new Insets(5, 10, 5, 10);
    private final static Insets LABEL_MARGIN = new Insets(0, 0, 0, 10);

    public ThinPlotUI(Plot plot) {
        setAlignment(Pos.CENTER_LEFT);
        setPadding(PADDING);
        //setMaxHeight(25);

        final Rectangle header = new Rectangle();

        final IntegerProperty h = new SimpleIntegerProperty();
        h.bind(heightProperty().multiply(0.5f));
        header.heightProperty().bind(h);

        header.widthProperty().bind(header.heightProperty().multiply(2f));
        header.setStroke(Color.BLACK);
        header.setFill(GuiUtils.PLOT_COLOR_MAP.get(plot.getType()));
        getChildren().add(header);

        final Label label = new Label();
        label.setText(plot.getName());
        getChildren().add(label);
        HBox.setMargin(label, LABEL_MARGIN);

        if(plot instanceof PropertyPlot) {
            final PropertyPlot prop = (PropertyPlot) plot;
            final Label label2 = new Label();
            label2.setText("£" + prop.getValue() + ".00");
            label2.getStyleClass().add("bold");
            getChildren().add(label2);
            HBox.setMargin(label2, LABEL_MARGIN);
        }
    }
}
