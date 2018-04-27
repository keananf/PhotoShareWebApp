(function () {

    window.Components.Common.Modal = {
        template: `
<div class="modal fade" tabindex="-1" role="dialog" aria-hidden="true" ref="modal">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">{{ title }}</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <slot></slot>
            </div>
        </div>
    </div>
</div>`,

        props: ['title'],

        methods: {
            open(){
                jQuery(this.$refs['modal']).modal('show')
            },

            close(){
                jQuery(this.$refs['modal']).modal('hide')
            }
        }
    }

})()