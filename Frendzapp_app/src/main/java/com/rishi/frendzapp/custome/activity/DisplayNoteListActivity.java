package com.rishi.frendzapp.custome.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.model.QBCustomObject;
import com.rishi.frendzapp.R;
import com.rishi.frendzapp.custome.adapter.OfferListAdapter;
import com.rishi.frendzapp.custome.helper.OfferDataHolder;
import com.rishi.frendzapp.custome.utils.DialogUtils;
import com.rishi.frendzapp.ui.base.BaseActivity;
import com.rishi.frendzapp_core.service.QBServiceConsts;

import java.util.ArrayList;
import java.util.List;


public class DisplayNoteListActivity extends BaseActivity{

    private final String POSITION = "position";
    private ListView notesListView;
    //private NoteListAdapter noteListAdapter;
    private OfferListAdapter offerListAdapter;
    private String category;

    public static void start(Context context, String category) {
        Intent intent = new Intent(context, DisplayNoteListActivity.class);
        intent.putExtra(QBServiceConsts.EXTRA_CATEGORY, category);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list);
        category = getIntent().getExtras().getString(QBServiceConsts.EXTRA_CATEGORY);
        initUI();
        getNoteList();
    }

    @Override
    public void onResume() {
        super.onResume();
        offerListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initUI() {
        notesListView = (ListView) findViewById(R.id.notes_listview);
        offerListAdapter = new OfferListAdapter(DisplayNoteListActivity.this);
    }

    public void onClick(View view) {
      /*  switch (view.getId()) {
            case R.id.add_new_note_button:
                Intent intent = new Intent(this, AddNewNoteActivity.class);
                startActivity(intent);
                break;
        } */
    }

    private void getNoteList() {
        final ProgressDialog progressDialog = DialogUtils.getProgressDialog(this);
        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
      //  requestBuilder.eq("City", "Kolhapur");
        requestBuilder.eq("Category", category);
        requestBuilder.setPagesLimit(5);
        //requestBuilder.eq("Shop_name", "frendzapp");
        //requestBuilder.sortAsc("City");
        QBCustomObjects.getObjects("Offers", requestBuilder, new QBEntityCallbackImpl<ArrayList<QBCustomObject>>() {
            @Override
            public void onSuccess(ArrayList<QBCustomObject> qbCustomObjects, Bundle bundle) {

                if (OfferDataHolder.getOfferDataHolder().size() > 0) {
                    OfferDataHolder.getOfferDataHolder().clear();
                }

                if (qbCustomObjects != null && qbCustomObjects.size() != 0) {
                    for (QBCustomObject customObject : qbCustomObjects) {
                        OfferDataHolder.getOfferDataHolder().addOfferToList(customObject);
                    }
                }
                progressDialog.dismiss();
                offerListAdapter = new OfferListAdapter(DisplayNoteListActivity.this);
                notesListView.setAdapter(offerListAdapter);
            }

            @Override
            public void onError(List<String> strings) {
                Toast.makeText(getBaseContext(), strings.get(0), Toast.LENGTH_SHORT).show();
                //progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

}