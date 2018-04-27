import org.junit.Test;
import server.datastore.exceptions.InvalidResourceRequestException;
import server.objects.Album;
import server.objects.Photo;
import server.objects.Receipt;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;

public class AlbumAPITests extends TestUtility{

    @Test
    public void addAlbumTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Add new album, and retrieve the returned id
        Response response = apiClient.addAlbum(albumName, description);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long albumId = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Check server has record of album
        Album album = resolver.getAlbum(albumId);
        assertEquals(albumName, album.getAlbumName());
        assertEquals(description, album.getDescription());
    }

    @Test
    public void updateAlbumDescriptionTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Update album's description
        String newDescription = "new " + description;
        Response response = apiClient.updateAlbumDescription(albumId, newDescription);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        // Check server has record of album's new description
        Album album = resolver.getAlbum(albumId);
        assertEquals(albumName, album.getAlbumName());
        assertEquals(newDescription, album.getDescription());
    }

    @Test
    public void getAllAlbumsTest() throws InvalidResourceRequestException {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Add a new album, with the same name and description as the default album.
        Response response = apiClient.addAlbum(albumName, description);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // Check server has record of both albums
        for(Album album : resolver.getAlbums(username)) {
            assertEquals(albumName, album.getAlbumName());
            assertEquals(description, album.getDescription());
        }
    }

    @Test
    public void getAllPhotosFromAlbumTest() {
        // Add sample user and register it
        loginAndSetupNewUser(username);

        // Upload photo to default album twice
        Response response = apiClient.uploadPhoto(photoName, ext, description, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = apiClient.uploadPhoto(photoName, ext, description, albumId, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // Create second album, in preparation to upload a photo to it.
        response = apiClient.addAlbum(albumName, description);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        long albumId2 = gson.fromJson(response.readEntity(String.class), Receipt.class).getReferenceId();

        // Upload photo to second album
        response = apiClient.uploadPhoto(photoName, ext, description, albumId2, contents);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // Get all photos from album on server
        response = apiClient.getAllPhotos(albumId);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        // Parse response, ensuring only the 2 original photos are present.
        String photosStr = response.readEntity(String.class);
        Photo[] photoArray = gson.fromJson(photosStr, Photo[].class);
        assertEquals(2, photoArray.length);

        // Check each photo's contents and who posted it
        for(Photo photo : photoArray) {
            assertEquals(photo.getAuthorName(), username);
            assertEquals(photo.getPhotoName(), photoName);
            assertEquals(photo.getDescription(), description);
        }
    }
}
