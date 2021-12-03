package ie.ul.mobileappdevproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText loginEmail;
    EditText loginPassword;
    Button btnLogin;
    Button btnReg;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        btnLogin = findViewById(R.id.loginButton);
        btnReg = findViewById(R.id.regButton);

        auth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(view ->{
            loginUser();
        });
        btnReg.setOnClickListener(view ->{
            startActivity(new Intent(LoginActivity.this, RegestrationActivity.class));
        });
    }

    private void loginUser(){
        String email = loginEmail.getText().toString();
        String password = loginPassword.getText().toString();
        if (TextUtils.isEmpty(email)){
            loginEmail.setError("Email can not be empty");
            loginEmail.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            loginPassword.setError("Password can not be empty");
            loginPassword.requestFocus();
        }else{
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "User logged in successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }else{
                        Toast.makeText(LoginActivity.this, "Login error", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }


}