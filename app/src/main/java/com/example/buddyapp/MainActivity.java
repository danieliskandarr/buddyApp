package com.example.buddyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DBHelper dbHelper;
    ListView listView;
    SearchView searchView;
    ArrayList<Friend> friendList;
    ArrayList<String> displayList; // Just to display names in simple list
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        listView = findViewById(R.id.listView);
        searchView = findViewById(R.id.searchView);

        loadFriends(""); // Load all friends initially

        // Search Functionality [cite: 28]
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadFriends(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                loadFriends(newText);
                return false;
            }
        });

        // Handle clicks on list items (Open Edit Page)
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Friend selectedFriend = friendList.get(position);
            Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
            intent.putExtra("ID", selectedFriend.getId()); // Pass ID to edit
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFriends(""); // Refresh list when coming back
    }

    private void loadFriends(String query) {
        friendList = dbHelper.getAllFriends(query);
        displayList = new ArrayList<>();
        for (Friend f : friendList) {
            // Display Name and State in the list
            displayList.add(f.getName() + " (" + f.getAddress4() + ")");
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        listView.setAdapter(adapter);
    }

    public void goToAdd(View view) {
        startActivity(new Intent(this, AddEditActivity.class));
    }

    public void goToReports(View view) {
        startActivity(new Intent(this, ReportActivity.class));
    }
}