package server.datastore;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static server.datastore.DatabaseResources.*;
import static server.datastore.DatabaseResources.USERNAME;

/**
 * Class handling all table creation
 */
class TableCreator {
    private final Connection conn;

    TableCreator(Connection conn) {
        this.conn = conn;
    }

    /**
     * Creates all tables for this database
     */
    void createTables() {
        createUsersTable();
        createAlbumsTable();
        createPhotosTable();
        createPhotoRatingsTable();
        createCommentsTable();
        createCommentVoteTable();
        createNotificationsTable();
        createFollowingsTable();
    }

    /**
     * Creates the users table
     */
    private void createUsersTable() {
        // Construct create users table query
        String query = "CREATE TABLE IF NOT EXISTS "+USERS_TABLE+" ("+USERNAME+" varchar(25) NOT NULL, " +
                PASSWORD+" int NOT NULL," +
                USERS_ADMIN+" boolean NOT NULL," +
                "PRIMARY KEY("+USERNAME+"))";

        // Execute statement such that table is made
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the albums table
     */
    private void createAlbumsTable() {
        // Construct create albums table query
        String query = "CREATE TABLE IF NOT EXISTS "+ALBUMS_TABLE+" ("+ALBUMS_ID+" BIGINT, " +
                ALBUMS_NAME+" varchar(25) NOT NULL," +
                USERNAME+" varchar(25) NOT NULL," +
                ALBUMS_DESCRIPTION+" varchar(255) NOT NULL," +
                ALBUMS_TIME+" TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "PRIMARY KEY ("+ALBUMS_ID+"), " +
                "FOREIGN KEY("+USERNAME+") references "+USERS_TABLE+"("+USERNAME+"))";

        // Execute statement such that table is made
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the photos table
     */
    private void createPhotosTable() {
        // Construct create photos table query
        String query = "CREATE TABLE IF NOT EXISTS "+PHOTOS_TABLE+" ("+PHOTOS_ID+" BIGINT, " +
                PHOTOS_NAME+" varchar(25) NOT NULL," +
                USERNAME+" varchar(25) NOT NULL," +
                ALBUMS_ID+" BIGINT NOT NULL," +
                PHOTOS_CONTENTS+" BLOB NOT NULL," +
                PHOTOS_TIME+" TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "FOREIGN KEY("+USERNAME+") references "+USERS_TABLE+"("+USERNAME+")," +
                "FOREIGN KEY("+ALBUMS_ID+") references "+ALBUMS_TABLE+"("+ALBUMS_ID+") ON DELETE CASCADE)";

        // Execute statement such that table is made
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the comments table
     */
    private void createCommentsTable() {
        // Construct create comments table query
        String query = "CREATE TABLE IF NOT EXISTS "+COMMENTS_TABLE+" ("+COMMENTS_ID+" BIGINT, " +
                USERNAME+" varchar(25) NOT NULL," +
                COMMENTS_CONTENTS+" varchar(255) NOT NULL," +
                COMMENT_TYPE+" boolean," +
                COMMENTS_TIME+" TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                REFERENCE_ID+" BIGINT," +
                "PRIMARY KEY ("+COMMENTS_ID+"), " +
                "FOREIGN KEY("+USERNAME+") references "+USERS_TABLE+"("+USERNAME+"))";

        // Execute statement such that table is made
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the comment votes table
     */
    private void createCommentVoteTable() {
        // Construct create comment votes table query
        String commentVoteQuery = "CREATE TABLE IF NOT EXISTS "+COMMENTS_VOTES_TABLE+" " +
                "("+REFERENCE_ID+" BIGINT, " +
                USERNAME+" varchar(25) NOT NULL," +
                COMMENT_VOTE +" boolean," +
                "PRIMARY KEY ("+REFERENCE_ID+", "+USERNAME+"), " +
                "FOREIGN KEY("+USERNAME+") references "+USERS_TABLE+"("+USERNAME+")," +
                "FOREIGN KEY("+REFERENCE_ID+") references "+COMMENTS_TABLE+"("+COMMENTS_ID+"))";

        // Execute statement such that table is made
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(commentVoteQuery);
        }
        catch (SQLException e) {e.printStackTrace();}
    }

    /**
     * Creates the photo ratings table
     */
    private void createPhotoRatingsTable() {
        // Construct create comment votes table query
        String commentVoteQuery = "CREATE TABLE IF NOT EXISTS "+PHOTO_RATINGS_TABLE+" " +
                "("+REFERENCE_ID+" BIGINT, " +
                USERNAME+" varchar(25) NOT NULL," +
                PHOTO_RATING+" boolean," +
                "PRIMARY KEY ("+REFERENCE_ID+", "+USERNAME+"), " +
                "FOREIGN KEY("+USERNAME+") references "+USERS_TABLE+"("+USERNAME+")," +
                "FOREIGN KEY("+REFERENCE_ID+") references "+PHOTOS_TABLE+"("+PHOTOS_ID+"))";

        // Execute statement such that table is made
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(commentVoteQuery);
        }
        catch (SQLException e) {e.printStackTrace();}
    }

    /**
     * Creates the notifications table
     *
     */
    private void createNotificationsTable() {
        // Construct create users table query
        String query = "CREATE TABLE IF NOT EXISTS "+NOTIFICATIONS_TABLE+" ("+COMMENTS_ID+" bigint, " +
                REFERENCE_ID+" bigint," +
                PARENTNAME+" varchar(25) NOT NULL," +
                USERNAME+" varchar(25) NOT NULL," +
                COMMENT_TYPE+" boolean," +
                "PRIMARY KEY("+COMMENTS_ID+", "+REFERENCE_ID+")," +
                "FOREIGN KEY("+COMMENTS_ID+") references "+COMMENTS_TABLE+"("+COMMENTS_ID+")," +
                "FOREIGN KEY("+PARENTNAME+") references "+USERS_TABLE+"("+USERNAME+"))";

        // Execute statement such that table is made
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        }
        catch (SQLException e) {e.printStackTrace();}
    }

    /**
     * Creates the followings table
     */
    private void createFollowingsTable() {
        // Construct create users table query
        String query = "CREATE TABLE IF NOT EXISTS "+FOLLOWINGS_TABLE+" ("+FOLLOW_ID+" int NOT NULL, " +
                USER_FROM+" varchar(25) NOT NULL," +
                USER_TO+" varchar(25) NOT NULL," +
                "PRIMARY KEY("+FOLLOW_ID+")," +
                "FOREIGN KEY("+USER_FROM+") references "+USERS_TABLE+"("+USERNAME+")," +
                "FOREIGN KEY("+USER_TO+") references "+USERS_TABLE+"("+USERNAME+"))";

        // Execute statement such that table is made
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
