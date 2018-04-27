(function () {

    class User {
        constructor(username) {
            this.username = username
            this.password = null
            this.isAdmin = false
            this.albums = null
            this.followers = null
            this.following = null
        }

        static fromJson(data) {

            if (typeof data !== "object") {
                data = JSON.parse(data)
            }

            let user = new User(data.username)
            user.password = data.password

            if (data.isAdmin === true || data.admin === true) {
                user.isAdmin = true
            }

            if (data.followers) {
                user.followers = data.followers
            }

            if (data.following) {
                user.following = data.following
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

        getFollowers() {
            let $user = this

            return new Promise((resolve, reject) => {

                if ($user.followers === null) {
                    API.Users.getUsersFollowers($user.username).then(followers => {
                        $user.followers = followers
                        resolve(followers)
                    }).catch(err => reject(err))
                } else {
                    resolve($user.followers)
                }

            })
        }

        addFollower(follower) {
            let $user = this

            if (this.followers === null) {
                this.getFollowers.then(() => {
                    $user.followers.push(follower)
                })
            } else {
                $user.followers.push(follower)
            }
        }

        getFollowing() {
            let $user = this

            return new Promise((resolve, reject) => {

                if ($user.following === null) {
                    API.Users.getUsersFollowing($user.username).then(following => {
                        $user.following = following
                        resolve(following)
                    }).catch(err => reject(err))
                } else {
                    resolve($user.following)
                }

            })
        }

        addFollowing(following) {
            let $user = this

            if (this.following === null) {
                this.getFollowing.then(() => {
                    $user.following.push(following)
                })
            } else {
                $user.following.push(following)
            }
        }
    }


    window.Models.User = User
})()