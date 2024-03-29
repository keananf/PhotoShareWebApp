package server.datastore;

import server.Resources;
import server.datastore.exceptions.InvalidResourceRequestException;
import server.objects.*;
import server.requests.AddCommentRequest;
import server.requests.UploadPhotoRequest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static server.datastore.DatabaseResources.*;

/**
 * DataStore implemented in terms of a H2 database
 */
final class DatabaseBackedDataStore implements DataStore {
    private static final String db_url = "jdbc:h2:./database";
    private static final String DB_CONFIG = "src/main/resources/db_config.txt";
    private static String uname;
    private static String pw;
    private Connection conn;
    private int CURRENT_ID = 0;

    // Before anything else, read in the username and password for accessing the database
    static {
        try {
            Scanner reader = new Scanner(new File(DB_CONFIG), Resources.CHARSET_AS_STRING);
            uname= reader.nextLine();
            pw = reader.nextLine();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    DatabaseBackedDataStore() {
        try {
            // Create database connection, and create all database tables (if they don't already exist)
            conn = DriverManager.getConnection(db_url, uname, pw);
            new TableCreator(conn).createTables();
            System.out.println("Connected");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public long persistUploadPhoto(String author, UploadPhotoRequest request, String date) {
        // Set up query for inserting a new photo into the table
        String query = "INSERT INTO "+PHOTOS_TABLE+"("+PHOTOS_NAME+","+PHOTOS_EXT+","+USERNAME+","
            +ALBUMS_ID+","+PHOTOS_CONTENTS+","+PHOTOS_TIME+","+PHOTOS_DESCRIPTION+") "+"values(?, ?, ?, ?, ?, ?, ?)";

        // Persist photo
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            // Insert user info into prepared statement
            stmt.setString(1, request.getPhotoName());
            stmt.setString(2, request.getExtension());
            stmt.setString(3, author);
            stmt.setLong(4, request.getAlbumId());
            stmt.setBlob(5, new ByteArrayInputStream(request.getEncodedPhotoContents()
                    .getBytes(StandardCharsets.UTF_8)));
            stmt.setString(6, date);
            stmt.setString(7, request.getDescription());

            // Persist data
            stmt.executeUpdate();

            // Retrieve last generated ID and return
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                long key = generatedKeys.getLong(1);
                stmt.close();
                return key;
            }
            stmt.close();
        }
        catch (SQLException e) {e.printStackTrace(); }

        // Default
        return -1;
    }

    @Override
    public List<Photo> getPhotos(String user) {
        // Set up query to retrieve each row in the photos table
        String query = "SELECT * FROM "+PHOTOS_TABLE+" WHERE "+USERNAME+" = ?";
        List<Photo> photos = new ArrayList<>();

        // Execute query on database
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user);
            ResultSet rs = stmt.executeQuery();

            // Iterate through result set, constructing PHOTO Objects
            while(rs.next()) {
                // Create photos
                long id = rs.getLong(1);
                String photoName = rs.getString(2);
                String ext = rs.getString(3);
                String username = rs.getString(4);
                long albumId = rs.getLong(5);
                String timestamp = rs.getString(7);
                String description = rs.getString(8);

                photos.add(new Photo(username, photoName, ext, description, id, albumId,
                        getPhotoRatings(id), timestamp));
            }
            stmt.close();
        }
        catch (SQLException e) {e.printStackTrace(); }

        // Return found photos
        return photos;
    }

    @Override
    public List<Photo> getPhotos(long albumId) {
        // Set up query to retrieve each row in the photos table
        String query = "SELECT * FROM "+PHOTOS_TABLE+" WHERE "+ALBUMS_ID+" = ?";
        List<Photo> photos = new ArrayList<>();

        // Execute query on database
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, albumId);
            ResultSet rs = stmt.executeQuery();

