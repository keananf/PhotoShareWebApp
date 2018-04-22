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
    static final String COMMENTS_VOTES_TABLE = "commentVotes";
    static final String PHOTO_RATINGS_TABLE = "photoRatings";
    static final String NOTIFICATIONS_TABLE = "notifications";
    static final String FOLLOWINGS_TABLE = "followings";

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

    // Vote tables attributes
    static final String COMMENT_VOTE = "commentVote";
    static final String PHOTO_RATING = "photoRating";

    // Followings table attributes
    static final String FOLLOW_ID = "followId";
    static final String USER_FROM = "userFrom";
    static final String USER_TO = "userTo";

    // Notifications table attributes
    static final String CONTENT_TYPE = "content_type";
    static final String CONTENT_ID = "content_id";

}
