(function () {

    window.Components.Pages.Post = {
        template: `<div>

    <div class="row">
        <div class="col-12">

            <loader v-show="loading" ref="post-loader"></loader>

            <article class="post" v-if="!loading">
                <header>
                    <span class="username"><router-link :to="'/user/'+post.username">{{ post.username }}</router-link></span>
                    <span class="date">{{ post.date }}</span>
                </header>
                <div class="post-image">
                    <img :src="post.filename"/>
                </div>
                <footer>
                    <span class="comments">
                        {{ post.commentsCount }} comments
                    </span>
                    <span class="likes">
                        {{ post.likesCount }}
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
            </div>
        </div>
    </div>


</div>
`,

        data() {
            return {
                postId: this.$route.params.id,
                post: null,
                comments: [],
                loading: true
            }
        },

        methods: {
            fetchPostData(){
                this.loading = true
                let loader = this.$refs['post-loader']
                loader.show()

                API.Posts.getPostData(this.postId).then(post => {
                    this.post = post

                    post.getComments().then(comments => {
                        this.comments = comments
                        this.loading = false
                    })

                })
            }
        },

        mounted(){
            this.fetchPostData()
        }
    }

})()