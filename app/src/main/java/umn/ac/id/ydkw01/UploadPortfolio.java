package umn.ac.id.ydkw01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadPortfolio extends AppCompatActivity {
    private static final int READ_PERMISSION_CODE = 100;
    private static final int PICK_IMAGE_REQUEST_CODE = 101;
    public static final String TAG = "TAG";

    ImageView postpreview;
    ImageButton btnpick, btnback, btnuploadport;
    ProgressBar loadingpost;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage fstorage;
    FirebaseUser user;
    DatabaseReference dbref;
    DocumentReference reference, docref;
    String userID;
//    RecyclerView recyclerView;
//    List<CustomModel> portoList;
    List<String> savedImagesUri;
//    PortfolioAdapter adapter;
//    CoreHelper coreHelper;
//    int counter;
    Uri imguri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_portfolio);
        getSupportActionBar().hide();

        postpreview = findViewById(R.id.postpreview);
        btnpick = findViewById(R.id.btnpick);
        btnback = findViewById(R.id.btndiscardport);
        btnuploadport = findViewById(R.id.btnuploadport);
        loadingpost = findViewById(R.id.loadingpost);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fstorage = FirebaseStorage.getInstance();
        user = fAuth.getCurrentUser();
        userID = user.getUid();
        reference = fStore.collection("users").document(userID);
//        docref = fStore.collection("portfolios/").document(userID);
        dbref = FirebaseDatabase.getInstance().getReference(userID).child("portfolios");

//        recyclerView = findViewById(R.id.recyclerview);
//        portoList = new ArrayList<>();
//        adapter = new PortfolioAdapter(this, portoList);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setAdapter(adapter);
//        coreHelper = new CoreHelper(this);
        savedImagesUri = new ArrayList<>();

        btnpick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPermissionAndPickImage();
            }
        });

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UploadPortfolio.this, PortfolioActivity.class);
                i.addFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });

        btnuploadport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                uploadPortfolio(v);
                if (imguri != null){
                    uploadPortfolio(imguri);
                }else {
                    Toast.makeText(UploadPortfolio.this, "No Image Added", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadPortfolio(Uri uri) {
        StorageReference storageReference = fstorage.getReference("users/" + user.getUid() + "/" + "portfolios/").child(System.currentTimeMillis() + "." + getFileExt(uri));

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        PostImg model = new PostImg(uri.toString());
//                        String modelId = dbref.push().getKey();
//                        dbref.child(modelId).setValue(model);
//                        loadingpost.setVisibility(View.INVISIBLE);
//                        Toast.makeText(UploadPortfolio.this, "Upload Successfully", Toast.LENGTH_SHORT).show();
//
//                        Intent intent = new Intent(getApplicationContext(), PortfolioActivity.class);
//                        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//                    }
//                });

                storageReference.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.i("problem", task.getException().toString());
                        }
                        return storageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();

                            Map<String,Object> imageUrl = new HashMap<>();
                            imageUrl.put("PortfolioUrl", downloadUri.toString());
                            reference.set(imageUrl, SetOptions.merge());
                            Toast.makeText(UploadPortfolio.this, "Upload Successfully", Toast.LENGTH_SHORT).show();
                            loadingpost.setVisibility(View.INVISIBLE);

                            Intent intent = new Intent(getApplicationContext(), PortfolioActivity.class);
                            intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Toast.makeText(UploadPortfolio.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                loadingpost.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingpost.setVisibility(View.INVISIBLE);
                Toast.makeText(UploadPortfolio.this, "Upload Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION_CODE);
            }
        } else {
            pickImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImage();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
        }
    }

//    private void pickImage() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
//    }

    private void pickImage() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case PICK_IMAGE_REQUEST_CODE:
//                if (resultCode == RESULT_OK && data != null) {
//                    ClipData clipData = data.getClipData();
//                    if (clipData != null) {
//                        for (int i = 0; i < clipData.getItemCount(); i++) {
//                            Uri uri = clipData.getItemAt(i).getUri();
//                            portoList.add(new CustomModel(coreHelper.getFileNameFromUri(uri), uri));
//                            adapter.notifyDataSetChanged();
//                        }
//                    } else {
//                        Uri uri = data.getData();
//                        portoList.add(new CustomModel(coreHelper.getFileNameFromUri(uri), uri));
//                        adapter.notifyDataSetChanged();
//                    }
//                }
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null){
            imguri = data.getData();
            Picasso.get().load(imguri).into(postpreview);
        }
    }

    private String getFileExt(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mt = MimeTypeMap.getSingleton();
        return mt.getExtensionFromMimeType(cr.getType(uri));
    }

