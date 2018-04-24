(function () {

    let http = API.http

    window.API.Auth = {
        login(username, password){
            return new Promise((resolve, reject) => {
                //resolve(null)
                resolve(Models.User.fromJson({'username': username}))
            })
        }
    }

})()