PhotoShare RESTful API specification
----

## Album-related APIs
* `/albums/addalbum`

    * **Method:** `POST`
  
    * **URL Parameters:** `None`
    
    * **Body Parameters**
    
      ```
      {
          "auth" : 
          {
            "apiKey" : string,
            "time": long,
            "user": string,
            "password": int
          },
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
    
    * **Sample Call:**
    
      ```javascript
        $.ajax(
        {
          url: "/albums/addalbum",
          dataType: "json",
          data :
          {
              {
                "auth" : 
                {
                  "apiKey" : abc123,
                  "time": 1524219966,
                  "user": "sampleUserName",
                  "password": 1
                },
                "albumName": "sampleAlbum",
                "description": "description"
            }
          },
          type : "POST",
          success : function(r) 
          {
            console.log(r);
          }
        });
        
* `/albums/users/{user}`

    * **Method:** `POST`
  
    * **URL Parameters:** 
        * user: `refers to a valid username (as a string)`
    
    * **Body Parameters**
    
      ``` 
      {
          "auth" : 
          {
            "apiKey" : string,
            "time": long,
            "user": string,
            "password": int
          }
      }
      
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
    
    * **Sample Call:**
    
      ```javascript
        $.ajax(
        {
          url: "/albums/users/sampleUserName",
          dataType: "json",
          data :
          {
              {
                "auth" : 
                {
                  "apiKey" : abc123,
                  "time": 1524219966,
                  "user": "username1",
                  "password": 1
                }
            }
          },
          type : "POST",
          success : function(r) 
          {
            console.log(r);
          }
        });
        
* `/albums/ids/{id}`

    * **Method:** `POST`
  
    * **URL Parameters:** 
        * id: `an album's unique id (as a long)`
    
    * **Body Parameters**
    
      ```
      {
          "auth" : 
          {
            "apiKey" : string,
            "time": long,
            "user": string,
            "password": int
          }
      }
      
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
    
    * **Sample Call:**
    
      ```javascript
        $.ajax(
        {
          url: "/albums/ids/123456",
          dataType: "json",
          data :
          {
              {
                "auth" : 
                {
                  "apiKey" : abc123,
                  "time": 1524219966,
                  "user": "username1",
                  "password": 1
                }
            }
          },
          type : "POST",
          success : function(r) 
          {
            console.log(r);
          }
        });

## Photo-related APIs


* `/photos/upload`

    * **Method:** `POST`
  
    * **URL Parameters:** `None`
    
    * **Body Parameters**
    
      ```
      {
          "auth" : 
          {
            "apiKey" : string,
            "time": long,
            "user": string,
            "password": int
          },
          "photoName": string,
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
    
    * **Sample Call:**
    
      ```javascript
        $.ajax(
        {
          url: "/photos/upload",
          dataType: "json",
          data :
          {
              {
                "auth" : 
                {
                  "apiKey" : abc123,
                  "time": 1524219966,
                  "user": "username1",
                  "password": 1
                },
               "photoName": "photo",
               "albumId": 0,
               "encodedPhotoContents": "CBwbGVhc3VyZS4 ... ="
            }
          },
          type : "POST",
          success : function(r) 
          {
            console.log(r);
          }
        });

