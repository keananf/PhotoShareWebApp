package server.objects;

/**
 * Class representing a transaction receipt for a comment / photo
 */
public final class Receipt {
    private final long referenceId;

    public Receipt(long referenceId) {
        this.referenceId = referenceId;
    }

    /**
     * @return the unique id of the newly created comment / photo
     */
    public long getReferenceId() {
        return referenceId;
    }
}
