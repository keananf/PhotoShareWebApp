(function () {

    class Post {
        constructor(id, title, username, date, extension) {
            this.id = id
            this.title = title
            this.username = username
            this.date = date
            this.extension = extension

            this.albumId = null
            this._comments = null
            this.commentsCount = 0
            this.likesCount = 0
        }

        static fromJson(data) {

            if (typeof data !== "object") {
                data = JSON.parse(data)
            }

            let post = new Post(
                data.id,
                data.photoName,
                data.authorName,
                data.photoTime / 1000, // Transforms milliseconds to seconds
                data.ext)

            if (data.comments) {
                post.comments = data.comments
            }

            if (data.commentsCount) {
                post.commentsCount = data.commentsCount
            }

            if (data.likes) {
                post.likes = data.likes
            }

            if (data.likesCount) {
                post.likesCount = data.likesCount
            }

            if (data.albumId) {
                post.albumId = data.albumId
            }

            return post
        }

        toJson() {
            return {
                id: this.id,
                photoName: this.title,
                authorName: this.username,
                photoTime: this.date * 1000, // Transform seconds to millisconds
                albumId: this.albumId,
                ext: this.extension,
                comments: this._comments,
                likes: this._likes,
            }
        }

        getComments() {
            return new Promise((resolve, reject) => {
                if (this._comments === null) {
                    API.Posts.getPostComments(this.id).then(comments => {
                        this._comments = comments
                        resolve(this._comments)
                    })
                } else {
                    resolve(this._comments)
                }
            })
        }

        set comments(comments) {
            this._comments = comments
        }

        get commentsCount() {
            if (this._comments) {
                return this._comments.length
            }

            if (this._commentsCount) {
                return this._commentsCount
            }

            return 0
        }

        set commentsCount(count) {
            this._commentsCount = count
        }

        get likesCount() {
            if (this._likes) {
                return this._likes.length
            }

            if (this._likesCount) {
                return this._likesCount
            }

            return 0
        }

        set likesCount(count) {
            this._likesCount = count
        }

        get route() {
            return '/post/' + this.id
        }

        get filename() {
            return '/photos/content/' + this.extension + '/' + this.id + '.' + this.extension
        }

        get friendlyDate(){
            return moment(this.date).fromNow()
        }
    }


    window.Models.Post = Post
})()