(function () {

    window.Components.Pages.Post = {
        template: `<div>

    <div class="row">
        <div class="col-12">

            <loader v-show="loading" ref="post-loader"></loader>

            <article class="post" v-if="!loading">
                <header>
                    <span class="username"><router-link :to="'/user/'+post.username">{{ post.username }}</router-link></span>
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
                <ul>
                    <li v-for="comment in comments">
                        <span class="username">
                            <router-link :to="'/user/'+comment.username">
                                {{ comment.username }}
                            </router-link>
                        </span>
                        <p v-text="comment.comment"></p>
                    </li>
                </ul>
                
                <textarea class="form-control" placeholder="Add your comment" rows="2" v-model="newComment"/>
                <br/>
                
                <button class="btn btn-block btn-primary" @click="addComment">Comment</button>
            </div>
            
            <br/><br/>
            
            <button v-if="usersPost" class="btn btn-sm btn-danger" @click="deletePost">Delete your post</button>
            
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
                usersPost: false
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

            toggleVote(){
                this.userHasLiked = !this.userHasLiked

                if (this.userHasLiked) {
                    API.Posts.upvotePost(this.postId)
                    this.post.likesCount++
                } else {
                    API.Posts.downvotePost(this.postId)
                    this.post.likesCount--
                }
            },

            addComment(){
                API.Posts.addComment(this.postId, this.newComment).then(() => {
                    let comment = new Models.PostComment(this.postId, this.$root.auth().username, this.newComment)
                    this.comments.push(comment)
                    this.post.comments = this.comments
                    this.newComment = ''
                })
            },

            deletePost(){
                API.Posts.deletePost(this.postId).then(() => {
                    // Go back to user's profile
                    router.push(this.$root.auth().user.route)
                })
            }
        },

        mounted() {
            this.fetchPostData()
        },

        computed: {
            likesCount(){
                return this.post.likesCount
            },

            commentsCount(){
                return this.post.commentsCount
            }
        }
    }

})()