(function () {

    let http = API.http

    window.API.Posts = {

        getNewsFeedForUser(username) {
            return new Promise((resolve, reject) => {
                http.get(API.endpoints.POSTS_FEED.replace(':username', username)).then(res => {
                    let posts = []

                    for (let i in res) {
                        posts.push(Models.Post.fromJson(res[i]))
                    }

                    resolve(posts)
                }).catch(err => reject(err))
            })
        },

        getPostsByUser(username) {
            return new Promise((resolve, reject) => {
                http.get(API.endpoints.POSTS_GET_BY_USER.replace(':username', username)).then(res => {

                    let posts = []
                    for (let i in res) {
                        posts.push(Models.Post.fromJson(res[i]))
                    }

                    resolve(posts)
                }).catch(err => reject(err))
            })
        },

        getPostsInAlbum(albumId) {
            return new Promise((resolve, reject) => {
                http.get(API.endpoints.ALBUM_GET_POSTS.replace(':id', albumId)).then(res => {

                    let posts = []
                    for (let i in res) {
                        posts.push(Models.Post.fromJson(res[i]))
                    }

                    resolve(posts)
                }).catch(err => reject(err))
            })
        },

        getPostData(postId) {
            return new Promise((resolve, reject) => {
                http.get(API.endpoints.POSTS_GET.replace(':id', postId)).then(data => {
                    if (!data) {
                        console.log(data)
                        reject(null)
                    } else {
                        resolve(Models.Post.fromJson(data))
                    }
                }).catch(err => {
                    reject(err)
                })
            })
        },

        getPostComments(postId) {
            return new Promise((resolve, reject) => {

                http.get(API.endpoints.COMMENTS_GET_FOR_POST.replace(':id', postId)).then(data => {
                    let posts = []

                    if (data.length > 0) {
                        for (let i = 0; i < data.length; i++) {
                            posts.push(Models.PostComment.fromJson(data[i]))
                        }
                    }

                    resolve(posts)
                }).catch(err => {
                    reject(err)
                })

            })
        },

        createPost(title, description, extension, albumId, base64contents){
            return new Promise((resolve, reject) => {
                http.post(API.endpoints.POSTS_CREATE, {
                    extension,
                    description,
                    albumId,
                    photoName: title,
                    encodedPhotoContents: base64contents
                }).then(res => {
                    if (res.referenceId) {
                        // All succesfull, return the post id
                        resolve(res.referenceId)
                    } else {
                        // Something went wrong
                        reject('Unknown error')
                    }
                }).catch(err => {
                    reject(err)
                })
            })
        },

        upvotePost(id){
            return http.put(API.endpoints.UPVOTE_POST.replace(':id', id))
        },

        downvotePost(id){
            return http.put(API.endpoints.DOWNVOTE_POST.replace(':id', id))
        },

        addComment(id, comment){
            return http.post(API.endpoints.ADD_COMMENT, {referenceId: id, commentContents: comment, eventType: 'PHOTO_COMMENT'})
        },

        deletePost(id) {
            return http.del(API.endpoints.POST_DELETE.replace(':id', id))
        }

    }

})()