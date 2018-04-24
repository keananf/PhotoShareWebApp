(function () {
    /**
     HttpWrapper for easy work with HTTP Requests
     */
    class HW {
        constructor(baseUrl) {
            this.base = baseUrl
        }

        request(url, type, data, opts) {

            let $this = this

            // Set additional headers if auth is provided
            if (HW.auth !== undefined && HW.auth !== null) {
                // Make the token/digest
                let timestamp = Math.round((new Date()).getTime() / 1000)
                let endPoint = '/' + url
                let token = timestamp + endPoint + HW.auth.username + ':' + HW.auth.passwordHash
                token = sha256(token)
                token = btoa(token)

                HW.setAdditionalHeaders({
                    'Authentication': HW.auth.username + ':' + token
                })
            }

            return new Promise(function (resolve, reject) {

                // Generate the full url from base url and endpoint
                let fullUrl = $this.base + '/' + url

                // Request options
                let options = {
                    method: type,
                    headers: {
                        "Content-Type": "application/json" // JSON by default, can be overridden
                    }
                }

                // Additional headers
                if (HW.additionalHeaders !== undefined && HW.additionalHeaders.length > 0) {
                    for (let k in HW.additionalHeaders) {
                        for (let hKey in HW.additionalHeaders[k]) {
                            options.headers[hKey] = HW.additionalHeaders[k][hKey]
                        }
                    }
                }

                // Add data to the post body
                if (type === "POST") {
                    if (typeof data !== "undefined") {
                        options.body = JSON.stringify(data)
                    } else {
                        options.body = ''
                    }
                }

                // Check if custom options have been supplied
                if (typeof opts === "object") {
                    for (let key in opts) {
                        options[key] = opts[key]
                    }
                }

                // Send the request
                fetch(fullUrl, options)
                    .then(function (response) {

                        if (!response.ok) {
                            throw response
                        }

                        return response.json()
                    })
                    .then(response => resolve(response))
                    .catch(function (response) {
                        try {
                            response.json().then(error => reject(error.error ? error.error : error))
                        } catch (e) {
                            reject(response)
                        }
                    })

            })
        }

        get(url) {
            return this.request(url, 'GET')
        }

        post(url, data) {
            return this.request(url, 'POST', data)
        }

        del(url) {
            return this.request(url, 'DELETE')
        }

        static setAdditionalHeaders(headers) {
            HW.additionalHeaders = headers
        }

        static setAuthParameters(username, passwordHash) {
            HW.auth = {
                username: username,
                passwordHash: passwordHash
            }
        }

        static unsetAuthParameters(){
            HW.auth = null
        }

    }

    // Export to global scope
    window.HW = HW
})()