(function () {

    let http = API.http

    window.API.Users = {

        isFollowingUser(username) {
            // @todo
            return new Promise((resolve, reject) => {
                resolve(false)
            })
        },

        followUser(username) {
            // @todo
            return new Promise((resolve, reject) => {
                resolve()
            })
        },

        unfollowUser(username) {
            // @todo
            return new Promise((resolve, reject) => {
                resolve()
            })
        },

        searchByQuery(query) {
            return new Promise((resolve, reject) => {
                setTimeout(() => {
                    resolve([
                        new Models.User('user1'),
                        new Models.User('user2')
                    ])
                }, 1000) // Simulate API call
            })
        },

        getUserData(username) {
            // @todo
            return new Promise((resolve, reject) => {
                setTimeout(() => {
                    resolve(
                        new Models.User(username)
                    )
                }, 1000)
            })
        },

        getUsersFollowers(username){
            // @todo
            return new Promise((resolve, reject) => {
                resolve([])
            })
        },

        getUsersFollowing(username){
            // @todo
            return new Promise((resolve, reject) => {
                resolve([])
            })
        }

    }

})()