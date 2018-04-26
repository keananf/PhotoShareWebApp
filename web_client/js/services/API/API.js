(function () {

    window.API = {
        http: new HW('http://localhost:8080/photoshare'),
        endpoints: {
            USERS_CREATE: 'users/adduser',
            USERS_LOGIN: 'users/login',

            POSTS_CREATE: 'photos/upload',
            POSTS_GET: 'photos/:id',
            POSTS_FEED: 'newsfeeds/:username',

            COMMENTS_GET_FOR_POST: 'comments/photos/:id'
        }
    }

})()