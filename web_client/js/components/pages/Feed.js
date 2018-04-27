(function () {

    window.Components.Pages.Feed = {
        template: `<div>

    <div class="row">
        <div class="col-sm-8 col-md-9">
            <loader ref="posts-loader"></loader>
            
            <div v-if="posts">
                <post v-for="post in posts" :data="post.toJson()" :key="post.id"></post>
            </div>
            
            <div v-if="posts !== null && posts.length === 0">
                <p class="lead">No posts in your network</p>
            </div>
        </div>
        <div class="col-sm-4 col-md-3">

            <div class="side-panel">
                <div class="panel-heading">
                    Following
                </div>
                <ul v-if="following">
                    <li v-for="user in following">
                        <router-link :to="'/user/' + user.username">{{ user.username }}</router-link>
                    </li>
                </ul>
                <p v-if="following !== null && following.length === 0">
                    You are not following anyone
                </p>
            </div>

            <div class="side-panel">
                <div class="panel-heading">
                    Your Followers
                </div>
                <ul v-if="followers">
                    <li v-for="user in followers">
                        <router-link :to="'/user/' + user.username">{{ user.username }}</router-link>
                    </li>
                </ul>
                <p v-if="followers !== null && followers.length === 0">
                    You don't have any followers yet
                </p>
            </div>

        </div>
    </div>


</div>
`,

        data() {
            return {
                posts: null,
                following: null,
                followers: null
            }
        },

        methods: {
            refreshFeed() {
                let loader = this.$refs['posts-loader']
                loader.show()

                this.posts = []

                // Get posts from the API
                API.Posts.getNewsFeedForUser(this.$root.auth().username).then(posts => {
                    this.posts = posts
                    loader.hide()
                }).catch(e => {
                    this.$root.error(e)
                })
            },

            fetchFollowers(){
                API.Users.getUsersFollowers(this.$root.auth().username).then(followers => {
                    this.followers = followers
                })
            },

            fetchFollowing(){
                API.Users.getUsersFollowing(this.$root.auth().username).then(following => {
                    this.following = following
                })
            }
        },

        mounted() {
            this.refreshFeed()
            this.fetchFollowers()
            this.fetchFollowing()
        }
    }

})()