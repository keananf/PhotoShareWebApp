import org.junit.Test;
import server.objects.*;

import javax.ws.rs.core.Response;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;


public class UserAPITests extends TestUtility{
    @Test
    public void addUserTest() {
        addUser(username);
    }

    @Test
    public void getUsersTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Get users, and ensure it was successful
        Response response = apiClient.getUsers();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // Parse JSON
        String users = response.readEntity(String.class);
        assertEquals(gson.fromJson(users, User[].class)[0].getUsername(), username);
    }

    @Test
    public void getEmptyListOfFollowingTest() {
        // Add sample user and register it

        loginAndSetupNewUser(username);

        // Get users, and ensure it was successful
        Response response = apiClient.getFollowing();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // Parse JSON
        String users = response.readEntity(String.class);
        assertEquals(gson.fromJson(users, User[].class).length, 0);

    }

    @Test
    public void getOneCorrectFollowingTest() {
        // Add sample user and register it

        // Set up users who are being followeds
        String userBeingFollowedOne = "user_followed_one";

        loginAndSetupNewUser(userBeingFollowedOne);
        loginAndSetupNewUser(username);

        apiClient.followUser(userBeingFollowedOne);

        // Get users, and ensure it was successful
        Response response = apiClient.getFollowing();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // Parse JSON
        String users = response.readEntity(String.class);

        assertEquals(gson.fromJson(users, User[].class)[0].getUsername(), userBeingFollowedOne);
    }

    @Test
    public void getTwoFollowingTest() {
        // Add sample user and register it

        // Set up users who are being followeds
        String userBeingFollowedOne = "user_followed_one";
        loginAndSetupNewUser(userBeingFollowedOne);

        String userBeingFollowedTwo = "user_followed_two";
        loginAndSetupNewUser(userBeingFollowedTwo);

        loginAndSetupNewUser(username);

        apiClient.followUser(userBeingFollowedOne);
        apiClient.followUser(userBeingFollowedTwo);

        // Get users, and ensure it was successful
        Response response = apiClient.getFollowing();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // Parse JSON
        String users = response.readEntity(String.class);

        assertEquals(gson.fromJson(users, User[].class).length, 2);
    }

    @Test
    public void getEmptyListOfFollowersTest() {
        // Add sample user and register it

        loginAndSetupNewUser(username);

        // Get users, and ensure it was successful
        Response response = apiClient.getFollowers();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // Parse JSON
        String users = response.readEntity(String.class);
        assertEquals(gson.fromJson(users, User[].class).length, 0);

    }

    @Test
    public void getOneCorrectFollowerTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Set up users who are following our sample user
        String userFollowingOne = "user_following_one";
        loginAndSetupNewUser(userFollowingOne);
        apiClient.followUser(username);

        // Log back into the sample user
        apiClient.loginUser(username, pw);


        // Get followers, and ensure it was successful
        Response response = apiClient.getFollowers();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // Parse JSON
        String users = response.readEntity(String.class);

        assertEquals(gson.fromJson(users, User[].class)[0].getUsername(), userFollowingOne);
    }

    @Test
    public void getTwoFollowersTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Set up users who are following our sample user
        String userFollowingOne = "user_following_one";
        loginAndSetupNewUser(userFollowingOne);
        apiClient.followUser(username);

        String userFollowingTwo = "user_following_two";
        loginAndSetupNewUser(userFollowingTwo);
        apiClient.followUser(username);

        // Log back into the sample user
        apiClient.loginUser(username, pw);


        // Get followers, and ensure it was successful
        Response response = apiClient.getFollowers();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // Parse JSON
        String users = response.readEntity(String.class);

        assertEquals(gson.fromJson(users, User[].class).length, 2);
    }

    @Test
    public void emptyUsersNameSearchTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Get followers, and ensure it was successful
        String searchTerm = "user";
        Response response = apiClient.getUserWithNameBegining(searchTerm);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        String content = response.readEntity(String.class);
        User[] users = gson.fromJson(content, User[].class);
        assertEquals(0, users.length);

    }

    @Test
    public void usersNameSearchForOneSubjectTests() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Set up users who are following our sample user
        String sampleSearchedUser = "user_one";
        loginAndSetupNewUser(sampleSearchedUser);

        // Log back into the sample user
        apiClient.loginUser(username, pw);


        // Get followers, and ensure it was successful
        String searchTerm = sampleSearchedUser.substring(0, 4);
        Response response = apiClient.getUserWithNameBegining(searchTerm);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        String content = response.readEntity(String.class);
        User[] users = gson.fromJson(content, User[].class);
        assertEquals(1, users.length);

        for (User user: users){
            assertTrue(user.getUsername().contains(searchTerm));
        }

    }

    @Test
    public void usersNameSearchForTwoSubjectTests() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Set up users who are following our sample user
        String sampleSearchedUserOne = "user_one";
        loginAndSetupNewUser(sampleSearchedUserOne);

        String sampleSearchedUserTwo = "user_two";
        loginAndSetupNewUser(sampleSearchedUserTwo);

        // Log back into the sample user
        apiClient.loginUser(username, pw);


        // Get followers, and ensure it was successful
        String searchTerm = "user";
        Response response = apiClient.getUserWithNameBegining(searchTerm);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        String content = response.readEntity(String.class);
        User[] users = gson.fromJson(content, User[].class);
        assertEquals(2, users.length);

        for (User user: users){
            assertTrue(user.getUsername().contains(searchTerm));
        }

    }
}
