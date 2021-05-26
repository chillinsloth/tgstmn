package umn.ac.id.ydkw01;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class FirestoreAdapter extends FirestorePagingAdapter<PortfolioModel, FirestoreAdapter.PortfoliosViewHolder> {

    private OnClickedPortfolio onClickedPortfolio;

    public FirestoreAdapter(@NonNull FirestorePagingOptions<PortfolioModel> options, OnClickedPortfolio onClickedPortfolio) {
        super(options);
        this.onClickedPortfolio = onClickedPortfolio;
    }

    @Override
    protected void onBindViewHolder(@NonNull PortfoliosViewHolder holder, int position, @NonNull PortfolioModel model) {
        GlideApp.with(holder.itemView.getContext()).load(model.getPortfolioUrl()).thumbnail( 0.1f ).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).skipMemoryCache(true).into(holder.singleport);
//                holder.uploadername.setText(model.getFullname());
    }

    @NonNull
    @Override
    public PortfoliosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view, parent, false);
        return new PortfoliosViewHolder(view);
    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        super.onLoadingStateChanged(state);
        switch (state){
            case LOADING_INITIAL:
                Log.d("Paging Log", "Loading Initial Data");
                break;
            case LOADED:
                Log.d("Paging Log", "Total Data Loaded : " + getItemCount());
                break;
            case LOADING_MORE:
                Log.d("Paging Log", "Loading More Data");
                break;
            case FINISHED:
                Log.d("Paging Log", "Loaded All Data");
                break;
            case ERROR:
                Log.d("Paging Log", "Error Loading");
                break;
        }
    }

    public class PortfoliosViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView singleport;
//        TextView uploadername;

        public PortfoliosViewHolder(@NonNull View itemView){
            super(itemView);

            singleport = itemView.findViewById(R.id.singleport);
//            uploadername = itemView.findViewById(R.id.uploadername);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onClickedPortfolio.onItemClick(getItem(getAdapterPosition()), getAdapterPosition());
//            onClickedPortfolio.onClick(v, getAdapterPosition());
        }
    }

    public interface OnClickedPortfolio{
        void onItemClick(DocumentSnapshot snapshot, int position);
//        void onClick(View v, int position);
    }
}
