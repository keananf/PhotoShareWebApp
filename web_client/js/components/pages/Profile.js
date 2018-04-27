(function () {

    window.Components.Pages.Profile = {
        template: `<div>

    <loader ref="user-loader" visible="1" v-if="!userDataLoaded"></loader>

    <div v-if="userDataLoaded">
        <h3>{{ username }}</h3>
        <hr/>

        <div class="user-data">

            <button class="btn btn-sm">
                <i class="fa fa-users"></i> {{ followingCount }} Following
            </button>

            <button class="btn btn-sm">
                <i class="fa fa-star"></i> {{ followersCount }} Followers
            </button>

            <button :class="{btn: true, 'btn-sm': true, 'btn-success': isFollowing}"
                    @click="toggleFollow">
                <i class="fas fa-star"></i>
                Follow{{ isFollowing ? 'ing' : '' }}
            </button>

        </div>
    </div>

    <br/><br/>

    <div class="row">
        <div class="col-sm-8">

            <div class="user-posts">

                <post v-for="post in posts" :data="post.toJson()" :key="post.id"></post>

            </div>
        </div>
    </div>


</div>`,

        data() {
            return {
                username: this.$route.params.username,
                followersCount: 0,
                followingCount: 0,
                followers: [],
                following: [],
                posts: [], // @todo
                isFollowing: false,
                loaded: {
                    posts: false,
                    followers: false,
                    following: false
                }
            }
        },

        computed: {
            userDataLoaded(){
                return this.loaded.posts && this.loaded.followers && this.loaded.following
            },
        },

        methods: {
            toggleFollow() {
                this.isFollowing = !this.isFollowing

                if (this.isFollowing) {
                    this.followersCount++
                    API.Users.followUser(this.$root.auth().username, this.username)
                } else {
                    this.followersCount--
                    API.Users.unfollowUser(this.$root.auth().username, this.username)
                }
            },

            fetchUsersPosts() {
                API.Posts.getPostsByUser(this.username).then(posts => {
                    this.posts = posts
                    this.loaded.posts = true
                })
            },

            fetchUsersFollowers() {
                API.Users.getUsersFollowers(this.username).then(followers => {
                    this.followers = followers
                    this.followersCount = followers.length
                    this.loaded.followers = true
                })
            },

            fetchUsersFollowing() {
                API.Users.getUsersFollowing(this.username).then(following => {
                    this.following = following
                    this.followingCount = following.length
                    this.loaded.following = true
                })
            }
        },

        // Whenever a user profile is loaded
        // call this and fetch initial API data
        mounted() {
            this.fetchUsersPosts()
            this.fetchUsersFollowers()
            this.fetchUsersFollowing()
        }
    }

})()