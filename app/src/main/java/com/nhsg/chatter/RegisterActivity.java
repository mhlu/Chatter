package com.nhsg.chatter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_register);

        Bundle extras = getIntent().getExtras();
        String extraName = extras.getString("name");
        String extraPassword = extras.getString("password");
        if( !extraName.isEmpty() )
            ((EditText) findViewById(R.id.username_reg)).setText(extraName);
        if( !extraPassword.isEmpty() )
            ((EditText) findViewById(R.id.password_reg)).setText(extraPassword);

        Button registerBtn = (Button) findViewById(R.id.register_button);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ((EditText) findViewById(R.id.username_reg)).getText().toString();
                String password = ((EditText) findViewById(R.id.password_reg)).getText().toString();
                String password_confirm = ((EditText) findViewById(R.id.password_confirm_reg)).getText().toString();
                String email = ((EditText) findViewById(R.id.email_reg)).getText().toString();

                if( !password.equals(password_confirm) ) {
                    EditText confirmET = (EditText) findViewById(R.id.password_confirm_reg);
                    confirmET.setError("Passwords must match.");
                }

                if( !isEmailValid(email)) {
                    EditText emailET = (EditText) findViewById(R.id.email_reg);
                    emailET.setError("Please input a valid email address");
                }

                // TODO: send registration request to server
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
