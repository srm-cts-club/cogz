package karan.cogz;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.View;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    public MyFirebaseInstanceIDService() {
    }
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    private void sendRegistrationToServer(String token){
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            JSONObject jsonObject = new JSONObject();
            JSONObject args = new JSONObject();
            JSONObject where = new JSONObject();
            JSONObject set = new JSONObject();
            jsonObject.put("type", "update");
            args.put("table","users");
            where.put("$eq",sharedPreferences.getInt("hasura_id",0));
            set.put("fcm_id",token);
            args.put("where",where);
            args.put("$set",set);
            jsonObject.put("args",args);
            AndroidNetworking.post("https://data." + getString(R.string.cluster_name) + ".hasura-app.io/v1/query")
                    .addHeaders("Content-Type","application/json")
                    .addHeaders("Authorization","Bearer "+sharedPreferences.getString("token",""))
                    .addJSONObjectBody(jsonObject)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // do anything with response
                        }

                        @Override
                        public void onError(ANError error) {
                            int errCode = error.getErrorCode();
                            // handle error
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
