package karan.cogz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomePage extends AppCompatActivity {


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    private ViewPager mViewPager;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        context = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        FragmentManager fm = getSupportFragmentManager();
        MyFragmentManager myFragmentManager = new MyFragmentManager(fm,3);


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(myFragmentManager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.Profileid) {
            Intent profileIntent = new Intent(context, ProfilePage.class);
            startActivity(profileIntent);
        } else if (id == R.id.Logoutid) {
            logout();

        } else if (id == R.id.Aboutid) {
            Intent aboutIntent = new Intent(context, AboutPage.class);
            startActivity(aboutIntent);
        }


        return super.onOptionsItemSelected(item);
    }

    private void logout(){
        AndroidNetworking.post("https://auth." + getString(R.string.cluster_name) + ".hasura-app.io/v1/user/logout")
                .addHeaders("Content-Type","application/json")
                .addHeaders("Authorization","Bearer "+sharedPreferences.getString("token",""))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        editor.putString("token", "");
                        editor.commit();
                        Intent intent = new Intent(context, LoginPage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onError(ANError error) {
                        int errCode = error.getErrorCode();
                        Toast.makeText(context,"Error logging out",Toast.LENGTH_SHORT);
                    }

                });
        FirebaseMessaging.getInstance().unsubscribeFromTopic("chatroom");
        if(sharedPreferences.getString("acc_type","").equals("student")){
            FirebaseMessaging.getInstance().unsubscribeFromTopic("task_"+sharedPreferences.getString("college","").replaceAll(" ","_").replaceAll("'",""));
        }
        FirebaseMessaging.getInstance().unsubscribeFromTopic("update");

    }
}
