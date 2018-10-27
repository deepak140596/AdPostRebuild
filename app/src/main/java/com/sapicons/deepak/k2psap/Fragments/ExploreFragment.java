package com.sapicons.deepak.k2psap.Fragments;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
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
import com.sapicons.deepak.k2psap.Activities.MapsActivity;
import com.sapicons.deepak.k2psap.Activities.NavigationActivity;
import com.sapicons.deepak.k2psap.Adapters.AdPostAdapter;
import com.sapicons.deepak.k2psap.Adapters.AdPostGridViewAdapter;
import com.sapicons.deepak.k2psap.Adapters.AdPostRecyclerAdapter;
import com.sapicons.deepak.k2psap.Adapters.AdPostViewPagerAdapter;
import com.sapicons.deepak.k2psap.Adapters.CategoryAdapter;
import com.sapicons.deepak.k2psap.Objects.CategoryItem;
import com.sapicons.deepak.k2psap.Objects.PostItem;
import com.sapicons.deepak.k2psap.Others.CalculateDistance;
import com.sapicons.deepak.k2psap.Others.OnScrollObserver;
import com.sapicons.deepak.k2psap.Others.RecyclerViewTouchListener;
import com.sapicons.deepak.k2psap.Others.UserLocation;
import com.sapicons.deepak.k2psap.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Deepak Prasad on 29-09-2018.
 */

public class ExploreFragment extends Fragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {
    String TAG = "EXP_FRAG";

    ListView adListView;
    GridView gridView;
    LinearLayout filterAndLocationLL;
    LinearLayout emptyLL;

    //RecyclerView adRecyclerView;
    ViewPager mostRecentViewPager;
    List<PostItem> nearbyPostList;
    List<PostItem> selectedCategoryPostList;
    List<PostItem> searchList;
    AdPostAdapter postItemAdapter;
    AdPostGridViewAdapter gridViewAdapter;
    //RecyclerView.Adapter postItemRAdapter;
    //RecyclerView.LayoutManager mLayoutManager;
    Context context;


    PopupWindow popupWindow;
    List<CategoryItem> categoryList = NavigationActivity.getCategoryList();

    // related to slideshow
    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.
    int NUM_PAGES=0;
    Handler handler;
    Runnable Update;


    // for location and category selection
    RelativeLayout selectCategoryRl, selectLocationRl;
    TextView categorySelectedTv, selectedLocationTv;

    int PLACE_PICKER_REQUEST = 1;
    ProgressDialog progressDialog ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("");

