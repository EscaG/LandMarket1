package com.example.esca.landmarket.models;

/**
 * Created by Esca on 10.06.2017.
 */

public class Seller {
    private String login;
    private String email;
    private String password;
    private String confirm;
    private String passport;
    private String phone;

    public Seller(String login, String email, String password, String confirm, String passport, String phone) {
        this.login = login;
        this.email = email;
        this.password = password;
        this.confirm = confirm;
        this.passport = passport;
        this.phone = phone;
    }

    public Seller() {

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

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
