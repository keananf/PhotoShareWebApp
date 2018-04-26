// Define components use
Vue.component('auth', window.Components.Auth)
Vue.component('post', window.Components.Common.Post)
Vue.component('loader', window.Components.Common.Loader)

// Define the app routes
const routes = [
    {
        path: '/', component: window.Components.Pages.Feed
    },
    {
        path: '/upload', component: window.Components.Pages.Upload
    },
    {
        path: '/user/:username', component: window.Components.Pages.Profile
    },
    {
        path: '/post/:id', component: window.Components.Pages.Post
    },
    {
        path: '/search/:query?', component: window.Components.Pages.Search
    },
    {
        path: '/login', component: window.Components.Pages.Login
    },
    {
        path: '/register', component: window.Components.Pages.Register
    }
]

// Create router instance
const router = new VueRouter({routes})

// Create the Vue app instance and mount it to the DOM object
const app = new Vue({
    router,

    data: {
        searchQuery: ""
    },

    methods: {
        error(e) {
            console.log(e)
        },

        submitSearch() {
            router.push('/search/' + this.searchQuery)
        },

        auth() {
            if (!this.$refs['auth']) return {}
            return this.$refs['auth']
        }
    },

    created(){
        // Redirect to login page if user is not logged in and requiring a non-login route
        if (!this.auth().isLoggedIn && this.$route.fullPath !== "/login" && this.$route.fullPath !== "/register") {
            router.push('/login')
        }
    }

}).$mount('#photoshare-app')