* `/photos/albums/{id}`

    * **Method:** `POST`
  
    * **URL Parameters:** 
        * id: `an album's unique id (as a long)`
    
    * **Body Parameters**
    
      ```
      {
          "auth" : 
          {
            "apiKey" : string,
            "time": long,
            "user": string,
            "password": int
          }
      }
      
    * **Success Response:**
    
      * **Code:** 200 OK <br />
        **Content:** 
        ``` 
        [
            {
                "id": long, 
                "photoName": string,
                "authorName": string,
                "albumId": long,                 
                "photoContents": (base64) string,
                "photoTime": long 
            },
            ...
        ]
     
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />
    
    * **Sample Call:**
    
      ```javascript
        $.ajax(
        {
          url: "/photos/albums/123456",
          dataType: "json",
          data :
          {
              {
                "auth" : 
                {
                  "apiKey" : abc123,
                  "time": 1524219966,
                  "user": "username1",
                  "password": 1
                }
            }
          },
          type : "POST",
          success : function(r) 
          {
            console.log(r);
          }
        });
        
* `/photos/users/{username}`

    * **Method:** `POST`
  
    * **URL Parameters:** 
        * username: `a user's unique username (as a string)`
    
    * **Body Parameters**
    
      ```
      {
          "auth" : 
          {
            "apiKey" : string,
            "time": long,
            "user": string,
            "password": int
          }
      }
      
    * **Success Response:**
    
      * **Code:** 200 OK <br />
        **Content:** 
        ``` 
        [
            {
                "id": long, 
                "photoName": string,
                "authorName": string,
                "albumId": long,                 
                "photoContents": (base64) string,
                "photoTime": long 
            },
            ...
        ]
     
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />
    
    * **Sample Call:**
    
      ```javascript
        $.ajax(
        {
          url: "/photos/users/123456",
          dataType: "json",
          data :
          {
              {
                "auth" : 
                {
                  "apiKey" : abc123,
                  "time": 1524219966,
                  "user": "username1",
                  "password": 1
                }
            }
          },
          type : "POST",
          success : function(r) 
          {
            console.log(r);
          }
        });

* `/photos/ids/{id}`

    * **Method:** `POST`
  
    * **URL Parameters:** 
        * id: `a photo's unique id (as a long)`
    
    * **Body Parameters**
    
      ```
      {
          "auth" : 
          {
            "apiKey" : string,
            "time": long,
            "user": string,
            "password": int
          }
      }
      
    * **Success Response:**
    
      * **Code:** 200 OK <br />
        **Content:** 
        ``` 
        {
            "id": long, 
            "photoName": string,
            "authorName": string,
            "albumId": long,                 
            "photoContents": (base64) string,
            "photoTime": long 
        }
     
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />
    
    * **Sample Call:**
    
      ```javascript
        $.ajax(
        {
          url: "/photos/ids/123456",
          dataType: "json",
          data :
          {
              {
                "auth" : 
                {
                  "apiKey" : abc123,
                  "time": 1524219966,
                  "user": "username1",
                  "password": 1
                }
            }
          },
          type : "POST",
          success : function(r) 
          {
            console.log(r);
          }
        });


* `/photos/upvote/{id}`

    * **Method:** `POST`
  
    * **URL Parameters:** 
        * id: `a photo's unique id (as a long)`
    
    * **Body Parameters**
    
      ```
      {
          "auth" : 
          {
            "apiKey" : string,
            "time": long,
            "user": string,
            "password": int
          }
      }
      
    * **Success Response:**
    
      * **Code:** 204 No Content <br />
        
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />
    
    * **Sample Call:**
    
      ```javascript
        $.ajax(
        {
          url: "/photos/upvote/123456",
          dataType: "json",
          data :
          {
              {
                "auth" : 
                {
                  "apiKey" : abc123,
                  "time": 1524219966,
                  "user": "username1",
                  "password": 1
                }
            }
          },
          type : "POST",
          success : function(r) 
          {
            console.log(r);
          }
        });
        
* `/photos/downvote/{id}`

    * **Method:** `POST`
  
    * **URL Parameters:** 
        * id: `a photo's unique id (as a long)`
    
    * **Body Parameters**
    
      ```
      {
          "auth" : 
          {
            "apiKey" : string,
            "time": long,
            "user": string,
            "password": int
          }
      }
      
    * **Success Response:**
    
      * **Code:** 204 No Content <br />
        
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />
    
    * **Sample Call:**
    
      ```javascript
        $.ajax(
        {
          url: "/photos/downvote/123456",
          dataType: "json",
          data :
          {
              {
                "auth" : 
                {
                  "apiKey" : abc123,
                  "time": 1524219966,
                  "user": "username1",
                  "password": 1
                }
            }
          },
          type : "POST",
          success : function(r) 
          {
            console.log(r);
          }
        });
       

## Comment-related APIs


* `/comments/addcomment`

    * **Method:** `POST`
  
    * **URL Parameters:** `None`
    
    * **Body Parameters**
    
      ```
      {
          "auth" : 
          {
            "apiKey" : string,
            "time": long,
            "user": string,
            "password": int
          },
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
    
    * **Sample Call:**
    
      ```javascript
        $.ajax(
        {
          url: "/comments/addcomment",
          dataType: "json",
          data :
          {
              {
                "auth" : 
                {
                  "apiKey" : abc123,
                  "time": 1524219966,
                  "user": "username1",
                  "password": 1
                },
               "commentContents": "comment",
               "referenceId": 100,
               "commentType": "REPLY"
            }
          },
          type : "POST",
          success : function(r) 
          {
            console.log(r);
          }
        });

