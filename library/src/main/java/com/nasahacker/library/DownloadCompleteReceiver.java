package com.nasahacker.library;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;
import androidx.core.content.FileProvider;

import java.io.File;

/**
 * CodeWithTamim
 *
 * @developer Tamim Hossain
 * @mail tamimh.dev@gmail.com
 */
public class DownloadCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

        if (downloadId != -1) {
            // Get the downloaded file
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "update.apk");

            if (file.exists()) {
                // Get URI for the file using FileProvider
                Uri fileUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);

                // Create intent to install the APK
                Intent installIntent = new Intent(Intent.ACTION_VIEW);
                installIntent.setDataAndType(fileUri, "application/vnd.android.package-archive");
                installIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);

                // Start the install activity
                context.startActivity(installIntent);
            } else {
                Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show();
        }
    }
}
