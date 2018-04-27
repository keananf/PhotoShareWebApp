(function () {

    window.Components.Pages.Register = {
        template: `<div>

    <div class="row justify-content-center">
        <div class="col-sm-4 box">

            <div v-if="!success">

                <h3>Register for an account</h3>
                <hr/>

                <div class="form-group">
                    <label>Username</label>
                    <input type="text" class="form-control" v-model="username" @blur="validate"/>
                </div>

                <div class="form-group">
                    <label>Password</label>
                    <input type="password" class="form-control" v-model="password" @blur="validate"/>
                </div>

                <div class="form-group">
                    <label>Repeat Password</label>
                    <input type="password" class="form-control" v-model="password_confirmation" @keyup="validate"/>
                </div>

                <div class="alert alert-danger" v-if="error" v-text="error"></div>

                <loader visible="1" v-if="isLoading"></loader>
                <br/>

                <button class="btn btn-block btn-primary" :disabled="isLoading || error" @click="register">
                    Register
                </button>

            </div>
            <div v-else>
                <div class="alert alert-success">
                    You have registered successfully! <br/>
                    You now may <router-link to="/login">login</router-link>
                </div>
            </div>

        </div>

    </div>

</div>`,

        data() {
            return {
                username: '',
                password: '',
                password_confirmation: '',
                error: null,
                isLoading: false,
                success: null
            }
        },

        methods: {

            register(){
                this.validate()

                if (!this.error) {
                    this.isLoading = true
                    API.Auth.register(this.username, this.password).then(success => {
                        this.success = !!success
                        this.isLoading = false
                    }).catch(err => {
                        this.success = false
                        this.error = err
                        this.isLoading = false
                    })
                }
            },

            validate(){
                if (this.username.length === 0) {
                    this.error = 'Please enter a username'
                } else if (this.password.length === 0) {
                    this.error = 'Please enter a password'
                } else if (this.password !== this.password_confirmation) {
                    this.error = 'Please make sure the passwords match'
                } else {
                    this.error = null
                }
            },
        }
    }

})()