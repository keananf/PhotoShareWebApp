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
            "apiKey" : [alphanumeric],
            "time": long,
            "user": string,
            "password": int
          },
          "albumName": string,
          "description": string, 
      }
      
    * **Success Response:**
    
      * **Code:** 200 <br />
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
                  "time": 1000,
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
            "apiKey" : [alphanumeric],
            "time": long,
            "user": string,
            "password": int
          }
      }
      
    * **Success Response:**
    
      * **Code:** 200 <br />
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
                  "time": 1000,
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
            "apiKey" : [alphanumeric],
            "time": long,
            "user": string,
            "password": int
          }
      }
      
    * **Success Response:**
    
      * **Code:** 200 <br />
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
                  "time": 1000,
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
            "apiKey" : [alphanumeric],
            "time": long,
            "user": string,
            "password": int
          },
          "photoName": string,
          "albumId": long,
          "encodedPhotoContents": string
      }
      
    * **Success Response:**
    
      * **Code:** 200 <br />
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
                  "time": 1000,
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

* `/photos/albums/{id}`

    * **Method:** `POST`
  
    * **URL Parameters:** 
        * id: `an album's unique id (as a long)`
    
    * **Body Parameters**
    
      ```
      {
          "auth" : 
          {
            "apiKey" : [alphanumeric],
            "time": long,
            "user": string,
            "password": int
          }
      }
      
    * **Success Response:**
    
      * **Code:** 200 <br />
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
                  "time": 1000,
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
        * username: `a user's unique name (as a string)`
    
    * **Body Parameters**
    
      ```
      {
          "auth" : 
          {
            "apiKey" : [alphanumeric],
            "time": long,
            "user": string,
            "password": int
          }
      }
      
    * **Success Response:**
    
      * **Code:** 200 <br />
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
                  "time": 1000,
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
            "apiKey" : [alphanumeric],
            "time": long,
            "user": string,
            "password": int
          }
      }
      
    * **Success Response:**
    
      * **Code:** 200 <br />
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
                  "time": 1000,
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
