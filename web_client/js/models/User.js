(function () {

    class User {
        constructor(username) {
            this.username = username
            this.password = null
            this.isAdmin = false
        }

        static fromJson(data) {

            if (typeof data !== "object") {
                data = JSON.parse(data)
            }

            let user = new User(data.username)

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