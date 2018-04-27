(function () {

    window.API = {
        http: new HW('http://localhost:8080/photoshare'),
        endpoints: {
            USERS_CREATE: 'users/adduser',
            USERS_LOGIN: 'users/login',
            USERS_FOLLOWERS: 'users/followers/:username',
            USERS_FOLLOWING: 'users/following/:username',

            FOLLOW_USER: 'users/follow/:username',
            UNFOLLOW_USER: 'users/unfollow/:username',

            POSTS_CREATE: 'photos/upload',
            POSTS_GET: 'photos/:id',
            POSTS_FEED: 'newsfeeds/:username',
            UPVOTE_POST: 'photos/upvote/:id',
            DOWNVOTE_POST: 'photos/downvote/:id',

            POSTS_GET_BY_USER: 'users/:username/photos',

            COMMENTS_GET_FOR_POST: 'comments/photos/:id'
        }
    }

})()