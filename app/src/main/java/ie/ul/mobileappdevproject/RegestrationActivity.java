package ie.ul.mobileappdevproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegestrationActivity extends AppCompatActivity {

    EditText regEmail;
    EditText regPassword;
    Button btnReg;
    Button btnCancel;


    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regestration);

        regEmail = findViewById(R.id.regEmail);
        regPassword = findViewById(R.id.regPassword);
        btnReg = findViewById(R.id.regRegButton);
        btnCancel = findViewById(R.id.cancel);

        auth = FirebaseAuth.getInstance();

        btnReg.setOnClickListener(view ->{
            createUser();
        });

        btnCancel.setOnClickListener(view->{
            startActivity(new Intent(RegestrationActivity.this, LoginActivity.class));
        });
    }

    private void createUser(){
       String email = regEmail.getText().toString();
       String password = regPassword.getText().toString();
       if (TextUtils.isEmpty(email)){
           regEmail.setError("Email can not be empty");
           regEmail.requestFocus();
       }else if (TextUtils.isEmpty(password)){
           regPassword.setError("Password can not be empty");
           regPassword.requestFocus();
       }else{
           auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
               @Override
               public void onComplete(@NonNull Task<AuthResult> task) {
                   if (task.isSuccessful()){
                       Toast.makeText(RegestrationActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                       startActivity(new Intent(RegestrationActivity.this, LoginActivity.class));
                   }else{
                       Toast.makeText(RegestrationActivity.this, "Registration Error", Toast.LENGTH_SHORT).show();

                   }
               }
           });
       }
    }
}