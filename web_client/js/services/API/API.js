(function () {

    window.API = {
        http: new HW('http://localhost:8080/photoshare'),
        endpoints: {
            USERS_CREATE: 'users/adduser',
            USERS_LOGIN: 'users/login',
            USERS_FOLLOWERS: 'users/followers/:username',
            USERS_FOLLOWING: 'users/following/:username',
            USERS_SEARCH: 'users/search?name=:query',

            FOLLOW_USER: 'users/follow/:username',
            UNFOLLOW_USER: 'users/unfollow/:username',

            POSTS_CREATE: 'photos/upload',
            POSTS_GET: 'photos/:id',
            POSTS_FEED: 'newsfeeds/:username',
            POST_DELETE: 'photos/delete/:id',
            POST_DELETE_ADMIN: 'admin/removephoto/:id',
            UPVOTE_POST: 'photos/like/:id',
            DOWNVOTE_POST: 'photos/unlike/:id',

            POSTS_GET_BY_USER: 'users/:username/photos',

            COMMENTS_GET_FOR_POST: 'comments/photos/:id',
            ADD_COMMENT: 'comments/addcomment',

            ALBUMS_GET_FOR_USER: 'albums/users/:username',
            ALBUM_CREATE: 'albums/addalbum',
            ALBUM_GET: 'albums/:id',
            ALBUM_GET_POSTS: 'photos/albums/:id',
            ALBUM_UPDATE_DESCRIPTION: 'albums/updatedescription',

            COMMENT_UPVOTE: 'comments/like/:id',
            COMMENT_DOWNVOTE: 'comments/unlike/:id'
        }
    }

})()