* `/comments/photos/{id}`

    * **Method:** `POST`
  
    * **URL Parameters:** 
        * id: `a photo's unique id (as a long)`
    
    * **Body Parameters**
    
      ```
      {
          "auth" : 
          {
            "apiKey" : string,
            "time": long,
            "user": string,
            "password": int
          }
      }
      
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
    
    * **Sample Call:**
    
      ```javascript
        $.ajax(
        {
          url: "/comments/photos/123456",
          dataType: "json",
          data :
          {
              {
                "auth" : 
                {
                  "apiKey" : abc123,
                  "time": 1524219966,
                  "user": "username1",
                  "password": 1
                }
            }
          },
          type : "POST",
          success : function(r) 
          {
            console.log(r);
          }
        });

* `/comments/replies/{id}`

    * **Method:** `POST`
  
    * **URL Parameters:** 
        * id: `a comment's unique id (as a long)`
    
    * **Body Parameters**
    
      ```
      {
          "auth" : 
          {
            "apiKey" : string,
            "time": long,
            "user": string,
            "password": int
          }
      }
      
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
    
    * **Sample Call:**
    
      ```javascript
        $.ajax(
        {
          url: "/comments/replies/123456",
          dataType: "json",
          data :
          {
              {
                "auth" : 
                {
                  "apiKey" : abc123,
                  "time": 1524219966,
                  "user": "username1",
                  "password": 1
                }
            }
          },
          type : "POST",
          success : function(r) 
          {
            console.log(r);
          }
        });

* `/comments/upvote/{id}`

    * **Method:** `POST`
  
    * **URL Parameters:** 
        * id: `a comment's unique id (as a long)`
    
    * **Body Parameters**
    
      ```
      {
          "auth" : 
          {
            "apiKey" : string,
            "time": long,
            "user": string,
            "password": int
          }
      }
      
    * **Success Response:**
    
      * **Code:** 204 No Content <br />
        
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />
    
    * **Sample Call:**
    
      ```javascript
        $.ajax(
        {
          url: "/comments/upvote/123456",
          dataType: "json",
          data :
          {
              {
                "auth" : 
                {
                  "apiKey" : abc123,
                  "time": 1524219966,
                  "user": "username1",
                  "password": 1
                }
            }
          },
          type : "POST",
          success : function(r) 
          {
            console.log(r);
          }
        });
        
* `/comments/downvote/{id}`

    * **Method:** `POST`
  
    * **URL Parameters:** 
        * id: `a comment's unique id (as a long)`
    
    * **Body Parameters**
    
      ```
      {
          "auth" : 
          {
            "apiKey" : string,
            "time": long,
            "user": string,
            "password": int
          }
      }
      
    * **Success Response:**
    
      * **Code:** 204 No Content <br />
        
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />
    
    * **Sample Call:**
    
      ```javascript
        $.ajax(
        {
          url: "/comments/downvote/123456",
          dataType: "json",
          data :
          {
              {
                "auth" : 
                {
                  "apiKey" : abc123,
                  "time": 1524219966,
                  "user": "username1",
                  "password": 1
                }
            }
          },
          type : "POST",
          success : function(r) 
          {
            console.log(r);
          }
        });
        
## Notification-Related APIs
        
* `/notifications/`

    * **Method:** `POST`
  
    * **URL Parameters:** `None`
    
    * **Body Parameters**
    
      ```
      {
          "auth" : 
          {
            "apiKey" : string,
            "time": long,
            "user": string,
            "password": int
          }
      }
      
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
    
    * **Sample Call:**
    
      ```javascript
        $.ajax(
        {
          url: "/notifications",
          dataType: "json",
          data :
          {
              {
                "auth" : 
                {
                  "apiKey" : abc123,
                  "time": 1524219966,
                  "user": "username1",
                  "password": 1
                }
            }
          },
          type : "POST",
          success : function(r) 
          {
            console.log(r);
          }
        });
        
        
## User-Related APIs
        
