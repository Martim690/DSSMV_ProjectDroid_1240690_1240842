package pt.isep.dssmv.mrgym.controllers;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import pt.isep.dssmv.mrgym.R;
import pt.isep.dssmv.mrgym.network.RestClient;
import com.google.gson.JsonObject;

public class RegisterActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPassword;
    private Button btnRegister, btnGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnGoToLogin = findViewById(R.id.btnGoToLogin);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if(name.isEmpty() || email.isEmpty() || password.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Preenche todos os campos!", Toast.LENGTH_SHORT).show();
                    return;
                }

                registerUser(name, email, password);
            }
        });

        btnGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void registerUser(String name, String email, String password) {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("email", email);
        json.addProperty("password", password);
        json.addProperty("active", true);
        
        String url = "https://mrgym-dbdf.restdb.io/rest/users";
        String apiKey = "a9a7646274509cf090183d26fba41bb423130";

        RestClient.makeRequest(url, "POST", json.toString(), apiKey, new RestClient.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(RegisterActivity.this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(RegisterActivity.this, "Erro de rede: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
