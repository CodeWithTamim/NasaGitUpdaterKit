# Thanks For Using NasaGitUpdaterKit
## This library can be used to show auto update dialog and update your app from github releases.
## Follow the documentation below to know how to use the library

### Step 0: Add this to your project gradle
#### `settings.gradle`
```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        ...
        ...
        //add jitpack
        maven { url 'https://jitpack.io' }
    }
}
```
#### `settings.gradle.kts`
```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        ...
        ...
        // add jitpack
        maven(url = "https://jitpack.io")
    }
}
```
#### `build.gradle` app level module
```groovy
dependencies 
{
  implementation 'com.github.CodeWithTamim:NasaGitUpdaterKit:1.0.0'
}
```
#### `build.gradle.kts` app level module
```groovy
dependencies 
{
implementation("com.github.CodeWithTamim:NasaGitUpdaterKit:1.0.0")
}
```

### If your min sdk is not 21 or different then add this to the `AndroidManifest.xml`

```xml

<uses-sdk android:minSdkVersion="your_min_sdk" tools:overrideLibrary="com.nasahacker.library" />
```

### Step 1 : Add these permissions and the receiver to your project manifest
#### Add the permissions
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
 ```
#### And the receiver
```xml
<application
    ...>
    <!-- Other components such as activities, services, etc. -->

    <receiver
        android:name="com.nasahacker.library.DownloadCompleteReceiver"
        android:exported="true">
        <intent-filter>
            <action android:name="android.intent.action.DOWNLOAD_COMPLETE" />
        </intent-filter>
    </receiver>

    <!-- Other components such as providers, activities, etc. -->
</application>

```
### Step 2 : Initialize the NasaGitUpdaterKit
#### Java

```java
// Define globally
private NasaGitUpdaterKit updaterKit;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

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
          //ignore
        }

        @Override
        public void onUpdateFailed(Exception e) {
            // Handle update failure
            // For example, you might want to show a toast message or log the error
            Toast.makeText(MainActivity.this, "Update check failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    });

    // Check for updates
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

```
#### Kotlin

```Kotlin
// Define globally
private lateinit var updaterKit: NasaGitUpdaterKit

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // Initialize NasaGitUpdaterKit using the Builder
    updaterKit = NasaGitUpdaterKit.Builder(this)
        .setGithubUsername("2dust")
        .setGithubRepoName("V2rayNG")
        .setCurrentVersionName("1.0.0")
        .build()

    // Set update listener
    updaterKit.setUpdateListener(object : NasaGitUpdaterKit.UpdateListener {
        override fun onUpdateAvailable(downloadUrl: String) {
            //ignore
        }

        override fun onUpdateFailed(e: Exception) {
            // Handle update failure
            // For example, you might want to show a toast message or log the error
            Toast.makeText(this@MainActivity, "Update check failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    })

    // Check for updates
    updaterKit.checkForUpdates()
}

override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == NasaGitUpdaterKit.REQUEST_PERMISSION_CODE) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            updaterKit.checkForUpdates()
        } else {
            Toast.makeText(this, "Permission required for update", Toast.LENGTH_SHORT).show()
        }
    }
}
```

### Thanks for reading the documentation, I'm `Tamim`, I made this library and I'm the one who was helping you throughout the documentation :)
### If the library helped you out then please give it a start and share with your dev friends ! The project is open for contrubution so if you have any fixes or new feature enhancement then just fork it then make your changes create a new brach and then just hit a pull request.

## Thank you guys for your love and support
## If you have any queries or need help then just open a issue or  <a href="mailto:tamimh.dev@gmail.com">mail me</a>
## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.