* `/users/`

    * **Method:** `POST`
  
    * **URL Parameters:** `None`
    
    * **Body Parameters**
    
      ```
      {
          "auth" : 
          {
            "apiKey" : string,
            "time": long,
            "user": string,
            "password": int
          }
      }
      
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
    
    * **Sample Call:**
    
      ```javascript
        $.ajax(
        {
          url: "/users",
          dataType: "json",
          data :
          {
              {
                "auth" : 
                {
                  "apiKey" : abc123,
                  "time": 1524219966,
                  "user": "username1",
                  "password": 1
                }
            }
          },
          type : "POST",
          success : function(r) 
          {
            console.log(r);
          }
        });
        

* `/users/adduser`

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
    
    * **Sample Call:**
    
      ```javascript
        $.ajax(
        {
          url: "/users/adduser",
          dataType: "json",
          data :
          {
            {
               "username": "username1",
               "password": 100
            }
          },
          type : "POST",
          success : function(r) 
          {
            console.log(r);
          }
        });


* `/users/login`

    * **Method:** `POST`
  
    * **URL Parameters:** `None`
    
    * **Body Parameters**
    
      ```
      {
          "auth" : 
          {
            "apiKey" : string,
            "time": long,
            "user": string,
            "password": int
          }
      }
      
    * **Success Response:**
    
      * **Code:** 204 No Content <br />
      
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />
    
    * **Sample Call:**
    
      ```javascript
        $.ajax(
        {
          url: "/users/login",
          dataType: "json",
          data :
          {
              {
                "auth" : 
                {
                  "apiKey" : abc123,
                  "time": 1524219966,
                  "user": "username1",
                  "password": 1
                }
            }
          },
          type : "POST",
          success : function(r) 
          {
            console.log(r);
          }
        });

* `/users/follow`

    * **Method:** `POST`
  
    * **URL Parameters:** `None`
    
    * **Body Parameters**
    
      ```
      {
          "auth" : 
          {
            "apiKey" : string,
            "time": long,
            "user": string,
            "password": int
          },
          "userFrom": string,
          "userTo": string
      }
      
    * **Success Response:**
    
      * **Code:** 200 No Content <br />
      
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />
    
    * **Sample Call:**
    
      ```javascript
        $.ajax(
        {
          url: "/users/follow",
          dataType: "json",
          data :
          {
              {
                "auth" : 
                {
                  "apiKey" : abc123,
                  "time": 1524219966,
                  "user": "username1",
                  "password": 1
                },
               "userFrom": "username1",
               "userTo": "username2"
            }
          },
          type : "POST",
          success : function(r) 
          {
            console.log(r);
          }
        });


* `/users/unfollow`

    * **Method:** `POST`
  
    * **URL Parameters:** `None`
    
    * **Body Parameters**
    
      ```
      {
          "auth" : 
          {
            "apiKey" : string,
            "time": long,
            "user": string,
            "password": int
          },
          "userFrom": string,
          "userTo": string
      }
      
    * **Success Response:**
    
      * **Code:** 200 No Content <br />
      
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />
    
    * **Sample Call:**
    
      ```javascript
        $.ajax(
        {
          url: "/users/unfollow",
          dataType: "json",
          data :
          {
              {
                "auth" : 
                {
                  "apiKey" : abc123,
                  "time": 1524219966,
                  "user": "username1",
                  "password": 1
                },
               "userFrom": "username1",
               "userTo": "username2"
            }
          },
          type : "POST",
          success : function(r) 
          {
            console.log(r);
          }
        });

## Admin APIs
        
* `/admin/removecomment/{id}`

    * **Method:** `POST`
  
    * **URL Parameters:** 
        
        id: `a comment's unique id (as a long)`
    
    * **Body Parameters**
    
      ```
      {
          "auth" : 
          {
            "apiKey" : string,
            "time": long,
            "user": string,
            "password": int
          }
      }
      
    * **Success Response:**
    
      * **Code:** 204 No Content <br />
        
    * **Error Response:**
    
      * **Code:** 400 Bad Request <br />
    
      or
    
      * **Code:** 401 Unauthorized <br />
    
    * **Sample Call:**
    
      ```javascript
        $.ajax(
        {
          url: "/admin/removecomment",
          dataType: "json",
          data :
          {
              {
                "auth" : 
                {
                  "apiKey" : abc123,
                  "time": 1524219966,
                  "user": "username1",
                  "password": 1
                }
            }
          },
          type : "POST",
          success : function(r) 
          {
            console.log(r);
          }
        });