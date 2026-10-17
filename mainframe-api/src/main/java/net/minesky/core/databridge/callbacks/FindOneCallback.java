package net.minesky.core.databridge.callbacks;

import org.bson.Document;

public interface FindOneCallback {
    void onQueryError(ErrorType type);

    void onQueryDone(Document document);
}
