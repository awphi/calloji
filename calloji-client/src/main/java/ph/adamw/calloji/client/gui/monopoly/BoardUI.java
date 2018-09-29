package ph.adamw.calloji.client.gui.monopoly;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.*;
import lombok.extern.slf4j.Slf4j;
import ph.adamw.calloji.packet.data.Board;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BoardUI extends GridPane {
    private static final int SIZE = 11;

    private final List<PlotUI> renderedPlots = new ArrayList<>();

    public BoardUI() {
        super();

        setStyle("-fx-padding: 50px;");

        getRowConstraints().clear();
        getColumnConstraints().clear();



        for(int i = 0; i < SIZE; i ++) {
            ColumnConstraints n = new ColumnConstraints();

            n.setPrefWidth(75);
            n.setHgrow(Priority.ALWAYS);

            getColumnConstraints().add(n);
        }

        for(int i = 0; i < SIZE; i ++) {
            RowConstraints n = new RowConstraints();

            n.setPrefHeight(120);
            n.setVgrow(Priority.ALWAYS);

            getRowConstraints().add(n);

            final PlotUI[] panes = new PlotUI[getCols()];
            for(int j = 0; j < panes.length; j ++) {
                panes[j] = new PlotUI(this);
                setHalignment(panes[j], HPos.CENTER);
                setValignment(panes[j], VPos.CENTER);
            }

            addRow(getRows() - 1, panes);
        }
    }

    public void loadBoard(Board board) {
        int xDir = -1;
        int yDir = 0;

        int col = 10;
        int row = 10;

        // "Walks" along the edges of the board and fills it with the a ui plot built from the given board
        //      (starts in the bottom right w/ go)
        for(int i = 0; i < board.getPlots().size(); i ++) {
            final Node y = getManagedChildren().get(row * getCols() + col);
            if(y instanceof PlotUI) {
                final PlotUI x = (PlotUI) y;
                x.load(board.plotAt(i));
                if(renderedPlots.size() < 40) {
                    renderedPlots.add(x);
                }
            }

            col += xDir;
            row += yDir;

            if(i == 9) {
                xDir = 0;
                yDir = -1;
            }

            if(i == 19) {
                xDir = 1;
                yDir = 0;
            }

            if(i == 29) {
                xDir = 0;
                yDir = 1;
            }
        }
    }

    public PlotUI getRenderedPlot(int x) {
        return renderedPlots.get(x);
    }

    public int getRows() {
        return getRowConstraints().size();
    }

    public int getCols() {
        return getColumnConstraints().size();
    }
}
