(function () {

    window.Components.Pages.Login = {
        template: `<div>

    <div class="row justify-content-center">
        <div class="col-sm-4 box">

            <div v-if="!authSuccess">

                <h3>Login</h3>
                <hr/>

                <div class="form-group">
                    <label>Username</label>
                    <input type="text" class="form-control" v-model="username" @blur="validate"/>
                </div>

                <div class="form-group">
                    <label>Password</label>
                    <input type="password" class="form-control" v-model="password" @keyup="validate"/>
                </div>

                <div class="alert alert-danger" v-if="error" v-text="error"></div>

                <div class="alert alert-danger" v-if="!authSuccess && authSuccess !== null">
                    Could not login - please check your username and password
                </div>

                <loader visible="1" v-if="isLoading"></loader>
                <br/>

                <button class="btn btn-block btn-primary" :disabled="isLoading || error" @click="login">
                    <i class="fa fa-sign-in-alt"></i>
                    Login
                </button>

            </div>
            <div v-else>
                <div class="alert alert-success text-center">
                    Logged in successfully!
                </div>
            </div>

        </div>

    </div>

</div>`,

        data() {
            return {
                username: '',
                password: '',
                error: null,
                authSuccess: null,
                isLoading: false
            }
        },

        methods: {
            login(){
                this.validate()

                if (!this.error) {
                    this.isLoading = true

                    API.Auth.login(this.username, this.password).then((user) => {

                        this.authSuccess = true
                        this.postLogin()
                        this.$root.auth().loginUser(user)

                        this.isLoading = false
                    }).catch(e => {
                        this.authSuccess = false
                        this.isLoading = false
                        console.log(e)
                    })
                }
            },

            validate(){
                if (this.username.length === 0) {
                    this.error = 'Please enter your username'
                } else if (this.password.length === 0) {
                    this.error = 'Please enter your password'
                } else {
                    this.error = null
                }
            },

            postLogin(){
                // After successful login take the user to feed page
                router.push('/')
            }
        }
    }

})()