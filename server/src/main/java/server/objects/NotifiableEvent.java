package server.objects;

/**
 * Defines the common behaviour of objects who can
 * have notifications asserted with them
 *
 */

public interface NotifiableEvent {
    long getContentID();
    EventType getEventType();
    String getParentName();

}
