(function () {

    window.API = {
        http: new HW('http://localhost:3000/photoshare'),
        endpoints: {
            USERS_CREATE: 'users/adduser',
            USERS_LOGIN: 'users/login',
            POSTS_CREATE: 'photos/upload',
            COMMENTS_GET_FOR_POST: 'comments/photos/:id'
        }
    }

})()