            // Iterate through result set, constructing PHOTO Objects
            while(rs.next()) {
                // Create photos
                long id = rs.getLong(1);
                String photoName = rs.getString(2);
                String ext = rs.getString(3);
                String username = rs.getString(4);
                String timestamp = rs.getString(7);
                String description = rs.getString(8);

                photos.add(new Photo(username, photoName, ext, description, id, albumId,
                        getPhotoRatings(id), timestamp));
            }
            stmt.close();
        }
        catch (SQLException e) {e.printStackTrace(); }

        // Return found photos
        return photos;
    }

    @Override
    public Photo getPhotoMetaData(long id) throws InvalidResourceRequestException {
        // Set up query to retrieve the requested photo in the photos table
        String query = "SELECT * FROM "+PHOTOS_TABLE+" WHERE "+PHOTOS_ID+" = ?";
        List<Photo> photos = new ArrayList<>();

        // Execute query on database
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            // Iterate through result set, constructing PHOTO Objects
            while(rs.next()) {
                // Create photos
                String photoName = rs.getString(2);
                String ext = rs.getString(3);
                String username = rs.getString(4);
                long albumId = rs.getLong(5);
                String timestamp = rs.getString(7);
                String description = rs.getString(8);

                photos.add(new Photo(username, photoName, ext, description, id, albumId,
                        getPhotoRatings(id), timestamp));
            }
            stmt.close();
        }
        catch (SQLException e) { throw new InvalidResourceRequestException(id); }

        // If no photos found, throw exception
        if(photos.size() == 0) throw new InvalidResourceRequestException(id);

        // Return found photo contents
        return photos.get(0);
    }

    @Override
    public String getPhotoContents(long id, String ext) throws InvalidResourceRequestException {
        // Set up query to retrieve the requested photo in the photos table
        String query = "SELECT * FROM "+PHOTOS_TABLE+" WHERE "+PHOTOS_ID+" = ?";
        List<String> photoContents = new ArrayList<>();

        // Execute query on database
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            // Iterate through result set, constructing PHOTO Objects
            while(rs.next()) {
                // Check provided file extension is correct
                String foundExt = rs.getString(3);
                if(!ext.equals(foundExt)) throw new InvalidResourceRequestException(id);

                // Retrieve base 64 encoded contents
                Blob photo = rs.getBlob(6);
                Scanner s = new Scanner(photo.getBinaryStream(), Resources.CHARSET_AS_STRING).useDelimiter("\\A");
                String encodedPhotoContents = s.hasNext() ? s.next() : "";
                photoContents.add(encodedPhotoContents);
            }
            stmt.close();
        }
        catch (SQLException e) { throw new InvalidResourceRequestException(id); }

        // If no photos found, throw exception
        if(photoContents.size() == 0) throw new InvalidResourceRequestException(id);

        // Return found photo contents
        return photoContents.get(0);
    }

    @Override
    public long persistAddAlbum(String albumName, String author, String description, String date) {
        // Set up query for inserting a new album into the table
        String query = "INSERT INTO " + ALBUMS_TABLE + "(" + ALBUMS_NAME + ","
                + USERNAME + "," + ALBUMS_DESCRIPTION + "," + ALBUMS_TIME + ") values(?, ?, ?, ?)";

        // Persist photo
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            // Insert user info into prepared statement
            stmt.setString(1, albumName);
            stmt.setString(2, author);
            stmt.setString(3, description);
            stmt.setString(4, date);

            // Persist data
            stmt.executeUpdate();

            // Retrieve last generated ID and return
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                long key = generatedKeys.getLong(1);
                stmt.close();
                return key;
            }
            stmt.close();
        }
        catch(SQLException e) {
            e.printStackTrace();
        }

        // Failed
        return -1;
    }

        @Override
    public Album getAlbum(long albumId) throws InvalidResourceRequestException {
        // Set up query to retrieve the requested album in the albums table
        String query = "SELECT * FROM "+ALBUMS_TABLE+" WHERE "+ALBUMS_ID+" = ?";
        List<Album> albums = new ArrayList<>();

        // Execute query on database
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, albumId);
            ResultSet rs = stmt.executeQuery();

            // Iterate through result set, constructing the Album object
            while(rs.next()) {
                // Create albums
                String albumName = rs.getString(2);
                String authorName = rs.getString(3);
                String description = rs.getString(4);
                String timestamp = rs.getString(5);

                albums.add(new Album(albumId, albumName, authorName, description, timestamp));
            }
            stmt.close();
        }
        catch (SQLException e) { throw new InvalidResourceRequestException(albumId); }

        // If no albums found, throw exception
        if(albums.size() == 0) throw new InvalidResourceRequestException(albumId);

        // Return found album
        return albums.get(0);
    }

    @Override
    public List<Album> getAlbums(String author) {
        // Set up query to retrieve the requested album in the albums table
        String query = "SELECT * FROM "+ALBUMS_TABLE+" WHERE "+USERNAME+" = ?";
        List<Album> albums = new ArrayList<>();

        // Execute query on database
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, author);
            ResultSet rs = stmt.executeQuery();

            // Iterate through result set, constructing the Album object
            while(rs.next()) {
                // Create albums
                long albumId = rs.getLong(1);
                String albumName = rs.getString(2);
                String authorName = rs.getString(3);
                String description = rs.getString(4);
                String timestamp = rs.getString(5);

                albums.add(new Album(albumId, albumName, authorName, description, timestamp));
            }
            stmt.close();
        }
        catch (SQLException e) { e.printStackTrace(); }

        // Return found albums
        return albums;
    }

    @Override
    public void updateAlbumDescription(long albumId, String description) throws InvalidResourceRequestException {
        // The album's description will be overwritten.
        String query = "UPDATE " + ALBUMS_TABLE + " SET " + ALBUMS_DESCRIPTION + " = ? WHERE " + ALBUMS_ID + " = ?";

        // Setup update query.
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, description);
            stmt.setLong(2, albumId);

            // Execute query and ensure a row was changed
            int ret = stmt.executeUpdate();
            stmt.close();
            if(ret == 1) return;
        }
        catch (SQLException e) {e.printStackTrace();}

        // Album didn't exist
        throw new InvalidResourceRequestException(albumId);
    }

    @Override
    public void updatePhotoDescription(long photoId, String description) throws InvalidResourceRequestException {
        // The photo's description will be overwritten.
        String query = "UPDATE " + PHOTOS_TABLE + " SET " + PHOTOS_DESCRIPTION + " = ? WHERE " + PHOTOS_ID + " = ?";

        // Setup update query.
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, description);
            stmt.setLong(2, photoId);

            // Execute query and ensure a row was changed
            int ret = stmt.executeUpdate();
            stmt.close();
            if(ret == 1) return;
        }
        catch (SQLException e) {e.printStackTrace();}

        // Photo didn't exist
        throw new InvalidResourceRequestException(photoId);
    }

    @Override
    public Comment getComment(long id) throws InvalidResourceRequestException {
        List<Comment> comments = new ArrayList<>();

        // Set up query to retrieve the requested photo in the comments table
        String query = "SELECT * FROM "+COMMENTS_TABLE+" WHERE "+COMMENTS_ID+" = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            // Execute query on database
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            // Iterate through result set, constructing PHOTO Objects
            while(rs.next()) {
                // Get info
                String username = rs.getString(2);
                String contents = rs.getString(3);
                String timestamp = rs.getString(5);
                long referenceId = rs.getLong(6);

                // Get type
                boolean reply = rs.getBoolean(4);
                EventType type = reply ? EventType.REPLY : EventType.PHOTO_COMMENT;

                // Create comment and retrieve upvotes / downvotes
                Comment comm = new Comment(id, username, contents, referenceId, type, getCommentVotes(id), timestamp);
                comments.add(comm);
            }
            stmt.close();
        }
        catch (SQLException e) { throw new InvalidResourceRequestException(id); }

        // If no comments found, throw exception
        if(comments.size() == 0) throw new InvalidResourceRequestException(id);

        // Return found comment
        return comments.get(0);
    }

    @Override
    public List<User> getUsers() {
        // Set up query to retrieve each row in the users table
        String query = "SELECT * FROM "+ USERS_TABLE;
        List<User> users = new ArrayList<>();

        // Execute query on database
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);

            // Iterate through result set, constructing User Objects
            while(rs.next()) {
                // Create users
                String userName = rs.getString(1);
                String password = rs.getString(2);
                boolean admin = rs.getBoolean(3);

                // Add users to collection to return
                User user = new User(userName, password);
                user.setAdmin(admin);
                users.add(user);
            }
            stmt.close();
        }
        catch (SQLException e) { e.printStackTrace();}

        // Return found users
        return users;
    }

    @Override
    public User getUser(String name) throws InvalidResourceRequestException {
        // Set up query
        String query = "SELECT * FROM "+USERS_TABLE+" WHERE "+USERNAME+" = ?";
        List<User> users = new ArrayList<>();

        // Execute query on database
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            // Iterate through result set, constructing User Objects
            while(rs.next()) {
                // Create users
                String userName = rs.getString(1);
                String password = rs.getString(2);
                boolean admin = rs.getBoolean(3);

                // Add users to collection to return
                User user = new User(userName, password);
                user.setAdmin(admin);
                users.add(user);
            }
            stmt.close();
        }
        catch (SQLException e) {e.printStackTrace();}

        // If no users found, throw exception
        if(users.size() == 0) throw new InvalidResourceRequestException(name);

        // Return found user
        return users.get(0);
    }

    @Override
    public void persistAddUser(String username, String password, boolean admin) {
        // Set up query for inserting a new user into the table
        String query = "INSERT INTO "+USERS_TABLE+"("+USERNAME+","+PASSWORD+","+USERS_ADMIN+") values(?, ?, ?)";

        // Persist the user
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            // Insert user info into prepared statement
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setBoolean(3, admin);

            stmt.executeUpdate();
            stmt.close();
        }
        catch (SQLException e) {e.printStackTrace();}
    }

    @Override
    public List<Comment> getComments(String username) {
        // Set up query and collection
        String query = "SELECT * FROM "+COMMENTS_TABLE+" WHERE "+USERNAME+" = ?";
        List<Comment> comments = new ArrayList<>();

        // Get comments
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            // Execute query on database
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            // Iterate through result set, constructing PHOTO Objects
            while (rs.next()) {
                // Get info
                long id = rs.getLong(1);
                String contents = rs.getString(3);
                String timestamp = rs.getString(5);
                long referenceId = rs.getLong(6);

                // Get Type
                boolean reply = rs.getBoolean(4);
                EventType type = reply ? EventType.REPLY : EventType.PHOTO_COMMENT;

                // Create comment and retrieve upvotes / downvotes
                Comment comm = new Comment(id, username, contents, referenceId, type, getCommentVotes(id), timestamp);
                comments.add(comm);
            }
            stmt.close();
        }
        catch (SQLException e) { e.printStackTrace(); }

        // Return collection
        return comments;
    }

    @Override
    public List<Comment> getPhotoComments(long referenceId) {
        // Set up query to retrieve the requested comments in the comments table
        List<Comment> comments = new ArrayList<>();
        String query = "SELECT * FROM "+COMMENTS_TABLE+" WHERE "+REFERENCE_ID+" = ? AND "+COMMENT_TYPE+" = false";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            // Execute query on database
            stmt.setLong(1, referenceId);
            ResultSet rs = stmt.executeQuery();

            // Iterate through result set, constructing PHOTO Objects
            while(rs.next()) {
                // Get info
                long id = rs.getLong(1);
                String username = rs.getString(2);
                String contents = rs.getString(3);
                String timestamp = rs.getString(5);

                // Create comment and retrieve upvotes / downvotes
                Comment comm = new Comment(id, username, contents, referenceId, EventType.PHOTO_COMMENT,
                        getCommentVotes(id), timestamp);
                comments.add(comm);
            }
            stmt.close();
        }
        catch (SQLException e) { e.printStackTrace(); }

        // Return comments
        return comments;
    }

    @Override
    public List<Comment> getReplies(long referenceId) {
        // Set up query to retrieve the requested comments in the comments table
        List<Comment> comments = new ArrayList<>();
        String query = "SELECT * FROM "+COMMENTS_TABLE+" WHERE "+REFERENCE_ID+" = ? AND "+COMMENT_TYPE+" = true";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            // Execute query on database
            stmt.setLong(1, referenceId);
            ResultSet rs = stmt.executeQuery();

            // Iterate through result set, constructing PHOTO Objects
            while(rs.next()) {
                // Get info
                long id = rs.getLong(1);
                String username = rs.getString(2);
                String contents = rs.getString(3);
                String timestamp = rs.getString(5);

                // Create comment and retrieve upvotes / downvotes
                Comment comm = new Comment(id, username, contents, referenceId, EventType.REPLY,
                        getCommentVotes(id), timestamp);
                comments.add(comm);
            }
            stmt.close();
        }
        catch (SQLException e) { e.printStackTrace(); }

        // Return collection
        return comments;
    }


    @Override
    public long persistAddComment(String user, AddCommentRequest request, String date) {
        // Set up query for inserting a new comment into the table
        String query = "INSERT INTO "+COMMENTS_TABLE+"("+USERNAME+","
                        +COMMENTS_CONTENTS+","+COMMENT_TYPE+","+REFERENCE_ID+","+COMMENTS_TIME+") values(?, ?, ?, ?, ?)";

        // Add comment
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            // Insert user info into prepared statement)
            stmt.setString(1, user);
            stmt.setString(2, request.getCommentContents());
            stmt.setBoolean(3, request.getEventType() == EventType.REPLY);
            stmt.setLong(4, request.getReferenceId());
            stmt.setString(5, date);

            // Persist data
            stmt.executeUpdate();

            // Retrieve last generated ID and return
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                long key = generatedKeys.getLong(1);
                stmt.close();
                return key;
            }
            stmt.close();
        }
        catch (SQLException e) { e.printStackTrace(); }

        // Default
        return -1;
    }

    @Override
    public void persistEditComment(long commentId, String content) {
        // The comment's contents will be overwritten.
        String query = "UPDATE " + COMMENTS_TABLE + " SET " + COMMENTS_CONTENTS + " = ? WHERE " + COMMENTS_ID + " = ?";

        // Setup update query.
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, content);
            stmt.setLong(2, commentId);

            // Execute query and ensure a row was changed
            int ret = stmt.executeUpdate();
            stmt.close();
            if(ret == 1) return;
        }
        catch (SQLException e) { e.printStackTrace(); }
    }


    @Override
    public void persistAddNotification(String parentName, NotifiableEvent event) {
        // Set up query for inserting a new notification into the table
        String query = "INSERT INTO "+NOTIFICATIONS_TABLE+"("+NOTIFICATIONS_ID+","+CONTENT_ID+","
                +PARENTNAME+","+USERNAME+","+CONTENT_TYPE+") values(?, ?, ?, ?, ?)";

        // Persist notification
        try (PreparedStatement stmt = conn.prepareStatement(query)) {


            // Insert notification info into prepared statement
            stmt.setLong(1, CURRENT_ID++);
            stmt.setLong(2, event.getContentId());
            stmt.setString(3, parentName);
            stmt.setString(4, event.getParentName());
            stmt.setString(5, encodeCommentTypeToString(event.getEventType()));

            // Persist data
            stmt.executeUpdate();
            stmt.close();
        }
        catch (SQLException e) {e.printStackTrace();}
    }

    @Override
    public void persistRemoveNotification(String user, long id) {
        // Update query overwriting the comment's text
        String query = "DELETE FROM "+NOTIFICATIONS_TABLE+" WHERE "+PARENTNAME+" = ? AND "+CONTENT_ID+" = ?";

        // Execute query
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user);
            stmt.setLong(2, id);
            stmt.executeUpdate();
            stmt.close();
        }
        catch (SQLException e) {e.printStackTrace();}
    }

    @Override
    public List<Notification> getNotifications(String user) {
        // Set up query
        String query = "SELECT * FROM "+NOTIFICATIONS_TABLE+" WHERE "+PARENTNAME+" = ?";
        List<Notification> notifications = new ArrayList<>();


        // Get notifications
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            // Execute query on database
            stmt.setString(1, user);
            ResultSet rs = stmt.executeQuery();


            // Iterate through result set, constructing PHOTO Objects
            while (rs.next()) {
                // Get info


                long commentId = rs.getLong(2);
                String notifiedUser = rs.getString(3);
                String commentPostedBy = rs.getString(4);

                // Get Type
                String stored_type = rs.getString(5);
                EventType type = decodeCommentTypeFromString(stored_type);

                // Create notification
                notifications.add(new Notification(commentId, commentPostedBy, notifiedUser, type));
            }
            stmt.close();
        }
        catch (SQLException e) { e.printStackTrace(); }

        return notifications;
    }

    private EventType decodeCommentTypeFromString(String commentType){

        switch (commentType){
            case "follow":
                return EventType.FOLLOW;
            case "reply":
                return EventType.REPLY;
            case "photo_comment":
                return EventType.PHOTO_COMMENT;
            default:
                return null;
        }
    }

    private String encodeCommentTypeToString(EventType commentType){

        switch (commentType){
            case FOLLOW:
                return "follow";
            case REPLY:
                return "reply";
            case PHOTO_COMMENT:
                return "photo_comment";
            default:
                return null;
        }
    }

    @Override
    public void persistRemoveComment(long commentId) {
        // The comment's contents will be overwritten.
        String query = "DELETE FROM " + COMMENTS_TABLE + " WHERE " + COMMENTS_ID + " = ?";

        // Setup and execute delete query.
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, commentId);
            stmt.executeUpdate();
            stmt.close();
        }
        catch (SQLException e) {e.printStackTrace();}
    }

    @Override
    public void persistRemovePhoto(long photoId) {
        // The photo will be deleted.
        String query = "DELETE FROM " + PHOTOS_TABLE + " WHERE " + PHOTOS_ID + " = ?";

        // Setup delete query.
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, photoId);

            // Execute query to delete row
            stmt.executeUpdate();
            stmt.close();
        }
        catch (SQLException e) {e.printStackTrace();}
    }

    @Override
    public void persistCommentVote(long commentId, String user, boolean rating) throws InvalidResourceRequestException {
        // Set up query for updating / inserting a new photo into the table
        String query = "INSERT INTO "+COMMENTS_VOTES_TABLE+"("+REFERENCE_ID+","+USERNAME+") values(?, ?)";
        String update = "DELETE FROM "+COMMENTS_VOTES_TABLE+" WHERE "+USERNAME+" = ? AND "+REFERENCE_ID+" = ?";

        // Delete the existing row from the table, if present.
        try (PreparedStatement stmt = conn.prepareStatement(update)) {
            stmt.setString(1, user);
            stmt.setLong(2, commentId);

            // Delete rating if present
            stmt.executeUpdate();
            stmt.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        // Add new like
        if(rating) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                // Insert user info into prepared statement
                stmt.setLong(1, commentId);
                stmt.setString(2, user);

                // Persist data
                stmt.executeUpdate();
                stmt.close();
            }
            catch (SQLException e) {
                throw new InvalidResourceRequestException(commentId);
            }
        }
    }

    @Override
    public void persistPhotoRating(long photoId, String user, boolean rating) throws InvalidResourceRequestException {
        // Set up query for updating / inserting a new photo into the table
        String query = "INSERT INTO "+PHOTO_RATINGS_TABLE+"("+REFERENCE_ID+","+USERNAME+") values(?, ?)";
        String update = "DELETE FROM "+PHOTO_RATINGS_TABLE+ " WHERE "+USERNAME+" = ? AND "+REFERENCE_ID+" = ?";

        // Delete the existing row from the table, if present.
        try (PreparedStatement stmt = conn.prepareStatement(update)) {
            stmt.setString(1, user);
            stmt.setLong(2, photoId);

            // Delete rating if present
            stmt.executeUpdate();
            stmt.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        // Add new like
        if(rating) {
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                // Insert user info into prepared statement
                stmt.setLong(1, photoId);
                stmt.setString(2, user);

                // Persist data
                stmt.executeUpdate();
                stmt.close();
            }
            catch (SQLException e) {
                throw new InvalidResourceRequestException(photoId);
            }
        }
    }

    /**
     * Queries the database for all votes for the given comment
     * @param commentId the comment
     * @return the votes for the given comment
     */
    private List<String> getCommentVotes(long commentId) {
        // Set up query to retrieve each row in the votes table
        String query = "SELECT * FROM "+COMMENTS_VOTES_TABLE+" WHERE "+REFERENCE_ID+" = ?";
        List<String> votes = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            // Execute query on database
            stmt.setLong(1, commentId);
            ResultSet rs = stmt.executeQuery();

            // Iterate through result set
            while(rs.next()) {
                String userName = rs.getString(2);
                votes.add(userName);
            }
            stmt.close();
        }
        catch (SQLException e) { e.printStackTrace();}

        // Return found votes
        return votes;
    }


    /**
     * Queries the database for all votes for the given photo
     * @param photoId the photo
     * @return the votes for the given photo
     */
    private List<String> getPhotoRatings(long photoId) {
        // Set up query to retrieve each row in the votes table
        String query = "SELECT * FROM "+PHOTO_RATINGS_TABLE+" WHERE "+REFERENCE_ID+" = ?";
        List<String> votes = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            // Execute query on database
            stmt.setLong(1, photoId);
            ResultSet rs = stmt.executeQuery();

            // Iterate through result set
            while(rs.next()) {
                String userName = rs.getString(2);
                votes.add(userName);
            }
            stmt.close();
        }
        catch (SQLException e) { e.printStackTrace();}

        // Return found votes
        return votes;
    }

    @Override
    public long persistFollowing(String userFrom, String userTo) {

        String query = "INSERT INTO "+FOLLOWINGS_TABLE+"("+USER_FROM+","+USER_TO+") values(?, ?)";

        // Persist the user
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            // Insert user info into prepared statement
            stmt.setString(1, userFrom);
            stmt.setString(2, userTo);

            // Persist data
            stmt.executeUpdate();

            // Retrieve last generated ID and return
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                long key = generatedKeys.getLong(1);
                stmt.close();
                return key;
            }
            stmt.close();
        }
        catch (SQLException e) {e.printStackTrace();}

        // Default
        return -1;
    }

    @Override
    public void persistDeleteFollowing(String userFrom, String userTo) {

        String query = "DELETE FROM "+FOLLOWINGS_TABLE+" WHERE "+USER_FROM+" = ? AND "+USER_TO+" = ?";

        // Execute query
        try (PreparedStatement stmt = conn.prepareStatement(query)) {

            // Persist
            stmt.setString(1, userFrom);
            stmt.setString(2, userTo);
            stmt.executeUpdate();
            stmt.close();
        }
        catch (SQLException e) {e.printStackTrace();}
    }

    @Override
    public List<User> getFollowers(String username) throws InvalidResourceRequestException{

        // Set up query to retrieve each row in the votes table
        String query = "SELECT * FROM "+FOLLOWINGS_TABLE+" WHERE "+USER_TO+" = ?";
        List<User> following = new ArrayList<>();

        // Get followers
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            // Execute query on database
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            // Iterate through result set, constructing PHOTO Objects
            while (rs.next()) {
                // Get info
                String usernameOfFollower = rs.getString(2);
                User follower = getUser(usernameOfFollower);

                // Add to list of followees
                following.add(follower);
            }
            stmt.close();
        }
        catch (SQLException e) { e.printStackTrace(); }

        return following;
    }

    @Override
    public List<User> getFollowing(String username) {

        // Set up query to retrieve each row in the votes table
        String query = "SELECT * FROM "+FOLLOWINGS_TABLE+" WHERE "+USER_FROM+" = ?";
        List<User> following = new ArrayList<>();

        // Get followers
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            // Execute query on database
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            // Iterate through result set, constructing PHOTO Objects
            while (rs.next()) {
                // Get info
                String usernameOfFollowee = rs.getString(3);
                User follower = getUser(usernameOfFollowee);

                // Add to list of followes
                following.add(follower);
            }
            stmt.close();
        }
        catch (SQLException e) { e.printStackTrace(); }
        catch (InvalidResourceRequestException e) { e.printStackTrace(); }


        return following;
    }

    @Override
    public void clear() {
        String query = "DELETE FROM ";
        String[] tables = new String[] {USERS_TABLE,ALBUMS_TABLE,PHOTOS_TABLE,
                COMMENTS_TABLE,COMMENTS_VOTES_TABLE, PHOTO_RATINGS_TABLE,NOTIFICATIONS_TABLE, FOLLOWINGS_TABLE};

        // Clear each table, but retaining their schema.
        for(String table : tables) {
            // Execute statement
            try (PreparedStatement stmt = conn.prepareStatement(query + table)) {
                // Allow clearing of data without caring about foreign keys
                stmt.executeUpdate();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<User> getUserWithNameBegining(String name) {
        // Set up query
        String query = "SELECT * FROM "+USERS_TABLE+" WHERE  "+USERNAME+" LIKE ?";
        List<User> users = new ArrayList<>();

        // Execute query on database
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "%" + name + "%");
            ResultSet rs = stmt.executeQuery();

            // Iterate through result set, constructing User Objects
            while(rs.next()) {
                // Create users
                String userName = rs.getString(1);
                String password = rs.getString(2);
                boolean admin = rs.getBoolean(3);

                // Add users to collection to return
                User user = new User(userName, password);
                user.setAdmin(admin);
                users.add(user);
            }
            stmt.close();
        }
        catch (SQLException e) {e.printStackTrace();}

        // Return found user
        return users;
    }
}
