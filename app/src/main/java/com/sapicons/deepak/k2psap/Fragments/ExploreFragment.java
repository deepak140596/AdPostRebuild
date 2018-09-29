package com.sapicons.deepak.k2psap.Fragments;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sapicons.deepak.k2psap.Activities.AdPreviewActivity;
import com.sapicons.deepak.k2psap.Adapters.AdPostAdapter;
import com.sapicons.deepak.k2psap.Adapters.AdPostViewPagerAdapter;
import com.sapicons.deepak.k2psap.Objects.PostItem;
import com.sapicons.deepak.k2psap.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Deepak Prasad on 29-09-2018.
 */

public class ExploreFragment extends ListFragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener  {

    ListView adListView;
    ViewPager mostRecentViewPager;
    List<PostItem> postList;
    AdPostAdapter postItemAdapter;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_explore,container,false);
        adListView = view.findViewById(R.id.frag_explore_ads_list_view);
        mostRecentViewPager = view.findViewById(R.id.frag_explore_vp);
        context = getActivity();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        initialiseViews();
        listenToChanges();
    }

    public void initialiseViews(){
        postList = new ArrayList<>();
        postItemAdapter = new AdPostAdapter(getActivity(),R.layout.item_ad_post,postList);
        adListView.setAdapter(postItemAdapter);


        adListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), AdPreviewActivity.class);
                //Bundle bundle =
                PostItem item = (PostItem)adapterView.getItemAtPosition(i);
                intent.putExtra("selected_post_item",item);
                startActivity(intent);
            }
        });
    }

    public void listenToChanges(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //final CollectionReference docRef = db.collection("users").document(user.getEmail()).collection("");

        db.collection("ads")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("EXPL_FRAG", "Listen failed.", e);
                            return;
                        }

                        List<PostItem> new_list = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            PostItem newItem = doc.toObject(PostItem.class);
                            Log.d("EXPL_FRAG","Post: "+newItem.getTitle());
                            new_list.add(newItem);

                        }
                        postList = new_list;
                        postItemAdapter = new AdPostAdapter(context,R.layout.item_ad_post,postList);
                        postItemAdapter.notifyDataSetChanged();
                        adListView.setAdapter(postItemAdapter);

                        setUpViewPager();
                        //progressDialog.dismiss();

                    }
                });
    }

    public void setUpViewPager(){

        List<PostItem> newList = postList;
        Collections.sort(newList,PostItem.PostTimeComparator);
        newList = newList.subList(0,5);
        AdPostViewPagerAdapter adapter = new AdPostViewPagerAdapter(context,newList);
        mostRecentViewPager.setAdapter(adapter);
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
    public boolean onQueryTextChange(String s) {
        return false;
    }
}
