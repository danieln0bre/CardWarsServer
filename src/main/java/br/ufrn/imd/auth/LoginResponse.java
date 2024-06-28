package br.ufrn.imd.auth;



public class LoginResponse {

    private String token;
    private long expiresIn;

    public LoginResponse setToken(String token) {
        this.token = token;
        return this;
    }

    public LoginResponse setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public String getToken() {
        return token;
    }
}
