package ph.adamw.calloji.client.gui.anim;

import javafx.animation.Transition;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import ph.adamw.calloji.client.gui.monopoly.GenericPlayerUI;

public class MoneyTransition extends Transition {
    private final Text text;
    private final int previousValue;
    private final int value;
    private final GenericPlayerUI parent;

    private final Text changeLabel = new Text();

    public MoneyTransition(Text text, int previousValue, int value) {
        this.text = text;
        this.previousValue = previousValue;
        this.value = value;
        this.parent = (GenericPlayerUI) text.getParent();

        changeLabel.setText("$" + Integer.toString(value - previousValue));

        if(value - previousValue > 0) {
            changeLabel.setFill(Color.GREEN);
        } else {
            changeLabel.setFill(Color.RED);
        }

        changeLabel.setVisible(false);
        changeLabel.setX(text.getX());
        parent.getChildren().add(changeLabel);
    }


    @Override
    protected void interpolate(double frac) {
        changeLabel.setVisible(true);
        //TODO test these values
        changeLabel.setY(10f + frac * 30f);
        changeLabel.setOpacity(1 / (frac + 0.01f));

        if(frac == 1.0) {
            parent.getChildren().remove(changeLabel);
        }
    }
}
