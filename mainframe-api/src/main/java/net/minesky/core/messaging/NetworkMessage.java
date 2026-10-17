package net.minesky.core.messaging;

import org.bson.Document;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * Cross-network message envelope. Payload is typed; use accessors for safe reads.
 */
public final class NetworkMessage {

    private final String channel;
    private final String subchannel;
    private final MessageType type;
    private final Object payload;
    private final String sourceServerId;
    private final long timestamp;
    private final Map<String, String> headers;

    public NetworkMessage(
            String channel,
            String subchannel,
            MessageType type,
            Object payload,
            String sourceServerId,
            long timestamp,
            Map<String, String> headers
    ) {
        this.channel = channel;
        this.subchannel = subchannel;
        this.type = type;
        this.payload = payload;
        this.sourceServerId = sourceServerId;
        this.timestamp = timestamp;
        this.headers = headers == null ? Map.of() : Map.copyOf(headers);
    }

    public String channel() {
        return channel;
    }

    public String subchannel() {
        return subchannel;
    }

    public MessageType type() {
        return type;
    }

    public String sourceServerId() {
        return sourceServerId;
    }

    public long timestamp() {
        return timestamp;
    }

    public Map<String, String> headers() {
        return headers;
    }

    @Nullable
    public String header(String key) {
        return headers.get(key);
    }

    public String asString() {
        if (payload == null) {
            return null;
        }
        return payload.toString();
    }

    public int asInt() {
        if (payload instanceof Number n) {
            return n.intValue();
        }
        return Integer.parseInt(asString());
    }

    public long asLong() {
        if (payload instanceof Number n) {
            return n.longValue();
        }
        return Long.parseLong(asString());
    }

    public double asDouble() {
        if (payload instanceof Number n) {
            return n.doubleValue();
        }
        return Double.parseDouble(asString());
    }

    public boolean asBoolean() {
        if (payload instanceof Boolean b) {
            return b;
        }
        return Boolean.parseBoolean(asString());
    }

    @Nullable
    public Document asDocument() {
        if (payload instanceof Document doc) {
            return doc;
        }
        return null;
    }

    public Object raw() {
        return payload;
    }

    /** Legacy string-only listeners */
    public String legacyValue() {
        return asString() == null ? "" : asString();
    }

    Document toMongoDocument(String serverId) {
        Document doc = new Document()
                .append("channel", channel)
                .append("subchannel", subchannel)
                .append("type", type.name())
                .append("source", sourceServerId != null ? sourceServerId : serverId)
                .append("ts", timestamp);

        if (!headers.isEmpty()) {
            doc.append("headers", new Document(headers));
        }

        switch (type) {
            case STRING -> doc.append("value", Objects.toString(payload, ""));
            case INTEGER -> doc.append("value", asInt());
            case LONG -> doc.append("value", asLong());
            case DOUBLE -> doc.append("value", asDouble());
            case BOOLEAN -> doc.append("value", asBoolean());
            case DOCUMENT -> doc.append("value", asDocument());
            case JSON -> doc.append("value", Objects.toString(payload, ""));
            default -> doc.append("value", payload);
        }
        return doc;
    }

    static NetworkMessage fromMongoDocument(Document updatedDocument) {
        String channel = updatedDocument.getString("channel");
        String subchannel = updatedDocument.getString("subchannel");
        String typeName = updatedDocument.getString("type");
        MessageType type = typeName == null ? MessageType.STRING : MessageType.valueOf(typeName);
        String source = updatedDocument.getString("source");
        long ts = updatedDocument.containsKey("ts")
                ? updatedDocument.getLong("ts")
                : System.currentTimeMillis();

        Map<String, String> headers = Map.of();
        if (updatedDocument.get("headers") instanceof Document headerDoc) {
            headers = headerDoc.entrySet().stream()
                    .collect(java.util.stream.Collectors.toMap(
                            Map.Entry::getKey,
                            e -> Objects.toString(e.getValue(), "")
                    ));
        }

        Object value = updatedDocument.get("value");
        if (type == MessageType.JSON && value instanceof String json) {
            value = Document.parse(json);
            type = MessageType.DOCUMENT;
        }

        return new NetworkMessage(channel, subchannel, type, value, source, ts, headers);
    }
}
