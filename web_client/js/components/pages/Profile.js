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
    
    </div>
    
    <br/><br/>
    
    <div class="row">
    <div class="col-sm-8">
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
                posts: [] // @todo
            }
        }
    }

})()