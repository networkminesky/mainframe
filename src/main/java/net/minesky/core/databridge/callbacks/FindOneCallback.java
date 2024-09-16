package net.minesky.core.databridge.callbacks;

import org.bson.Document;

public interface FindOneCallback {

    public void onQueryError(ErrorType type);

    public void onQueryDone(Document document);

}
