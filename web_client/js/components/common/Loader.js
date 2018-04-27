(function () {
    window.Components.Common.Loader = {

        template: `
<div>
        <div v-if="isVisible" class="text-center">
            <i class="fa fa-spinner fa-spin fa-2x"></i>
        </div>
        </div>
        `,

        props: ['visible'],

        data() {
            return {
                isVisible: !!this.visible
            }
        },

        methods: {
            show() {
                this.isVisible = true
            },

            hide() {
                this.isVisible = false
            }
        }

    }
})()