package com.bxtore.dev.bxt.Adapters;

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
import com.bxtore.dev.bxt.Objects.MsgItem;
import com.bxtore.dev.bxt.R;

import java.util.List;

/**
 * Created by Deepak Prasad on 30-09-2018.
 */

public class MsgItemAdapter extends ArrayAdapter<MsgItem> {

    Context context;
    FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();

    public MsgItemAdapter(@NonNull Context context, int resource, @NonNull List<MsgItem> objects) {
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
        MsgItem msgItem = getItem(position);

        if(msgItem.getFrom().equals(user.getEmail())){
            receivedTv.setVisibility(View.GONE);
            sentTv.setVisibility(View.VISIBLE);
            sentTv.setText(msgItem.getMsg());

        }else{
            sentTv.setVisibility(View.GONE);
            receivedTv.setVisibility(View.VISIBLE);
            receivedTv.setText(msgItem.getMsg());
        }


        return  convertView;

    }
}
