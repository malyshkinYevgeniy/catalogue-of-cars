package auto.app;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import auto.app.model.Advertisement;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.AdvertisementViewHolder> {

    private Context context;
    private List<Advertisement> advertisements;

    RecyclerAdapter(Context context, List<Advertisement> advertisements) {
        this.context = context;
        this.advertisements = advertisements;
    }


    @NonNull
    @Override
    public AdvertisementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_elem, parent, false);
        return new AdvertisementViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AdvertisementViewHolder holder, int position) {
        Advertisement advertisementCurrent = advertisements.get(position);
        String price = advertisementCurrent.getmPrice() + " р.";
        holder.carPrice.setText(price);
        holder.carDescription.setText(advertisementCurrent.getmDescription());
        holder.carTitle.setText(advertisementCurrent.getmTitle());
        if (advertisementCurrent.getFav())
            holder.carFav.setImageResource(R.drawable.ic_favorite_white);
        else
            holder.carFav.setImageResource(R.drawable.ic_favorite_border);

        Picasso.get()
                .load(advertisementCurrent.getmImageUrl())
                .placeholder(R.drawable.ic_car)
                .centerCrop()
                .fit()
                .into(holder.carImageView);
    }

    @Override
    public int getItemCount() {
        return advertisements.size();
    }

    class AdvertisementViewHolder extends RecyclerView.ViewHolder {
        TextView carPrice;
        TextView carDescription;
        TextView carTitle;
        ImageView carImageView;
        ImageButton carFav;

        AdvertisementViewHolder(@NonNull View itemView) {
            super(itemView);
            carPrice = itemView.findViewById(R.id.carPriceTextView);
            carDescription = itemView.findViewById(R.id.carDescriptionTextView);
            carTitle = itemView.findViewById(R.id.carTitleTextView);
            carImageView = itemView.findViewById(R.id.carImageView);
            carFav = itemView.findViewById(R.id.fav_button);

        }
    }
}
