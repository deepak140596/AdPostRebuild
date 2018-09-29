package com.sapicons.deepak.k2psap.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.sapicons.deepak.k2psap.Objects.PostItem;
import com.sapicons.deepak.k2psap.R;

public class AdPreviewActivity extends AppCompatActivity {

    PostItem postItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_preview);

        postItem = (PostItem)getIntent().getSerializableExtra("selected_post_item");

        //initialiseViews();
        //downloadImagesForViewPager();
    }
}
