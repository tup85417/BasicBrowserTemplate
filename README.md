# Composable Web Browser

Jetpack Compose browser for the CIS 3515 Composable Web Browser assignment.

The screen has a URL `TextField` and a Go `Button` above a `WebView`. Compose hosts the `WebView` with `AndroidView`. Pressing Go or the keyboard's Go action runs `normalizeWebUrl`, adds `https://` when no scheme is present, and passes the result to `WebView.loadUrl()`.

Examples: `wikipedia.com` becomes `https://wikipedia.com`; an existing `http://` or `https://` URL remains unchanged. Empty, malformed, and non-web URLs show an error.

Open this folder in Android Studio and run the `app` configuration. For a terminal build, set `JAVA_HOME` to Android Studio's bundled JBR, then run:

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.
