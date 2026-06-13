package com.example.electricitybill;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    EditText etMonth, etUnits, etRebate;
    TextView tvCharges, tvFinal;
    Button btnUpdate, btnDelete;
    DatabaseHelper myDb;
    String recordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        etMonth = findViewById(R.id.etDetailMonth);
        etUnits = findViewById(R.id.etDetailUnits);
        etRebate = findViewById(R.id.etDetailRebate);
        tvCharges = findViewById(R.id.tvDetailCharges);
        tvFinal = findViewById(R.id.tvDetailFinal);
        btnUpdate = findViewById(R.id.btnUpdateRecord);
        btnDelete = findViewById(R.id.btnDeleteRecord);

        myDb = new DatabaseHelper(this);
        recordId = getIntent().getStringExtra("RECORD_ID");

        loadRecordDetails();

        btnUpdate.setOnClickListener(v -> updateRecord());
        btnDelete.setOnClickListener(v -> deleteRecord());
    }

    private void loadRecordDetails() {
        Cursor cursor = myDb.getAllData();
        while (cursor.moveToNext()) {
            if (cursor.getString(0).equals(recordId)) {
                etMonth.setText(cursor.getString(1));
                etUnits.setText(cursor.getString(2));
                etRebate.setText(cursor.getString(3));
                tvCharges.setText("RM " + cursor.getString(4));
                tvFinal.setText("RM " + cursor.getString(5));
                break;
            }
        }
        cursor.close();
    }

    private void updateRecord() {
        String unitsStr = etUnits.getText().toString().trim();
        String rebateStr = etRebate.getText().toString().trim();

        if(unitsStr.isEmpty() || rebateStr.isEmpty()) {
            Toast.makeText(this, "Fields cannot be blank", Toast.LENGTH_SHORT).show();
            return;
        }

        double units = Double.parseDouble(unitsStr);
        double rebatePercent = Double.parseDouble(rebateStr);

        if (units < 1 || units > 1000 || rebatePercent < 0 || rebatePercent > 5) {
            Toast.makeText(this, "Units (1-1000), Rebate (0-5%)", Toast.LENGTH_SHORT).show();
            return;
        }

        // Recalculate based on assignments blocks rules
        double totalCharges = 0.0;
        double remainingUnits = units;

        if (remainingUnits > 200) { totalCharges += 200 * 0.218; remainingUnits -= 200; }
        else { totalCharges += remainingUnits * 0.218; remainingUnits = 0; }

        if (remainingUnits > 100) { totalCharges += 100 * 0.334; remainingUnits -= 100; }
        else if (remainingUnits > 0) { totalCharges += remainingUnits * 0.334; remainingUnits = 0; }

        if (remainingUnits > 300) { totalCharges += 300 * 0.516; remainingUnits -= 300; }
        else if (remainingUnits > 0) { totalCharges += remainingUnits * 0.516; remainingUnits = 0; }

        if (remainingUnits > 0) { totalCharges += remainingUnits * 0.546; }

        double finalCost = totalCharges - (totalCharges * (rebatePercent / 100.0));

        boolean isUpdated = myDb.updateData(recordId, etMonth.getText().toString(), units, totalCharges, rebatePercent, finalCost);
        if(isUpdated) {
            Toast.makeText(this, "Updated successfully!", Toast.LENGTH_SHORT).show();
            finish(); // Close activity, return back
        } else {
            Toast.makeText(this, "Update failed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteRecord() {
        Integer deletedRows = myDb.deleteData(recordId);
        if(deletedRows > 0) {
            Toast.makeText(this, "Record deleted successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Delete failed.", Toast.LENGTH_SHORT).show();
        }
    }
}