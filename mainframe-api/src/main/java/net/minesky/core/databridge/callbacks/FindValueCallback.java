package net.minesky.core.databridge.callbacks;

import org.bson.Document;

public interface FindValueCallback {
    void onQueryDone(Document player, Object value, boolean found);

    void onQueryError(ErrorType type);
}
