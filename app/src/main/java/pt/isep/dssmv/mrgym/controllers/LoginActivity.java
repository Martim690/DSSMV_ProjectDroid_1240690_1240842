package pt.isep.dssmv.mrgym.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import pt.isep.dssmv.mrgym.R;
import pt.isep.dssmv.mrgym.utils.SharedPrefsUtils;
import pt.isep.dssmv.mrgym.network.RestClient;
import com.google.gson.JsonObject;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin, btnGoToRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (SharedPrefsUtils.getEmail(this) != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                if(email.isEmpty() || password.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Preenche todos os campos!", Toast.LENGTH_SHORT).show();
                    return;
                }
                loginUser(email, password);
            }
        });

        btnGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void loginUser(final String email, String password) {
        String url = "https://mrgym-dbdf.restdb.io/rest/users?q={\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
        String apiKey = "a9a7646274509cf090183d26fba41bb423130";

        RestClient.makeRequest(url, "GET", null, apiKey, new RestClient.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                if (result.length() > 2) { 
                    SharedPrefsUtils.saveSession(LoginActivity.this, email, "dummy_token");
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Credenciais inv\u00e1lidas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(LoginActivity.this, "Erro de rede: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
