package server.objects;

/**
 *
 * Class representing a follow relationship from one user to another
 *
 */

public class Follow implements NotifiableEvent{

    String userFrom;
    String userTo;
    long id;
    EventType eventType = EventType.FOLLOW;

    public Follow(String userFrom, String userTo, long id){
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.id = id;
    }


    @Override
    public long getContentID() {
        return id;
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }

    @Override
    public String getParentName() {
        return userFrom;
    }
}
