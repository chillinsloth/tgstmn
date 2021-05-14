package umn.ac.id.ydkw01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText mFullname, mEmail, mPassword, mNis;
    Button btnSignup;
    TextView btnTSignin;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();

        mFullname = findViewById(R.id.su_nama);
        mEmail = findViewById(R.id.su_email);
        mPassword = findViewById(R.id.su_password);
        mNis = findViewById(R.id.su_nis);
        btnSignup = findViewById(R.id.signupbtn);
        btnTSignin = findViewById(R.id.hac);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

//        if(fAuth.getCurrentUser() != null){
//            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
//            i.addFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(i);
//        }

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fname = mFullname.getText().toString();
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String nis = mNis.getText().toString();

                if(TextUtils.isEmpty(fname)){
                    mEmail.setError("Fullname is Required");
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Required");
                    return;
                }
                if(password.length()<8){
                    mPassword.setError("Password must be >= 8 characters");
                    return;
                }
                if(!password.matches("(.*[0-9].*)")) {
                    mPassword.setError("Password must contain atleast one number");
                    return;
                }
                if(TextUtils.isEmpty(nis)){
                    mEmail.setError("NIS is Required");
                    return;
                }

                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignupActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("Fullname", fname);
                            user.put("Email", email);
                            user.put("NIS", nis);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "User profile created for ID: " + userID);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: " + e.toString());
                                        }
                                    });
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(SignupActivity.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        btnTSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, SigninActivity.class));
            }
        });
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = fAuth.getCurrentUser();
//        if(currentUser != null){
//            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
//            i.addFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(i);
//        }
//    }
}