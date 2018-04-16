package common.requests;


import common.Auth;

/**
 * Simple wrapper over Auth. Used for
 * all objects.common involving authorisation
 */
public class AuthRequest {
    private final Auth auth;

    public AuthRequest(Auth auth) {
        this.auth = auth;
    }

    /**
     * @return this request's auth information
     */
    public Auth getAuth() {
        return auth;
    }
}
