(function () {

    let http = API.http

    window.API.Posts = {

        getNewsFeedForUser(username) {
            return new Promise((resolve, reject) => {
                http.get(API.endpoints.POSTS_FEED.replace(':username', username)).then(res => {
                    console.log(res)
                    resolve([])
                }).catch(err => {
                    console.log(err)
                    reject(err)
                })
            })
        },

        getPostsByUser(username) {
            // @todo
            return new Promise((resolve, reject) => {
                http.get(API.endpoints.POSTS_GET_BY_USER.replace(':username', username)).then(res => {
                    console.log(res)
                    let posts = []
                    for (let i in res) {
                        posts.push(Models.Post.fromJson(res[i]))
                    }

                    resolve(posts)
                }).catch(err => reject(err))
            })
        },

        getPostData(postId) {
            // @todo
            return new Promise((resolve, reject) => {
                // Debug
                setTimeout(() => {
                    resolve(Models.Post.fromJson({
                        id: 'testId',
                        filename: 'https://newevolutiondesigns.com/images/freebies/abstract-background-preview-1.jpg',
                        username: 'username',
                        date: '5 minutes ago'
                    }))
                }, 1000) // Simulate API Call

                return

                http.get(API.endpoints.POSTS_GET.replace(':id', postId)).then(data => {
                    if (!data) {
                        // @todo 404?
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
            // @todo
            return new Promise((resolve, reject) => {
                // Debug
                setTimeout(() => {
                    resolve([
                        Models.PostComment.fromJson({
                            referenceId: postId,
                            author: 'test-user',
                            commentContents: 'This is a test comment'
                        }),
                    ])
                }, 1000) // Simulate API Call

                return

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
        }

    }

})()