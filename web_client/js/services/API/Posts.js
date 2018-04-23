(function () {

    let http = API.getHttpWrapper()

    window.API.Posts = {

        getNewsFeedForUser(username){
            // @todo
            return new Promise((resolve, reject) => {
                setTimeout(() => {
                    resolve([
                        new Models.Post('id1', 'https://newevolutiondesigns.com/images/freebies/abstract-background-preview-1.jpg', 'username1', '8 minutes ago'),
                        new Models.Post('id2', 'https://newevolutiondesigns.com/images/freebies/abstract-background-preview-1.jpg', 'username2', '16 minutes ago'),
                        new Models.Post('id3', 'https://newevolutiondesigns.com/images/freebies/abstract-background-preview-1.jpg', 'username2', '26 minutes ago'),
                    ])
                }, 1000) // Simulate API Call

            })
        },

        getPostsByUser(username){
            // @todo
            return new Promise((resolve, reject) => {
                setTimeout(() => {
                    resolve([
                        new Models.Post('id1', 'https://newevolutiondesigns.com/images/freebies/abstract-background-preview-1.jpg', 'username1', '8 minutes ago'),
                        new Models.Post('id2', 'https://newevolutiondesigns.com/images/freebies/abstract-background-preview-1.jpg', 'username2', '16 minutes ago'),
                        new Models.Post('id3', 'https://newevolutiondesigns.com/images/freebies/abstract-background-preview-1.jpg', 'username2', '26 minutes ago'),
                    ])
                }, 1000) // Simulate API Call

            })
        },

        getPostData(postId){
            // @todo
            return new Promise((resolve, reject) => {
                setTimeout(() => {
                    resolve(Models.Post.fromJson({
                        id: 'testId',
                        filename: 'https://newevolutiondesigns.com/images/freebies/abstract-background-preview-1.jpg',
                        username: 'username',
                        date: '5 minutes ago'
                    }))
                }, 1000) // Simulate API Call
            })
        },

        getPostComments(postId){
            // @todo
            return new Promise((resolve, reject) => {
                setTimeout(() => {
                    resolve([
                        Models.PostComment.fromJson({
                            postId: postId,
                            username: 'test-user',
                            comment: 'This is a test comment'
                        }),
                    ])
                }, 1000) // Simulate API Call
            })
        }

    }

})()