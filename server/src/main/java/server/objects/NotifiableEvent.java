package server.objects;

public interface NotifiableEvent {
    long getContentID();
    long getReferenceId();
    EventType getEventType();
    String getParentName();

}
