package com.nasahacker.library;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * NasaGitUpdaterKit provides functionality to check for updates from GitHub,
 * show an update dialog, and download and install the update.
 * <p>
 * This class requires the GitHub username and repository name to fetch the latest
 * release information. It also needs the current version name to compare with
 * the latest version available.
 * </p>
 */
public class NasaGitUpdaterKit
{

    public static final int REQUEST_PERMISSION_CODE = 1;

    private final Activity context;
    private final String updateTitle;
    private final String updateMessage;
    private final String positiveButtonText;
    private final String negativeButtonText;
    private final String githubUsername;
    private final String githubRepoName;
    private final String currentVersionName;
    private UpdateListener updateListener;

    private NasaGitUpdaterKit(Builder builder)
    {
        this.context = builder.context;
        this.updateTitle = builder.updateTitle;
        this.updateMessage = builder.updateMessage;
        this.positiveButtonText = builder.positiveButtonText;
        this.negativeButtonText = builder.negativeButtonText;
        this.githubUsername = builder.githubUsername;
        this.githubRepoName = builder.githubRepoName;
        this.currentVersionName = builder.currentVersionName;
    }

    public static class Builder
    {
        private final Activity context;
        private String updateTitle = "Update Available";
        private String updateMessage = "A new version is available. Do you want to update?";
        private String positiveButtonText = "Update";
        private String negativeButtonText = "Cancel";
        private String githubUsername = "";
        private String githubRepoName = "";
        private String currentVersionName = "";

        public Builder(Activity context)
        {
            this.context = context;
        }

        public Builder setUpdateTitle(String title)
        {
            this.updateTitle = title;
            return this;
        }

        public Builder setUpdateMessage(String message)
        {
            this.updateMessage = message;
            return this;
        }

        public Builder setPositiveButtonText(String text)
        {
            this.positiveButtonText = text;
            return this;
        }

        public Builder setNegativeButtonText(String text)
        {
            this.negativeButtonText = text;
            return this;
        }

        public Builder setGithubUsername(String username)
        {
            this.githubUsername = username;
            return this;
        }

        public Builder setGithubRepoName(String repoName)
        {
            this.githubRepoName = repoName;
            return this;
        }

        public Builder setCurrentVersionName(String versionName)
        {
            this.currentVersionName = versionName;
            return this;
        }

        public NasaGitUpdaterKit build()
        {
            if (githubUsername.isEmpty() || githubRepoName.isEmpty() || currentVersionName.isEmpty())
            {
                throw new IllegalStateException("GitHub username, repository name, and current version name must be set.");
            }
            return new NasaGitUpdaterKit(this);
        }
    }

    /**
     * Listener interface for update events.
     */
    public interface UpdateListener
    {
        /**
         * Called when an update is available.
         *
         * @param downloadUrl The URL from which the update can be downloaded.
         */
        void onUpdateAvailable(String downloadUrl);

        /**
         * Called when the update check fails.
         *
         * @param e The exception that occurred.
         */
        void onUpdateFailed(Exception e);
    }

    /**
     * Sets the listener for update events.
     *
     * @param listener The listener to be set.
     */
    public void setUpdateListener(UpdateListener listener)
    {
        this.updateListener = listener;
    }

    /**
     * Checks for updates by fetching the latest release information from GitHub.
     * If an update is available, it shows an update dialog.
     */
    public void checkForUpdates()
    {
        new Thread(() ->
        {
            try
            {
                String latestVersion = fetchLatestVersion();
                if (!currentVersionName.equals(latestVersion))
                {
                    String downloadUrl = fetchDownloadUrl();
                    if (updateListener != null)
                    {
                        updateListener.onUpdateAvailable(downloadUrl);
                    }
                    context.runOnUiThread(() -> showUpdateDialog(downloadUrl));
                }
            } catch (Exception e)
            {
                if (updateListener != null)
                {
                    updateListener.onUpdateFailed(e);
                }
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Fetches the latest version tag from the GitHub repository.
     *
     * @return The latest version tag.
     * @throws Exception If an error occurs during the network request.
     */
    private String fetchLatestVersion() throws Exception
    {
        URL url = new URL("https://api.github.com/repos/" + githubUsername + "/" + githubRepoName + "/releases/latest");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)
        {
            result.append(line);
        }
        reader.close();
        JSONObject jsonResponse = new JSONObject(result.toString());
        return jsonResponse.getString("tag_name");
    }

    /**
     * Fetches the download URL for the latest release from the GitHub repository.
     *
     * @return The download URL.
     * @throws Exception If an error occurs during the network request.
     */
    private String fetchDownloadUrl() throws Exception
    {
        URL url = new URL("https://api.github.com/repos/" + githubUsername + "/" + githubRepoName + "/releases/latest");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)
        {
            result.append(line);
        }
        reader.close();
        JSONObject jsonResponse = new JSONObject(result.toString());
        JSONArray assets = jsonResponse.getJSONArray("assets");
        return assets.getJSONObject(0).getString("browser_download_url");
    }

    /**
     * Shows a dialog to the user indicating that an update is available.
     * Provides options to update or cancel.
     *
     * @param downloadUrl The URL from which to download the update.
     */
    private void showUpdateDialog(String downloadUrl)
    {
        new MaterialAlertDialogBuilder(context)
                .setTitle(updateTitle)
                .setMessage(updateMessage)
                .setPositiveButton(positiveButtonText, (dialog, which) ->
                {
                    if (hasPermissions())
                    {
                        downloadAndInstallUpdate(downloadUrl);
                        Log.d("HACKER", "showUpdateDialog: " + " HAS Permission ");
                    } else
                    {
                        Log.d("HACKER", "showUpdateDialog: " + "Permission requested");
                        requestPermissions();
                    }
                })
                .setNegativeButton(negativeButtonText, null)
                .show();
    }

    /**
     * Checks if the required permissions are granted.
     *
     * @return True if permissions are granted, false otherwise.
     */
    private boolean hasPermissions()
    {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                || ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Requests necessary permissions from the user.
     */
    private void requestPermissions()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_PERMISSION_CODE);
        } else
        {
            ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
        }
    }

    /**
     * Downloads and installs the update from the provided URL.
     *
     * @param downloadUrl The URL from which to download the update.
     */
    private void downloadAndInstallUpdate(String downloadUrl)
    {
        if (!URLUtil.isValidUrl(downloadUrl))
        {
            Toast.makeText(context, "Invalid download URL", Toast.LENGTH_SHORT).show();
            return;
        }
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl))
                .setTitle("Downloading Update")
                .setDescription("Please wait...")
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "update.apk")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
        registerReceiver();
    }

    /**
     * Registers a receiver to listen for download completion events.
     */
    private void registerReceiver()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
            context.registerReceiver(new DownloadCompleteReceiver(), new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_EXPORTED);
        } else
        {
            context.registerReceiver(new DownloadCompleteReceiver(), new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
    }
}
