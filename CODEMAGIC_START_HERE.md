# Codemagic - exact steps

1. Create a new empty GitHub repository.
2. Upload the **contents** of this folder to the repository root. The root must contain `codemagic.yaml`, `settings.gradle`, `build.gradle`, `gradlew`, and `app/`.
3. In Codemagic, choose **Add application** and connect the GitHub repository.
4. Select the branch containing `codemagic.yaml` and scan the configuration file.
5. Choose workflow **android-apk-debug**.
6. Start the build.
7. When the build completes, open **Artifacts** and download the `.apk`.

The debug workflow uses Codemagic's `mac_mini_m2`, which is the machine included in the current individual free tier (500 minutes/month). Linux machines are billed separately on personal accounts.

For a Play Store-ready signed release, configure an Android keystore in Codemagic and wire it to the release workflow. Never upload `.jks` or `.keystore` files to GitHub.
