(function () {

    let http = API.http

    window.API.Posts = {

        getNewsFeedForUser(username) {
            return new Promise((resolve, reject) => {
                http.get(API.endpoints.POSTS_FEED.replace(':username', username)).then(res => {
                    let posts = []

                    for (let i in res) {
                        let post = Models.Post.fromJson(res[i])
                        posts.push(post)
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
                        let post = Models.Post.fromJson(res[i].photo)
                        post.commentsCount = res[i].childComments.length
                        posts.push(post)
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
                        let post = Models.Post.fromJson(res[i].photo)
                        post.commentsCount = res[i].childComments.length
                        posts.push(post)
                    }

                    resolve(posts)
                }).catch(err => reject(err))
            })
        },

        getPostData(postId) {
            return new Promise((resolve, reject) => {
                http.get(API.endpoints.POSTS_GET.replace(':id', postId)).then(data => {
                    console.log(data)
                    if (!data) {
                        reject(null)
                    } else {
                        resolve(Models.Post.fromJson(data.photo))
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
                            posts.push(Models.PostComment.fromJson(data[i].comment))
                        }
                    }

                    resolve(posts)
                }).catch(err => {
                    reject(err)
                })

            })
        },

        createPost(title, description, extension, albumId, base64contents) {
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

        upvotePost(id) {
            return http.put(API.endpoints.UPVOTE_POST.replace(':id', id))
        },

        downvotePost(id) {
            return http.put(API.endpoints.DOWNVOTE_POST.replace(':id', id))
        },

        addComment(id, comment) {
            return new Promise((resolve, reject) => {
                http.post(API.endpoints.ADD_COMMENT, {
                    referenceId: id,
                    commentContents: comment,
                    eventType: 'PHOTO_COMMENT'
                }).then(res => {
                    resolve(res.referenceId)
                }).catch(err => reject(err))
            })
        },

        deletePost(id) {
            return http.del(API.endpoints.POST_DELETE.replace(':id', id))
        },

        adminDeletePost(id) {
            return http.del(API.endpoints.POST_DELETE_ADMIN.replace(':id', id))
        },

        upvoteComment(id) {
            return http.put(API.endpoints.COMMENT_UPVOTE.replace(':id', id))
        },

        downvoteComment(id) {
            return http.put(API.endpoints.COMMENT_DOWNVOTE.replace(':id', id))
        },

        deleteCommentAdmin(id) {
            return http.del(API.endpoints.COMMENT_DELETE_ADMIN.replace(':id', id))
        }

    }

})()