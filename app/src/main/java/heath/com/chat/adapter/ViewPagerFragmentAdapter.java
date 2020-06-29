package heath.com.chat.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerFragmentAdapter extends FragmentPagerAdapter {
 
    private Fragment[] fragments;
 
    public ViewPagerFragmentAdapter(FragmentManager fm, Fragment[] fragments) {
        super(fm);
        this.fragments=fragments;
    }
 
    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }
 
    @Override
    public int getCount() {
        return fragments.length;
    }
}
