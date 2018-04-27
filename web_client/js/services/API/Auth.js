(function () {

    let http = API.http

    window.API.Auth = {
        login(username, password){
            return new Promise((resolve, reject) => {
                http.post(API.endpoints.USERS_LOGIN, {username: username, password: password}).then(res => {
                    resolve(Models.User.fromJson(res))
                }).catch(e => {
                    if (e instanceof Response && e.status === 401) {
                        reject('Username or password incorrect')
                    } else {
                        reject('Unknown error, please try again')
                    }
                })
            })
        },

        register(username, password){
            return new Promise((resolve, reject) => {
                http.post(API.endpoints.USERS_CREATE, {username: username, password: password}).then((data, response) => {
                    resolve(true)
                }).catch(e => {
                    if (e instanceof Response && e.status === 409) {
                        reject('Username already taken')
                    } else {
                        reject('Unknown error, please try again')
                    }
                })

            })
        }
    }

})()