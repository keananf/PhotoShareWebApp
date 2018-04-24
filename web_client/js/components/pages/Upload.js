(function () {

    window.Components.Pages.Upload = {
        template: `<div>

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
                <option value="0">No album</option>
            </select>
            
        </div>
    </div>
    <div class="row justify-content-center" v-if="step >= 2">
        <div class="col-sm-6 text-center box">
            
            <label>Photo title</label>
            <input type="text" class="form-control" v-model="title"/>
            
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
            <button class="btn btn-block btn-lg btn-info" :disabled="error || isLoading" @click="savePost">
                Upload Post
            </button>
        </div>
    </div>
</div>

</div>`,

        data() {
            return {
                step: 1,
                filePreview: null,
                error: null,
                isLoading: false,
                albumId: 0,
                title: ''
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
                    this.filePreview = reader.result
                    this.step = 2
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
                    API.Posts.createPost(this.title, this.albumId, this.filePreview).then(id => {
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
                } else {
                    this.error = null
                }
            },

            reset() {
                this.step = 1
                this.filePreview = null
            }
        }
    }

})()