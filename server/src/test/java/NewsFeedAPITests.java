import org.junit.Test;
import server.datastore.exceptions.InvalidResourceRequestException;
import server.objects.Photo;
import server.objects.Receipt;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class NewsFeedAPITests extends TestUtility{

    @Test
    public void emptyNewsFeedTest() throws InvalidResourceRequestException {

        // Set up user who is being followed
        String userBeingFollowed = "user_being_followed";
        loginAndSetupNewUser(userBeingFollowed);

        // Set up user whose news feed we want
        loginAndSetupNewUser(username);

        // Follow the first user
        Response followResponse = apiClient.followUser(userBeingFollowed);

        // Check Status code
        Response newsFeedResponse = apiClient.getNewsFeed();
        assertEquals(Response.Status.OK.getStatusCode(), newsFeedResponse.getStatus());

        List<Photo> photosInNewsFeed =  resolver.getNewsFeed(username);
        assertEquals(photosInNewsFeed.size(), 0);
    }


    // Followee denotes the person being followed

    @Test
    public void singleItemInNewsFeedFromSingleFolloweeTest() throws InvalidResourceRequestException {

        // Set up user who is being followed
        String userBeingFollowed = "user_being_followed";
        loginAndSetupNewUser(userBeingFollowed);

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(userBeingFollowed, ext, description, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // Set up user whose news feed we want
        loginAndSetupNewUser(username);

        // Follow the first user
        Response followResponse = apiClient.followUser(userBeingFollowed);

        // Check Status code
        Response newsFeedResponse = apiClient.getNewsFeed();
        assertEquals(Response.Status.OK.getStatusCode(), newsFeedResponse.getStatus());

        // Check correct albums were provided
        List<Photo> photosOfFollowedUser =  resolver.getPhotos(userBeingFollowed);
        List<Photo> photosInNewsFeed =  resolver.getNewsFeed(username);

        List<Long> photoIdsOfFollowedUser = photosOfFollowedUser.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<Long> photoIdsInNewsFeed = photosInNewsFeed.stream().map(e -> e.getId()).collect(Collectors.toList());

        assertArrayEquals(photoIdsOfFollowedUser.toArray(), photoIdsInNewsFeed.toArray());

    }

    @Test
    public void multipleItemsInNewsFeedFromSingleFolloweeTest() throws InvalidResourceRequestException {

        // Set up user who is being followed
        String userBeingFollowed = "user_being_followed";
        loginAndSetupNewUser(userBeingFollowed);

        // Create sample data
        byte[] contentsOne = new byte[] {1, 2, 3, 4, 5};
        byte[] contentsTwo = new byte[] {1, 1, 3, 1, 2};

        // Upload 'photo' (byte[])
        Response uploadResponseOne = apiClient.uploadPhoto(userBeingFollowed, ext, description, albumId, contentsOne);
        assertEquals(Response.Status.OK.getStatusCode(), uploadResponseOne.getStatus());

        // Upload 'photo' (byte[])
        Response uploadResponseTwo = apiClient.uploadPhoto(userBeingFollowed, ext, description, albumId, contentsTwo);
        assertEquals(Response.Status.OK.getStatusCode(), uploadResponseTwo.getStatus());

        // Set up user whose news feed we want
        loginAndSetupNewUser(username);

        // Follow the first user
        Response followResponse = apiClient.followUser(userBeingFollowed);

        // Check Status code
        Response newsFeedResponse = apiClient.getNewsFeed();
        assertEquals(Response.Status.OK.getStatusCode(), newsFeedResponse.getStatus());

        // Check correct albums were provided
        List<Photo> photosOfFollowedUser =  resolver.getPhotos(userBeingFollowed);
        List<Photo> photosInNewsFeed =  resolver.getNewsFeed(username);

        List<Long> photoIdsOfFollowedUser = photosOfFollowedUser.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<Long> photoIdsInNewsFeed = photosInNewsFeed.stream().map(e -> e.getId()).collect(Collectors.toList());

        assertArrayEquals(photoIdsOfFollowedUser.toArray(), photoIdsInNewsFeed.toArray());

    }

    @Test
    public void itemsInNewsFeedFromMultipleFolloweesTest() throws InvalidResourceRequestException {

        // Set up users who are being followeds
        String userBeingFollowedOne = "user_followed_one";
        String userBeingFollowedTwo = "user_followes_two";

        // Create sample data
        byte[] contentsOne = new byte[] {1, 2, 3, 4, 5};
        byte[] contentsTwo = new byte[] {1, 1, 3, 1, 2};

        // Upload 'photo' (byte[]) for first followee
        loginAndSetupNewUser(userBeingFollowedOne);
        Response uploadResponseOne = apiClient.uploadPhoto(userBeingFollowedOne, ext, description, albumId, contentsOne);
        assertEquals(Response.Status.OK.getStatusCode(), uploadResponseOne.getStatus());

        // Upload 'photo' (byte[]) for second followee
        loginAndSetupNewUser(userBeingFollowedTwo);

        // Add new album, and retrieve the returned id
        Response response = apiClient.addAlbum(albumName, description);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long albumIdTwo = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        Response uploadResponseTwo = apiClient.uploadPhoto(userBeingFollowedTwo, ext, description, albumIdTwo, contentsTwo);
        assertEquals(Response.Status.OK.getStatusCode(), uploadResponseTwo.getStatus());

        // Set up user whose news feed we want
        loginAndSetupNewUser(username);

        // Follow the first user
        Response followResponseOne = apiClient.followUser(userBeingFollowedOne);
        Response followResponseTwo = apiClient.followUser(userBeingFollowedTwo);

        // Check Status code
        Response newsFeedResponse = apiClient.getNewsFeed();
        assertEquals(Response.Status.OK.getStatusCode(), newsFeedResponse.getStatus());

        // Check correct albums were provided
        List<Photo> photosOfFollowedUserOne =  resolver.getPhotos(userBeingFollowedOne);
        List<Photo> photosOfFollowedUserTwo =  resolver.getPhotos(userBeingFollowedTwo);

        // Getting photoIds of all followees' photos
        ArrayList<Photo> photoOfAllFollowers = new ArrayList<Photo>();
        photoOfAllFollowers.addAll(photosOfFollowedUserOne);
        photoOfAllFollowers.addAll(photosOfFollowedUserTwo);

        // Getting photoIds of all photos in new feed
        List<Photo> photosInNewsFeed =  resolver.getNewsFeed(username);
        ;


        assertEquals(photoOfAllFollowers.size(), photosInNewsFeed.size());

    }
}
