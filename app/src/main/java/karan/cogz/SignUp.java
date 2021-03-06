package karan.cogz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class SignUp extends AppCompatActivity {


    EditText username, password;
    TextView login;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;
    Boolean isMentor = false;
    Button signup;
    CheckBox select_mentor;
    EditText admin_key;
    AutoCompleteTextView textViewCollege;
    AutoCompleteTextView textViewDomain;
    TextView errormsg;
    EditText name;
    EditText passconf;
    ProgressBar pb;
    String[] arrayC = {"SSN", "RMK", "RMD", "Velammal Engineering College", "Sathyabama", "St.Joseph's Institute of Technology", "RMKCET", "SRM University", "Velammal institute of Technology","B.S.Abdur Rahman Institute of Science and Technology", "Sri Venkateswara College of Engineering","Meenakshi Sundararajan College","St.Joseph's College of Engineering","VIT Chennai","IIT Madras"};
    String[] arrayD = {"HC NA", "LS", "BFS", "BFS-TAO", "AVM CommsMedia", "AVM Ismo", "AVM EAS", "RCGTH", "P&R","MLEU P&R"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        context = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();

        AndroidNetworking.initialize(getApplicationContext());

        username = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.pass1);
        passconf = findViewById(R.id.pass2);
        login = findViewById(R.id.text_login);
        signup = (Button) findViewById(R.id.button_sign_up);
        select_mentor = (CheckBox) findViewById(R.id.checkBox);
        admin_key = (EditText) findViewById(R.id.adminKey);
        textViewCollege = findViewById(R.id.autoComplete_college);
        textViewDomain = findViewById(R.id.autoComplete_domain);
        errormsg = findViewById(R.id.error_msg);
        name = findViewById(R.id.name);
        pb = findViewById(R.id.progressBar2);
        setViewOnclickListeners();
    }

    public void sign_up() {
        try {
            Boolean val = validateInput();
            if(val) {
                errormsg.setVisibility(View.GONE);
                signup.setVisibility(View.GONE);
                pb.setIndeterminate(true);
                pb.setVisibility(View.VISIBLE);
                JSONObject jsonObject = new JSONObject();
                errormsg.setVisibility(View.GONE);
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
                                    editor.commit();
                                    if (!isMentor) {
                                        updateOtherData();
                                    } else {
                                        upgradeAcc();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(ANError error) {
                                pb.setVisibility(View.GONE);
                                pb.setIndeterminate(false);
                                signup.setVisibility(View.VISIBLE);
                                int errCode = error.getErrorCode();
                                if (errCode == 400) {
                                    JSONObject errBody = null;
                                    try {
                                        errBody = new JSONObject(error.getErrorBody());
                                        if (errBody.getString("message").equals("This user already exists")) {
                                            errormsg.setText("Username already taken");
                                            errormsg.setVisibility(View.VISIBLE);
                                        } else {
                                            errormsg.setText("Couldn't sign up, Please try again");
                                            errormsg.setVisibility(View.VISIBLE);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        errormsg.setText("Couldn't sign up, Please try again");
                                        errormsg.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    errormsg.setText("Couldn't sign up, Please try again");
                                    errormsg.setVisibility(View.VISIBLE);
                                }

                                // handle error
                            }
                        });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void setViewOnclickListeners(){
        select_mentor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isMentor = isChecked;
                if (isChecked) {
                    admin_key.setVisibility(View.VISIBLE);
                    textViewCollege.setVisibility(View.GONE);
                    textViewDomain.setVisibility(View.VISIBLE);
                } else {
                    admin_key.setVisibility(View.GONE);
                    textViewCollege.setVisibility(View.VISIBLE);
                    textViewDomain.setVisibility(View.GONE);
                }
            }
        });

        login.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context, LoginPage.class);
                        startActivity(i);
                    }
                }
        );
        signup.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       sign_up();
                    }
                }
        );
        ArrayAdapter<String> adapterC = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, arrayC);
        textViewCollege.setAdapter(adapterC);

        ArrayAdapter<String> adapterD = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, arrayD);
        textViewDomain.setAdapter(adapterD);
    }
    private void upgradeAcc() {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mentor_secret", admin_key.getText().toString());
            jsonObject.put("userid", sharedPreferences.getInt("hasura_id",0));
            AndroidNetworking.post("https://api." + getString(R.string.cluster_name) + ".hasura-app.io/mentor/addmentor")
                    .addJSONObjectBody(jsonObject)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("UPGRADE OK",response.toString());
                            // do anything with response
                            editor.putString("acc_type", "mentor");
                            editor.commit();
                            relogin();
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.d("UPGRADE FAIL",error.getErrorBody());
                            deleteUser();
                            int errCode = error.getErrorCode();
                            if (errCode == 401) {
                                errormsg.setText("Unauthorised mentor key !");
                                errormsg.setVisibility(View.VISIBLE);
                            } else {
                                Log.d("Signup","upgrade error "+errCode);
                                errormsg.setText("Couldn't sign up, Please try again");
                                errormsg.setVisibility(View.VISIBLE);
                            }
                            // handle error
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void relogin(){
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
                                JSONArray roles = response.getJSONArray("hasura_roles");
                                if(roles.length()==2 && roles.getString(1).equals("cts")){
                                    editor.putString("acc_type", "mentor");
                                }
                                else {
                                    editor.putString("acc_type", "student");
                                }
                                editor.commit();
                                updateOtherData();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError error) {
                            deleteUser();
                            errormsg.setText("Couldn't sign up, Please try again");
                            errormsg.setVisibility(View.VISIBLE);
                            // handle error
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void updateOtherData(){
        editor.putString("name",name.getText().toString());
        editor.putString("college", textViewCollege.getText().toString());
        editor.putString("domain",textViewDomain.getText().toString());
        editor.commit();
        try {

            JSONObject jsonObject = new JSONObject();
            JSONObject args = new JSONObject();
            JSONObject object = new JSONObject();
            jsonObject.put("type", "insert");
            args.put("table","users");
            object.put("id",sharedPreferences.getInt("hasura_id",0));
            object.put("name",name.getText().toString());
            if(isMentor){
                object.put("college",textViewDomain.getText().toString());
            }
            else {
                object.put("college", textViewCollege.getText().toString());
            }
            object.put("fcm_id", FirebaseInstanceId.getInstance().getToken());
            JSONArray arr = new JSONArray();
            arr.put(object);
            args.put("objects",arr);
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
                            subscribeToFCM();
                        }

                        @Override
                        public void onError(ANError error) {

                            deleteUser();
                            int errCode = error.getErrorCode();
                            Log.d("Signup - updateother","Error code = "+errCode+ " Error details = "+error.getErrorBody());
                            if (errCode == 400) {
                                errormsg.setText("Couldn't sign up, Please try again");
                                errormsg.setVisibility(View.VISIBLE);
                            } else {
                                errormsg.setText("Couldn't sign up, Please try again");
                                errormsg.setVisibility(View.VISIBLE);
                            }
                            // handle error
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void subscribeToFCM() {
        FirebaseMessaging.getInstance().subscribeToTopic("chatroom");
        if(sharedPreferences.getString("acc_type","").equals("student")){
            FirebaseMessaging.getInstance().subscribeToTopic("task_"+sharedPreferences.getString("college","").replaceAll(" ","_").replaceAll("'",""));
        }
        FirebaseMessaging.getInstance().subscribeToTopic("update");
        Intent i = new Intent(context,HomePage.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
    }

    private void deleteUser(){
        try {
            Log.d("DELETE USER", "Deleting user");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userid", sharedPreferences.getInt("hasura_id",0));
            AndroidNetworking.post("https://api." + getString(R.string.cluster_name) + ".hasura-app.io/user/delete")
                    .addJSONObjectBody(jsonObject)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("DELETE USER OK", response.toString());
                            pb.setVisibility(View.GONE);
                            pb.setIndeterminate(false);
                            signup.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.d("DELETE USER FAIL",error.getErrorBody());
                            pb.setVisibility(View.GONE);
                            pb.setIndeterminate(false);
                            signup.setVisibility(View.VISIBLE);
                            // handle error
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private Boolean validateInput(){
        Boolean flag = true;
        if(name.getText().toString().equals("")){
            errormsg.setText("Name cannot be empty !");
            errormsg.setVisibility(View.VISIBLE);
            flag = false;
            return flag;
        }
        else if(username.getText().toString().equals("")){
            errormsg.setText("Username cannot be empty !");
            errormsg.setVisibility(View.VISIBLE);
            flag = false;
            return flag;
        }
        else if(password.getText().toString().length()<6){
            errormsg.setText("Password must be atleast 6 characters long");
            errormsg.setVisibility(View.VISIBLE);
            flag = false;
            return  flag;
        }
        else if(!passconf.getText().toString().equals(password.getText().toString())){
            errormsg.setText("Passwords do not match");
            errormsg.setVisibility(View.VISIBLE);
            flag = false;
            return flag;
        }
        if(isMentor){
            String domain = textViewDomain.getText().toString();
            if(!Arrays.asList(arrayD).contains(domain)){
                errormsg.setText("Invalid domain");
                errormsg.setVisibility(View.VISIBLE);
                flag = false;
                return flag;
            }
        }
        else{
            String clg = textViewCollege.getText().toString();
            if(!Arrays.asList(arrayC).contains(clg)){
                errormsg.setText("Invalid College");
                errormsg.setVisibility(View.VISIBLE);
                flag = false;
                return flag;
            }
        }
        return flag;
    }
}
