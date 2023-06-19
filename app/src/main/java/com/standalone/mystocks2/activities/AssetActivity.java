package com.standalone.mystocks2.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.standalone.droid.dbase.DatabaseManager;
import com.standalone.mystocks2.R;
import com.standalone.mystocks2.adapters.AssetAdapter;
import com.standalone.mystocks2.fragments.TradeDialogFragment;
import com.standalone.mystocks2.helpers.AssetTableHandler;
import com.standalone.mystocks2.helpers.RecyclerItemTouchHelper;
import com.standalone.mystocks2.interfaces.DialogCloseListener;

public class AssetActivity extends AppCompatActivity implements DialogCloseListener {

    AssetAdapter adapter;
    AssetTableHandler tableHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset);

        tableHandler = new AssetTableHandler(DatabaseManager.getDatabase(this));

        RecyclerView assetRecyclerView = findViewById(R.id.assetRecyclerView);

        adapter = new AssetAdapter(this, tableHandler);
        assetRecyclerView.setAdapter(adapter);

        adapter.setItemList(tableHandler.fetchAll());

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(assetRecyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setQueryHint("Type here to search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.setItemList(tableHandler.fetchAll());
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_item_add) {
            new TradeDialogFragment().show(getSupportFragmentManager(), TradeDialogFragment.TAG);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        adapter.setItemList(tableHandler.fetchAll());
    }
}