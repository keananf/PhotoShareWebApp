(function () {
    window.Components.Common.Post = {

        template: `
        <article class="post">
                <header>
                    <span class="username">{{ post.username }}</span>
                    <span class="date">{{ post.date }}</span>
                </header>
                <div class="post-image">
                    <router-link :to="route">
                        <img src="https://img00.deviantart.net/95e7/i/2014/007/d/3/google_abstract_by_dynamicz34-d718hzj.png"/>
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
            route(){
                return this.post.route
            }
        }

    }
})()