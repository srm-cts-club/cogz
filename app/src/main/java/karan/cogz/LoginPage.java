package karan.cogz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginPage extends AppCompatActivity {

    EditText username, password;
    Button login;
    TextView register;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        context = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        AndroidNetworking.initialize(getApplicationContext());
        username = (EditText) findViewById(R.id.editText_username);
        password = (EditText) findViewById(R.id.editText_password);
        login = (Button) findViewById(R.id.button_login);
        //adminlogin = (Button) findViewById(R.id.button_loginadmin);
        register = (TextView) findViewById(R.id.text_register);
        // login();
        signup();
    }

    public void login() {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("username", username.getText().toString());
            data.put("password", password.getText().toString());
            jsonObject.put("provider", "username");
            jsonObject.put("data", data);
            AndroidNetworking.post("https://auth." + getString(R.string.cluster_name) + ".hasura-app.io/v1/login")
                    .addJSONObjectBody(jsonObject)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // do anything with response
                            try {
                                editor.putString("token", response.getString("auth_token"));
                                editor.putString("username", response.getString("username"));
                                editor.putInt("hasura_id", response.getInt("hasura_id"));
                                editor.putString("acc_type", "student");
                                // todo: redirect to chat screen
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            int errCode = error.getErrorCode();
                            if (errCode == 400) {
                                //most likely username already exist, check error body to confirm
                            }

                            // handle error
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void signup() {
        try {
            register.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent("karan.cogz.SignUp");
                            startActivity(i);
                        }
                    }
            );
        }
        catch(Exception e)
        {
            Toast.makeText(this, ""+e , Toast.LENGTH_SHORT).show();
        }

    }
}
