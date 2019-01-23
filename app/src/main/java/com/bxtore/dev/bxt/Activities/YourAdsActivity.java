package com.bxtore.dev.bxt.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.bxtore.dev.bxt.Adapters.AdPostAdapter;
import com.bxtore.dev.bxt.Objects.PostItem;
import com.bxtore.dev.bxt.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class YourAdsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

    String TAG = "YOUR_ADS_ACTIVITY";

    ListView adListView;
    List<PostItem> postList;
    AdPostAdapter postItemAdapter;

    ProgressDialog progressDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Your Posts");
        setContentView(R.layout.activity_your_ads);
        getSupportActionBar().setElevation(0);

        initialiseViews();
        listenToChanges();

    }

    public void initialiseViews(){


        adListView =findViewById(R.id.activity_your_ads_listview);

        postList = new ArrayList<>();
        postItemAdapter = new AdPostAdapter(this,R.layout.item_ad,postList);
        adListView.setAdapter(postItemAdapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait ...");


        adListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(YourAdsActivity.this, AdPreviewActivity.class);
                //Bundle bundle =
                PostItem item = (PostItem)adapterView.getItemAtPosition(i);
                intent.putExtra("selected_post_item",item);
                startActivity(intent);
            }
        });
    }


    public void listenToChanges(){
        progressDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //final CollectionReference docRef = db.collection("users").document(user.getEmail()).collection("");

        CollectionReference adRef = db.collection("ads");
        Query query = adRef.whereEqualTo("emailId",user.getEmail());

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                List<PostItem> new_list = new ArrayList<>();
                for (QueryDocumentSnapshot doc : value) {
                    PostItem newItem = doc.toObject(PostItem.class);
                    Log.d(TAG,"Post: "+newItem.getTitle());
                    new_list.add(newItem);

                }
                Collections.sort(new_list,PostItem.PostTimeComparator);
                postList = new_list;
                postItemAdapter = new AdPostAdapter(YourAdsActivity.this,R.layout.item_ad_post,postList);
                adListView.setAdapter(postItemAdapter);

                progressDialog.dismiss();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.collapsed_search_menu,menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        //searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search Your Ads ...");
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem menuItem) {
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        if (newText == null || newText.trim().isEmpty()) {
            resetSearch();
            return false;
        }
        List<PostItem> filteredValues = new ArrayList<PostItem>(postList);
        for (PostItem value : postList) {

            String searchString = value.getTitle()+ " " + value.getCategoryName()+" "+ value.getDescription();
            searchString=searchString.toLowerCase();

            if (!searchString.contains(newText.toLowerCase())) {

                filteredValues.remove(value);
            }
        }
        postItemAdapter = new AdPostAdapter(this, R.layout.item_ad_post, filteredValues);
        adListView.setAdapter(postItemAdapter);
        return false;
    }

    public void resetSearch(){
        postItemAdapter = new AdPostAdapter(this, R.layout.item_ad_post, postList);
        adListView.setAdapter(postItemAdapter);
    }
}
