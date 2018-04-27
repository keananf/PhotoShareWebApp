(function () {

    window.Components.Pages.Post = {
        template: `<div>

    <div class="row">
        <div class="col-12">

            <loader v-show="loading" ref="post-loader"></loader>

            <article class="post" v-if="!loading">
                <header>
                    <span class="username"><router-link
                            :to="'/user/'+post.username">{{ post.username }}</router-link></span>
                    <span class="date">{{ post.friendlyDate }}</span>
                    <hr/>
                    <h5>{{ post.title }}</h5>
                </header>
                <div class="post-image">
                    <img :src="post.filename"/>
                </div>
                <footer>
                    <p>
                        {{ post.description }}
                    </p>

                    <hr/>
                    <span class="comments">
                        {{ commentsCount }} comments
                    </span>
                    <span :class="{likes: true, liked: userHasLiked}" @click="toggleVote()">
                        {{ likesCount }}
                        <button title="Like">
                            <i class="fa fa-heart"></i>
                        </button>
                    </span>
                </footer>
            </article>
            <div class="post-comments" v-if="!loading">

                <post-comment v-for="comment in comments" :data="comment.toJson()" @delete="removeComment"></post-comment>
                <br/>

                <textarea class="form-control" placeholder="Add your comment" rows="2" v-model="newComment"/>
                <br/>

                <button class="btn btn-block btn-primary" @click="addComment">Comment</button>
            </div>

            <br/><br/>

            <button v-if="usersPost" class="btn btn-sm btn-danger" @click="deletePost">Delete your post</button>
            <button v-if="$root.auth().user.isAdmin && !usersPost" class="btn btn-sm btn-danger"
                    @click="adminDeletePost">Delete this post
            </button>

            <br/><br/>

        </div>
    </div>


</div>
`,

        data() {
            return {
                postId: this.$route.params.id,
                post: null,
                comments: [],
                loading: true,
                userHasLiked: false,
                newComment: '',
                usersPost: false // Viewing user owns this post
            }
        },

        methods: {
            fetchPostData() {
                this.loading = true
                let loader = this.$refs['post-loader']
                loader.show()

                API.Posts.getPostData(this.postId).then(post => {
                    this.post = post

                    this.usersPost = post.username === this.$root.auth().username

                    post.getComments().then(comments => {
                        this.comments = comments
                        this.post.comments = comments
                        this.loading = false
                    })

                    // Check if user has voted
                    if (this.post.userHasUpvoted(this.$root.auth().username)) {
                        this.userHasLiked = true
                    }

                })
            },

            toggleVote() {
                this.userHasLiked = !this.userHasLiked

                if (this.userHasLiked) {
                    API.Posts.upvotePost(this.postId)
                    this.post.likesCount++
                } else {
                    API.Posts.downvotePost(this.postId)
                    this.post.likesCount--
                }
            },

            addComment() {
                API.Posts.addComment(this.postId, this.newComment).then((id) => {
                    let comment = new Models.PostComment(id, this.postId, this.$root.auth().username, this.newComment)
                    this.comments.push(comment)
                    this.post.comments = this.comments
                    this.newComment = ''
                })
            },

            deletePost() {
                API.Posts.deletePost(this.postId).then(() => {
                    // Go back to own user's profile
                    router.push(this.$root.auth().user.route)
                })
            },

            adminDeletePost() {
                API.Posts.adminDeletePost(this.postId).then(() => {
                    // Go back to the user's profile
                    router.push('/user/' + this.post.username)
                })
            },

            removeComment(id) {
                let pos = null

                for (let i = 0; i < this.comments.length; i++) {
                    if (this.comments[i].id === id) {
                        pos = i
                        break
                    }
                }

                if (pos !== null) {
                    this.comments.splice(pos, 1)
                }

                this.post.commentsCount--
            }
        },

        mounted() {
            this.fetchPostData()
        },

        computed: {
            likesCount() {
                return this.post.likesCount
            },

            commentsCount() {
                return this.post.commentsCount
            }
        }
    }

})()