# Firebase  https://github.com/firebase/firebase-android-sdk/issues/4900
-keep public class com.google.firebase.** { *;}
-keep class com.google.android.gms.internal.** { *;}
-keepclasseswithmembers class com.google.firebase.FirebaseException


# https://github.com/square/retrofit/issues/3751#issuecomment-1564410089
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

# https://github.com/google/gson/commit/43396e45fd1f03e408e0e83b168a72a0f3e0b84e#diff-5da161239475717e284b3a9a85e2f39256d739fb7564ae7fda7f79cee000c413
-keepclasseswithmembers,allowobfuscation,includedescriptorclasses class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

-keep class com.jonrysimbolonstory.model.** { *; }