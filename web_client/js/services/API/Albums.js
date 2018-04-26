(function () {

    let http = API.http

    window.API.Albums = {

        getForUser(username){
            return new Promise((resolve, reject) => {
                http.get('albums/users/' + username).then(res => {
                    let albums = []

                    for (let i in res) {
                        albums.push(Models.Album.fromJson(res[i]))
                    }

                    resolve(albums)

                }).catch(err => {
                    reject(err)
                })
            })
        },

        create(name, description){
            return new Promise((resolve, reject) => {
                http.post('albums/addalbum', {albumName: name, description}).then(res => {
                    if (res.hasOwnProperty('referenceId')) {
                        resolve(res.referenceId)
                    } else {
                        reject('Unknown error')
                    }
                }).catch(err => {
                    reject(err)
                })
            })
        }

    }

})()