//    private void uploadPortfolio(View view) {
//        if (imguri != null){
//            fstorage.getReference("users/" + user.getUid() + "/" + "portfolios/").putFile(imguri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
//            {
//                @Override
//                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
//                {
//                    if (!task.isSuccessful())
//                    {
//                        throw task.getException();
//                    }
//                    return fstorage.getReference("users/" + user.getUid() + "/" + "portfolios/").getDownloadUrl();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri>()
//            {
//                @Override
//                public void onComplete(@NonNull Task<Uri> task)
//                {
//                    if (task.isSuccessful())
//                    {
//                        savedImagesUri.add(task.getResult().toString());
//                    } else
//                    {
//                        Toast.makeText(UploadPortfolio.this, "upload failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        }else {
//            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
//        }
//    }

//    private void uploadPortfolio(View view) {
//        if (portoList.size() != 0) {
//            final ProgressDialog progressDialog = new ProgressDialog(this);
//            progressDialog.setMessage("Uploaded 0/"+portoList.size());
//            progressDialog.setCanceledOnTouchOutside(false); //Remove this line if you want your user to be able to cancel upload
//            progressDialog.setCancelable(false);    //Remove this line if you want your user to be able to cancel upload
//            progressDialog.show();
//            final StorageReference storageReference = fstorage.getReference();
//            for (int i = 0; i < portoList.size(); i++) {
//                final int finalI = i;
//                storageReference.child("users/"+user.getUid()+"/"+"portfolios/").child(portoList.get(i).getPortoName()).putFile(portoList.get(i).getPortoURI()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                        if (task.isSuccessful()){
//                            storageReference.child("users/"+user.getUid()+"/"+"portfolios/").child(portoList.get(finalI).getPortoName()).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Uri> task) {
//                                    counter++;
//                                    progressDialog.setMessage("Uploaded "+counter+"/"+portoList.size());
//                                    if (task.isSuccessful()){
//                                        savedImagesUri.add(task.getResult().toString());
//                                    }else{
//                                        storageReference.child("users/"+user.getUid()+"/"+"portfolios/").child(portoList.get(finalI).getPortoName()).delete();
//                                        Toast.makeText(UploadPortfolio.this, "Couldn't save "+portoList.get(finalI).getPortoName(), Toast.LENGTH_SHORT).show();
//                                    }
//                                    if (counter == portoList.size()){
//                                        saveImageDataToFirestore(progressDialog);
//                                    }
//                                }
//                            });
//                        }else{
//                            progressDialog.setMessage("Uploaded "+counter+"/"+portoList.size());
//                            counter++;
//                            Toast.makeText(UploadPortfolio.this, "Couldn't upload "+portoList.get(finalI).getPortoName(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        } else {
//            coreHelper.createSnackBar(view, "Please add some images first.", "", null, Snackbar.LENGTH_SHORT);
//        }
//    }

//    private void saveImageDataToFirestore(final ProgressDialog progressDialog) {
//        progressDialog.setMessage("Saving uploaded images...");
//        Map<String, Object> dataMap = new HashMap<>();
//        //Below line of code will put your images list as an array in firestore
//        dataMap.put("images", savedImagesUri);
//        //Below commented code is no more recommended..!
//        /*for (int i = 0; i < savedImagesUri.size(); i++) {
//            dataMap.put("image" + i, savedImagesUri.get(i));
//        }*/
//        reference.update(dataMap).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                progressDialog.dismiss();
//                coreHelper.createAlert("Success", "Images uploaded and saved successfully!", "OK", "", null, null, null);
//                Intent i = new Intent(getApplicationContext(), PortfolioActivity.class);
//                i.addFlags(i.FLAG_ACTIVITY_NEW_TASK | i.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(i);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                progressDialog.dismiss();
//                coreHelper.createAlert("Error", "Images uploaded but we couldn't save them to database.", "OK", "", null, null, null);
//                Log.e("UploadPort:SaveData", e.getMessage());
//            }
//        });
//    }
}