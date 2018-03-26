package karan.cogz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUp extends AppCompatActivity {


    EditText username, password;
    Button login;
    TextView register;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    Boolean isMentor = false;
    Button signup;
    CheckBox select_mentor;
    EditText admin_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        context = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        AndroidNetworking.initialize(getApplicationContext());
        username = (EditText) findViewById(R.id.editText_username);
        password = (EditText) findViewById(R.id.editText_password);
        //login = (Button) findViewById(R.id.button_login);
        //adminlogin = (Button) findViewById(R.id.button_loginadmin);
        signup = (Button) findViewById(R.id.button_sign_up);
        select_mentor = (CheckBox) findViewById(R.id.checkBox);
        admin_key = (EditText) findViewById(R.id.adminKey);
        sign_up();
        mentor_select();
        register = (TextView) findViewById(R.id.text_register);
        // login();
        sign_up();
    }

    public void sign_up() {
        signup.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            JSONObject data = new JSONObject();
                            data.put("username", username.getText().toString());
                            data.put("password", password.getText().toString());
                            jsonObject.put("provider", "username");
                            jsonObject.put("data", data);
                            AndroidNetworking.post("https://auth." + getString(R.string.cluster_name) + ".hasura-app.io/v1/signup")
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
                                                if (!isMentor) {
                                                    // todo: update other data
                                                    // todo: redirect to chat screen
                                                } else {
                                                    //todo: perform acc upgrade request
                                                    // todo: update other data
                                                    // todo: redirect to chat screen
                                                }
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
                }
        );
    }

    public void mentor_select() {
        select_mentor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    admin_key.setVisibility(View.VISIBLE);
                } else {
                    admin_key.setVisibility(View.GONE);
                }
            }
        });
    }

    public void login() {
        register.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context, LoginPage.class);
                        startActivity(i);
                    }
                }
        );
    }
}
