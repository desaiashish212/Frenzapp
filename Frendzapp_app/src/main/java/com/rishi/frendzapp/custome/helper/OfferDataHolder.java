package com.rishi.frendzapp.custome.helper;

import com.quickblox.customobjects.model.QBCustomObject;
import com.rishi.frendzapp.custome.model.Note;
import com.rishi.frendzapp.custome.model.Offer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AD on 07-Apr-16.
 */
public class OfferDataHolder {
    private static OfferDataHolder offerDataHolder;
    private int signInUserId;
    private List<Offer> offerList;

    public static synchronized OfferDataHolder getOfferDataHolder() {
        if (offerDataHolder == null) {
            offerDataHolder = new OfferDataHolder();
        }
        return offerDataHolder;
    }

    public int getSignInUserId() {
        return signInUserId;
    }

    public void setSignInUserId(int signInUserId) {
        this.signInUserId = signInUserId;
    }

    public int getOfferListSize() {
        if (offerList == null) {
            offerList = new ArrayList<Offer>();
        }
        return offerList.size();
    }

    public String getOfferId(int position) {
        return offerList.get(position).getId();
    }

    public String getOfferCity(int position) {
        return offerList.get(position).getCity();
    }

    public String getOfferShopName(int position) {
        return offerList.get(position).getShop_name();
    }

    public String getOfferShopAddress(int position) {
        return offerList.get(position).getShop_address();
    }

    public String getOfferContactPerson(int position) {
        return offerList.get(position).getContact_person();
    }

    public String getOfferMobieNo(int position) {
        return offerList.get(position).getMobile_no();
    }

    public String getOfferName(int position) {
        return offerList.get(position).getOffer_name();
    }

    public String getOfferStartDate(int position) {
        return offerList.get(position).getStart_date();
    }

    public String getOfferEndDate(int position) {
        return offerList.get(position).getEnd_date();
    }

    public String getOfferDescription(int position) {
        return offerList.get(position).getDescription();
    }

    public String getOfferPrice(int position) {
        return offerList.get(position).getPrice();
    }

    public String getOfferSale(int position) {
        return offerList.get(position).getSale_price();
    }

    public String getOfferMainImage(int position){return offerList.get(position).getMain_image();}

  //  public String[] getOfferProductImage(int position){return offerList.get(position).getProduct_image();}

    public String getOfferProductImage1(int position){return offerList.get(position).getProduct_image1();}

    public String getOfferProductImage2(int position){return offerList.get(position).getProduct_image2();}

    public String getOfferProductImage3(int position){return offerList.get(position).getProduct_image3();}

    public String getOfferProductImage4(int position){return offerList.get(position).getProduct_image4();}

    public String getOfferProductImage5(int position){return offerList.get(position).getProduct_image5();}

    public String getOfferCategory(int position){return offerList.get(position).getCaregory();}

    public String getOffersViews(int position){return  offerList.get(position).getViews();}

    public void clear() {
        offerList.clear();
    }

    public int size() {
        if (offerList != null) {
            return offerList.size();
        }
        return 0;
    }

    public void addOfferToList(QBCustomObject customObject) {
        if (offerList == null) {
            offerList = new ArrayList<Offer>();
        }
        offerList.add(new Offer(customObject));
    }
    public List<String> getNoteComments(int position) {
        return offerList.get(position).getCommentsList();
    }
    public void addNewComment(int notePosition, String comment) {
        offerList.get(notePosition).addNewComment(comment);
    }
    public String getComments(int notePosition) {
        return offerList.get(notePosition).getComments();
    }
    public void addNoteToList(QBCustomObject customObject) {
        if (offerList == null) {
            offerList = new ArrayList<Offer>();
        }
        offerList.add(new Offer(customObject));
    }

}
