(function () {
    window.Components.Common.PostComment = {

        template: `
        <div class="col-12 box">
            <div class="row">
                <div class="col-10">
                    <span class="username">
                        <router-link :to="'/user/'+comment.username">
                            {{ comment.username }}
                        </router-link>
                    </span>
                    <p v-text="comment.comment"></p>
                </div>
                <div class="col-2 text-right">
                    <button :class="voteClass" @click="toggleVote"><i class="fa fa-thumbs-up"></i></button>
                    <span class="badge badge-warning">{{ score }}</span>
                </div>
            </div>
        </div>
        `,

        props: ['data'],

        data() {
            return {
                comment: Models.PostComment.fromJson(this.data),
                userHasUpvoted: false,
                score: 0
            }
        },

        methods: {
            toggleVote() {
                this.userHasUpvoted = !this.userHasUpvoted

                if (this.userHasUpvoted) {
                    this.comment.addUpvote(this.$root.auth().username)
                    this.score++
                    API.Posts.upvoteComment(this.comment.id)
                } else {
                    this.comment.removeUpvote(this.$root.auth().username)
                    this.score--
                    API.Posts.downvoteComment(this.comment.id)
                }

                this.$forceUpdate()
            }
        },

        computed: {
            voteClass() {
                return {
                    'btn': true,
                    'btn-sm': true,
                    'btn-primary': this.userHasUpvoted
                }
            }
        },

        created() {
            this.score = this.comment.score
            this.userHasUpvoted = this.comment.userHasUpvoted(this.$root.auth().username)
        }

    }
})()