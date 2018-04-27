import org.junit.Test;
import server.datastore.exceptions.InvalidResourceRequestException;
import server.objects.Photo;
import server.objects.Receipt;

import javax.ws.rs.core.Response;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class PhotoAPITests extends TestUtility{

    @Test
    public void uploadPhotoTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, ext, description, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // Check server has record of photo by decoding its base64 representation and checking for
        // equivalence.
        List<Photo> photos = resolver.getPhotos(this.username);
        assertEquals(photoName, photos.get(0).getPhotoName());
        assertEquals(description, photos.get(0).getDescription());
    }

    @Test
    public void uploadSamePhotoTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, ext, description, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Upload photo again
        response = apiClient.uploadPhoto(photoName, ext, description, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id2 = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Check server has record of both photos, that their contents and names are identical,
        // but that their ids are different.
        List<Photo> photos = resolver.getPhotos(username);
        assertEquals(2, photos.size());
        assertNotEquals(id, id2);
        for(Photo p : photos) {
            assertEquals(photoName, p.getPhotoName());
            assertEquals(description, p.getDescription());
        }
    }

    @Test
    public void getAllPhotosFromUserTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, ext, description, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // Get all photos from user on server
        Response photosResponse = apiClient.getAllPhotos(username);

        // Parse JSON and check photo contents and who posted it
        String photosStr = photosResponse.readEntity(String.class);
        Photo[] photos = gson.fromJson(photosStr, Photo[].class);
        for(Photo photo : photos) {
            assertEquals(photo.getAuthorName(), username);
            assertEquals(photo.getPhotoName(), photoName);
            assertEquals(photo.getDescription(), description);
        }
    }



    @Test
    public void get0PhotosFromUserTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Get all photos from user on server
        Response photosResponse = apiClient.getAllPhotos(username);

        // Parse JSON
        String photos = photosResponse.readEntity(String.class);
        Photo[] photo = gson.fromJson(photos, Photo[].class);
        assertEquals(0, photo.length);
    }

    @Test
    public void getPhotoTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Upload 'photo' (byte[])
        Response response = apiClient.uploadPhoto(photoName, ext, description, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Get recently uploaded photo from server
        Response photosResponse = apiClient.getAllPhotos(albumId);
        assertEquals(Response.Status.OK.getStatusCode(), photosResponse.getStatus());

        // Parse JSON and check photo contents and who posted it
        String photoStr = photosResponse.readEntity(String.class);
        Photo photo = gson.fromJson(photoStr, Photo[].class)[0];
        assertEquals(photo.getAuthorName(), username);
        assertEquals(photo.getPhotoName(), photoName);

        // Make separate request for photo contents
        byte[] receivedContents = apiClient.getPhotoContentsJPG(id, ext).readEntity(byte[].class);
        assertArrayEquals(contents, receivedContents);
    }

    @Test
    public void ratePhotoTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Upload 'photo'
        Response response = apiClient.uploadPhoto(photoName, ext, description, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Send upvote request to server, and check it was successful on the server.
        Response voteResponse = apiClient.ratePhoto(id, true);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), voteResponse.getStatus());
        assertEquals(1, resolver.getPhotoMetaData(id).getLikes().size());
    }

    @Test
    public void idempotentPhotoRateTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Upload 'photo'
        Response response = apiClient.uploadPhoto(photoName, ext, description, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Send upvote request to server, and check it was successful on the server.
        Response voteResponse = apiClient.ratePhoto(id, true);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), voteResponse.getStatus());
        assertEquals(1, resolver.getPhotoMetaData(id).getLikes().size());

        // Send same upvote request again. Ensure it worked, but that nothing changed on the server
        voteResponse = apiClient.ratePhoto(id, true);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), voteResponse.getStatus());
        assertEquals(1, resolver.getPhotoMetaData(id).getLikes().size());
    }

    @Test
    public void undoPhotoRatingTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Upload 'photo'
        Response response = apiClient.uploadPhoto(photoName, ext, description, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long id = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Send upvote request to server, and check it was successful on the server.
        Response voteResponse = apiClient.ratePhoto(id, true);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), voteResponse.getStatus());
        assertEquals(1, resolver.getPhotoMetaData(id).getLikes().size());

        // Send same upvote request again. Ensure it worked, but that nothing changed on the server
        voteResponse = apiClient.ratePhoto(id, false);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), voteResponse.getStatus());
        assertEquals(0, resolver.getPhotoMetaData(id).getLikes().size());
    }
}
