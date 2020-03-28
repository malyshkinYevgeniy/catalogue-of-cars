package auto.app;


import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
    private OnItemClickListener listener;

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
            holder.carFav.setImageResource(R.drawable.ic_favorite_red);
        else
            holder.carFav.setImageResource(R.drawable.ic_favorite_border_red);

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

    class AdvertisementViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener, View.OnClickListener {
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

            itemView.setOnCreateContextMenuListener(this);
            carFav.setOnClickListener(this);
        }


        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuItem delete = contextMenu.add(Menu.NONE, 1, 1, "Удалить");
            MenuItem fav = contextMenu.add(Menu.NONE, 2, 2, "Избранное");
            MenuItem edit = contextMenu.add(Menu.NONE, 3, 3, "Изменить");
            delete.setOnMenuItemClickListener(this);
            fav.setOnMenuItemClickListener(this);
            edit.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                    switch (menuItem.getItemId()) {
                        case 1:
                            listener.onDeleteClick(position);
                            break;
                        case 2:
                            listener.onFavClick(position);
                            break;
                        case 3:
                            listener.onEditClick(position);
                            break;
                    }
            }
            return false;
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (view.getId() == R.id.fav_button)
                if (listener != null)
                    listener.onFavClick(position);
        }
    }

    public interface OnItemClickListener {
        void onFavClick(int position);

        void onDeleteClick(int position);

        void onEditClick(int position);
    }

    void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
