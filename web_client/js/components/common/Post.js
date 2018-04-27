(function () {
    window.Components.Common.Post = {

        template: `
        <article class="post">
                <header>
                    <span class="username">
                        <router-link :to="'/user/'+post.username">
                            {{ post.username }}
                        </router-link>
                    </span>
                    <span class="date">{{ post.friendlyDate }}</span>
                </header>
                <div class="post-image">
                    <router-link :to="route">
                        <img :src="post.filename"/>
                    </router-link>
                </div>
                <footer>
                    <span class="comments">
                        <router-link :to="route">
                            Comment
                        </router-link>
                    </span>
                    <span :class="{likes: true, liked: userHasLiked}" @click="toggleVote()">
                        {{ likesCount }}
                        <button title="Like">
                            <i class="fa fa-heart"></i>
                        </button>
                    </span>
                </footer>
            </article>
        `,

        props: ['data'],

        data() {
            return {
                post: Models.Post.fromJson(this.data),
                userHasLiked: Models.Post.fromJson(this.data).userHasUpvoted(this.$root.auth().username)
            }
        },

        methods: {
            toggleVote(){
                this.userHasLiked = !this.userHasLiked

                if (this.userHasLiked) {
                    API.Posts.upvotePost(this.post.id)
                    this.post.likesCount++
                } else {
                    API.Posts.downvotePost(this.post.id)
                    this.post.likesCount--
                }
            }
        },

        computed: {
            route() {
                return this.post.route
            },

            likesCount(){
                return this.post.likesCount
            }
        }

    }
})()