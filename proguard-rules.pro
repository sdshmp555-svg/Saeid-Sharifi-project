# Keep Room generated implementations and Kotlin metadata safe for future release builds.
-keep class * extends androidx.room.RoomDatabase { <init>(...); }
-keep class **_Impl { *; }
