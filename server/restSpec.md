PhotoShare RESTful API specification
----

## Album-related APIs

* `/albums/addalbum`

    * **Summary:** Uploads the new album

    * **Method:** `POST`
  
    * **URL Parameters:** `None`
    
    * **Body Parameters**
    
      ```
      {
          "albumName": string,
          "description": string, 
      }
      
    * **Success Response:**
    
      * **Code:** 200 OK <br />
        **Content:** `{ "referenceId": long }`
     
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />
        
* `/albums/updatedescription`

    * **Summary:** Updates an album's description
    
    * **Method:** `POST`
  
    * **URL Parameters:** `None`
    
    * **Body Parameters**
    
      ```
      {
          "albumId": long,
          "description": string, 
      }
      
    * **Success Response:**
    
      * **Code:** 204 No Content <br />
     
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />
        
* `/albums/users/{user}`

    * **Summary:** Retrieves all albums belonging to the user

    * **Method:** `GET`
  
    * **URL Parameters:** 
        * user: `refers to a valid username (as a string)`
      
    * **Success Response:**
    
      * **Code:** 200 OK <br />
        **Content:** 
        ```
         [ 
          {
            "albumId": long, 
            "albumName": string,
            "description": string,
            "authorName": string,
            "albumTime": long 
          },
          ... 
        ]
     
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />
        
* `/albums/{id}`

    * **Summary:** Retrieves the given album

    * **Method:** `GET`
  
    * **URL Parameters:** 
        * id: `an album's unique id (as a long)`
      
    * **Success Response:**
    
      * **Code:** 200 OK <br />
        **Content:** 
        ``` 
        {
            "albumId": long, 
            "albumName": string,
            "description": string,
            "authorName": string,
            "albumTime": long 
        }
     
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />
    
## Photo-related APIs


* `/photos/upload`

    * **Summary:** Uploads the given photo

    * **Method:** `POST`
  
    * **URL Parameters:** `None`
    
    * **Body Parameters**
    
      ```
      {
          "photoName": string,
          "extension": string,
          "albumId": long,
          "encodedPhotoContents": string
      }
      
    * **Success Response:**
    
      * **Code:** 200 OK <br />
        **Content:** `{"referenceId": long}`
     
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />

* `/photos/albums/{id}`

    * **Summary:** Retrieves all photos in the given album
    
    * **Method:** `GET`
  
    * **URL Parameters:** 
        * id: `an album's unique id (as a long)`
      
    * **Success Response:**
    
      * **Code:** 200 OK <br />
        **Content:** 
        ``` 
        [
            {
                "id": long, 
                "photoName": string,
                "extension": string,
                "authorName": string,
                "albumId": long,                 
                "photoTime": long 
            },
            ...
        ]
     
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />

* `/photos/{id}`

    * **Summary:** Retrieves the given photo's meta-data 

    * **Method:** `GET`
  
    * **URL Parameters:** 
        * id: `a photo's unique id (as a long)`
    
    * **Success Response:**
    
      * **Code:** 200 OK <br />
        **Content:** 
        ``` 
        {
            "id": long, 
            "photoName": string,
            "extension": string,
            "authorName": string,
            "albumId": long,
            "photoTime": long 
        }
     
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />

* `/photos/{id}/content`

    * **Summary:** Retrieves the content for the given photo 

    * **Method:** `GET`
  
    * **URL Parameters:** 
        * id: `a photo's unique id (as a long)`
    
    * **Success Response:**
    
      * **Code:** 200 OK <br />
        **Content:** 
        ``` 
        The photo, encoded as a raw base64 string.
     
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />
  
* `/photos/upvote/{id}`

    * **Summary:** Up-votes the given photo

    * **Method:** `PUT`
  
    * **URL Parameters:** 
        * id: `a photo's unique id (as a long)`
      
    * **Success Response:**
    
      * **Code:** 204 No Content <br />
        
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />
        
