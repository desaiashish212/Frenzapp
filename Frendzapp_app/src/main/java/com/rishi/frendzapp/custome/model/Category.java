package com.rishi.frendzapp.custome.model;

import com.quickblox.customobjects.model.QBCustomObject;

/**
 * Created by AD on 18-Apr-16.
 */
public class Category {

    private String id;
    private String category_name;
    private String category_image;


    public Category(QBCustomObject customObject){

        id = customObject.getCustomObjectId();
        category_name = parseField("Category_name", customObject);
        category_image = parseField("Category_Image", customObject);

    }

    private String parseField(String field, QBCustomObject customObject) {
        Object object = customObject.getFields().get(field);
        if (object != null) {
            return object.toString();
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getCategory_image() {
        return category_image;
    }
}
