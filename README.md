# ScanCard 

Android library for credit card scanning using Google ML Kit, CameraX and Jetpack Compose.

## Installation

Add it in your root `build.gradle` at the end of repositories:
```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency:
```gradle
dependencies {
    implementation 'com.github.Gustavo-MedeirosS:ScanCard:1.0.0'
}
```

## Usage

In your Activity/Fragment:

```kotlin
private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    activityResultLauncher = registerForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            handleScanCardResult(data = result.data)
        }
    }
}

private fun scanCard() {
    val intent = Intent(this, ScanCardActivity::class.java)
    activityResultLauncher.launch(intent)
}

private fun handleScanCardResult(data: Intent?) {
    if (data != null) {
        // Here you handle the result
    }
}
```

If you don't allow Camera permission in your app and try to scan the card, a message will pop up and you'll need to treat this in `onActivityResult`, something like this:
```kotlin
override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        caller: ComponentCaller
    ) {
        super.onActivityResult(requestCode, resultCode, data, caller)

        if (resultCode == ScanCardActivity.CAMERA_NOT_GRANTED_CODE) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                ScanCardActivity.CAMERA_PERMISSION_CODE
            )
        }
    }
```

## Sample App

Check the `sample` module for a complete implementation example.

## License

Apache License 2.0