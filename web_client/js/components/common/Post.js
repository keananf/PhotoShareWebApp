(function () {
    window.Components.Common.Post = {

        template: `
        <article class="post">
                <header>
                    <span class="username">{{ username }}</span>
                    <span class="date">{{ date }}</span>
                </header>
                <div class="post-image">
                    <img src="https://img00.deviantart.net/95e7/i/2014/007/d/3/google_abstract_by_dynamicz34-d718hzj.png"/>
                </div>
                <footer>
                    <span class="comments">
                        <router-link to="/">
                            {{ commentsCount }} comments
                        </router-link>
                    </span>
                    <span class="likes">
                        {{ likesCount }}
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
            username(){
                return this.post.username
            },

            commentsCount(){
                return this.post.commentsCount
            },

            likesCount() {
                return this.post.likesCount
            },

            date(){
                return this.post.date
            }
        }

    }
})()