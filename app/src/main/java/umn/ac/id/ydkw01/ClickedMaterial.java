package umn.ac.id.ydkw01;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.android.exoplayer2.Player.STATE_BUFFERING;
import static com.google.android.exoplayer2.Player.STATE_ENDED;
import static com.google.android.exoplayer2.Player.STATE_IDLE;
import static com.google.android.exoplayer2.Player.STATE_READY;

public class ClickedMaterial extends AppCompatActivity {
    TextView pfullname, profilenis, mattitle, uploadername;
    ImageView btnmaterial, btnpost, btnportfolio, btnshare, btnfullscreen;
    CircleImageView btnProfile;
    ImageButton btnback;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID, vidurl, vidtitle, viduploader;
    StorageReference storageReference;
    DocumentReference reference;
    ProgressBar progressBar;
    Dialog dialog;
//    RelativeLayout top_menu, search_bar, info_bar, bot_nav;

    private SimpleExoPlayer player;
    private PlayerView playerView;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition =  0;
    private PlaybackStateListener playbackStateListener;
    private static final String TAG = ClickedMaterial.class.getName();
    boolean fullscreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicked_material);
        getSupportActionBar().hide();
        playbackStateListener = new PlaybackStateListener();

//        top_menu = findViewById(R.id.top_menu);
//        search_bar = findViewById(R.id.search_bar);
//        info_bar = findViewById(R.id.info_bar);
//        bot_nav = findViewById(R.id.bot_nav);

        btnProfile = findViewById(R.id.btnprofile);
        pfullname = findViewById(R.id.profile_name);
        profilenis = findViewById(R.id.profile_nis);
        btnback = findViewById(R.id.btnback);
        btnmaterial = findViewById(R.id.btnmaterial);
        btnpost = findViewById(R.id.btnpost);
        btnportfolio = findViewById(R.id.btnportfolio);

        playerView = findViewById(R.id.display_vid);
        mattitle = findViewById(R.id.mattitle);
        uploadername = findViewById(R.id.uploadername);
        btnshare = findViewById(R.id.btn_share);
	    btnfullscreen = playerView.findViewById(R.id.exo_fullscreen);
        dialog = new Dialog(this);
        progressBar = findViewById(R.id.progress_bar);

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

        btnshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.share_popup);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

	    btnfullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fullscreen){
                    btnfullscreen.setImageDrawable(ContextCompat.getDrawable(ClickedMaterial.this, R.drawable.btnfullscreen));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                    params.height = (int) ( 200 * getApplicationContext().getResources().getDisplayMetrics().density);
                    playerView.setLayoutParams(params);
                    fullscreen = false;
//                    top_menu.setVisibility(View.INVISIBLE);
//                    search_bar.setVisibility(View.INVISIBLE);
//                    info_bar.setVisibility(View.INVISIBLE);
//                    bot_nav.setVisibility(View.INVISIBLE);
                }else{
                    btnfullscreen.setImageDrawable(ContextCompat.getDrawable(ClickedMaterial.this, R.drawable.btnfullscreenp));
//                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

//                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
//                    params.width = ViewGroup.LayoutParams.MATCH_PARENT;
//                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;
//                    playerView.setLayoutParams(params);
                    fullscreen = true;
//                    top_menu.setVisibility(View.INVISIBLE);
//                    search_bar.setVisibility(View.INVISIBLE);
//                    info_bar.setVisibility(View.INVISIBLE);
//                    bot_nav.setVisibility(View.INVISIBLE);
                }
            }
        });

        if(getIntent().hasExtra("MaterialUrl")){
            vidurl = getIntent().getStringExtra("MaterialUrl");
            vidtitle = getIntent().getStringExtra("VideoTitle");
            viduploader = getIntent().getStringExtra("Fullname");

            mattitle.setText(vidtitle);
            uploadername.setText(viduploader);
        }
    }

    private void initializeplayer() {
        player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(vidurl);
        player.setMediaItem(mediaItem);
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
        player.addListener(playbackStateListener);
        player.prepare();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(Util.SDK_INT >= 24){
            initializeplayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if((Util.SDK_INT < 24 || player == null)){
            initializeplayer();
            hideSystemUi();
        }
    }

    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
        | View.SYSTEM_UI_FLAG_FULLSCREEN
        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(Util.SDK_INT < 24){
            releasePlayer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(Util.SDK_INT >= 24){
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if(player != null){
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.removeListener(playbackStateListener);
            player.release();
            player = null;
        }
    }

    private class PlaybackStateListener implements Player.Listener{
        @Override
        public void onPlaybackStateChanged(int state) {
            String stateString;
            switch (state) {
                case STATE_IDLE:
                    stateString = "ExoPlayer.STATE_IDLE      -";
                    break;
                case STATE_BUFFERING:
                    stateString = "ExoPlayer.STATE_BUFFERING -";
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case STATE_READY:
                    stateString = "ExoPlayer.STATE_READY     -";
                    progressBar.setVisibility(View.GONE);
                    break;
                case STATE_ENDED:
                    stateString = "ExoPlayer.STATE_ENDED     -";
                    break;
                default:
                    stateString = "UNKNOWN_STATE             -";
                    break;
            }
            Log.d(TAG, "changed state to " + stateString);
        }
    }
}