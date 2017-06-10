package com.example.esca.landmarket.models;

/**
 * Created by Esca on 09.06.2017.
 */

public class Registration {
    private String login;
    private String email;
    private String password;
    private String confirm;

    public Registration(String login, String email, String password, String confirm) {
        this.login = login;
        this.email = email;
        this.password = password;
        this.confirm = confirm;
    }

    public Registration() {

    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }
}
