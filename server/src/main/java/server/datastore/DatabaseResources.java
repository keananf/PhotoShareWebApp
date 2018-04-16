package server.datastore;

/**
 * Class featuring database information (table names, schema, etc).
 */
public abstract class DatabaseResources {
    // Table names
    public static final String PHOTOS_TABLE = "photos";
    public static final String COMMENTS_TABLE = "comments";
    public static final String COMMENTS_VOTES_TABLE = "votes";
    public static final String USERS_TABLE = "users";
    public static final String NOTIFICATIONS_TABLE = "notifications";

    // User table attributes
    public static final String PARENTNAME = "parentname";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String USERS_ADMIN = "admin";

    // Photo table attributes
    public static final String PHOTOS_ID = "photoId";
    public static final String PHOTOS_NAME = "photoName";
    public static final String PHOTOS_CONTENTS = "photoContents";
    public static final String PHOTOS_TIME = "photoTimestamp";

    // Comment table attributes
    public static final String REFERENCE_ID = "referenceId";
    public static final String COMMENTS_ID = "commentId";
    public static final String COMMENT_TYPE = "type";
    public static final String COMMENTS_CONTENTS = "commentContents";
    public static final String COMMENTS_TIME = "commentTimestamp";
    public static final String VOTE = "vote";
}
