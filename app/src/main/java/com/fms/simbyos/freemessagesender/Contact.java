package com.fms.simbyos.freemessagesender;

/**
 * Created by SimbyOS on 01.02.2018.
 */

public class Contact {
    public int id;
    public String ContactName;
    public String ContactPhone;
    public Contact(String name, String phone) {
        this.ContactName = name;
        this.ContactPhone = phone;
    }
    public Contact(int id,String name, String phone) {
        this.ContactName = name;
        this.ContactPhone = phone;
        this.id = id;
    }
}