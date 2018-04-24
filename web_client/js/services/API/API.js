(function () {

    window.API = {
        baseUrl: 'http://localhost:8080/photoshare',
        http: new HW(this.baseUrl),
        endpoints: {
            USERS_CREATE: 'users/adduser',
            POSTS_CREATE: 'photos/upload'
        }
    }

})()