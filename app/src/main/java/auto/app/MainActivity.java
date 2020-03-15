package auto.app;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Boolean isFavFilterSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isFavFilterSelected = false;

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
