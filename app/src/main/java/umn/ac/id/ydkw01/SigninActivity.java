package umn.ac.id.ydkw01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SigninActivity extends AppCompatActivity {
    EditText mEmail, mPassword;
    Button btnSignin;
    TextView btnTSignup;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        getSupportActionBar().hide();

        mEmail = findViewById(R.id.si_email);
        mPassword = findViewById(R.id.si_password);
        btnSignin = findViewById(R.id.signinbtn);
        btnTSignup = findViewById(R.id.createacc);
        fAuth = FirebaseAuth.getInstance();


        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Required");
                    return;
                }
                if(password.length()<8){
                    mPassword.setError("Password must be more than 8 character");
                    return;
                }
                if(!password.matches("(.*[0-9].*)"))
                {
                    mPassword.setError("Password must contain atleast one number");
                    return;
                }
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SigninActivity.this, "Sign In Successfull", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                            i.addFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        }
                        else {
                            Toast.makeText(SigninActivity.this, "Invalid Email or Password!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        btnTSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SigninActivity.this, SignupActivity.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = fAuth.getCurrentUser();
        if(currentUser != null){
            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
            i.addFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
    }
}