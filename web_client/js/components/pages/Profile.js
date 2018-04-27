(function () {

    window.Components.Pages.Profile = {
        template: `<div>

    <div class="row">
        <div class="col-sm-8">
        
            <loader ref="user-loader" visible="1" v-if="!userDataLoaded"></loader>
    
            <div v-if="userDataLoaded">
                <h3>{{ username }}</h3>
                <hr/>
            
                <div class="user-data">
            
                    <span class="btn btn-sm">
                        <i class="fa fa-users"></i> {{ followingCount }} Following
                    </span>
            
                    <span class="btn btn-sm">
                        <i class="fa fa-star"></i> {{ followersCount }} Followers
                    </span>
            
                    <button v-if="!usersProfile" :class="{btn: true, 'btn-sm': true, 'btn-success': isFollowing}"
                            @click="toggleFollow">
                        <i class="fas fa-star"></i>
                        Follow{{ isFollowing ? 'ing' : '' }}
                    </button>
            
                </div>
            </div>
            
            <br/><br/>

            <div class="user-posts">

                <post v-for="post in posts" :data="post.toJson()" :key="post.id"></post>

            </div>
        </div>
        
        <div class="col-sm-4" v-if="userDataLoaded">
            <div class="side-panel">
                <div class="panel-heading">
                    Following
                </div>
                <ul v-if="following">
                    <li v-for="user in following">
                        <router-link :to="'/user/' + user.username">{{ user.username }}</router-link>
                    </li>
                </ul>
                <p v-if="following.length === 0">
                    {{ username }} is not following anyone
                </p>
            </div>

            <div class="side-panel">
                <div class="panel-heading">
                    Followers
                </div>
                <ul v-if="followers">
                    <li v-for="user in followers">
                        <router-link :to="'/user/' + user.username">{{ user.username }}</router-link>
                    </li>
                </ul>
                <p v-if="following.length === 0">
                    {{ username }} hasn't got any followers yet
                </p>
            </div>
        </div>
    </div>


</div>`,

        data() {
            return {
                username: this.$route.params.username,
                usersProfile: this.$route.params.username === this.$root.auth().username,
                followersCount: 0,
                followingCount: 0,
                followers: [],
                following: [],
                posts: [],
                isFollowing: false,
                loaded: {
                    posts: false,
                    followers: false,
                    following: false
                },
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