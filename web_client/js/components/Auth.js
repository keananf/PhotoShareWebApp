(function () {

    window.Components.Auth = {
        template: `<div></div>`,

        data() {
            return {
                currentUser: null
            }
        },

        methods: {
            loginUser(user){
                this.currentUser = user

                // Set up the headers on HttpWrapper class (used for all API calls)
                HW.setAuthParameters(user.username, user.password)
            },

            logoutUser(){
                this.currentUser = null

                // Reset the HttpWrapper class auth
                HW.unsetAuthParameters()

                // Take user to the login page
                router.push('/login')
            }
        },

        computed: {
            isLoggedIn(){
                return !!this.currentUser
            },

            user(){
                return this.currentUser
            },

            username(){
                return this.currentUser.username
            }
        }
    }

})()