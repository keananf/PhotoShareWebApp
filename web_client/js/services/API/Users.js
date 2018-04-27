(function () {

    let http = API.http

    window.API.Users = {

        isFollowingUser(username) {
            // @todo
            return new Promise((resolve, reject) => {
                resolve(false)
            })
        },

        followUser(userFrom, userTo) {
            return new Promise((resolve, reject) => {
                http.put(API.endpoints.FOLLOW_USER.replace(':username', userTo), {userFrom, userTo}).then(() => resolve()).catch(err => reject(err))
            })
        },

        unfollowUser(userFrom, userTo) {
            return new Promise((resolve, reject) => {
                http.del(API.endpoints.UNFOLLOW_USER.replace(':username', userTo), {userFrom, userTo}).then(() => resolve()).catch(err => reject(err))
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
            return new Promise((resolve, reject) => {
                http.get(API.endpoints.USERS_FOLLOWERS.replace(':username', username)).then(res => {

                    console.log(res)

                    let followers = []
                    for (let i in res) {
                        followers.push(Models.User.fromJson(res[i]))
                    }

                    resolve(followers)

                }).catch(err => reject(err))
            })
        },

        getUsersFollowing(username){
            return new Promise((resolve, reject) => {
                http.get(API.endpoints.USERS_FOLLOWING.replace(':username', username)).then(res => {

                    console.log(res)

                    let following = []
                    for (let i in res) {
                        following.push(Models.User.fromJson(res[i]))
                    }

                    resolve(following)

                }).catch(err => reject(err))
            })
        }

    }

})()