package server.objects;

/**
 * Defines the common behaviour of objects who can
 * have notifications asserted with them
 *
 */

public interface NotifiableEvent {
    long getContentId();
    EventType getEventType();
    String getParentName();
}
