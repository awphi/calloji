package ph.adamw.calloji.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@AllArgsConstructor
@Getter
public class UIState implements Serializable {
    private final int balance;
    private final int jailTime;

    //TODO add all relevant fields
}
