package server.datastore;

import server.Resources;
import server.datastore.exceptions.InvalidResourceRequestException;
import server.objects.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import static server.datastore.DatabaseResources.*;
import static server.Resources.REMOVAL_STRING;

/**
 * DataStore implemented in terms of a H2 database
 */
final class DatabaseBackedDataStore implements DataStore {
    private static final String db_url = "jdbc:h2:~/Documents/CS5031/P3/Code/server/database";
    private static final String DB_CONFIG = "src/main/resources/db_config.txt";
    private static String uname;
    private static String pw;
    private Connection conn;

    // Read in the username and password for accessing the database
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
    public void persistUploadPhoto(Photo newPhoto) {
        // Set up query for inserting a new photo into the table
        String query = "INSERT INTO "+PHOTOS_TABLE+"("+PHOTOS_ID+","+PHOTOS_NAME+","
                +USERNAME+","+PHOTOS_CONTENTS+","+PHOTOS_TIME+") values(?, ?, ?, ?, ?)";

        // Persist photo
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            // Insert user info into prepared statement
            stmt.setLong(1, newPhoto.getId());
            stmt.setString(2, newPhoto.getPhotoName());
            stmt.setString(3, newPhoto.getPostedBy());
            stmt.setBlob(4, new ByteArrayInputStream(newPhoto.getPhotoContents().getBytes(StandardCharsets.UTF_8)));
            stmt.setTimestamp(5, new Timestamp(newPhoto.getTimestamp()));

            // Persist data
            stmt.executeUpdate();
            stmt.close();
        }
        catch (SQLException e) {e.printStackTrace(); }
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
                String username = rs.getString(3);
                long albumId = rs.getLong(4);
                Blob photoContents = rs.getBlob(5);
                Timestamp timestamp = rs.getTimestamp(6);