* `/photos/downvote/{id}`

    * **Summary:** Down-votes the given photo

    * **Method:** `PUT`
  
    * **URL Parameters:** 
        * id: `a photo's unique id (as a long)`
      
    * **Success Response:**
    
      * **Code:** 204 No Content <br />
        
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />
       
* `/photos/delete/{id}`
       
    * **Summary:** Deletes the given photo, if it belongs to the sender

    * **Method:** `DELETE`
  
    * **URL Parameters:** 
        * id: `a photo's unique id (as a long)`
      
    * **Success Response:**
    
      * **Code:** 204 No Content <br />
        
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />

## Comment-related APIs


* `/comments/addcomment`

    * **Summary:** Uploads a new comment

    * **Method:** `POST`
  
    * **URL Parameters:** `None`
    
    * **Body Parameters**
    
      ```
      {
          "commentContents": string,
          "referenceId": long,
          "commentType": string
      }
      
    * **Success Response:**
    
      * **Code:** 200 OK <br />
        **Content:** `{"referenceId": long}`
     
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />


* `/comments/edit/{commentId}`

    * **Summary:** Edits the given comment

    * **Method:** `POST`
  
    * **URL Parameters:** 
        * commentId: `refers to a comment ID (as a long)`
    
    * **Body Parameters**
    
      ``` 
      {
          "commentContents" : string
      }
      
    * **Success Response:**
    
      * **Code:** 200 <br />
     
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />

* `/comments/photos/{id}`

    * **Summary:** Retrieves all comments on the given photo
    
    * **Method:** `GET`
  
    * **URL Parameters:** 
        * id: `a photo's unique id (as a long)`
      
    * **Success Response:**
    
      * **Code:** 200 OK <br />
        **Content:** 
        ``` 
        [
            {
                "id": long, 
                "referenceId": long,
                "author": string,             
                "commentContents": string,
                "commentTime": long,
                "commentType": string
            },
            ...
        ]
        ```
        Note, "commentType" here refers to "PHOTO_COMMENT".
     
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />
  
* `/comments/replies/{id}`

    * **Summary:** Retrieves all replies to the given comment

    * **Method:** `GET`
  
    * **URL Parameters:** 
        * id: `a comment's unique id (as a long)`
      
    * **Success Response:**
    
      * **Code:** 200 OK <br />
        **Content:** 
        ``` 
        [
            {
                "id": long, 
                "referenceId": long,
                "author": string,             
                "commentContents": string,
                "commentTime": long,
                "commentType": string
            },
            ...
        ]
        ``` 
       
        Note, "commentType" here refers to "REPLY".
     
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />

* `/comments/upvote/{id}`

    * **Summary:** Up-votes the given comment
    
    * **Method:** `PUT`
  
    * **URL Parameters:** 
        * id: `a comment's unique id (as a long)`
      
    * **Success Response:**
    
      * **Code:** 204 No Content <br />
        
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />
        
* `/comments/downvote/{id}`

    * **Summary:** Down-votes the given comment

    * **Method:** `PUT`
  
    * **URL Parameters:** 
        * id: `a comment's unique id (as a long)`
    
    * **Success Response:**
    
      * **Code:** 204 No Content <br />
        
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />
         
 * `/comments/delete/{id}`
 
    * **Summary:** Deletes the given comment
 
     * **Method:** `DELETE`
   
     * **URL Parameters:** 
         * id: `refers to a comment's unique ID (as a long)`
       
     * **Success Response:**
     
       * **Code:** 204 No Content <br />
      
     * **Error Response:**
     
       * **Code:** 400 Bad Request <br />
     
       or
     
       * **Code:** 401 Unauthorized <br />
        
## Notification-Related APIs
        
