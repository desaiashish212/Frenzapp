package com.rishi.frendzapp.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.rishi.frendzapp.R;
import com.rishi.frendzapp_core.models.InviteFriend;
import com.rishi.frendzapp_core.utils.ConstsCore;

import java.util.ArrayList;
import java.util.List;

public class EmailUtils {

    public static void sendInviteEmail(Context context, String[] selectedFriends) {
        Resources resources = context.getResources();
        Intent intentEmail = new Intent(Intent.ACTION_SEND);
        intentEmail.putExtra(Intent.EXTRA_EMAIL, selectedFriends);
        intentEmail.putExtra(Intent.EXTRA_SUBJECT, resources.getText(R.string.inf_subject_of_invitation));
        intentEmail.putExtra(Intent.EXTRA_TEXT, resources.getText(R.string.inf_body_of_invitation));
        intentEmail.setType(ConstsCore.TYPE_OF_EMAIL);
        context.startActivity(Intent.createChooser(intentEmail, resources.getText(
                R.string.inf_choose_email_provider)));
    }

    public static void sendFeedbackEmail(Context context, String feedbackType) {
        Resources resources = context.getResources();
        Intent intentEmail = new Intent(Intent.ACTION_SEND);
        intentEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{resources.getText(R.string.fdb_support_email)
                .toString()});
        intentEmail.putExtra(Intent.EXTRA_SUBJECT, feedbackType);
        intentEmail.putExtra(Intent.EXTRA_TEXT,
                (java.io.Serializable) DeviceInfoUtils.getDeviseInfoForFeedback(context));
        intentEmail.setType(ConstsCore.TYPE_OF_EMAIL);
        context.startActivity(Intent.createChooser(intentEmail, resources.getText(
                R.string.fdb_choose_email_provider)));
    }

    public static List<InviteFriend> getContactsWithEmail(Context context) {
        List<InviteFriend> friendsContactsList = new ArrayList<InviteFriend>();
        Uri uri = null;
/*
        ContentResolver contentResolver = context.getContentResolver();

        String[] PROJECTION = new String[]{ContactsContract.RawContacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.PHOTO_ID, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Photo.CONTACT_ID};

        String order = "CASE WHEN " + ContactsContract.Contacts.DISPLAY_NAME + " NOT LIKE '%@%' THEN 1 ELSE 2 END, " + ContactsContract.CommonDataKinds.Phone.CONTACT_ID + ", " + ContactsContract.Contacts.DISPLAY_NAME + ", " + ContactsContract.CommonDataKinds.Phone.NUMBER + " COLLATE NOCASE";

        String filter = ContactsContract.CommonDataKinds.Phone.NUMBER + " NOT LIKE ''";

        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION,
                filter, null, order);

        if (cursor != null && cursor.moveToFirst()) {
            String id;
            String name;
            String email;
            do {
                name = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                email = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                id = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                if (ContactsContract.Contacts.CONTENT_URI != null) {
                    uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(
                            id));
                    uri = Uri.withAppendedPath(uri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
                }
                friendsContactsList.add(new InviteFriend(email, name, null, InviteFriend.VIA_CONTACTS_TYPE,
                        uri, false));
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }

        */

        ContentResolver cr = context.getContentResolver(); //Activity/Application android.content.Context
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if(cursor.moveToFirst())
        {

            do
            {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",new String[]{ id }, null);
                    while (pCur.moveToNext())
                    {
                        if (ContactsContract.Contacts.CONTENT_URI != null) {
                            uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(
                                    id));
                            uri = Uri.withAppendedPath(uri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
                        }
                        String name = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String input = contactNumber.replace(" ", "");
                        if(input.length()>10)
                        {
                            friendsContactsList.add(new InviteFriend(input, name, null, InviteFriend.VIA_CONTACTS_TYPE,
                                    uri, false));
                        }
                        //System.out.println("Contact:" + friendsContactsList);
                        break;
                    }
                    pCur.close();
                }

            } while (cursor.moveToNext()) ;
        }
        return friendsContactsList;
    }
}