                // Retrieve base 64 encoded contents
                Scanner s = new Scanner(photoContents.getBinaryStream(), Resources.CHARSET_AS_STRING).useDelimiter("\\A");
                String encodedPhotoContents = s.hasNext() ? s.next() : "";
                photos.add(new Photo(encodedPhotoContents, username, photoName, id, albumId, timestamp.getTime()));
            }
            stmt.close();
        }
        catch (SQLException e) {e.printStackTrace(); }

        // Return found photos
        return photos;
    }

    @Override
    public Photo getPhoto(long id) throws InvalidResourceRequestException {
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
                String username = rs.getString(3);
                long albumId = rs.getLong(4);
                Blob photoContents = rs.getBlob(5);
                Timestamp timestamp = rs.getTimestamp(6);

                // Retrieve base 64 encoded contents
                Scanner s = new Scanner(photoContents.getBinaryStream(), Resources.CHARSET_AS_STRING).useDelimiter("\\A");
                String encodedPhotoContents = s.hasNext() ? s.next() : "";
                photos.add(new Photo(encodedPhotoContents, username, photoName, id, albumId, timestamp.getTime()));
            }
            stmt.close();
        }
        catch (SQLException e) { throw new InvalidResourceRequestException(id); }

        // If no photos found, throw exception
        if(photos.size() == 0) throw new InvalidResourceRequestException(id);

        // Return found photo
        return photos.get(0);
    }

    @Override
    public void persistAddAlbum(Album album) throws InvalidResourceRequestException {
        // Set up query for inserting a new album into the table
        String query = "INSERT INTO "+ALBUMS_TABLE+"("+ALBUMS_ID+","+ALBUMS_NAME+","
                +USERNAME+","+ALBUMS_DESCRIPTION+","+ALBUMS_TIME+") values(?, ?, ?, ?, ?)";

        // Persist photo
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            // Insert user info into prepared statement
            stmt.setLong(1, album.getAlbumId());
            stmt.setString(2, album.getAlbumName());
            stmt.setString(3, album.getAuthorName());
            stmt.setString(4, album.getAlbumDescription());
            stmt.setTimestamp(5, new Timestamp(album.getAlbumTime()));

            // Persist data
            stmt.executeUpdate();
            stmt.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
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
                Timestamp timestamp = rs.getTimestamp(5);

                albums.add(new Album(albumId, albumName, authorName, description, timestamp.getTime()));
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
                Timestamp timestamp = rs.getTimestamp(5);

                albums.add(new Album(albumId, albumName, authorName, description, timestamp.getTime()));
            }
            stmt.close();
        }
        catch (SQLException e) { e.printStackTrace(); }

        // Return found albums
        return albums;
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
                Timestamp timestamp = rs.getTimestamp(5);
                long referenceId = rs.getLong(6);

                // Get type
                boolean reply = rs.getBoolean(4);
                CommentType type = reply ? CommentType.REPLY : CommentType.PHOTO_COMMENT;

                // Create comment and retrieve upvotes / downvotes
                Comment comm = new Comment(username, contents, referenceId, type, timestamp.getTime());
                comm.setId(id);
                comm.setVotes(getVotes(id));
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
                int password = rs.getInt(2);
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
                int password = rs.getInt(2);
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
    public void persistAddUser(User user) {
        // Set up query for inserting a new user into the table
        String query = "INSERT INTO "+USERS_TABLE+"("+USERNAME+","+PASSWORD+","+USERS_ADMIN+") values(?, ?, ?)";

        // Persist the user
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            // Insert user info into prepared statement
            stmt.setString(1, user.getName());
            stmt.setInt(2, user.getPassword());
            stmt.setBoolean(3, user.isAdmin());

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
                Timestamp timestamp = rs.getTimestamp(5);
                long referenceId = rs.getLong(6);

                // Get Type
                boolean reply = rs.getBoolean(4);
                CommentType type = reply ? CommentType.REPLY : CommentType.PHOTO_COMMENT;

                // Create comment
                Comment comm = new Comment(username, contents, referenceId, type, timestamp.getTime());
                comm.setId(id);
                comm.setVotes(getVotes(id));
                comments.add(comm);
            }
            stmt.close();
        }
        catch (SQLException e) { e.printStackTrace(); }

        // Return collection
        return comments;
    }

    @Override
    public List<Comment> getPhotoComments(String user, long referenceId) {
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
                String contents = rs.getString(3);
                Timestamp timestamp = rs.getTimestamp(5);

                // Create comment and add to the collection
                Comment comm = new Comment(user, contents, referenceId, CommentType.PHOTO_COMMENT, timestamp.getTime());
                comm.setId(id);
                comm.setVotes(getVotes(id));
                comments.add(comm);
            }
            stmt.close();
        }
        catch (SQLException e) { e.printStackTrace(); }

        // Return comments
        return comments;
    }

    @Override
    public List<Comment> getReplies(String user, long referenceId) {
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
                String contents = rs.getString(3);
                Timestamp timestamp = rs.getTimestamp(5);

                // Create comment
                Comment comm = new Comment(user, contents, referenceId, CommentType.REPLY, timestamp.getTime());
                comm.setId(id);
                comm.setVotes(getVotes(id));
                comments.add(comm);
            }
            stmt.close();
        }
        catch (SQLException e) { e.printStackTrace(); }

        // Return collection
        return comments;
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
                long commentId = rs.getLong(1);
                long referenceId = rs.getLong(2);
                String notifiedUser = rs.getString(3);
                String commentPostedBy = rs.getString(4);

                // Get Type
                boolean reply = rs.getBoolean(5);
                CommentType type = reply ? CommentType.REPLY : CommentType.PHOTO_COMMENT;

                // Create notification
                notifications.add(new Notification(commentId, referenceId, notifiedUser, commentPostedBy, type));
            }
            stmt.close();
        }
        catch (SQLException e) { e.printStackTrace(); }

        return notifications;
    }

    @Override
    public void persistAddComment(Comment comment) {
        // Set up query for inserting a new comment into the table
        String query = "INSERT INTO "+COMMENTS_TABLE+"("+COMMENTS_ID+","+USERNAME+","
                        +COMMENTS_CONTENTS+","+COMMENT_TYPE+","+REFERENCE_ID+") values(?, ?, ?, ?, ?)";

        // Add comment
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            // Insert user info into prepared statement)
            stmt.setLong(1, comment.getId());
            stmt.setString(2, comment.getPostedBy());
            stmt.setString(3, comment.getContents());
            stmt.setBoolean(4, comment.getCommentType() == CommentType.REPLY);
            stmt.setLong(5, comment.getReferenceId());

            // Persist data
            stmt.executeUpdate();
            stmt.close();
        }
        catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void persistAddNotification(String parentName, Comment comment) {
        // Set up query for inserting a new notification into the table
        String query = "INSERT INTO "+NOTIFICATIONS_TABLE+"("+COMMENTS_ID+","+REFERENCE_ID+","
                +PARENTNAME+","+USERNAME+","+COMMENT_TYPE+") values(?, ?, ?, ?, ?)";

        // Persist notification
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            // Insert notification info into prepared statement
            stmt.setLong(1, comment.getId());
            stmt.setLong(2, comment.getReferenceId());
            stmt.setString(3, parentName);
            stmt.setString(4, comment.getPostedBy());
            stmt.setBoolean(5, comment.getCommentType() == CommentType.REPLY);

            // Persist data
            stmt.executeUpdate();
            stmt.close();
        }
        catch (SQLException e) {e.printStackTrace();}
    }

    @Override
    public void persistRemoveNotification(String user, long id) {
        // Update query overwriting the comment's text
        String query = "DELETE FROM "+NOTIFICATIONS_TABLE+" WHERE "+PARENTNAME+" = ? AND "+COMMENTS_ID+" = ?";

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
    public void persistRemoveComment(long commentId) throws InvalidResourceRequestException {
        // The comment's contents will be overwritten.
        String query = "UPDATE " + COMMENTS_TABLE + " SET " + COMMENTS_CONTENTS + " = ? WHERE " + COMMENTS_ID + " = ?";

        // Setup update query.
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, REMOVAL_STRING);
            stmt.setLong(2, commentId);

            // Execute query and ensure a row was changed
            int ret = stmt.executeUpdate();
            stmt.close();
            if(ret == 1) return;
        }
        catch (SQLException e) {e.printStackTrace();}

        // Comment didn't exist
        throw new InvalidResourceRequestException(commentId);
    }

    @Override
    public void persistVote(long commentId, String user, boolean upvote) throws InvalidResourceRequestException {
        // Set up query for updating / inserting a new photo into the table
        String query = "INSERT INTO "+COMMENTS_VOTES_TABLE+"("+REFERENCE_ID+","+USERNAME+","+VOTE+") values(?, ?, ?)";
        String update = "UPDATE "+COMMENTS_VOTES_TABLE+" SET "+VOTE+" = ? " +
                "WHERE "+USERNAME+" = ? AND "+REFERENCE_ID+" = ?";

        // Try to update row first
        try (PreparedStatement stmt = conn.prepareStatement(update)) {
            stmt.setBoolean(1, upvote);
            stmt.setString(2, user);
            stmt.setLong(3, commentId);

            // Execute, and check if any updates were made
            int ret = stmt.executeUpdate();
            stmt.close();
            if(ret == 1) return;
        }
        catch(SQLException e) {e.printStackTrace();}

        // If update didn't succeed, add new row
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            // Insert user info into prepared statement
            stmt.setLong(1, commentId);
            stmt.setString(2, user);
            stmt.setBoolean(3, upvote);

            // Persist data
            stmt.executeUpdate();
            stmt.close();
        }
        catch (SQLException e) { throw new InvalidResourceRequestException(commentId); }
    }

    @Override
    public void clear() {
        String query = "DELETE FROM ";
        String[] tables = new String[]
                {USERS_TABLE,ALBUMS_TABLE,PHOTOS_TABLE,COMMENTS_TABLE,COMMENTS_VOTES_TABLE,NOTIFICATIONS_TABLE};

        // Disable foreign key
        try (Statement stmt = conn.createStatement()) {
            // Allow clearing of data without caring about foreign keys
            stmt.executeUpdate("SET REFERENTIAL_INTEGRITY FALSE");
        }
        catch (SQLException e) {e.printStackTrace();}

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

        // Reset
        try (Statement stmt = conn.createStatement()) {
            // Reset, so foreign keys are enforced
            stmt.executeUpdate("SET REFERENTIAL_INTEGRITY TRUE");
        }
        catch (SQLException e) {e.printStackTrace();}
    }

    /**
     * Queries the database for all votes for the given comment
     * @param commentId the comment
     * @return the votes for the given comment
     */
    private HashMap<String, Boolean> getVotes(long commentId) {
        // Set up query to retrieve each row in the votes table
        String query = "SELECT * FROM "+COMMENTS_VOTES_TABLE+" WHERE "+REFERENCE_ID+" = ?";
        HashMap<String, Boolean> votes = new HashMap<>();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            // Execute query on database
            stmt.setLong(1, commentId);
            ResultSet rs = stmt.executeQuery();

            // Iterate through result set
            while(rs.next()) {
                String userName = rs.getString(2);
                boolean vote = rs.getBoolean(3);
                votes.put(userName, vote);
            }
            stmt.close();
        }
        catch (SQLException e) { e.printStackTrace();}

        // Return found votes
        return votes;
    }
}