* `/notifications/`

    * **Summary:** Retrieves all notifications for the authenticated user

    * **Method:** `GET`
  
    * **URL Parameters:** `None`
      
    * **Success Response:**
    
      * **Code:** 200 OK <br />
      **Content:**
      ``` 
      [
          {
              "commentId": long, 
              "referenceId": long,
              "commentAuthor": string,             
              "notifiedUser": string,
              "commentType": string
          },
          ...
      ]
      ```
      Note, "commentType" here refers to "PHOTO_COMMENT" or "REPLY".
           
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />    
        
## User-Related APIs
        
* `/users/`

    * **Summary:** Retrieve all users on the system

    * **Method:** `GET`
  
    * **URL Parameters:** `None`
      
    * **Success Response:**
    
      * **Code:** 200 OK <br />
      **Content:**
      ``` 
      [
          {
              "username": string, 
              "admin": boolean
          },
          ...
      ]
      ```
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />
    
* `/users/adduser`

    * **Summary:** Adds a new user.

    * **Method:** `POST`
  
    * **URL Parameters:** `None`
    
    * **Body Parameters**
    
      ```
      {
          "username": string,
          "password": int
      }
      
    * **Success Response:**
    
      * **Code:** 204 No Content <br />
     
    * **Error Response:**
    
      * **Code:** 409 Conflict <br />

* `/users/login`

    * **Summary:** Verifies the provided credentials

    * **Method:** `POST`
  
    * **URL Parameters:** `None`
      
    * **Success Response:**
    
      * **Code:** 200 OK <br />
      * **Content:** 
      ```
      {
        "username": string,
        "admin": boolean
      }
      ```
      
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />
        
* `/users/{username}/photos`

    * **Summary:** Retrieves all photos a user has posted, across all albums.

    * **Method:** `GET`
  
    * **URL Parameters:** 
        * username: `a user's unique username (as a string)`
      
    * **Success Response:**
    
      * **Code:** 200 OK <br />
        **Content:** 
        ``` 
        [
            {
                "id": long, 
                "photoName": string,
                "extension": string,
                "authorName": string,
                "albumId": long,                 
                "photoTime": long 
            },
            ...
        ]
     
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />
  
  
* `/users/follow/{username}`

    * **Summary:** Follows the given user

    * **Method:** `PUT`
  
    * **URL Parameters:** 
         * username: `refers to a user's unique name (as a string)`
    
    * **Body Parameters**
    
      ```
      {
          "userFrom": string,
          "userTo": string
      }
      
    * **Success Response:**
    
      * **Code:** 200 No Content <br />
      
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />
    
* `/users/unfollow/{username}`

    * **Summary:** Unfollows the given user

    * **Method:** `DELETE`
  
    * **URL Parameters:** 
         * username: `refers to a user's unique name (as a string)`
    
    * **Body Parameters**
    
      ```
      {
          "userFrom": string,
          "userTo": string
      }
      
    * **Success Response:**
    
      * **Code:** 200 No Content <br />
      
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />

## NewsFeed APIs
* `/newsfeed/{username}`

    * **Summary:** Retrieves all the photos posted by users the given user is following

    * **Method:** `POST`
  
    * **URL Parameters:** 
    
        username: `the unique user name (as a string)`
      
    * **Success Response:**
    
      * **Code:** 200 OK <br />
      **Content:** 
      ``` 
      [
          {
              "id": long, 
              "photoName": string,
              "extension": string,
              "authorName": string,
              "albumId": long,                 
              "photoTime": long 
          },
          ...
      ]
      
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />

## Admin APIs
        
* `/admin/removecomment/{id}`

    * **Summary:** Deletes the given comment

    * **Method:** `DELETE`
  
    * **URL Parameters:** 
        
        id: `a comment's unique id (as a long)`
 
    * **Success Response:**
    
      * **Code:** 204 No Content <br />
        
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />
        
* `/admin/removephoto/{id}`

    * **Summary:** Deletes the given photo

    * **Method:** `DELETE`
  
    * **URL Parameters:** 
        
        id: `a photo's unique id (as a long)`
      
    * **Success Response:**
    
      * **Code:** 204 No Content <br />
        
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />