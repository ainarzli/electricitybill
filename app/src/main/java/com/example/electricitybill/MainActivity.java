package com.example.electricitybill;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.electricitybill.DatabaseHelper;
import com.example.electricitybill.R;

public class MainActivity extends AppCompatActivity {

    Spinner spinnerMonth;
    EditText etUnits;
    SeekBar seekBarRebate;
    TextView tvRebateValue, tvTotalCharges, tvFinalCost;
    Button btnCalculate, btnHistory, btnAbout;

    DatabaseHelper myDb;

    String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDb = new DatabaseHelper(this);

        spinnerMonth = findViewById(R.id.spinnerMonth);
        etUnits = findViewById(R.id.etUnits);
        seekBarRebate = findViewById(R.id.seekBarRebate);
        tvRebateValue = findViewById(R.id.tvRebateValue);
        tvTotalCharges = findViewById(R.id.tvTotalCharges);
        tvFinalCost = findViewById(R.id.tvFinalCost);
        btnCalculate = findViewById(R.id.btnCalculate);
        btnHistory = findViewById(R.id.btnHistory);
        btnAbout = findViewById(R.id.btnAbout);

        // Setup Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);

        // Setup SeekBar
        seekBarRebate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvRebateValue.setText(progress + "%");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Click Calculate
        btnCalculate.setOnClickListener(v -> calculateBill());

        // Click History
        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        // Click About
        btnAbout.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        });

        Button[] buttons = {btnCalculate, btnHistory, btnAbout};

        for (Button btn : buttons) {
            btn.setOnTouchListener((v, event) -> {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    v.animate().scaleX(0.93f).scaleY(0.93f).setDuration(100).start();
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP ||
                        event.getAction() == android.view.MotionEvent.ACTION_CANCEL) {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                }
                return false;
            });
        }
    }

    private void calculateBill() {
        String unitStr = etUnits.getText().toString();

        if (unitStr.isEmpty()) {
            etUnits.setError("Please enter electricity units");
            return;
        }

        double units = Double.parseDouble(unitStr);

        if (units < 1 || units > 1000) {
            etUnits.setError("Units must be between 1 and 1000");
            return;
        }

        double totalCharges = 0.0;
        double remainingUnits = units;

        // Logic Pengiraan Bil
        if (remainingUnits > 200) {
            totalCharges += 200 * 0.218;
            remainingUnits -= 200;
        } else {
            totalCharges += remainingUnits * 0.218;
            remainingUnits = 0;
        }

        if (remainingUnits > 100) {
            totalCharges += 100 * 0.334;
            remainingUnits -= 100;
        } else if (remainingUnits > 0) {
            totalCharges += remainingUnits * 0.334;
            remainingUnits = 0;
        }

        if (remainingUnits > 300) {
            totalCharges += 300 * 0.516;
            remainingUnits -= 300;
        } else if (remainingUnits > 0) {
            totalCharges += remainingUnits * 0.516;
            remainingUnits = 0;
        }

        if (remainingUnits > 0) {
            totalCharges += remainingUnits * 0.546;
        }

        // Rebate
        double rebatePercentage = seekBarRebate.getProgress();
        double finalCost = totalCharges - (totalCharges * (rebatePercentage / 100.0));

        // Output
        tvTotalCharges.setText(String.format("Total Charges: RM %.2f", totalCharges));
        tvFinalCost.setText(String.format("Final Cost: RM %.2f", finalCost));

        // Insert to database
        String month = spinnerMonth.getSelectedItem().toString();

        boolean isInserted = myDb.insertData(
                month,
                units,
                totalCharges,
                rebatePercentage,
                finalCost
        );

        if(isInserted) {
            Toast.makeText(this, "Calculation & Save Successful!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Save Failed!", Toast.LENGTH_SHORT).show();
        }

        if(isInserted) {
            Toast.makeText(this, "Calculation & Save Successful!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Save Failed!", Toast.LENGTH_SHORT).show();
        }
    }
}