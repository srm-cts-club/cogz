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
    TextView err_msg;
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
        register = (TextView) findViewById(R.id.text_register);
        err_msg = findViewById(R.id.error_msg);

        setViewOnclickListeners();
    }

    public void login() {
        try {
            err_msg.setVisibility(View.GONE);
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
                                editor.commit();
                                Intent intent = new Intent(context, HomePage.class);
                                context.startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            int errCode = error.getErrorCode();
                            if (errCode == 400) {
                                try {
                                    JSONObject errorbody = new JSONObject(error.getErrorBody());
                                    if (errorbody.getString("message").equals("Invalid credentials")) {
                                        err_msg.setText("Incorrect credentials");
                                        err_msg.setVisibility(View.VISIBLE);
                                    } else {
                                        err_msg.setText("Unable to login, Please try again");
                                        err_msg.setVisibility(View.VISIBLE);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    err_msg.setText("Unable to login, Please try again");
                                    err_msg.setVisibility(View.VISIBLE);
                                }

                                //most likely username already exist, check error body to confirm
                            } else {
                                err_msg.setText("Unable to login, Please try again");
                                err_msg.setVisibility(View.VISIBLE);
                            }
                            // handle error
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setViewOnclickListeners() {

        register.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context, SignUp.class);
                        startActivity(i);
                    }
                }
        );
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }


}
