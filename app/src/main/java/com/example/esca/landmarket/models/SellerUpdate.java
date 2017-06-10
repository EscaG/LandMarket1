package com.example.esca.landmarket.models;

/**
 * Created by Esca on 11.06.2017.
 */

public class SellerUpdate {
    private String  password;
    private String confirm;
    private String telephone;

    public SellerUpdate(String password, String confirm, String telephone) {
        this.password = password;
        this.confirm = confirm;
        this.telephone = telephone;
    }

    public SellerUpdate() {

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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
