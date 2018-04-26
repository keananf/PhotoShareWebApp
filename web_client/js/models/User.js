(function () {

    class User {
        constructor(username) {
            this.username = username
            this.password = null
            this.isAdmin = false
        }

        static fromJson(data) {

            console.log(data)

            if (typeof data !== "object") {
                data = JSON.parse(data)
            }

            let user = new User(data.username)
            user.password = data.password

            if (data.isAdmin === true) {
                user.isAdmin = true
            }

            return user
        }

        get route() {
            return '/user/' + this.username
        }
    }


    window.Models.User = User
})()