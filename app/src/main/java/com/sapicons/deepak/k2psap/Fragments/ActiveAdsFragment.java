package com.sapicons.deepak.k2psap.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.sapicons.deepak.k2psap.Activities.AdPreviewActivity;
import com.sapicons.deepak.k2psap.Activities.YourAdsActivity;
import com.sapicons.deepak.k2psap.Adapters.AdPostAdapter;
import com.sapicons.deepak.k2psap.Objects.PostItem;
import com.sapicons.deepak.k2psap.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Deepak Prasad on 24-10-2018.
 */

public class ActiveAdsFragment extends Fragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

    Context context;
    String TAG = "ACTIVE_ADS_FRAGMENT";

    ListView adListView;
    List<PostItem> postList;
    AdPostAdapter postItemAdapter;

    ProgressDialog progressDialog ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_active_ads,container,false);

        context = getActivity();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initialiseViews(view);
        listenToChanges();
    }

    public void initialiseViews(View view){


        adListView = view.findViewById(R.id.frag_active_ads_listview);

        postList = new ArrayList<>();
        postItemAdapter = new AdPostAdapter(context,R.layout.item_ad,postList);
        adListView.setAdapter(postItemAdapter);

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please Wait ...");


        adListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(context, AdPreviewActivity.class);
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
        Query query = adRef.whereEqualTo("emailId",user.getEmail()).whereEqualTo("status","open");

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
                postItemAdapter = new AdPostAdapter(context,R.layout.item_ad_post,postList);
                adListView.setAdapter(postItemAdapter);

                progressDialog.dismiss();

            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.collapsed_search_menu,menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        //searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search Your Ads ...");
        searchView.setOnQueryTextListener(this);

        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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
        postItemAdapter = new AdPostAdapter(context, R.layout.item_ad_post, filteredValues);
        adListView.setAdapter(postItemAdapter);
        return false;
    }

    public void resetSearch(){
        postItemAdapter = new AdPostAdapter(context, R.layout.item_ad_post, postList);
        adListView.setAdapter(postItemAdapter);
    }
}
