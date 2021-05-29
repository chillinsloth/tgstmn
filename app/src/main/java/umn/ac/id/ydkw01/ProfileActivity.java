package umn.ac.id.ydkw01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    public static final String TAG = "TAG";

    TextView fullname, email, nis;
    Button edtbtn;
    ImageButton btnback;
    CircleImageView imgp;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    StorageReference storageReference;
    DocumentReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().hide();

        fullname = findViewById(R.id.namep);
        email = findViewById(R.id.emailp);
        nis = findViewById(R.id.nisp);
        edtbtn = findViewById(R.id.editbtn);
        btnback = findViewById(R.id.btnback);
        imgp = findViewById(R.id.imgp);
        RequestManager manager = Glide.with(imgp);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        reference = fStore.collection("users").document(userID);

        edtbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), EditProfile.class);
                i.putExtra("fullname", fullname.getText().toString());
                i.putExtra("email", email.getText().toString());
                i.putExtra("nis", nis.getText().toString());
                startActivity(i);
            }
        });

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, HomeActivity.class);
                i.addFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });

        reference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d(TAG, "Listen failed.", error);
                    return;
                }
                if (documentSnapshot.exists()) {
                    fullname.setText(documentSnapshot.getString("Fullname"));
                    email.setText(documentSnapshot.getString("Email"));
                    nis.setText(documentSnapshot.getString("NIS"));
                    manager.load(documentSnapshot.getString("Profilepict")).into(imgp);
                } else {
                    Log.d(TAG, "No such document");
                }
            }
        });
    }

    public void signout(View view){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}