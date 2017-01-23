package com.telse.chating;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;


public class ContactModel {

    private static ContactModel sContactModel;
    private List<Contact> mContacts;

    public static ContactModel get(Context context)
    {
        if(sContactModel == null)
        {
            sContactModel = new ContactModel(context);
        }
        return  sContactModel;
    }

    private ContactModel(Context context)
    {
        mContacts = new ArrayList<>();
        populateWithInitialContacts(context);

    }

    private void populateWithInitialContacts(Context context)
    {
        //flood names
        Contact contact1 = new Contact("aaa@mail.ru");
        mContacts.add(contact1);
        Contact contact2 = new Contact("bbb@mail.ru");
        mContacts.add(contact2);
    }

    public List<Contact> getContacts()
    {
        return mContacts;
    }

}
