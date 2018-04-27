(function () {

    class PostComment {
        constructor(id, postId, username, comment) {
            this.id = id
            this.postId = postId
            this.username = username
            this.comment = comment
            this._votes = []
        }

        toJson() {
            return {
                id: this.id,
                referenceId: this.postId,
                author: this.username,
                commentContents: this.comment,
                votes: this._votes
            }
        }

        static fromJson(data) {

            if (typeof data !== "object") {
                data = JSON.parse(data)
            }

            let comment = new PostComment(data.id, data.referenceId, data.author, data.commentContents)

            if (data.votes) {
                comment._votes = data.votes
            }

            return comment
        }

        get score() {
            return this._votes.length
        }

        get votes() {
            return this._votes
        }

        userHasUpvoted(username) {
            return this._votes.indexOf(username) !== -1
        }

        addUpvote(username) {
            this._votes.push(username)
        }

        removeUpvote(username) {
            let pos = this._votes.indexOf(username)

            if (~pos) this._votes.splice(pos, 1)
        }
    }


    window.Models.PostComment = PostComment
})()