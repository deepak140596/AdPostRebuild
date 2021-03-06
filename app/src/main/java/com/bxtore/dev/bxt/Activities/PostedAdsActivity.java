package com.bxtore.dev.bxt.Activities;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bxtore.dev.bxt.Fragments.ActiveAdsFragment;
import com.bxtore.dev.bxt.Fragments.ClosedAdsFragment;
import com.bxtore.dev.bxt.R;

public class PostedAdsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Your Posts");
        setContentView(R.layout.activity_posted_ads);
        getSupportActionBar().setElevation(0);


        ViewPager vp_pages= findViewById(R.id.posted_ads_vp_pages);
        PagerAdapter pagerAdapter=new PostedAdsActivity.FragmentAdapter(getSupportFragmentManager());
        vp_pages.setAdapter(pagerAdapter);

        TabLayout tbl_pages= findViewById(R.id.posted_ads_tbl_pages);
        tbl_pages.setupWithViewPager(vp_pages);

    }

    class FragmentAdapter extends FragmentPagerAdapter {

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case 0:
                    //Toasty.info(PostedAdsActivity.this,"1").show();
                    return new ActiveAdsFragment();
                case 1:
                    //Toasty.info(PostedAdsActivity.this,"2").show();
                    return new ClosedAdsFragment();


            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                //
                //Your tab titles
                //
                case 0:
                    return "Active";
                case 1:
                    return "Closed";
                default:
                    return "Active";
            }


        }
    }
}
