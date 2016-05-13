package com.rishi.frendzapp.custome.helper;

import com.quickblox.customobjects.model.QBCustomObject;
import com.rishi.frendzapp.custome.model.Category;
import com.rishi.frendzapp.custome.model.Offer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AD on 18-Apr-16.
 */
public class CategoryDataHoler {
    private static CategoryDataHoler categoryDataHoler;
    private int signInUserId;
    private List<Category> categoryList;

    public static synchronized CategoryDataHoler getCategoryrDataHolder() {
        if (categoryDataHoler == null) {
            categoryDataHoler = new CategoryDataHoler();
        }
        return categoryDataHoler;
    }

    public int getSignInUserId() {
        return signInUserId;
    }

    public void setSignInUserId(int signInUserId) {
        this.signInUserId = signInUserId;
    }

    public int getCategoryListSize() {
        if (categoryList == null) {
            categoryList = new ArrayList<Category>();
        }
        return categoryList.size();
    }

    public String getCategoryId(int position) {
        return categoryList.get(position).getId();
    }

    public String getCategoryName(int position) {
        return categoryList.get(position).getCategory_name();
    }

    public String getCategoryImage(int position) {
        return categoryList.get(position).getCategory_image();
    }

    public void clear() {
        categoryList.clear();
    }

    public int size() {
        if (categoryList != null) {
            return categoryList.size();
        }
        return 0;
    }

    public void addCategoryToList(QBCustomObject customObject) {
        if (categoryList == null) {
            categoryList = new ArrayList<Category>();
        }
        categoryList.add(new Category(customObject));
    }
}
