package auto.app;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import auto.app.model.Advertisement;

public class MainActivity extends AppCompatActivity implements RecyclerAdapter.OnItemClickListener {

    private Boolean isFavFilterSelected;

    private RecyclerAdapter adapter;

    private DatabaseReference databaseReference;
    private ArrayList<Advertisement> advertisements;
    private ArrayList<Advertisement> advertisementsAll;


    private ProgressBar progressBar;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isFavFilterSelected = false;
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        progressBar = findViewById(R.id.progress_circle);
        advertisements = new ArrayList<>();
        advertisementsAll = new ArrayList<>();
        adapter = new RecyclerAdapter(MainActivity.this, advertisements);
        adapter.setOnItemClickListener(MainActivity.this);
        recyclerView.setAdapter(adapter);
        storage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("cars_advertisement");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                advertisements.clear();
                advertisementsAll.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Advertisement advertisement = postSnapshot.getValue(Advertisement.class);
                    if (advertisement != null)
                        advertisement.setKey(postSnapshot.getKey());

                    advertisementsAll.add(advertisement);
                    if (!isFavFilterSelected)
                        advertisements.add(advertisement);
                    else if (advertisement != null && advertisement.getFav())
                        advertisements.add(advertisement);
                }
                progressBar.setVisibility(View.INVISIBLE);
                if (advertisements.size() == 0)
                    Toast.makeText(MainActivity.this, "Объявлений нет", Toast.LENGTH_LONG).show();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addPost:
                new AddAdvertisementDialog().show(getSupportFragmentManager(), "add advertisement from MainActivity");
                return true;
            case R.id.fav:
                isFavFilterSelected = !isFavFilterSelected;
                if (isFavFilterSelected)
                    item.setIcon(R.drawable.ic_favorite_white);
                else
                    item.setIcon(R.drawable.ic_favorite_border);
                changeDataSet();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onFavClick(int position) {
        Advertisement selectedItem = advertisements.get(position);
        final String selectedKey = selectedItem.getKey();
        selectedItem.setFav(!selectedItem.getFav());
        databaseReference.child(selectedKey).setValue(selectedItem);
    }

    @Override
    public void onDeleteClick(int position) {
        Advertisement selectedItem = advertisements.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference advRef = storage.getReferenceFromUrl(selectedItem.getmImageUrl());
        advRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                databaseReference.child(selectedKey).removeValue();
                Toast.makeText(MainActivity.this, "Удалено", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onEditClick(int position) {
        Advertisement advertisement = advertisements.get(position);

        AddAdvertisementDialog edit = new AddAdvertisementDialog();
        Bundle bundle = new Bundle();
        bundle.putString("url", advertisement.getmImageUrl());
        bundle.putString("title", advertisement.getmTitle());
        bundle.putString("price", advertisement.getmPrice());
        bundle.putString("description", advertisement.getmDescription());
        bundle.putString("key", advertisement.getKey());

        edit.setArguments(bundle);
        edit.show(getSupportFragmentManager(), "Edit advertisement");
    }

    private void changeDataSet() {
        advertisements.clear();
        for (Advertisement adv : advertisementsAll) {
            if (isFavFilterSelected) {
                if (adv.getFav())
                    advertisements.add(adv);
            } else
                advertisements.add(adv);
        }
        if (advertisements.size() == 0)
            Toast.makeText(MainActivity.this, "Объявлений нет", Toast.LENGTH_LONG).show();
        adapter.notifyDataSetChanged();
    }
}
