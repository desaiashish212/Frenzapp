package com.rishi.frendzapp_core.db.tables;

import android.net.Uri;

import com.rishi.frendzapp_core.db.ContentDescriptor;

public class NotSendMessageTable {
    public static final String TABLE_NAME = "not_send_message";
    public static final String PATH = "not_send_message_table";
    public static final int PATH_TOKEN = 60;
    public static final Uri CONTENT_URI = ContentDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();

    public static class Cols {

        public static final String ID = "_id";
        public static final String DIALOG_ID = "dialog_id";
        public static final String BODY = "body";
        public static final String ATTACH_FILE_ID = "attach_file_id";
    }
}
