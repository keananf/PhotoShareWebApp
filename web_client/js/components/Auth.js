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
            },

            logoutUser(){
                this.currentUser = null

                // Take user to the landing page
                router.push('/')
            }
        },

        computed: {
            isLoggedIn(){
                return !!this.currentUser
            }
        }
    }

})()