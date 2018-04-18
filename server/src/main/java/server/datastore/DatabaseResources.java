package server.datastore;

/**
 * Class featuring database information (table names, schema, etc).
 */
abstract class DatabaseResources {
    // Table names
    static final String PHOTOS_TABLE = "photos";
    static final String COMMENTS_TABLE = "comments";
    static final String COMMENTS_VOTES_TABLE = "votes";
    static final String USERS_TABLE = "users";
    static final String NOTIFICATIONS_TABLE = "notifications";

    // User table attributes
    static final String PARENTNAME = "parentname";
    static final String USERNAME = "username";
    static final String PASSWORD = "password";
    static final String USERS_ADMIN = "admin";

    // Photo table attributes
    static final String PHOTOS_ID = "photoId";
    static final String PHOTOS_NAME = "photoName";
    static final String PHOTOS_CONTENTS = "photoContents";
    static final String PHOTOS_TIME = "photoTimestamp";

    // Comment table attributes
    static final String REFERENCE_ID = "referenceId";
    static final String COMMENTS_ID = "commentId";
    static final String COMMENT_TYPE = "type";
    static final String COMMENTS_CONTENTS = "commentContents";
    static final String COMMENTS_TIME = "commentTimestamp";
    static final String VOTE = "vote";
}
