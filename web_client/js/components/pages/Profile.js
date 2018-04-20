(function () {

    window.Components.Pages.Profile = {
        template: `<div>

    <h3>{{ username}}</h3>
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
            <i :class="{far: !isFollowing, fas: isFollowing, 'fa-star':true}"></i>
            Follow
        </button>
    
    </div>
    
    <br/><br/>
    
    <div class="row">
    <div class="col-sm-8">
    
    <loader ref="posts-loader"></loader>
    
    <div class="user-posts">
        
        <post v-for="post in posts" :data="post" :key="post.id"></post>
    
    </div>
</div>
</div>
    
    

</div>`,

        data() {
            return {
                username: this.$route.params.username,
                followersCount: 0, // @todo
                followingCount: 0, // @todo
                posts: [], // @todo
                isFollowing: false
            }
        },

        methods: {
            toggleFollow(){
                this.isFollowing = !this.isFollowing

                if (this.isFollowing) {
                    this.followersCount++
                    API.Users.followUser(this.username)
                } else {
                    this.followersCount--
                    API.Users.unfollowUser(this.username)
                }
            },

            fetchUsersPosts(){

                let loader = this.$refs['posts-loader']
                loader.show()

                API.Posts.getPostsByUser(this.username).then(posts => {
                    this.posts = posts
                    loader.hide()
                })
            }
        },

        // Whenever a user profile is loaded
        // call this and fetch initial API data
        mounted() {
            this.fetchUsersPosts()
        }
    }

})()