package com.rishi.frendzapp.custome.model;

import com.quickblox.customobjects.model.QBCustomObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by AD on 07-Apr-16.
 */
public class Offer {

    private String id;
    private String city;
    private String shop_name;
    private String shop_address;
    private String contact_person;
    private String mobile_no;
    private String offer_name;
    private String start_date;
    private String end_date;
    private String description;
    private String price;
    private String sale_price;
    private String main_image;
    private String product_image1;
    private String product_image2;
    private String product_image3;
    private String product_image4;
    private String product_image5;
    private String category;
    private String views;
    private List<String> product_image;
    private List<String> commentsList;
   // private String[] product_image;


    public Offer(QBCustomObject customObject) {
        id = customObject.getCustomObjectId();
        city = parseField("City", customObject);
        shop_name = parseField("Shop_Name", customObject);
        shop_address = parseField("Shop_Address", customObject);
        contact_person = parseField("Contact_Person", customObject);
        mobile_no = parseField("Mobile_No", customObject);
        offer_name = parseField("Offer_Name", customObject);
        start_date = parseField("Start_date", customObject);
        end_date = parseField("End_Date", customObject);
        description = parseField("Description", customObject);
        price = parseField("MRP", customObject);
        sale_price = parseField("Sale_Price", customObject);
        main_image = parseField("main_image", customObject);
        product_image1 = parseField("product_image1", customObject);
        product_image2 = parseField("product_image2", customObject);
        product_image3 = parseField("product_image3", customObject);
        product_image4 = parseField("product_image4", customObject);
        product_image5 = parseField("product_image5", customObject);
        //

        commentsList = new ArrayList<String>();
        String commentString = parseField("comments",customObject);
        if (commentString != null) {
            String[] comments = commentString.split("/");
            Collections.addAll(this.commentsList, comments);
        }
        category = parseField("Category", customObject);
        views=parseField("Views",customObject);

        //product_image = new ArrayList<String>();
       // product_image = new String[];
        String product_image_string = parseField("product_image", customObject);
        product_image_string.replace(","," ");
        product_image_string.replace("[","");
        product_image_string.replace("]","");
        System.out.println("product_image_string:"+product_image_string);
        String[] comments = product_image_string.split(product_image_string);
        System.out.println("Comment:"+comments);

    /*    if (product_image_string != null) {

            String[] comments = product_image_string.split(",");
            Collections.addAll(this.product_image, comments);
            System.out.println("Comment:"+comments);
        } */
       // System.out.println("product_image_list:"+product_image.toString());

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

    public String getCity() {
        return city;
    }

    public String getShop_name() {
        return shop_name;
    }

    public String getShop_address() {
        return shop_address;
    }

    public String getContact_person() {
        return contact_person;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public String getOffer_name() {
        return offer_name;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getSale_price() {
        return sale_price;
    }

    public String getMain_image() {
        return main_image;
    }

  /*  public String[] getProduct_image() {
        return product_image;
    }
*/
    public String getProduct_image1() {
        return product_image1;
    }

    public String getProduct_image2() {
        return product_image2;
    }

    public String getProduct_image3() {
        return product_image3;
    }

    public String getProduct_image4() {
        return product_image4;
    }

    public String getProduct_image5() {
        return product_image5;
    }

    public String getCaregory() {
        return category;
    }

    public String getViews(){return views;}


    public List<String> getCommentsList() {
        return commentsList;
    }

    public String getComments() {
        String comments = "";
        for (String comment : commentsList) {
            comments += comment + "/";
        }
        return comments;
    }

    public void addNewComment(String comment) {
        commentsList.add(comment);
    }


}
