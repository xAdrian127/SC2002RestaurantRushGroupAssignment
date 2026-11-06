package control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Immutable outcome object returned from Controller commands to the Boundary layer (BCE).
 * Eliminates console I/O from domain entities and controllers while preserving rich feedback.
 */
public final class ActionResult {
    private final boolean success;
    private final List<String> messages;

    private ActionResult(boolean success, List<String> messages) {
        this.success = success;
        this.messages = Collections.unmodifiableList(new ArrayList<>(messages));
    }

    public static ActionResult success(String... messages) {
        return new ActionResult(true, Arrays.asList(messages));
    }

    public static ActionResult success(List<String> messages) {
        return new ActionResult(true, messages);
    }

    public static ActionResult denied(String reason) {
        return new ActionResult(false, Collections.singletonList(reason));
    }

    public boolean isSuccess() {
        return success;
    }

    public List<String> getMessages() {
        return messages;
    }
}
