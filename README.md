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

## Sample App

Check the `sample` module for a complete implementation example.

## License

Apache License 2.0