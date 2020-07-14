package com.gsu.graphology;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EmailActivity extends AppCompatActivity {

    private Button submitEmailBtn;
    private EditText emailAddressInput;
    private String emailAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        submitEmailBtn = findViewById(R.id.submit_email_btn);
        emailAddressInput = findViewById(R.id.editTextEmailAddress);
        getSupportActionBar().setTitle("SAMPLE SUBMISSION");

        submitEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailAddress =emailAddressInput.getText().toString();
                Intent uploadIntent = new Intent(EmailActivity.this,UploadActivity.class);
                uploadIntent.putExtra("emailID", emailAddress);
                startActivity(uploadIntent);
            }
        });

    }
}