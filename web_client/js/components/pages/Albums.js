(function () {

    window.Components.Pages.Albums = {
        template: `<div>

    <div class="row justify-content-center">
        <div class="col-sm-8 box">

            <h2>Your Albums</h2>

            <hr/>

            <div class="alert alert-danger" v-if="albums === null">
                Could not fetch your albums!
            </div>

            <div class="text-center" v-if="albums.length === 0">
                <p>
                    You have no albums created
                </p>

                <hr/>
            </div>

            <ul v-if="albums.length > 0">
                <li v-for="album in albums" :key="album.id">
                    <router-link :to="'/album/' + album.id">{{ album.name }}</router-link>
                </li>
            </ul>

            <div class="text-center">
                <button class="btn btn-primary btn-sm" @click="openNewAlbumForm()">
                    <i class="fa fa-plus"></i>
                    Create new album
                </button>
            </div>

        </div>
    </div>

    <modal title="Create new album" ref="newAlbumModal">

        <input class="form-control" v-model="newAlbum.title" placeholder="Album name"/>
        <br/>
        
        <textarea class="form-control" v-model="newAlbum.description" placeholder="Album description"/>

        <hr/>

        <button @click="createAlbum" class="btn btn-block btn-success">Create album</button>

    </modal>


</div>
`,

        data() {
            return {
                albums: [],
                newAlbum: {
                    title: '',
                    description: ''
                }
            }
        },

        methods: {
            openNewAlbumForm() {
                this.$refs['newAlbumModal'].open()
            },

            createAlbum(){

                let user = this.$root.auth().user

                API.Albums.create(this.newAlbum.title, this.newAlbum.description).then(albumId => {
                    user.addAlbum(new Models.Album(albumId, this.newAlbum.title, this.newAlbum.description, user.username, moment().unix()))
                    user.getAlbums().then(albums => this.albums)
                    this.$refs['newAlbumModal'].close()
                }).catch(err => {
                    alert(err)
                })
            }
        },

        mounted() {
            // If user doesn't have albums set, fetch those
            let user = this.$root.auth().user
            user.getAlbums().then(albums => {
                this.albums = albums
            }).catch(err => this.albums = null)
        }
    }

})()