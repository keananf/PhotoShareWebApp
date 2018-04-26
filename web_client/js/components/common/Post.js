(function () {
    window.Components.Common.Post = {

        template: `
        <article class="post">
                <header>
                    <span class="username">
                        <router-link :to="'/user/'+post.username">
                            {{ post.username }}
                        </router-link>
                    </span>
                    <span class="date">{{ post.friendlyDate }}</span>
                </header>
                <div class="post-image">
                    <router-link :to="route">
                        <img :src="post.filename"/>
                    </router-link>
                </div>
                <footer>
                    <span class="comments">
                        <router-link to="/">
                            {{ post.commentsCount }} comments
                        </router-link>
                    </span>
                    <span class="likes">
                        {{ post.likesCount }}
                        <button title="Like">
                            <i class="fa fa-heart"></i>
                        </button>
                    </span>
                </footer>
            </article>
        `,

        props: ['data'],

        data() {
            return {
                post: Models.Post.fromJson(this.data)
            }
        },

        computed: {
            route() {
                return this.post.route
            },
        }

    }
})()