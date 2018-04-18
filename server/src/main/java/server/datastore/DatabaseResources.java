package server.datastore;

/**
 * Class featuring database information (table names, schema, etc).
 */
abstract class DatabaseResources {
    // Table names
    static final String USERS_TABLE = "users";
    static final String ALBUMS_TABLE = "albums";
    static final String PHOTOS_TABLE = "photos";
    static final String COMMENTS_TABLE = "comments";
    static final String COMMENTS_VOTES_TABLE = "votes";
    static final String NOTIFICATIONS_TABLE = "notifications";

    // User table attributes
    static final String PARENTNAME = "parentname";
    static final String USERNAME = "username";
    static final String PASSWORD = "password";
    static final String USERS_ADMIN = "admin";

    // Albums table attributes
    static final String ALBUMS_ID = "albumId";
    static final String ALBUMS_NAME = "albumName";
    static final String ALBUMS_DESCRIPTION = "albumDescription";
    static final String ALBUMS_TIME = "albumTime";

    // Photo table attributes
    static final String PHOTOS_ID = "photoId";
    static final String PHOTOS_NAME = "photoName";
    static final String PHOTOS_CONTENTS = "photoContents";
    static final String PHOTOS_TIME = "photoTime";

    // Comment table attributes
    static final String REFERENCE_ID = "referenceId";
    static final String COMMENTS_ID = "commentId";
    static final String COMMENT_TYPE = "type";
    static final String COMMENTS_CONTENTS = "commentContents";
    static final String COMMENTS_TIME = "commentTime";
    static final String VOTE = "vote";
}
