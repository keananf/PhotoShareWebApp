(function () {

    class Post {
        constructor(id, filename, username, date) {
            this.id = id
            this.filename = filename
            this.username = username
            this.date = date

            this._comments = null
            this._likes = null
        }

        static fromJson(data) {

            if (typeof data !== "object") {
                data = JSON.parse(data)
            }

            let post = new Post(data.id, data.filename, data.username, data.date)

            if (data.comments) {
                post.comments = data.comments
            }

            if (data.commentsCount) {
                post.commentsCount = data.commentsCount
            }

            if (data.likes) {
                post.likes = likes
            }

            if (data.likesCount) {
                post.likesCount = data.likesCount
            }

            return post
        }

        toJson() {
            return JSON.stringify({
                id: this.id,
                filename: this.filename,
                username: this.username,
                date: this.date,
                comments: this._comments,
                likes: this._likes,
            })
        }

        get comments() {
            if (this._comments === null) {
                // Comments are not set
                // Fetch them
                // @todo
            }

            return this._comments
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

            return null
        }

        set commentsCount(count) {
            this._commentsCount = count
        }

        get likes() {
            if (this._likes === null) {
                // Likes are not set
                // Fetch them
                // @todo
            }

            return this._likes
        }

        set likes(likes) {
            this._likes = likes
        }

        get likesCount() {
            if (this._likes) {
                return this._likes.length
            }

            if (this._likesCount) {
                return this._likesCount
            }

            return null
        }

        set likesCount(count) {
            this._likesCount = count
        }
    }


    window.Models.Post = Post
})()