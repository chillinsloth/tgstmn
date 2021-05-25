package umn.ac.id.ydkw01;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;

public class MaterialAdapter extends FirestorePagingAdapter<MaterialModel, MaterialAdapter.MaterialViewHolder> {

    public MaterialAdapter(@NonNull FirestorePagingOptions<MaterialModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MaterialViewHolder holder, int position, @NonNull MaterialModel model) {
        holder.mattitle.setText(model.getVideoTitle());
        holder.uploadername.setText(model.getFullname());

    }

    @NonNull
    @Override
    public MaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_videolist, parent, false);
        return new MaterialViewHolder(view);
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

    public class MaterialViewHolder extends  RecyclerView.ViewHolder{
        TextView mattitle;
        TextView uploadername;

        public MaterialViewHolder(@NonNull View itemView) {
            super(itemView);

            mattitle = itemView.findViewById(R.id.mattitle);
            uploadername = itemView.findViewById(R.id.uploadername);
        }
    }
}
