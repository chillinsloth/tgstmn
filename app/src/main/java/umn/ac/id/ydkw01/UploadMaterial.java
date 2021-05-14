package umn.ac.id.ydkw01;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class UploadMaterial extends AppCompatActivity {
    public static final int PICK_VIDEO = 1;
    VideoView preupvid;
    AppCompatButton discardvid, uploadvid;
    ProgressBar loadingupvid;
    EditText titlevid;
    private Uri videoUri;
    MediaController mediaController;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    StorageReference storageReference;
    Member member;
    UploadTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_material);
        getSupportActionBar().hide();

        member = new Member();
        storageReference = FirebaseStorage.getInstance().getReference("users");
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();

        preupvid = findViewById(R.id.preupvid);
        discardvid = findViewById(R.id.discardvid);
        uploadvid = findViewById(R.id.uploadvid);
        loadingupvid = findViewById(R.id.loadingupvid);
        titlevid = findViewById(R.id.titlevid);
        mediaController = new MediaController(this);
        preupvid.setMediaController(mediaController);
        preupvid.start();

        uploadvid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadVideo();
            }
        });

        discardvid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UploadMaterial.this, HomeActivity.class);
                i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(i);
            }
        });
    }

    private void UploadVideo() {
        String videoTitle = titlevid.getText().toString();
//        String search = titlevid.getText().toString().toLowerCase();
        if (videoUri != null || !TextUtils.isEmpty(videoTitle)){
            loadingupvid.setVisibility(View.VISIBLE);
            final StorageReference reference = storageReference.child("users/" + user.getUid() + "/" + System.currentTimeMillis() + "." + getExt(videoUri));
            uploadTask = reference.putFile(videoUri);

            Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        loadingupvid.setVisibility(View.INVISIBLE);
                        Toast.makeText(UploadMaterial.this, "Video Uploaded", Toast.LENGTH_SHORT).show();

                        member.setTitle(videoTitle);
                        member.setVideouri(downloadUri.toString());
//                        member.getSearch(search);
                        Task<Void> dref = fStore.collection("users").document(user.getUid()).set(member);
                    }else {
                        Toast.makeText(UploadMaterial.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(this, "Title Required", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_VIDEO || resultCode == RESULT_OK || data != null || data.getData() != null){
            videoUri = data.getData();
            preupvid.setVideoURI(videoUri);
        }
    }

    public void pickVideo(View view){
        Intent i = new Intent();
        i.setType("video/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(i, PICK_VIDEO);
    }

    private String getExt(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}