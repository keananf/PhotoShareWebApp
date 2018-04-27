(function () {

    let http = API.http

    window.API.Albums = {

        getForUser(username){
            return new Promise((resolve, reject) => {
                http.get(API.endpoints.ALBUMS_GET_FOR_USER.replace(':username', username)).then(res => {
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
                http.post(API.endpoints.ALBUM_CREATE, {albumName: name, description}).then(res => {
                    if (res.hasOwnProperty('referenceId')) {
                        resolve(res.referenceId)
                    } else {
                        reject('Unknown error')
                    }
                }).catch(err => {
                    reject(err)
                })
            })
        },

        getById(id){
            return new Promise((resolve, reject) => {
                http.get(API.endpoints.ALBUM_GET.replace(':id', id)).then(res => {
                    resolve(Models.Album.fromJson(res))
                }).catch(err => reject(err))
            })
        }

    }

})()