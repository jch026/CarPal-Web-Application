package com.app.server.models;


import com.app.server.http.utils.APPCrypt;

public class Session {

    String token = null;
    String userId = null;
    String firstName = null;
    String lastName = null;

    public Session(Owner owner) throws Exception{
        this.userId = owner.id;
        this.token = APPCrypt.encrypt(owner.id);
        this.firstName = owner.firstName;
        this.lastName = owner.lastName;
    }

    public Session(Renter renter) throws Exception{
        this.userId = renter.id;
        this.token = APPCrypt.encrypt(renter.id);
        this.firstName = renter.firstName;
        this.lastName = renter.lastName;
    }
}
