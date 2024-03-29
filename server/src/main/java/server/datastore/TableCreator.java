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
        String query = "CREATE TABLE IF NOT EXISTS "+USERS_TABLE+" ("+USERNAME+" varchar(50) NOT NULL, " +
                PASSWORD+" varchar(255) NOT NULL," +
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
        String query = "CREATE TABLE IF NOT EXISTS "+ALBUMS_TABLE+" ("+ALBUMS_ID+" BIGINT AUTO_INCREMENT, " +
                ALBUMS_NAME+" varchar(255) NOT NULL," +
                USERNAME+" varchar(255) NOT NULL," +
                ALBUMS_DESCRIPTION+" varchar(255) NOT NULL," +
                ALBUMS_TIME+" varchar(50) NOT NULL," +
                "PRIMARY KEY ("+ALBUMS_ID+"), " +
                "FOREIGN KEY("+USERNAME+") references "+USERS_TABLE+"("+USERNAME+") on delete cascade)";

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
        String query = "CREATE TABLE IF NOT EXISTS "+PHOTOS_TABLE+" ("+PHOTOS_ID+" BIGINT AUTO_INCREMENT, " +
                PHOTOS_NAME+" varchar(255) NOT NULL," +
                PHOTOS_EXT+" varchar(10) NOT NULL," +
                USERNAME+" varchar(255) NOT NULL," +
                ALBUMS_ID+" BIGINT NOT NULL," +
                PHOTOS_CONTENTS+" BLOB NOT NULL," +
                PHOTOS_TIME+" varchar(50) NOT NULL," +
                PHOTOS_DESCRIPTION+" varchar(255) NOT NULL," +
                "FOREIGN KEY("+USERNAME+") references "+USERS_TABLE+"("+USERNAME+") on delete cascade," +
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
        String query = "CREATE TABLE IF NOT EXISTS "+COMMENTS_TABLE+" ("+COMMENTS_ID+" BIGINT AUTO_INCREMENT, " +
                USERNAME+" varchar(255) NOT NULL," +
                COMMENTS_CONTENTS+" varchar(255) NOT NULL," +
                COMMENT_TYPE+" boolean," +
                COMMENTS_TIME+" varchar(50) NOT NULL," +
                REFERENCE_ID+" BIGINT," +
                "PRIMARY KEY ("+COMMENTS_ID+"), " +
                "FOREIGN KEY("+USERNAME+") references "+USERS_TABLE+"("+USERNAME+") ON DELETE CASCADE)";

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
                USERNAME+" varchar(255) NOT NULL," +
                "PRIMARY KEY ("+REFERENCE_ID+", "+USERNAME+"), " +
                "FOREIGN KEY("+USERNAME+") references "+USERS_TABLE+"("+USERNAME+") on delete cascade," +
                "FOREIGN KEY("+REFERENCE_ID+") references "+COMMENTS_TABLE+"("+COMMENTS_ID+") on delete cascade)";

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
                USERNAME+" varchar(255) NOT NULL," +
                "PRIMARY KEY ("+REFERENCE_ID+", "+USERNAME+"), " +
                "FOREIGN KEY("+USERNAME+") references "+USERS_TABLE+"("+USERNAME+") on delete cascade," +
                "FOREIGN KEY("+REFERENCE_ID+") references "+PHOTOS_TABLE+"("+PHOTOS_ID+") on delete cascade)";

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
        String query = "CREATE TABLE IF NOT EXISTS "+NOTIFICATIONS_TABLE+" ("+NOTIFICATIONS_ID+" bigint, " +
                CONTENT_ID+" bigint, " +
                PARENTNAME+" varchar(255) NOT NULL," +
                USERNAME+" varchar(255) NOT NULL," +
                CONTENT_TYPE+" varchar(255) NOT NULL," +
                "PRIMARY KEY("+NOTIFICATIONS_ID+")," +
                "FOREIGN KEY("+PARENTNAME+") references "+USERS_TABLE+"("+USERNAME+") on delete cascade)";

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
        String query = "CREATE TABLE IF NOT EXISTS "+FOLLOWINGS_TABLE+" ("+FOLLOW_ID+" bigint AUTO_INCREMENT, " +
                USER_FROM+" varchar(255) NOT NULL," +
                USER_TO+" varchar(255) NOT NULL," +
                "PRIMARY KEY("+FOLLOW_ID+")," +
                "FOREIGN KEY("+USER_FROM+") references "+USERS_TABLE+"("+USERNAME+") on delete cascade," +
                "FOREIGN KEY("+USER_TO+") references "+USERS_TABLE+"("+USERNAME+") on delete cascade)";

        // Execute statement such that table is made
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

}