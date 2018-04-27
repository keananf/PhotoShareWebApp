(function () {

    window.Components.Pages.Upload = {
        template: `<div>

    <div v-if="albums.length === 0" class="text-center">
        <p class="lead">
            You don't have any albums, please <router-link to="/albums">create one</router-link> first
        </p>
    </div>
    <div v-else>

    <div class="row justify-content-center">
        <div class="col-sm-6 text-center box">

            <h2>Create a post</h2>

            <hr/>

            <input v-if="!filePreview" type="file" accept="image/*" @change="selectFile" class="form-control"/>

            <img v-if="filePreview" :src="filePreview" class="img-fluid"/>
            <button class="btn btn-link" v-if="filePreview" @click="reset">Upload new picture</button>

        </div>
    </div>
    <div class="row justify-content-center" v-if="step >= 2">
        <div class="col-sm-6 text-center box">
            
            <label>Select album</label>
            <select class="form-control" v-model="albumId">
                <option v-for="album in albums" :value="album.id" v-text="album.name"></option>
            </select>
            
            <router-link to="/albums" class="btn btn-sm btn-link">Manage your albums</router-link>
            
        </div>
    </div>
    <div class="row justify-content-center" v-if="step >= 2">
        <div class="col-sm-6 text-center box">
            
            <label>Photo title</label>
            <input type="text" class="form-control" v-model="title"/>
            
            <hr/>
            
            <label>Description</label>
            <textarea class="form-control" v-model="description"></textarea>
            
        </div>
    </div>
    <div class="row justify-content-center">
        <div class="col-sm-6 text-center box">
            <loader visible="1" v-if="isLoading"></loader>
        </div>
    </div>
    <div class="row justify-content-center" v-if="error">

        <div class="col-sm-6 text-center box">
            <div class="alert alert-danger" v-text="error"></div>
        </div>
    </div>
    <div class="row justify-content-center">
        <div class="col-sm-6 text-center box">
            <button class="btn btn-block btn-lg btn-info" :disabled="isLoading" @click="savePost">
                Upload Post
            </button>
        </div>
    </div>
</div>

</div>

</div>`,

        data() {
            return {
                step: 1,
                filePreview: null,
                file: null,
                error: null,
                isLoading: false,
                albumId: null,
                title: '',
                description: '',
                albums: []
            }
        },

        methods: {
            selectFile(e) {
                this.filePreview = null
                this.error = null

                // Get the File selected
                let file = e.target.files[0]

                // Get Base64 encoding of the file
                let reader = new FileReader()
                reader.readAsDataURL(file)

                reader.onload = () => {

                    // Get the file extension
                    let extension = null
                    if (file.type === 'image/jpeg' || file.type === 'image/jpg') {
                        extension = 'jpg'
                    } else if (file.type === 'image/png') {
                        extension = 'png'
                    } else {
                        this.error = 'Please select a valid JPG or PNG image'
                    }

                    if (!this.error) {
                        this.file = {
                            name: file.name,
                            extension: extension
                        }

                        this.title = file.name

                        this.filePreview = reader.result
                        this.step = 2
                    }
                }

                reader.onerror = (err) => {
                    console.log(err)
                }
            },

            savePost() {
                this.validate()

                if (!this.error) {
                    this.isLoading = true

                    // Send the request
                    API.Posts.createPost(this.title, this.description, this.file.extension, this.albumId, this.filePreview).then(id => {
                        if (!id) {
                            this.error = 'Could not save your post, please try again'
                        } else {
                            router.push('/post/' + id)
                        }

                        this.isLoading = false

                    }).catch(err => {
                        this.error = err ? err : 'Could not save your post, please try again'
                        this.isLoading = false
                    })
                }
            },

            validate(){
                if (this.filePreview === null) {
                    this.error = 'Please select a picture'
                } else if (this.file === null || this.file.extension === null) {
                    this.error = 'Pleas select a valid JPG or PNG file'
                } else if (this.albumId === null) {
                    this.error = 'Please select an album'
                } else {
                    this.error = null
                }
            },

            reset() {
                this.step = 1
                this.filePreview = null
            }
        },

        mounted(){
            this.$root.auth().user.getAlbums().then(albums => {
                this.albums = albums
            })
        }
    }

})()