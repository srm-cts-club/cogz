package karan.cogz;

import android.opengl.Visibility;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RemoteViews;

public class SignUp extends AppCompatActivity {
    Button signup;
    CheckBox select_mentor;
    EditText admin_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        signup = (Button) findViewById(R.id.button_login);
        select_mentor = (CheckBox) findViewById(R.id.checkBox);
        admin_key = (EditText) findViewById(R.id.adminKey);
        sign_up();
        mentor_select();
    }

    public void sign_up() {
        signup.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }
        );
    }

    public void mentor_select()
    {
        if (select_mentor.isChecked()) {
            admin_key.setVisibility(View.VISIBLE);
        }
    }
}
