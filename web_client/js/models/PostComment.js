(function () {

    class PostComment {
        constructor(postId, username, comment) {
            this.postId = postId
            this.username = username
            this.comment = comment
        }

        static fromJson(data) {

            if (typeof data !== "object") {
                data = JSON.parse(data)
            }

            return new PostComment(data.postId, data.username, data.comment)
        }
    }


    window.Models.PostComment = PostComment
})()