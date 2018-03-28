package karan.cogz;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import org.json.JSONObject;

public class MyFragmentManager  extends FragmentPagerAdapter {
    private int PAGE_COUNT;
    /** Constructor of the class */
    public MyFragmentManager(FragmentManager fm, int count) {
        super(fm);
        PAGE_COUNT = count;
    }

    @Override
    public Fragment getItem(int arg0) {
        if(arg0 == 0){
            Log.d("FRAGMENT","Fragment Updates"+arg0);
            UpdatesFragment myFragment = new UpdatesFragment();
            return myFragment;
        }
        else if(arg0 == 1){
            //todo: change to tasks fragment

            Log.d("FRAGMENT","Fragment Tasks"+arg0);
            return new TaskFragment();
        }
        else if(arg0 == 2){
            //todo: change to chatroom fragment

            Log.d("FRAGMENT","Fragment Chatroom"+arg0);
            return new Fragment();
        }

        Log.d("FRAGMENT","Fragment other "+arg0);
        return new Fragment();
    }

    /** Returns the number of pages */
    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

}
