(function () {

    class User {
        constructor(username) {
            this.username = username
        }

        static fromJson(data) {

            if (typeof data !== "object") {
                data = JSON.parse(data)
            }
            return new User(data.username)
        }

        get route() {
            return '/user/' + this.username
        }
    }


    window.Models.User = User
})()