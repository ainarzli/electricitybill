package com.example.electricitybill;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView tvGithubLink = findViewById(R.id.tvGithubLink);

        // Making the GitHub target URL clickable to fulfill rubric requirements
        tvGithubLink.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://github.com/your-username/your-repository-name")); // Replace with your actual repo link later
            startActivity(intent);
        });
    }
}