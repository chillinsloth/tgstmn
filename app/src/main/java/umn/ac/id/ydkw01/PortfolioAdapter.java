package umn.ac.id.ydkw01;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PortfolioAdapter extends RecyclerView.Adapter<PortfolioAdapter.ImageViewHolder> {

    Context context;
    List<CustomModel> portoList;

    public PortfolioAdapter(Context context, List<CustomModel> portoList) {
        this.context = context;
        this.portoList = portoList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, final int position) {
        holder.titleport.setText(portoList.get(position).getPortoName());
//        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(context, imagesList.get(position).getImageName()+" removed!", Toast.LENGTH_SHORT).show();
//                imagesList.remove(position);
//                notifyDataSetChanged();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return portoList.size();
    }

    class ImageViewHolder extends RecyclerView.ViewHolder{
        TextView titleport;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            titleport = itemView.findViewById(R.id.titleport);
        }
    }

}
