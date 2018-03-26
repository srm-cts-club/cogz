package karan.cogz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.renderscript.RenderScript;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;

import java.util.Date;
import java.util.HashMap;

public class SplashScreen extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        context = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final Date start = new Date();
        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.get("https://auth." + getString(R.string.cluster_name) + ".hasura-app.io/v1/user/info")
                .addHeaders(getRequestHeader())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // success
                        Date end = new Date();
                        if(end.getTime()-start.getTime()>2000) {
                            Intent success = new Intent(context, HomePage.class); //for trial
                            startActivity(success);
                            finish();
                        }
                        else{
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent success = new Intent(context, HomePage.class); //for trial
                                    startActivity(success);
                                    finish();
                                }
                            },2000-(end.getTime()-start.getTime()));
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        // failure
                        Date end = new Date();
                        if(end.getTime()-start.getTime()>2000) {
                            Intent fail = new Intent(context,LoginPage.class);// for trial
                            startActivity(fail);
                            finish();
                        }
                        else{
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent fail = new Intent(context,LoginPage.class);// for trial
                                    startActivity(fail);
                                    finish();
                                }
                            },2000-(end.getTime()-start.getTime()));
                        }
                    }
                });

    }

    public static HashMap<String, String> getRequestHeader() {
        HashMap<String, String> headParams = new HashMap<>();
        headParams.put("Content-Type", "application/json");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        headParams.put("Authorization", "Bearer " + sharedPreferences.getString("token", ""));
        return headParams;
    }
}
