package com.standalone.mystocks2.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.standalone.droid.dbase.DatabaseManager;
import com.standalone.mystocks2.R;
import com.standalone.mystocks2.adapters.HistoryAdapter;
import com.standalone.mystocks2.helpers.HistoryTableHandler;
import com.standalone.mystocks2.models.Stock;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private HistoryTableHandler tableHandler;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        tableHandler = new HistoryTableHandler(DatabaseManager.getDatabase(this));

        RecyclerView historyRecyclerView = findViewById(R.id.historyRecyclerView);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new HistoryAdapter(this);
        historyRecyclerView.setAdapter(adapter);

        List<Stock> itemList = tableHandler.fetchAll();
        adapter.setItemList(itemList);

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

        menu.findItem(R.id.menu_item_add).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }
}
