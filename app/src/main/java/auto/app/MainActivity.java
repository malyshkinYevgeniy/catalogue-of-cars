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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import auto.app.model.Advertisement;

public class MainActivity extends AppCompatActivity {
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
        recyclerView.setAdapter(adapter);
        storage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("cars_advertisement");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                advertisements.clear();
                advertisementsAll.clear();
                Toast.makeText(MainActivity.this, "update", Toast.LENGTH_LONG).show();

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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
