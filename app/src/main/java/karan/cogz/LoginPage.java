package karan.cogz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginPage extends AppCompatActivity {

    EditText username, password;
    Button login;
    TextView register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
            username = (EditText) findViewById(R.id.editText_username);
            password = (EditText) findViewById(R.id.editText_password);
            login = (Button) findViewById(R.id.button_login);
            //adminlogin = (Button) findViewById(R.id.button_loginadmin);
            register = (TextView) findViewById(R.id.text_register);
            // login();
            signup();
        }
    public void login()
    {

    }
    public void signup()
    {
        register.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i=new Intent("karan.SignUp");
                        startActivity(i);
                    }
                }
        );
    }
}
