package lucis.lux.hff.interactions.events;

import com.hypixel.hytale.event.IEvent;

public class OnCheckTimeout implements IEvent<Void> {
    private final String data;

    public OnCheckTimeout(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

}
