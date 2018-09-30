package com.sapicons.deepak.k2psap.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sapicons.deepak.k2psap.Objects.ChatItem;
import com.sapicons.deepak.k2psap.Objects.ChatMsgItem;
import com.sapicons.deepak.k2psap.R;

import java.util.List;

/**
 * Created by Deepak Prasad on 30-09-2018.
 */

public class ChatMsgAdapter extends ArrayAdapter<ChatMsgItem> {

    Context context;
    FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();

    public ChatMsgAdapter(@NonNull Context context, int resource, @NonNull List<ChatMsgItem> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView==null)
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_msg, parent, false);

        TextView receivedTv = convertView.findViewById(R.id.item_msg_received_tv);
        TextView sentTv = convertView.findViewById(R.id.item_msg_sent_tv);
        ChatMsgItem chatMsgItem = getItem(position);

        if(chatMsgItem.getFrom().equals(user.getEmail())){
            receivedTv.setVisibility(View.GONE);
            sentTv.setVisibility(View.VISIBLE);
            sentTv.setText(chatMsgItem.getMsg());

        }else{
            sentTv.setVisibility(View.GONE);
            receivedTv.setVisibility(View.VISIBLE);
            receivedTv.setText(chatMsgItem.getMsg());
        }


        return  convertView;

    }
}
