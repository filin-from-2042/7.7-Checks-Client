package com.tradeassistant1c77;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class KKMItemViewPagerActivity extends AppCompatActivity {


    public static String EXT_ITEM_ID = "com.tradeassistant1c77.KKMItemViewPagerActivity.KKMItemID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_kkm_item_view_pager);

        final String KKMItemID =  getIntent().getStringExtra(EXT_ITEM_ID);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int type) {
                return KKMItemVewFragment.newInstance(KKMItemID, type);
            }
            @Override
            public CharSequence getPageTitle(int position) {
                CharSequence res = "" ;
                switch (position){
                    case 0 : res="Шапка";break;
                    case 1 : res="Табличная часть";break;
                }
                return res;
            }
            @Override
            public int getCount() {
                return 2;
            }
        });
        mViewPager.setCurrentItem(1);

    }
}
