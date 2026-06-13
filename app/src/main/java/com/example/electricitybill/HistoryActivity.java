package com.example.electricitybill;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    ListView listViewHistory;
    DatabaseHelper myDb;
    ArrayList<String> listItem;
    ArrayList<String> listIds; // Store database primary key IDs hiddenly
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listViewHistory = findViewById(R.id.listViewHistory);
        myDb = new DatabaseHelper(this);

        listItem = new ArrayList<>();
        listIds = new ArrayList<>();

        viewData();

        // When a row is clicked, open a detail page to View/Edit/Delete
        listViewHistory.setOnItemClickListener((parent, view, position, id) -> {
            String selectedId = listIds.get(position);
            Intent intent = new Intent(HistoryActivity.this, DetailActivity.class);
            intent.putExtra("RECORD_ID", selectedId);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewData(); // Refresh list if returning back after editing/deleting
    }

    private void viewData() {
        listItem.clear();
        listIds.clear();
        Cursor cursor = myDb.getAllData();

        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No records found.", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                // Fetch columns matching database indices
                String id = cursor.getString(0);
                String month = cursor.getString(1);
                String finalCost = cursor.getString(5);

                listIds.add(id);
                // Requirement check: Display Month and Final Cost ONLY on list view
                listItem.add("📅 " + month + " \t—\t RM " + finalCost);
            }
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listItem);
            listViewHistory.setAdapter(adapter);
        }
        cursor.close();
    }
}