(function () {

    let http = API.http

    window.API.Auth = {
        login(username, password){
            return new Promise((resolve, reject) => {
                // Debug
                resolve(Models.User.fromJson({'username': username}))
                return;

                http.post(API.endpoints.USERS_LOGIN, {username: username, password: password}).then(res => {
                    if (!res.error && res.user) {
                        resolve(Models.User.fromJson(res.user))
                    } else {
                        reject(res.error)
                    }
                }).catch(err => {
                    reject(err)
                })
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