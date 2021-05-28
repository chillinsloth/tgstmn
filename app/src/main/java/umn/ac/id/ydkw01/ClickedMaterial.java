package umn.ac.id.ydkw01;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class ClickedMaterial extends AppCompatActivity {
    TextView pfullname, profilenis, mattitle, uploadername;
    ImageView btnmaterial, btnpost, btnportfolio;
    CircleImageView btnProfile;
    VideoView display_vid;
    ImageButton btnback;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    StorageReference storageReference;
    DocumentReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicked_material);
        getSupportActionBar().hide();

        btnProfile = findViewById(R.id.btnprofile);
        pfullname = findViewById(R.id.profile_name);
        profilenis = findViewById(R.id.profile_nis);
        btnback = findViewById(R.id.btnback);
        btnmaterial = findViewById(R.id.btnmaterial);
        btnpost = findViewById(R.id.btnpost);
        btnportfolio = findViewById(R.id.btnportfolio);

        display_vid = findViewById(R.id.display_vid);
        mattitle = findViewById(R.id.mattitle);
        uploadername = findViewById(R.id.uploadername);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        reference = fStore.collection("users").document(userID);
        RequestManager manager = Glide.with(btnProfile);

        reference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                pfullname.setText(documentSnapshot.getString("Fullname"));
                profilenis.setText(documentSnapshot.getString("NIS"));
                manager.load(documentSnapshot.getString("Profilepict")).into(btnProfile);
            }
        });

        btnmaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClickedMaterial.this, HomeActivity.class));
            }
        });

        btnportfolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClickedMaterial.this, PortfolioActivity.class));
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClickedMaterial.this, ProfileActivity.class));
            }
        });

        btnpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClickedMaterial.this, UploadMaterial.class));
            }
        });

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClickedMaterial.this, HomeActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        getIncomingIntent();
    }

    private void getIncomingIntent(){
        if(getIntent().hasExtra("MaterialUrl")){
            String vidurl = getIntent().getStringExtra("MaterialUrl");
            String vidtitle = getIntent().getStringExtra("VideoTitle");
            String viduploader = getIntent().getStringExtra("Fullname");

            setMaterial(vidurl, vidtitle, viduploader);
        }
    }

    private void setMaterial(String vidurl, String vidtitle, String viduploader){
        mattitle.setText(vidtitle);
        uploadername.setText(viduploader);
    }
}