package com.rishi.frendzapp.ui.offer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.customobjects.QBCustomObjects;
import com.quickblox.customobjects.model.QBCustomObject;
import com.rishi.frendzapp.R;
import com.rishi.frendzapp.custome.activity.DisplayNoteListActivity;
import com.rishi.frendzapp.custome.adapter.CategoryListAdapter;
import com.rishi.frendzapp.custome.helper.CategoryDataHoler;
import com.rishi.frendzapp.custome.helper.DataHolder;
import com.rishi.frendzapp.custome.helper.OfferDataHolder;
import com.rishi.frendzapp.custome.utils.DialogUtils;
import com.rishi.frendzapp.ui.base.BaseLogeableActivity;
import com.rishi.frendzapp_core.models.AppSession;

import java.util.ArrayList;
import java.util.List;

public class OfferActivity extends BaseLogeableActivity {
    private CategoryListAdapter listAdapter;
    private GridView gridView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);
        gridView = (GridView) findViewById(R.id.grid);
        //OfferDataHolder.getOfferDataHolder().setSignInUserId(AppSession.getSession().getUser().getId());
       // getNoteList();
        CategoryDataHoler.getCategoryrDataHolder().setSignInUserId(AppSession.getSession().getUser().getId());
        getCategoryList();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                startDisplayNoteListActivity(CategoryDataHoler.getCategoryrDataHolder().getCategoryName(position));
                Toast.makeText(OfferActivity.this, "You Clicked at " + CategoryDataHoler.getCategoryrDataHolder().getCategoryName(position), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getCategoryList() {
        final ProgressDialog progressDialog = DialogUtils.getProgressDialog(this);
        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        //requestBuilder.eq("City", "kolhapur");
        requestBuilder.setPagesLimit(20);
        //requestBuilder.eq("Shop_name", "frendzapp");
        //requestBuilder.sortAsc("City");
        QBCustomObjects.getObjects("Category",requestBuilder, new QBEntityCallbackImpl<ArrayList<QBCustomObject>>() {
            @Override
            public void onSuccess(ArrayList<QBCustomObject> qbCustomObjects, Bundle bundle) {

                if (CategoryDataHoler.getCategoryrDataHolder().size() > 0) {
                    CategoryDataHoler.getCategoryrDataHolder().clear();
                }
                if (qbCustomObjects != null && qbCustomObjects.size() != 0) {
                    for (QBCustomObject customObject : qbCustomObjects) {
                        CategoryDataHoler.getCategoryrDataHolder().addCategoryToList(customObject);
                    }
                }
                progressDialog.dismiss();
               // startDisplayNoteListActivity();
                listAdapter = new CategoryListAdapter(OfferActivity.this);
                gridView.setAdapter(listAdapter);
            }

            @Override
            public void onError(List<String> strings) {
                Toast.makeText(getBaseContext(), strings.get(0), Toast.LENGTH_SHORT).show();
                //progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void startDisplayNoteListActivity(String name) {
        Intent intent = new Intent(this, DisplayNoteListActivity.class);
        intent.putExtra("category",name);
        startActivity(intent);
        finish();
    }
}
