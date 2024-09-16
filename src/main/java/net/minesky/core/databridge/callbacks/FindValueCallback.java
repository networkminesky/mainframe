package net.minesky.core.databridge.callbacks;

import org.bson.Document;

public interface FindValueCallback {

    public void onQueryDone(Document player, Object value, boolean found);

    public void onQueryError(ErrorType type);

}
