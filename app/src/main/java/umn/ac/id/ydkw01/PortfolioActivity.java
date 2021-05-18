package umn.ac.id.ydkw01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PortfolioActivity extends AppCompatActivity {
    public static final String TAG = "TAG";

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
    ArrayList<PostImg> list;
//    List<CustomModel> portoList;
//    PortfolioAdapter adapter;

//    MyAdapter adapter;
    private FirestoreRecyclerAdapter adapter;

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
//                    case R.id.btnpost:
//                        startActivity(new Intent(getApplicationContext(), UploadMaterial.class));
//                        overridePendingTransition(0,0);
//                        return true;
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
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        reference = fStore.collection("users").document(userID);
        Query query = FirebaseFirestore.getInstance().collection("users");

        storageReference = FirebaseStorage.getInstance().getReference();
        recyclerView = findViewById(R.id.recyclerview);
        list = new ArrayList<>();
//        adapter = new MyAdapter(this, list);
//        portoList = new ArrayList<>();
//        adapter = new PortfolioAdapter(this, portoList);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setAdapter(adapter);

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

//        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot doc = task.getResult();
//                    String imageUrl = (String) doc.get("PortfolioUrl");
//                    if (doc.exists()) {
//                        Log.d(TAG, "DocumentSnapshot data: " + doc.get("PortfolioUrl"));
//                    } else {
//                        Log.d(TAG, "No such document");
//                    }
//                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
//                }
//            }
//        });

        FirestoreRecyclerOptions<PortfolioModel> options = new FirestoreRecyclerOptions.Builder<PortfolioModel>().setQuery(query, PortfolioModel.class).build();

        adapter = new FirestoreRecyclerAdapter<PortfolioModel, PortfoliosViewHolder>(options) {
            @NonNull
            @Override
            public PortfoliosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view, parent, false);
                return new PortfoliosViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PortfoliosViewHolder holder, int position, @NonNull PortfolioModel model) {
                Picasso.get().load(model.getPortfolioUrl()).into(holder.singleport);
//                holder.uploadername.setText(model.getFullname());
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    private class PortfoliosViewHolder extends  RecyclerView.ViewHolder{
        ImageView singleport;
//        TextView uploadername;

        public PortfoliosViewHolder(@NonNull View itemView){
            super(itemView);

            singleport = itemView.findViewById(R.id.singleport);
//            uploadername = itemView.findViewById(R.id.uploadername);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
