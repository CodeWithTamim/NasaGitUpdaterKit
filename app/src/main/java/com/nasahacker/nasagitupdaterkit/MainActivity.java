package com.nasahacker.nasagitupdaterkit;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.nasahacker.library.NasaGitUpdaterKit;

public class MainActivity extends AppCompatActivity {

    private NasaGitUpdaterKit updaterKit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize NasaGitUpdaterKit using the Builder
        updaterKit = new NasaGitUpdaterKit.Builder(this)
                .setGithubUsername("2dust")
                .setGithubRepoName("V2rayNG")
                .setCurrentVersionName("1.0.0")
                .build();

        // Set update listener
        updaterKit.setUpdateListener(new NasaGitUpdaterKit.UpdateListener() {
            @Override
            public void onUpdateAvailable(String downloadUrl) {
                // Handle update availability
            }

            @Override
            public void onUpdateFailed(Exception e) {
                // Handle update failure

            }
        });

        updaterKit.checkForUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NasaGitUpdaterKit.REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updaterKit.checkForUpdates();
            } else {
                Toast.makeText(this, "Permission required for update", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
