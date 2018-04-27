(function () {

    window.Components.Pages.Search = {
        template: `<div>

    <h3 v-if="hasQuery">Searching for "{{ query }}"</h3>
    <h6 v-else>Please enter a search query</h6>

    <div v-if="hasQuery">
        <hr/>

        <loader ref="results-loader"></loader>

        <div v-if="!isLoading">
            <div v-if="results.length === 0">
                <i class="fa fa-frown"></i>
                No results found
            </div>
            <div v-else>
                <ul>
                    <li v-for="user in results">
                        <router-link :to="user.route">
                            {{ user.username }}
                        </router-link>
                    </li>
                </ul>
            </div>
        </div>
    </div>


</div>`,

        data() {
            return {
                query: this.$route.params.query,
                results: [],
                isLoading: true
            }
        },

        methods: {
            performSearch() {
                if (!this.hasQuery) {
                    // No query, nothing to perform
                    return
                }

                this.$refs['results-loader'].show()
                this.isLoading = true

                API.Users.searchByQuery(this.query).then(results => {
                    this.results = results
                    this.isLoading = false
                    this.$refs['results-loader'].hide()
                })
            }
        },

        computed: {
            hasQuery() {
                return this.query && this.query.length > 0
            }
        },

        mounted() {
            this.performSearch()
        }
    }

})()