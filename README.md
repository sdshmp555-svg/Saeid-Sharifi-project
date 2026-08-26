# Italy AI Cultural Tourism Pro App

A professional Kotlin + Jetpack Compose Android prototype derived from the thesis:

**Artificial Intelligence in Cultural Tourism Marketing, Enhancing Revenue Management Strategies for Made in Italy Experiences**

## What this build contains

- Material 3 English UI
- AI-style personalization engine based on visitor segment, interests, budget and off-peak preference
- Automatic multi-day itinerary generation
- Made in Italy cultural experience catalog
- Dynamic pricing simulator using occupancy, seasonal demand, online trend, competitor index and weather/event score
- Ethical pricing guardrail capped at +/-22% in this prototype
- Revenue and sustainability dashboard
- Local Room database for saved experiences
- Google Maps / map intent for saved experiences
- Codemagic YAML workflows for debug and release APKs
- GitHub Actions workflow

## Important note

This is a research/product prototype. The recommendation and pricing engine is a local simulation designed to operationalize the thesis framework. It is not a claim of field-validated AI forecasting, and it does not ship a secret external AI API key inside the APK.

## GitHub upload

Upload the **contents of this repository root** to GitHub. Do not upload the ZIP as a single file if you want Codemagic to detect `codemagic.yaml`.

## Codemagic

Codemagic supports native Android projects configured with a root-level `codemagic.yaml`. The current personal free tier includes 500 build minutes/month on macOS M2 machines, so this project uses `mac_mini_m2` in the provided workflows. Linux build machines are billed separately on personal accounts. The file contains two workflows:

- `android-apk-debug` - fastest test APK
- `android-release-apk` - release APK (unsigned unless you configure signing credentials in Codemagic)

After connecting the GitHub repository in Codemagic:

1. Select the repository.
2. Select Android / native Android.
3. Scan the branch containing `codemagic.yaml`.
4. Run `android-apk-debug` first.
5. Download the APK from Artifacts.

For Play Store distribution, upload a private keystore in Codemagic and configure the release workflow signing variables; never commit a keystore to GitHub.

## Local build

Open the project in Android Studio and use the included `gradlew` launcher. On first execution it downloads Gradle 8.7 if needed.

Debug APK:

`./gradlew :app:assembleDebug`

Release APK:

`./gradlew :app:assembleRelease`
