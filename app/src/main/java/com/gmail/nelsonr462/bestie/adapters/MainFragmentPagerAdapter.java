package com.gmail.nelsonr462.bestie.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.gmail.nelsonr462.bestie.R;
import com.gmail.nelsonr462.bestie.ui.BestieRankFragment;
import com.gmail.nelsonr462.bestie.ui.VoteFragment;
import com.gmail.nelsonr462.bestie.ui.SettingsFragment;


public class MainFragmentPagerAdapter extends FragmentPagerAdapter{

    final int PAGE_COUNT = 3;

    private int[] imageResId = {
            R.drawable.tab_settings,
            R.drawable.tab_vote,
            R.drawable.tab_bestie
    };


    protected Context mContext;

    public MainFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new SettingsFragment();
            case 1:
                return new VoteFragment();
            case 2:
                return new BestieRankFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    public int getDrawableId(int position){
        return imageResId[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Drawable image = mContext.getResources().getDrawable(imageResId[position]);
        if(image != null) image.setBounds(0, 0, 128, 128);
        SpannableString sb = new SpannableString(" ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

}
