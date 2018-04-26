(function () {

    class User {
        constructor(username) {
            this.username = username
            this.password = null
            this.isAdmin = false
            this.albums = null
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

        getAlbums() {
            let $user = this

            return new Promise((resolve, reject) => {

                if (this.albums === null) {

                    API.Albums.getForUser(this.username).then(albums => {
                        $user.albums = albums
                        resolve(albums)
                    }).catch(err => {
                        alert(err)
                    })

                } else {

                    resolve($user.albums)

                }
            })
        }

        addAlbum(album) {
            let $user = this

            if (this.albums === null) {
                this.getAlbums().then(() => {
                    $user.albums.push(album)
                })
            } else {
                $user.albums.push(album)
            }
        }
    }


    window.Models.User = User
})()