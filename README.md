# すたろぐ Study Binder for Android

This Android app packages the original StulogPlus web app (https://h78124905-png.github.io/stulogplus/) inside Android System WebView instead of reimplementing its data model and screens in a separate native app.

## Compatibility
- The original HTML, CSS, JavaScript, data normalization, and localStorage keys are bundled from the website repository.
- Study data uses the original storage schema (sl2_data and sl2_data_backup) within this app's private WebView storage.
- Timer state is persisted by the original app's sl2_timer key.
- Backup export uses Android's document picker to save a JSON file. Restore remains available through the app's backup/restore screen.
- The remote Google Fonts dependency is removed from the bundled copy so the app works offline.

## Build
GitHub Actions runs gradle assembleDebug and uploads stulog-app-debug-apk as an artifact.

The APK uses Android System WebView, so it does not bundle a browser engine or the original website's external dependencies.
