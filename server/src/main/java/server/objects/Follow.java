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
    long referenceID;
    EventType eventType = EventType.FOLLOW;

    public Follow(String userFrom, String userTo, long id, long referenceID){
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.id = id;
        this.referenceID = referenceID;
    }


    @Override
    public long getContentID() {
        return id;
    }

    @Override
    public long getReferenceId() {
        return referenceID;
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
