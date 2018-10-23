package com.sapicons.deepak.k2psap.Fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sapicons.deepak.k2psap.Activities.AdPreviewActivity;
import com.sapicons.deepak.k2psap.Activities.ChatActivity;
import com.sapicons.deepak.k2psap.Adapters.AdPostAdapter;
import com.sapicons.deepak.k2psap.Adapters.ChatItemAdapter;
import com.sapicons.deepak.k2psap.Objects.ChatItem;
import com.sapicons.deepak.k2psap.Objects.PostItem;
import com.sapicons.deepak.k2psap.Others.RecyclerViewTouchListener;
import com.sapicons.deepak.k2psap.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Deepak Prasad on 01-10-2018.
 */

public class ChatsFragment extends Fragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

    RecyclerView chatsRecyclerView;
    List<ChatItem> chatList;
    RecyclerView.Adapter chatItemAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    Context context;
    ProgressDialog progressDialog;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().setTitle("Chats ");

        View view = inflater.inflate(R.layout.fragment_chats,container,false);

        context = getActivity();
        progressDialog = new ProgressDialog(context);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        initialiseViews(view);
        listenToChanges();
    }

    public void initialiseViews(View view){


        chatList= new ArrayList<>();
        chatsRecyclerView = view.findViewById(R.id.frag_chat_recycler_view);

        mLayoutManager = new LinearLayoutManager(context);
        chatsRecyclerView.setLayoutManager(mLayoutManager);

        chatItemAdapter = new ChatItemAdapter(context,chatList);
        chatsRecyclerView.setAdapter(chatItemAdapter);





        /*chatsRecyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getActivity()
                , chatsRecyclerView, new RecyclerViewTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ChatItem chatItem = chatList.get(position);
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                //Bundle bundle =
                intent.putExtra("selected_chat",chatItem);
                intent.putExtra("")
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));*/


    }

    public void listenToChanges(){
        progressDialog.setMessage("Please Wait ...");
        progressDialog.show();


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference docRef = db.collection("chats");
        Query query = docRef.whereEqualTo("status", "open");

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("CHAT_FRAG", "Listen failed.", e);
                            return;
                        }

                        List<ChatItem> new_list = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            ChatItem newItem = doc.toObject(ChatItem.class);
                            Log.d("EXPL_FRAG","Post: "+newItem.getChatId());


                            // only show those chats which are
                            if(newItem.getLastMsgTimestamp()>0)
                                if(newItem.getUserIdOne().equals(user.getEmail()) ||
                                        newItem.getUserIdTwo().equals(user.getEmail()))
                                    new_list.add(newItem);

                        }
                        chatList = new_list;
                        Collections.sort(chatList,ChatItem.LastActiveChatComparator);
                        chatItemAdapter = new ChatItemAdapter(context,chatList);
                        chatsRecyclerView.setAdapter(chatItemAdapter);

                        //chatItemAdapter.notifyDataSetChanged();

                        progressDialog.dismiss();

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
