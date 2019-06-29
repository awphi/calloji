package ph.adamw.calloji.client.chain;

import com.google.gson.JsonElement;
import ph.adamw.calloji.client.Client;
import ph.adamw.calloji.client.gui.MessageType;
import ph.adamw.calloji.packet.PacketLinkBase;
import ph.adamw.calloji.packet.PacketLinkType;
import ph.adamw.calloji.packet.PacketType;

@PacketLinkType(PacketType.DICE_ROLL_RESPONSE)
public class PacketLinkDiceRolled extends PacketLinkBase {
    @Override
    public void handle(PacketType type, JsonElement content) {
        final int roll = content.getAsInt();
        Client.printMessage(MessageType.SYSTEM, "You rolled a " + roll + "!");
    }
}