        // setup options menu
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_explore, container, false);
        //adListView = view.findViewById(R.id.frag_explore_ads_list_view);

        context = getActivity();
        //getActivity().getTheme().applyStyle(R.style.CustomTheme, true);

        //getActivity().getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getActivity().getActionBar().setDisplayShowCustomEnabled(true);
        //getActivity().getActionBar().setCustomView(R.layout.custom_app_bar_layout);
        //View appBarView = getActivity().getActionBar().getCustomView();
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        //getCategoriesFromDatabase();
        initialiseViews(view);
        setOnClickListeners();
        setAddress();
        listenToChanges(false);
    }

    public void initialiseViews(View view) {


        adListView = view.findViewById(R.id.frag_explore_ads_list_view);
        gridView = view.findViewById(R.id.frag_explore_gridview);

        //adRecyclerView = view.findViewById(R.id.frag_explore_ads_recycler_view);
        mostRecentViewPager = view.findViewById(R.id.frag_explore_vp);
        progressDialog = new ProgressDialog(context);

        //mLayoutManager = new LinearLayoutManager(context);
        //adRecyclerView.setLayoutManager(mLayoutManager);

        //postItemRAdapter = new AdPostRecyclerAdapter(context,postList);
        //adRecyclerView.setAdapter(postItemRAdapter);


        // for location and category selection
        selectCategoryRl = view.findViewById(R.id.frag_explore_select_category_rl);
        selectLocationRl = view.findViewById(R.id.frag_explore_select_location_rl);
        categorySelectedTv = view.findViewById(R.id.frag_explore_category_selected_tv);
        selectedLocationTv = view.findViewById(R.id.frag_explore_address_tv);

        filterAndLocationLL = view.findViewById(R.id.frag_explore_filter_ll);
        emptyLL = view.findViewById(R.id.frag_exp_empty_listview_ll);


        nearbyPostList = new ArrayList<>();
        postItemAdapter = new AdPostAdapter(context, R.layout.item_ad, nearbyPostList);
        adListView.setAdapter(postItemAdapter);
        adListView.setEmptyView(emptyLL);
        setGridOrList();

        gridViewAdapter = new AdPostGridViewAdapter(context,R.layout.item_ad_grid,nearbyPostList);
        gridView.setAdapter(gridViewAdapter);
        gridView.setEmptyView(emptyLL);





    }

    public void setOnClickListeners(){

        setScrollListener();

        adListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG,"Ad Clicked: "+i);
                Intent intent = new Intent(getActivity(), AdPreviewActivity.class);
                //Bundle bundle =
                PostItem item = (PostItem) adapterView.getItemAtPosition(i);
                intent.putExtra("selected_post_item", item);
                startActivity(intent);
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG,"Ad Clicked: "+i);
                Intent intent = new Intent(getActivity(), AdPreviewActivity.class);
                //Bundle bundle =
                PostItem item = searchList.get(i);
                Log.d(TAG,"PI: "+item.getTitle());
                intent.putExtra("selected_post_item", item);
                startActivity(intent);
            }
        });

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


        selectCategoryRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupWindow popupWindow = popupCategories();
                popupWindow.showAtLocation(mostRecentViewPager, Gravity.CENTER,0,0);
            }
        });

        selectLocationRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog("Please Wait ...");
                //startActivity(new Intent(context, MapsActivity.class));
                createPlacePicker();


            }
        });
    }

    public void listenToChanges(final boolean isMapActivityClosed) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //final CollectionReference docRef = db.collection("users").document(user.getEmail()).collection("");
        CollectionReference collectionReference = db.collection("ads");
        Query query = collectionReference.whereEqualTo("status","open");

        query
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
                            Log.d("EXPL_FRAG", "Post: " + newItem.getTitle());
                            if (isNearby(newItem))  // filter according to distance
                                new_list.add(newItem);

                        }
                        nearbyPostList = new_list;
                        Collections.sort(nearbyPostList,PostItem.PostTimeComparator);
                        selectedCategoryPostList = new_list;
                        searchList = new_list;



                        // list view setup
                        postItemAdapter = new AdPostAdapter(context, R.layout.item_ad, nearbyPostList);
                        adListView.setAdapter(postItemAdapter);
                        setGridOrList();

                        // recycler view setup
                        //postItemRAdapter = new AdPostRecyclerAdapter(context,postList);
                        //adRecyclerView.setAdapter(postItemRAdapter);

                        //postItemRAdapter.notifyDataSetChanged();
                        //setUpViewPager(isMapActivityClosed);
                        //progressDialog.dismiss();

                        // gridview setup
                        gridViewAdapter = new AdPostGridViewAdapter(context,R.layout.item_ad_grid,nearbyPostList);
                        gridView.setAdapter(gridViewAdapter);


                    }
                });
    }

    public void setUpViewPager(boolean isMapActivityClosed) {

        List<PostItem> newList = nearbyPostList;
        Collections.sort(newList, PostItem.PostTimeComparator);
        int noOfItemsToShow = (newList.size()>5)?5:newList.size();
        NUM_PAGES = noOfItemsToShow;
        newList = newList.subList(0, noOfItemsToShow);
        AdPostViewPagerAdapter adapter = new AdPostViewPagerAdapter(context, newList);
        mostRecentViewPager.setAdapter(adapter);

        if(!isMapActivityClosed)
            setUpSlideShow();
    }

    public boolean isNearby(PostItem postItem) {
        CalculateDistance calculateDistance = new CalculateDistance(context);
        double lat1 = postItem.getLatitude();
        double long1 = postItem.getLongitude();

        double distanceInKMeters = calculateDistance.distanceInKM(lat1,long1);

        Log.d("EXP_FRAG","Distance: "+distanceInKMeters);

        return calculateDistance.isNearby(distanceInKMeters);
    }

    public void showProgressDialog(String msg){

        progressDialog.setMessage(msg);
        progressDialog.show();

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.collapsed_search_menu,menu);
        //inflater.inflate(R.menu.sort_by_type_menu,menu);

        MenuItem sortItem = menu.findItem(R.id.action_sort);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search Nearby Ads ...");
        searchView.setOnQueryTextListener(this);


        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        /*if(itemId == R.id.action_sort){

            //Toasty.normal(context,"Clicked").show();

            //View view = getLayoutInflater().inflate(R.layout.fragment_explore,null);
            PopupWindow popupWindow = popupCategories();
            popupWindow.showAtLocation(mostRecentViewPager, Gravity.CENTER,0,0);
        }*/
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

        mostRecentViewPager.setVisibility(View.GONE);


        if (newText == null || newText.trim().isEmpty()) {
            resetSearch();
            return false;
        }


        List<PostItem> filteredValues = new ArrayList<>(selectedCategoryPostList);
        for (PostItem value : selectedCategoryPostList) {

            String searchString = value.getTitle()+ " " + value.getCategoryName()+" "+ value.getDescription();
            searchString=searchString.toLowerCase();

            if (!searchString.contains(newText.toLowerCase())) {

                filteredValues.remove(value);

            }
        }
        searchList = filteredValues;
        postItemAdapter = new AdPostAdapter(context, R.layout.item_ad, filteredValues);
        adListView.setAdapter(postItemAdapter);
        setGridOrList();

        gridViewAdapter = new AdPostGridViewAdapter(context,R.layout.item_ad_grid,filteredValues);
        gridView.setAdapter(gridViewAdapter);

        return false;
    }

    public void filterByCategory(String category){
        if(category == null || category.trim().isEmpty()){
            return ;
        }

        selectedCategoryPostList = new ArrayList<>(nearbyPostList);
        for(PostItem item : nearbyPostList){
            String searchString = item.getCategoryName().toLowerCase();

            if(!searchString.contains(category.toLowerCase()))
                selectedCategoryPostList.remove(item);
        }

        searchList = selectedCategoryPostList;
        postItemAdapter = new AdPostAdapter(context, R.layout.item_ad, selectedCategoryPostList);
        adListView.setAdapter(postItemAdapter);
        setGridOrList();

        gridViewAdapter = new AdPostGridViewAdapter(context,R.layout.item_ad_grid,selectedCategoryPostList);
        gridView.setAdapter(gridViewAdapter);
    }



    public void resetSearch(){

        //mostRecentViewPager.setVisibility(View.VISIBLE);
        searchList = selectedCategoryPostList;
        postItemAdapter = new AdPostAdapter(context, R.layout.item_ad, selectedCategoryPostList);
        adListView.setAdapter(postItemAdapter);
        setGridOrList();

        gridViewAdapter = new AdPostGridViewAdapter(context,R.layout.item_ad_grid,selectedCategoryPostList);
        gridView.setAdapter(gridViewAdapter);
    }


    private PopupWindow popupCategories() {

        // initialize a pop up window type
        popupWindow = new PopupWindow(context);

        ArrayList<String> filterList = new ArrayList<String>();
        for(CategoryItem item: categoryList){
            filterList.add(item.getName());
        }

        final CategoryAdapter adapter = new CategoryAdapter(context, R.layout.item_single_category, categoryList);
        // the drop down list is a list view

        ListView listViewSort = new ListView(context);

        // set our adapter and pass our pop up window contents
        listViewSort.setAdapter(adapter);
        listViewSort.setBackgroundColor(Color.parseColor("#ffffff"));

        // set on item selected
        listViewSort.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // set filter
                String cateName = ((CategoryItem)adapter.getItem(i)).getName();
                Log.d(TAG,"CAT_SELECTED: "+cateName);

                //filter by category
                filterByCategory(cateName);
                categorySelectedTv.setText(cateName);

                popupWindow.dismiss();
            }
        });

        // some other visual settings for popup window
        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        // popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.white));
        popupWindow.setHeight(800);
        // set the listview as popup content
        popupWindow.setContentView(listViewSort);


        return popupWindow;
    }

    public void setUpSlideShow(){

        handler = new Handler();
        Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                else
                    mostRecentViewPager.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer .schedule(new TimerTask() { // task to be scheduled

            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);
    }


    @Override
    public void onResume() {
        if(progressDialog.isShowing())
            progressDialog.dismiss();

        //listenToChanges(true);
        super.onResume();
    }


    public void createPlacePicker(){

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();


        try {
            Log.d(TAG,"opening startActivityforResult");
            startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {

                Place place = PlacePicker.getPlace(data, getActivity());
                LatLng latLng = place.getLatLng();
                Log.d(TAG,"LatLng: "+latLng);


                saveLocation(latLng);

                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(context, toastMsg, Toast.LENGTH_LONG).show();

                listenToChanges(true);
                setAddress();
            }
        }
    }

    private void saveLocation(LatLng latLng){

        UserLocation userLocation = new UserLocation(context);
        userLocation.saveLocationToSharedPreferences(latLng);

    }

    private void setAddress(){
        UserLocation userLocation = new UserLocation(context);
        Location location = userLocation.getSavedLocation();
        String address = userLocation.getAddress(location.getLatitude(),location.getLongitude());
        selectedLocationTv.setText(address);
    }

    private void setGridOrList(){
        adListView.setVisibility(View.GONE);
    }

    private  void setScrollListener(){
        gridView.setOnScrollListener(new OnScrollObserver() {
            @Override
            public void onScrollUp() {
                Log.d(TAG,"Scroll Up");

                filterAndLocationLL.setVisibility(View.VISIBLE);
            }

            @Override
            public void onScrollDown() {

                Log.d(TAG,"Scroll Down");
                filterAndLocationLL.setVisibility(View.GONE);
            }
        });


    }
}
