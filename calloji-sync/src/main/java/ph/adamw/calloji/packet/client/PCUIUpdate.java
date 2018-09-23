package ph.adamw.calloji.packet.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ph.adamw.calloji.data.UIState;

@AllArgsConstructor
@Getter
public class PCUIUpdate extends PC {
    private final UIState state;

    @Override
    public void handle(IClient client) {
        client.onUIUpdate(state);
    }
}
