package com.bxtore.dev.bxt.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.bxtore.dev.bxt.Activities.AdPreviewActivity;
import com.bxtore.dev.bxt.Adapters.AdPostGridViewAdapter;
import com.bxtore.dev.bxt.Objects.PostItem;
import com.bxtore.dev.bxt.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deepak Prasad on 02-10-2018.
 */

public class FavoritesFragment extends Fragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener   {

    String TAG = "FAV_FRAG";

    //ListView adListView;
    GridView gridView;

    List<PostItem> postList;
    //AdPostAdapter postItemAdapter;
    AdPostGridViewAdapter gridViewAdapter;

    LinearLayout emptyFavLL;
    Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("Favorites ");

        View view = inflater.inflate(R.layout.fragment_favorites,container,false);
        context = getActivity();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        initialiseViews(view);
        listenToChanges();
    }


    public void initialiseViews(View view){


        //adListView =view.findViewById(R.id.frag_fav_listview);
        gridView = view.findViewById(R.id.frag_favorites_gridview);
        emptyFavLL = view.findViewById(R.id.frag_fav_empty_listview_ll);
        //adRecyclerView = view.findViewById(R.id.frag_explore_ads_recycler_view);

        //mLayoutManager = new LinearLayoutManager(context);
        //adRecyclerView.setLayoutManager(mLayoutManager);

        //postItemRAdapter = new AdPostRecyclerAdapter(context,postList);
        //adRecyclerView.setAdapter(postItemRAdapter);


        postList = new ArrayList<>();

        // for list view
        //postItemAdapter = new AdPostAdapter(context,R.layout.item_ad,postList);
        //adListView.setAdapter(postItemAdapter);
        //adListView.setEmptyView(emptyFavLL);

        // for grid view
        gridViewAdapter = new AdPostGridViewAdapter(context,R.layout.item_ad_grid,postList);
        gridView.setAdapter(gridViewAdapter);
        gridView.setEmptyView(emptyFavLL);

        // for recycler view
        /*adRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity()
                , adRecyclerView, new RecyclerViewTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                PostItem postItem = postList.get(position);
                Intent intent = new Intent(getActivity(), AdPreviewActivity.class);
                //Bundle bundle =
                intent.putExtra("selected_post_item",postItem);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));*/


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), AdPreviewActivity.class);
                //Bundle bundle =
                PostItem item = postList.get(i);
                intent.putExtra("selected_post_item",item);
                //intent.putExtra("is_fav_frag",true);
                startActivity(intent);
            }
        });
    }


    public void listenToChanges(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //final CollectionReference docRef = db.collection("users").document(user.getEmail()).collection("");

        CollectionReference favRef = db.collection("favorites")
                .document(user.getEmail())
                .collection("favoritedAds");

        favRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                        postList = new_list;
                        //postItemAdapter = new AdPostAdapter(context,R.layout.item_ad,postList);
                        //adListView.setAdapter(postItemAdapter);

                        gridViewAdapter = new AdPostGridViewAdapter(context,R.layout.item_ad_grid,postList);
                        gridView.setAdapter(gridViewAdapter);

                        //postItemRAdapter = new AdPostRecyclerAdapter(context,postList);
                        //adRecyclerView.setAdapter(postItemRAdapter);

                        //postItemRAdapter.notifyDataSetChanged();
                        //progressDialog.dismiss();

                    }
                });
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
