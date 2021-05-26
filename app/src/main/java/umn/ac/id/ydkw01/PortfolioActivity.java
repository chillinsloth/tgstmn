package umn.ac.id.ydkw01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

import static android.media.CamcorderProfile.get;

public class PortfolioActivity extends AppCompatActivity implements FirestoreAdapter.OnClickedPortfolio{
    TextView pfullname, profilenis;
    CircleImageView btn_Profile;
    ImageView btnpost, btnmaterial;
//    BottomNavigationView bottomNavigationView;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    StorageReference storageReference;
    DocumentReference reference;
    RecyclerView recyclerView;
    private FirestoreAdapter adapter;
//    private FirestoreAdapter.OnClickedPortfolio onClickedPortfolio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);
        getSupportActionBar().hide();

//        bottomNavigationView = findViewById(R.id.bot_nav);
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

        btn_Profile = findViewById(R.id.btn_profile);
        pfullname = findViewById(R.id.profile_name);
        profilenis = findViewById(R.id.profile_nis);
        btnpost = findViewById(R.id.btnpost);
        btnmaterial = findViewById(R.id.btnmaterial);
        recyclerView = findViewById(R.id.recyclerview);
        RequestManager manager = GlideApp.with(btn_Profile);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        reference = fStore.collection("users").document(userID);
        Query query = FirebaseFirestore.getInstance().collection("users");
        storageReference = FirebaseStorage.getInstance().getReference();

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(9).setPageSize(9)
                .build();

        reference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                pfullname.setText(documentSnapshot.getString("Fullname"));
                profilenis.setText(documentSnapshot.getString("NIS"));
                manager.load(documentSnapshot.getString("Profilepict")).thumbnail( 0.1f ).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).skipMemoryCache(true).into(btn_Profile);
            }
        });

        btn_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PortfolioActivity.this, ProfileActivity.class));
            }
        });

        btnpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PortfolioActivity.this, UploadPortfolio.class));
            }
        });

        btnmaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PortfolioActivity.this, HomeActivity.class));
            }
        });

        FirestorePagingOptions<PortfolioModel> options = new FirestorePagingOptions.Builder<PortfolioModel>()
                .setLifecycleOwner(this)
                .setQuery(query, config, PortfolioModel.class)
                .build();
        
//        setOnclickListener();
        adapter = new FirestoreAdapter(options, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

//    private void setOnclickListener() {
//        onClickedPortfolio = new FirestoreAdapter.OnClickedPortfolio() {
//            @Override
//            public void onClick(View v, int position) {
//                Intent intent = new Intent(getApplicationContext(), ClickedPortfolio.class);
//                intent.putExtra("PortfolioUrl", (findViewById(R.id.singleport)).)
//            }
//        }
//    }

    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {
        Log.d("Item Clicked", "clicked" + position + "ID : " + snapshot.getId());
        Intent intent = new Intent(getApplicationContext(), ClickedPortfolio.class);
        intent.putExtra("Fullname", snapshot.getString("Fullname"));
        intent.putExtra("NIS", snapshot.getString("NIS"));
        intent.putExtra("PortfolioUrl", snapshot.getString("PortfolioUrl"));
        startActivity(intent);
    }
}
