package com.bxtore.dev.bxt.Activities;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.bxtore.dev.bxt.Adapters.MsgItemAdapter;
import com.bxtore.dev.bxt.Objects.ChatItem;
import com.bxtore.dev.bxt.Objects.MsgItem;
import com.bxtore.dev.bxt.Objects.NotificationItem;
import com.bxtore.dev.bxt.Objects.UserItem;
import com.bxtore.dev.bxt.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Nullable;

import mehdi.sakout.fancybuttons.FancyButton;

public class ChatActivity extends AppCompatActivity {
    String TAG = "CHAT_ACTIVITY";
    ListView listView;
    EditText composeText;
    FancyButton sendBtn;
    FirebaseUser user;

    ChatItem chatItem;
    List<MsgItem> list;
    MsgItemAdapter adapter;

    String toUserName;
    String fromUserName;

    UserItem toUserItem;

    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        chatItem = (ChatItem)getIntent().getSerializableExtra("selected_chat");
        //setTitle(getIntent().getStringExtra("user_name"));

        user = FirebaseAuth.getInstance().getCurrentUser();
        fromUserName = user.getEmail();
        toUserName = (fromUserName.equals(chatItem.getUserIdOne())) ? chatItem.getUserIdTwo() : chatItem.getUserIdOne();

        progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Please Wait ...");
        progressDialog.show();

        getUserDetails(toUserName); // a tab in next lines indicate the following functions are called after completion of this function
            //initialiseViews();
            //fetchMessagesFromDatabase();
    }

    public void initialiseViews(){


        listView = findViewById(R.id.chat_activity_listview);
        composeText = findViewById(R.id.chat_activity_compose_text_et);
        sendBtn = findViewById(R.id.chat_activity_send_btn);
        sendBtn.setEnabled(false);

        list = new ArrayList<>();
        adapter = new MsgItemAdapter(this,R.layout.item_msg,list);
        listView.setAdapter(adapter);

        // send btn is disabled for empty text
        composeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(composeText.getText().toString().trim().length() > 0)
                    sendBtn.setEnabled(true);
                else
                    sendBtn.setEnabled(false);

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(composeText.getText().toString().trim().length() > 0)
                    sendBtn.setEnabled(true);
                else
                    sendBtn.setEnabled(false);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addMessageToDatabase(composeText.getText().toString());
                composeText.setText("");
            }
        });





    }

    public void addMessageToDatabase(final String msg){
        //String msg = composeText.getText().toString();
        final String msgId = Calendar.getInstance().getTimeInMillis()+"";


        MsgItem newMsg = new MsgItem(msgId,toUserName,fromUserName,msg);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("messages").document(chatItem.getChatId())
                .collection("messages").document(msgId);

        documentReference.set(newMsg).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG,"Successfully added message.");
                sendNotification(msg);
                addLatestTimestampInChats(msgId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Failed to add message. "+e);
                composeText.setText("");
            }
        });
    }

    public void addLatestTimestampInChats(String msgId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("chats").document(chatItem.getChatId());
        documentReference.update("lastMsgTimestamp",Long.parseLong(msgId))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Error updating lastMsgTimeStamp: "+e);
                    }
                });
    }


    public void fetchMessagesFromDatabase(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference msgRef = db.collection("messages").document(chatItem.getChatId())
                .collection("messages");

        msgRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    Log.d(TAG,"Listen Failed",e);
                    return;
                }

                List<MsgItem> newlist = new ArrayList<>();
                for(QueryDocumentSnapshot doc: queryDocumentSnapshots){
                    Log.d(TAG,"MSG_ITEM: "+doc);
                    MsgItem item=doc.toObject(MsgItem.class);
                    newlist.add(item);
                }
                list = newlist;
                adapter = new MsgItemAdapter(ChatActivity.this,R.layout.item_msg,list);
                listView.setAdapter(adapter);
                progressDialog.dismiss();
            }
        });
    }

    public void getUserDetails(String userName){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userName);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){

                        toUserItem = documentSnapshot.toObject(UserItem.class);
                        Log.d(TAG,"TO USER ITEM: "+toUserItem);
                        setTitle(toUserItem.getName());
                        initialiseViews();
                        fetchMessagesFromDatabase();
                    }
                }else{
                    Log.d(TAG,"Failed with exception: "+task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Failed to fetch user data. E: "+e);
            }
        });
    }


    public void sendNotification(String msg){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference  = db.collection("users").document(toUserName)
                .collection("notification");

        NotificationItem newNotification = new NotificationItem(toUserItem.getTokenId(),
                msg,user.getDisplayName());

        collectionReference.add(newNotification).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"Failed to add notification to firestore: "+e);
            }
        });

    }
}
