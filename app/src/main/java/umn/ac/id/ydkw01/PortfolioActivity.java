package umn.ac.id.ydkw01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PortfolioActivity extends AppCompatActivity implements FirestoreAdapter.OnClickedPortfolio {
    TextView pfullname, profilenis;
    CircleImageView btnProfile;
    ImageView btnpost;
    BottomNavigationView bottomNavigationView;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    StorageReference storageReference;
    DocumentReference reference;
    RecyclerView recyclerView;
    private FirestoreAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);
        getSupportActionBar().hide();

        bottomNavigationView = findViewById(R.id.botnav);
        bottomNavigationView.setSelectedItemId(R.id.btnportfolio);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.btnmaterial:
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.btnportfolio:
                        return true;
                }
                return false;
            }
        });

        btnProfile = findViewById(R.id.btnprofile);
        pfullname = findViewById(R.id.profile_name);
        profilenis = findViewById(R.id.profile_nis);
        btnpost = findViewById(R.id.btnpost);
        recyclerView = findViewById(R.id.recyclerview);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        reference = fStore.collection("users").document(userID);
        Query query = FirebaseFirestore.getInstance().collection("users");
        storageReference = FirebaseStorage.getInstance().getReference();
        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(9).setPageSize(3)
                .build();

        StorageReference dprofRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile pict");
        dprofRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(btnProfile);
            }
        });

        reference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                pfullname.setText(documentSnapshot.getString("Fullname"));
                profilenis.setText(documentSnapshot.getString("NIS"));
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
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

        FirestorePagingOptions<PortfolioModel> options = new FirestorePagingOptions.Builder<PortfolioModel>()
                .setLifecycleOwner(this)
                .setQuery(query, config, PortfolioModel.class)
                .build();

        adapter = new FirestoreAdapter(options, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {
        Log.d("Item Clicked", "clicked" + position + "ID : " + snapshot.getId());
        Intent intent = new Intent(getApplicationContext(), ClickedPortfolio.class);
        intent.putExtra("Fullname", snapshot.getDocumentReference("Fullname").getId());
        intent.putExtra("NIS", snapshot.getDocumentReference("NIS").getId());
        intent.putExtra("PortfolioUrl", snapshot.getDocumentReference("PortfolioUrl").getId());
        startActivity(intent);
    }
}
