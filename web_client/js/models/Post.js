(function () {

    class Post {
        constructor(id, title, username, date, extension, description) {
            this.id = id
            this.title = title
            this.username = username
            this.date = date
            this.extension = extension
            this.description = description

            this.albumId = null
            this._comments = null
            this._likes = null
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
                data.photoTime,
                data.ext,
                data.description)

            if (data.comments) {
                post.comments = data.comments
                post.commentsCount = data.comments.length
            }

            if (data.commentsCount) {
                post.commentsCount = data.commentsCount
            }

            if (data.likes) {
                post.likes = data.likes
                post.likesCount = data.likes.length
            }

            if (data.votes !== undefined) {
                post.likes = data.votes
                post.likesCount = data.votes.length
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
                photoTime: this.date,
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
            this._commentsCount = comments.length
        }

        get commentsCount() {
            if (this._commentsCount !== null) {
                return this._commentsCount
            }

            if (this._comments) {
                return this._comments.length
            }

            return 0
        }

        set commentsCount(count) {
            this._commentsCount = count
        }

        set likes(likes) {
            this._likes = likes
        }

        get likes() {
            return this._likes
        }

        get likesCount() {
            if (this._likesCount !== null) {
                return this._likesCount
            }

            if (this._likes) {
                return this._likes.length
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
            return '/photoshare/photos/content/' + this.extension + '/' + this.id + '.' + this.extension
        }

        get friendlyDate() {
            return moment(this.date).fromNow()
        }

        userHasUpvoted(username) {
            return this.likes && this.likes.indexOf(username) !== -1
        }
    }


    window.Models.Post = Post
})()