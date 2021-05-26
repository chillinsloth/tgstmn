package umn.ac.id.ydkw01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {
    TextView pfullname, profilenis;
    CircleImageView btn_Profile;
    ImageView btnpost, btnportfolio;
//    BottomNavigationView bottomNavigationView;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    StorageReference storageReference;
    DocumentReference reference;
    RecyclerView recyclerView;
    private MaterialAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();

//        bottomNavigationView = findViewById(R.id.bot_nav);
//        bottomNavigationView.setSelectedItemId(R.id.btnmaterial);
//        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId()){
//                    case R.id.btnmaterial:
//                        return true;
//                    case R.id.btnportfolio:
//                        startActivity(new Intent(getApplicationContext(), PortfolioActivity.class));
//                        overridePendingTransition(0,0);
//                        return true;
//                }
//                return false;
//            }
//        });

        btn_Profile = findViewById(R.id.btn_profile);
        pfullname = findViewById(R.id.profile_name);
        profilenis = findViewById(R.id.profile_nis);
        btnpost = findViewById(R.id.btnpost);
        btnportfolio = findViewById(R.id.btnportfolio);
        recyclerView = findViewById(R.id.recyclerview);
        RequestManager manager = Glide.with(btn_Profile);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        reference = fStore.collection("users").document(userID);
        storageReference = FirebaseStorage.getInstance().getReference();
        Query query = FirebaseFirestore.getInstance().collection("users");

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(2).setPageSize(2)
                .build();

//        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot snapshot) {
//                manager.load(snapshot.getString("Profilepict")).into(btnProfile);
//            }
//        });

        reference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                pfullname.setText(documentSnapshot.getString("Fullname"));
                profilenis.setText(documentSnapshot.getString("NIS"));
                manager.load(documentSnapshot.getString("Profilepict")).into(btn_Profile);
            }
        });

        btn_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            }
        });

        btnpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, UploadMaterial.class));
            }
        });

        btnportfolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, PortfolioActivity.class));
            }
        });

        FirestorePagingOptions<MaterialModel> options = new FirestorePagingOptions.Builder<MaterialModel>()
                .setLifecycleOwner(this)
                .setQuery(query, config, MaterialModel.class)
                .build();

//        setOnclickListener();
        adapter = new MaterialAdapter(options);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }
}