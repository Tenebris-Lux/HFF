package lucis.lux.hff.data;

import java.util.HashMap;
import java.util.Map;

public class AttachmentRegistry {

    private static final Map<String, AttachmentData> REGISTRY = new HashMap<>();

    public static void register(String id, AttachmentData data) {
        REGISTRY.put(id, data);
    }

    public static AttachmentData get(String id) {
        return REGISTRY.get(id);
    }
}
