package client;

import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Stream;

import static common.CommentType.*;
import static common.Resources.*;
import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static javax.ws.rs.core.Response.Status.OK;

/**
 * Console-based client.
 */
public final class ConsoleClient {

    // Input from user
    private Scanner scanner = new Scanner(System.in, CHARSET_STRING);

    // API client and Json parser
    private ApiClient apiClient = new ApiClient();

    public static void main(String[] args) {
        ConsoleClient client = new ConsoleClient();
        client.run();
    }

    /**
     * Client's main loop.
     */
    private void run() {
        System.out.println("Look at the README for a guide to valid commands.");

        // Runs forever
        while (true) {
            // Get input from user
            String input = scanner.nextLine();

            // Process input
            try {
                processInput(input);
            }
            catch(NumberFormatException e) {
                System.out.println("Incorrect arguments entered. Look at the README for a guide to valid commands.");
            }
        }
    }

    /**
     * Processes user input
     *
     * @param input the user's input
     */
    private void processInput(String input) {
        // Split input on spaces
        String[] args = input.split(" ");

        // Process each argument
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            // If the client wants to add a user, parse their input and add
            // Input in form of '--adduser {"name":xxx,"password":xxx}'
            if (arg.equals("--adduser")) {
                System.out.println("Parsing user");
                Response response = apiClient.addUser(args[++i], args[++i]);
                System.out.println(response.getStatus() == NO_CONTENT.getStatusCode() ?
                        "Parsed user" : "Failed to parse");
            }
            // If the client wants to get all users
            // Input in form of '--login {"name":xxx,"password":xxx}'
            else if(arg.equals("--login")) {
                System.out.println("Logging in user");
                Response response = apiClient.loginUser(args[++i], args[++i]);
                System.out.println(response.getStatus() == NO_CONTENT.getStatusCode()
                        ? "Logged in user" : "Failed login");
            }

            // If the client wants to upload a photo.
            // Input in form of '--upload [requested-photo-name] [filename]'
            else if(arg.equals("--upload")) {
                System.out.println("Uploading photo");
                Response response = apiClient.uploadPhoto(args[++i], loadFile(args[++i]));
                System.out.println(response.getStatus() == OK.getStatusCode()
                        ? "Uploaded." : "Failed to upload");
            }

            // If the client wants to get a photo
            // Input in form of '--getphoto [reference-id]'
            else if(arg.equals("--getphoto")) {
                System.out.println("Downloading photo");
                Response response = apiClient.getPhoto(Long.parseLong(args[++i]));
                System.out.println("Downloaded Data:");
                System.out.println(response.readEntity(String.class));
            }

            // If the client wants to comment a photo
            // Input in form of '--commentphoto [reference-id] [comment]'
            else if(arg.equals("--commentphoto")) {
                System.out.println("Adding photo comment");
                Response response = apiClient.addComment(Long.parseLong(args[++i]), PHOTO_COMMENT, args[++i]);
                System.out.println(response.getStatus() == OK.getStatusCode()
                        ? "Added comment." : "Failed to add comment");
            }

            // If the client wants to vote on a comment
            // Input in form of '--vote [reference-id] [true / false]'
            else if(arg.equals("--vote")) {
                System.out.println("Voting on comment");
                Response response = apiClient.vote(Long.parseLong(args[++i]), Boolean.parseBoolean(args[++i]));
                System.out.println(response.getStatus() == NO_CONTENT.getStatusCode()
                        ? "Voted." : "Failed to vote");
            }

            // If the client wants to comment a photo
            // Input in form of '--reply [reference-id] [comment]'
            else if(arg.equals("--reply")) {
                System.out.println("Adding reply");
                Response response = apiClient.addComment(Long.parseLong(args[++i]), REPLY, args[++i]);
                System.out.println(response.getStatus() == OK.getStatusCode()
                        ? "Added reply." : "Failed to add reply");
            }

            // If the client wants to get all comments from a user
            // Input in form of '--getcomments [name]'
            else if(arg.equals("--getcomments")) {
                System.out.println("Downloading comments...");
                Response response = apiClient.getAllComments(args[++i]);
                System.out.println("Downloaded Data:");
                System.out.println(response.readEntity(String.class));
            }

            // If the client wants to get all notifications for a user
            // Input in form of '--getnotifications'
            else if(arg.equals("--getnotifications")) {
                System.out.println("Downloading notifications...");
                Response response = apiClient.getNotifications();
                System.out.println("Downloaded Data:");
                System.out.println(response.readEntity(String.class));
            }

            // If the client wants to get all comments for a photo
            // Input in form of '--getphotocomments [id]'
            else if(arg.equals("--getphotocomments")) {
                System.out.println("Downloading comments...");
                Response response = apiClient.getAllPhotoComments(Long.parseLong(args[++i]));
                System.out.println("Downloaded Data:");
                System.out.println(response.readEntity(String.class));
            }

            // If the client wants to get all replies for a comment
            // Input in form of '--getreplies [id]'
            else if(arg.equals("--getreplies")) {
                System.out.println("Downloading comments...");
                Response response = apiClient.getAllReplies(Long.parseLong(args[++i]));
                System.out.println("Downloaded Data:");
                System.out.println(response.readEntity(String.class));
            }

            // If the ADMIN client wants to remove a comment
            // Input in form of '--removecomment [id]'
            else if(arg.equals("--removecomment")) {
                System.out.println("Removing comment...");
                Response response = apiClient.removeComment(Long.parseLong(args[++i]));
                System.out.println(response.getStatus() == NO_CONTENT.getStatusCode()
                        ? "Removed comment." : "Failed to remove comment");
            }

            // If the client wants to get all photos from a user
            // Input in form of '--getphotos [name]'
            else if(arg.equals("--getphotos")) {
                System.out.println("Downloading photos...");
                Response response = apiClient.getAllPhotos(args[++i]);
                System.out.println("Downloaded Data:");
                System.out.println(response.readEntity(String.class));
            }

            // If the client wants to get all users
            // Input in form of '--getusers'
            else if(arg.equals("--getusers")) System.out.println(apiClient.getUsers().readEntity(String.class));

            else System.out.println(String.format("Invalid argument at index %d", i));
        }
    }

    /**
     * Loads the file from the provided file name
     * @param arg the file name
     * @return the file contents
     */
    private byte[] loadFile(String arg) {
        // Open file
        try (BufferedReader file = new BufferedReader(new InputStreamReader(
                new FileInputStream(arg), CHARSET_STRING))) {
            // Retrieve photo contents
            Stream<String> lines = file.lines();
            Optional<String> contents = lines.reduce(String::concat);
            return contents.isPresent() ? contents.get().getBytes(CHARSET) : new byte[] {};
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return new byte[] {};
    }
}
