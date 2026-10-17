package net.minesky.core.messaging;

public enum MessageType {
    STRING,
    INTEGER,
    LONG,
    DOUBLE,
    BOOLEAN,
    /** BSON document embedded in the message document */
    DOCUMENT,
    /** JSON string parsed as BSON on receive */
    JSON
}
