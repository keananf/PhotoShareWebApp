package server;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Abstract class used for maintaining information about resource location.
 */
public abstract class Resources {
    static final Charset CHARSET = StandardCharsets.UTF_8;
    public static final String CHARSET_AS_STRING = CHARSET.name();

    // Base URI the Grizzly HTTP server will listen on
    private static final String BASE_URI = "http://localhost:8080";
    public static final String BASE_URL = BASE_URI + "/photoshare";

    // Resources regarding users
    public static final String USERS_PATH = "/users";
    public static final String ADD_USER = "/adduser";
    public static final String LOGIN_USER = "/login";
    public static final String FOLLOW = "/follow";
    public static final String FOLLOWERS = "/followers";
    public static final String FOLLOWING = "/following";
    public static final String UNFOLLOW = "/unfollow";
    public static final String UNFOLLOW_USERS_PATH = USERS_PATH + UNFOLLOW;
    public static final String FOLLOW_USERS_PATH = USERS_PATH + FOLLOW;
    public static final String ADD_USER_PATH = USERS_PATH + ADD_USER;
    public static final String LOGIN_USER_PATH = USERS_PATH + LOGIN_USER;
    public static final String USERS_FOLLOWING_PATH = USERS_PATH + FOLLOWING;
    public static final String USERS_FOLLOWERS_PATH = USERS_PATH + FOLLOWERS;


    // Resources regarding albums
    public static final String ALBUMS_PATH = "/albums";
    public static final String ADD_ALBUM = "/addalbum";
    public static final String ADD_ALBUM_PATH = ALBUMS_PATH + ADD_ALBUM;
    public static final String GET_ALBUM_BY_ID_PATH = ALBUMS_PATH;
    public static final String GET_USER_ALBUMS_PATH = ALBUMS_PATH + USERS_PATH;
    public static final String UPDATE_ALBUM_DESCRIPTION = "/updatedescription";
    public static final String UPDATE_ALBUM_DESCRIPTION_PATH = ALBUMS_PATH + UPDATE_ALBUM_DESCRIPTION;

    // Resources regarding photos
    public static final String PHOTOS_PATH = "/photos";
    public static final String UPLOAD_PHOTO = "/upload";
    public static final String UPLOAD_PHOTO_PATH = PHOTOS_PATH + UPLOAD_PHOTO;
    public static final String GET_USER_PHOTOS_PATH = USERS_PATH + "/%s/" + PHOTOS_PATH;
    public static final String GET_PHOTOS_BY_ALBUM_PATH = PHOTOS_PATH + ALBUMS_PATH;
    public static final String DELETE_PHOTO = "/delete";
    public static final String DELETE_PHOTO_PATH = PHOTOS_PATH + DELETE_PHOTO;
    public static final String UPDATE_PHOTO_DESCRIPTION = "/updatedescription";
    public static final String UPDATE_PHOTO_DESCRIPTION_PATH = PHOTOS_PATH + UPDATE_PHOTO_DESCRIPTION;

    // Resources regarding comments
    public static final String COMMENTS_PATH = "/comments";
    public static final String ADD_COMMENT = "/addcomment";
    public static final String ADD_COMMENT_PATH = COMMENTS_PATH + ADD_COMMENT;
    public static final String REPLY_PATH = "/replies";
    public static final String GET_ALL_REPLIES_PATH = COMMENTS_PATH + REPLY_PATH;
    public static final String GET_ALL_PHOTO_COMMENTS_PATH = COMMENTS_PATH + PHOTOS_PATH;
    public static final String DELETE_COMMENT = "/delete";
    public static final String DELETE_COMMENT_PATH = COMMENTS_PATH + DELETE_COMMENT;
    public static final String EDIT_COMMENT = "/edit";
    public static final String EDIT_COMMENT_PATH = COMMENTS_PATH + EDIT_COMMENT;

    // Resources regarding voting
    public static final String DOWNVOTE = "/downvote";
    public static final String UPVOTE = "/upvote";
    public static final String COMMENT_DOWNVOTE_PATH = COMMENTS_PATH + DOWNVOTE;
    public static final String COMMENT_UPVOTE_PATH = COMMENTS_PATH + UPVOTE;
    public static final String PHOTO_DOWNVOTE_PATH = PHOTOS_PATH + DOWNVOTE;
    public static final String PHOTO_UPVOTE_PATH = PHOTOS_PATH + UPVOTE;

    // Resources regarding admins
    public static final String ADMIN_PATH = "/admin";
    public static final String REMOVE_COMMENT = "/removecomment";
    public static final String ADMIN_REMOVE_COMMENT_PATH = ADMIN_PATH + REMOVE_COMMENT;
    public static final String REMOVE_PHOTO = "/removephoto";
    public static final String ADMIN_REMOVE_PHOTO_PATH = ADMIN_PATH + REMOVE_PHOTO;

    // Resources regarding News Feeds
    public static final String NEWS_FEED_PATH = "/newsfeeds";

    // Resources regarding notifications
    public static final String NOTIFICATIONS_PATH = "/notifications";
}
