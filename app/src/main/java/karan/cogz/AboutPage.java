package karan.cogz;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AboutPage extends AppCompatActivity {
 Button butt_on;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);
        butt_on=(Button)findViewById(R.id.button_about);
        onclick();
    }
    public void onclick()
    {
        butt_on.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent email = new Intent(Intent.ACTION_SENDTO);
                        email.setType("message/rfc822");
                        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"cts.srm@gmail.com"});
                        email.putExtra(Intent.EXTRA_SUBJECT, "subject");
                        email.putExtra(Intent.EXTRA_TEXT, "message");
                        email.setData(Uri.parse("mailto:"));

                        startActivity(Intent.createChooser(email, "Choose an Email client :"));
                    }
                }
        );
    }
}
