package ph.adamw.calloji.packet;

public enum PacketType {
    PLAYER_UPDATE,
    BOARD_UPDATE,
    CLIENT_CONNECTION_UPDATE,
    TURN_UPDATE,
    CHAT,
    NICK_APPROVED,
    NICK_EDIT,
    HEARTBEAT,
    ROLL_DICE_REQ,
    DICE_ROLL_RESPONSE,
    PLOT_LANDED_ON,
    PLOT_PURCHASED;

    private static PacketType[] VALUES = values();

    public int getId() {
        return ordinal();
    }

    public static PacketType getPacket(int id) {
        if(id < 0 || id >= VALUES.length) {
            return null;
        }

        return VALUES[id];
    }
}
