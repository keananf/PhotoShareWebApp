(function () {

    window.Components.Pages.Album = {
        template: `<div>

    <div class="row justify-content-center" v-if="album && posts !== null">
        <div class="col-sm-8">

            <h1>{{ album.name }}</h1>
            <hr/>
            <p class="lead" v-if="!editingDescription">{{ album.description }}</p>
            
            <div v-if="editingDescription">
                <textarea class="form-control" v-model="album.description"></textarea>
                <br/>
                
                <button class="btn btn-success btn-sm" @click="updateDescription">Save</button>
            </div>
            
            <button v-if="usersAlbum && !editingDescription" class="btn btn-link btn-sm" @click="editingDescription = true">Edit Description</button>
            
            <hr/>
            <small>By <strong>{{ album.author }}</strong></small>
            
            <br/><br/>
            
            <div v-if="posts.length === 0">
                This album is empty
            </div>
            <div v-else>
                <post v-for="post in posts" :data="post.toJson()" :key="post.id"></post>
            </div>

        </div>
    </div>

</div>
`,

        data() {
            return {
                albumId: this.$route.params.id,
                album: null,
                posts: null,
                usersAlbum: false,
                editingDescription: false
            }
        },

        methods: {
            fetchAlbumData(){
                API.Albums.getById(this.albumId).then(album => {
                    this.album = album
                    this.usersAlbum = album.author === this.$root.auth().username
                })
            },

            fetchAlbumPosts(){
                API.Posts.getPostsInAlbum(this.albumId).then(posts => {
                    this.posts = posts
                })
            },

            updateDescription(){
                API.Albums.updateDescription(this.albumId, this.album.description)
                this.editingDescription = false
            }
        },

        mounted() {
            this.fetchAlbumData()
            this.fetchAlbumPosts()
        }
    }

})()