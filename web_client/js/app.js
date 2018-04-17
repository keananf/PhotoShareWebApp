// Define the app routes
const routes = [
    {
        path: '/', component: window.Components.Pages.Feed
    },
    {
        path: '/user/:username', component: window.Components.Pages.Profile
    }
]

// Create router instance
const router = new VueRouter({routes})

// Define components use
Vue.component('post', window.Components.Common.Post)

// Create the Vue app instance and mount it to the DOM object
const app = new Vue({
    router
}).$mount('#photoshare-app')