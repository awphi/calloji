package ph.adamw.calloji.packet.data;

public enum MessageType {
    // Used for chat messages from fellow players
    CHAT,

    // Used for general game messages i.e. paying rent, passing go, pulling a chance card etc.
    SYSTEM,

    // Used for severe game messages i.e. verge of bankruptcy, being jailed etc. caution: WARNING will force chat focus
    WARNING,

    // Used for game-independent messages i.e. players leaving/joining, game restarting etc. caution: ADMIN will force chat focus
    ADMIN;
}
