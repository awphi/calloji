package ph.adamw.calloji.packet;

public enum PacketType {
    PLAYER_UPDATE,
    BOARD_UPDATE,
    CLIENT_CONNECTION_UPDATE,
    NEW_TURN,
    TURN_EXTENSION,
    CHAT_MESSAGE,
    NICK_APPROVED,
    NICK_EDIT,
    HEARTBEAT,
    ROLL_DICE_REQ,
    PLOT_LANDED_ON,
    PLOT_PURCHASED,
    CARD_DRAWN,
    AUCTION_REQUEST,
    AUCTION_START,
    AUCTION_BID,
    TOP_BIDDER_UPDATE,
    MORTGAGE_REQUEST,
    HOUSE_REQUEST,
    FORCE_MANAGE_ASSETS;

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
