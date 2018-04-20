// Define the app routes
const routes = [
    {
        path: '/', component: window.Components.Pages.Feed
    },
    {
        path: '/user/:username', component: window.Components.Pages.Profile
    },
    {
        path: '/post/:id', component: window.Components.Pages.Post
    }
]

// Create router instance
const router = new VueRouter({routes})

// Define components use
Vue.component('post', window.Components.Common.Post)
Vue.component('loader', window.Components.Common.Loader)

// Create the Vue app instance and mount it to the DOM object
const app = new Vue({
    router,

    data: {
        isLoading: false
    },

    methods: {
        showLoader(){
            this.isLoading = true
        },

        hideLoader(){
            this.isLoading = false
        },

        error(e){
            console.log(e)
        }
    }

}).$mount('#photoshare-app')