package ph.adamw.calloji.packet;

public enum PacketType {
    PLAYER_UPDATE,
    BOARD_UPDATE,
    CLIENT_CONNECTION_UPDATE,
    TURN_UPDATE,
    CHAT,
    NICK_APPROVED;

    private static PacketType[] VALUES = values();

    public int getId() {
        return ordinal();
    }

    public static PacketType getPacket(int id) {
        return VALUES[id];
    }
}
