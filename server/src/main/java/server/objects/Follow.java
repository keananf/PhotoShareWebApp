package server.objects;

/**
 *
 * Class representing a follow relationship from one user to another
 *
 */

public class Follow implements NotifiableEvent{
    private final String userFrom;
    private final String userTo;
    private final long id;
    private final EventType eventType = EventType.FOLLOW;

    public Follow(String userFrom, String userTo, long id){
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.id = id;
    }


    @Override
    public long getContentId() {
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

    public String getUserTo() {
        return userTo;
    }
}
