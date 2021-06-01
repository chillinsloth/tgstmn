package umn.ac.id.ydkw01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
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
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    StorageReference storageReference;
    DocumentReference reference;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicked_portfolio);
        getSupportActionBar().hide();

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
        reference = fStore.collection("users").document(userID);
        RequestManager manager = Glide.with(btnProfile);

        imgpostprof = findViewById(R.id.imgpostprof);
        namepost = findViewById(R.id.namepost);
        nispost = findViewById(R.id.nispost);
        portimgpost = findViewById(R.id.portimgpost);
        btnlike = findViewById(R.id.btnlike);
        btncomment = findViewById(R.id.btncomment);
        btnshare = findViewById(R.id.btnshare);
        dialog = new Dialog(this);

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

        btnshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.share_popup);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        getIncomingIntent();
    }

    private void getIncomingIntent(){
        if(getIntent().hasExtra("PortfolioUrl")){
            String uploaderimg = getIntent().getStringExtra("Profilepict");
            String imageurl = getIntent().getStringExtra("PortfolioUrl");
            String uploadername = getIntent().getStringExtra("Fullname");
            String uploadernis = getIntent().getStringExtra("NIS");

            setImage(uploaderimg, imageurl, uploadername, uploadernis);
        }
    }

    private void setImage(String uploaderimg, String imageurl, String uploadername, String uploadernis){
        Glide.with(this).load(uploaderimg).into(imgpostprof);
        namepost.setText(uploadername);
        nispost.setText(uploadernis);
        Glide.with(this).load(imageurl).into(portimgpost);
    }

}