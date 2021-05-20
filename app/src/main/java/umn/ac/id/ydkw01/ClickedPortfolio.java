package umn.ac.id.ydkw01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ClickedPortfolio extends AppCompatActivity {
    TextView pfullname, profilenis, namepost, nispost;
    CircleImageView btnProfile, imgpostprof;
    ImageView portimgpost, btnlike, btncomment, btnshare, btnmaterial, btnportfolio, btnpost;
    ImageButton btnback;
//    BottomNavigationView bottomNavigationView;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicked_portfolio);
        getSupportActionBar().hide();

//        bottomNavigationView = findViewById(R.id.botnav);
//        bottomNavigationView.setSelectedItemId(R.id.btnportfolio);
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()){
//                    case R.id.btnmaterial:
//                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
//                        overridePendingTransition(0,0);
//                        return true;
//                    case R.id.btnportfolio:
//                        return true;
//                }
//                return false;
//            }
//        });

        btnProfile = findViewById(R.id.btnprofile);
        pfullname = findViewById(R.id.profile_name);
        profilenis = findViewById(R.id.profile_nis);
        btnmaterial = findViewById(R.id.btnmaterial);
        btnportfolio = findViewById(R.id.btnportfolio);
        btnpost = findViewById(R.id.btnpost);
        btnback = findViewById(R.id.btnback);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

        namepost = findViewById(R.id.namepost);
        nispost = findViewById(R.id.nispost);
        imgpostprof = findViewById(R.id.imgpostprof);
        portimgpost = findViewById(R.id.portimgpost);
        btnlike = findViewById(R.id.btnlike);
        btncomment = findViewById(R.id.btncomment);
        btnshare = findViewById(R.id.btnshare);

        StorageReference dprofRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile pict");
        dprofRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(btnProfile);
            }
        });
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                pfullname.setText(documentSnapshot.getString("Fullname"));
                profilenis.setText(documentSnapshot.getString("NIS"));
            }
        });

//        StorageReference dprofRef2 = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile pict");
//        dprofRef2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                Picasso.get().load(uri).into(imgpostprof);
//            }
//        });
//        DocumentReference document2Reference = fStore.collection("users").document(userID);
//        document2Reference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
//                namepost.setText(documentSnapshot.getString("Fullname"));
//                nispost.setText(documentSnapshot.getString("NIS"));
//            }
//        });

        btnmaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClickedPortfolio.this, HomeActivity.class));
            }
        });

        btnportfolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClickedPortfolio.this, PortfolioActivity.class));
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClickedPortfolio.this, ProfileActivity.class));
            }
        });

        btnpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClickedPortfolio.this, UploadPortfolio.class));
            }
        });

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClickedPortfolio.this, PortfolioActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        getIncomingIntent();


    }

    private void getIncomingIntent(){
        if(getIntent().hasExtra("PortfolioUrl")){
            String imageurl = getIntent().getStringExtra("PortfolioUrl");
            String uploadername = getIntent().getStringExtra("Fullname");
            String uploadernis = getIntent().getStringExtra("NIS");

            setImage(imageurl, uploadername, uploadernis);
        }
    }

    private void setImage(String imageurl, String uploadername, String uploadernis){
        namepost.setText(uploadername);
        nispost.setText(uploadernis);
        Picasso.get().load(imageurl).into(imgpostprof);
    }

}