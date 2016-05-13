package com.rishi.frendzapp.custome.model;

import com.quickblox.customobjects.model.QBCustomObject;


public class Note {

    private String id;
    private String city;
    private String shop_name;
    private String contact_person;

    public Note(QBCustomObject customObject) {
        id = customObject.getCustomObjectId();
        city = parseField("City", customObject);
        shop_name = parseField("Shop_Name", customObject);
        contact_person = parseField("Contact_Person", customObject);
    }

    private String parseField(String field, QBCustomObject customObject) {
        Object object = customObject.getFields().get(field);
        if (object != null) {
            return object.toString();
        }
        return null;
    }

    public String getCity() {
        return city;
    }

    public String getShop_name() {
        return shop_name;
    }

    public String getContact_person() {
        return contact_person;
    }

    public String getId() {
        return id;
    }
}