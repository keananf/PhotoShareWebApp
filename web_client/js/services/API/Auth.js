(function () {

    let http = API.http

    window.API.Auth = {
        login(username, password){
            return new Promise((resolve, reject) => {
                //resolve(null)
                resolve(Models.User.fromJson({'username': username}))
            })
        },

        register(username, password){
            return new Promise((resolve, reject) => {
                // Debug
                return resolve(true)

                http.post(API.endpoints.USERS_CREATE, {username: username, password: password}).then(res => {
                    if (res.success) {
                        resolve(true)
                    } else {
                        reject('Unknown error')
                    }
                }).catch(e => {
                    reject('Unknown error')
                })

            })
        }
    }

})()