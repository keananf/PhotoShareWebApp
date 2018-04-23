(function () {

    window.API = {
        baseUrl: 'http://localhost:8080/photoshare',

        getHttpWrapper(baseEndPoint = ''){
            return new HW(this.baseUrl + baseEndPoint)
        }
    }

})()