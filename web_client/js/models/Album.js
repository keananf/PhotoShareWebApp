(function () {

    class Album {
        constructor(id, name, description, author, date) {
            this.id = id
            this.name = name
            this.description = description
            this.author = author
            this.date = date
        }

        static fromJson(data) {

            if (typeof data !== "object") {
                data = JSON.parse(data)
            }

            return new Album(data.albumId, data.albumName, data.description, data.authorName, data.albumTime)
        }

        toJson() {
            return JSON.stringify({
                albumId: this.id,
                albumName: this.name,
                description: this.description,
                authorName: this.author,
                albumTime: this.date,
            })
        }
    }


    window.Models.Album = Album
})()