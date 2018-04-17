// Define the app routes
const routes = [
    {
        path: '/', component: window.Components.Pages.Feed
    }
]

// Create router instance
const router = new VueRouter({routes})

// Create the Vue app instance and mount it to the DOM object
const app = new Vue({
    router
}).$mount('#photoshare-app')