(function () {

    window.Components.Pages.Feed = {
        template: `<div>

    <div class="row">
        <div class="col-sm-8 col-md-9">
            <loader ref="posts-loader"></loader>
            <post v-for="post in posts" :data="post" :key="post.id"></post>
        </div>
        <div class="col-sm-4 col-md-3">

            <div class="side-panel">
                <div class="panel-heading">
                    Following
                </div>
                <ul>
                    <li>
                        <router-link to="/user/username1">username1</router-link>
                    </li>
                    <li>
                        <router-link to="/user/username2">username2</router-link>
                    </li>
                    <li>
                        <router-link to="/user/username3">username3</router-link>
                    </li>
                    <li>
                        <router-link to="/user/username4">username4</router-link>
                    </li>
                    <li class="muted">
                        <router-link to="/">view all..</router-link>
                    </li>
                </ul>
            </div>

            <div class="side-panel">
                <div class="panel-heading">
                    Your Followers
                </div>
                <ul>
                    <li>
                        <router-link to="/user/username1">username1</router-link>
                    </li>
                    <li>
                        <router-link to="/user/username2">username2</router-link>
                    </li>
                    <li>
                        <router-link to="/user/username3">username3</router-link>
                    </li>
                    <li>
                        <router-link to="/user/username4">username4</router-link>
                    </li>
                    <li class="muted">
                        <router-link to="/">view all..</router-link>
                    </li>
                </ul>
            </div>

        </div>
    </div>


</div>
`,

        data() {
            return {
                posts: []
            }
        },

        methods: {
            refreshFeed() {
                let loader = this.$refs['posts-loader']
                loader.show()

                this.posts = []

                // Get posts from the API
                API.Posts.getNewsFeedForUser("CURRENT_USER").then(posts => {
                    this.posts = posts
                    loader.hide()
                }).catch(e => {
                    this.$root.error(e)
                })
            }
        },

        mounted() {
            this.refreshFeed()
        }
    }

})()