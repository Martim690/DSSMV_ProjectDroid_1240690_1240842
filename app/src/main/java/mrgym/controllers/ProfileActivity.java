package mrgym.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import mrgym.R;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUserEmail;
    private Button btnLogoutProfile, btnBackProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvUserEmail = findViewById(R.id.tvUserEmail);
        btnLogoutProfile = findViewById(R.id.btnLogoutProfile);
        btnBackProfile = findViewById(R.id.btnBackProfile);

        String email = SharedPrefsUtils.getEmail(this);
        if (email != null) {
            tvUserEmail.setText(email);
        } else {
            tvUserEmail.setText("guest@mrgym.com");
        }

        btnLogoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefsUtils.clearSession(ProfileActivity.this);
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        btnBackProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
