# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/nelson/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

-dontwarn java.nio.file.Files
-dontwarn java.nio.file.Path
-dontwarn java.nio.file.OpenOption
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-keepclassmembers class ** {
    public void onEvent(**);
}


-keepattributes *Annotation*
-keepattributes Signature
-dontwarn com.squareup.**
-keep class com.squareup.** { *; }


-dontwarn com.parse.**
-dontwarn com.mixpanel.**
-dontwarn com.nhaarman.**
-dontwarn rx.**

-keep public class com.daimajia.**
-keep public class com.google.android.gms.**
-dontwarn com.google.android.gms.**

-dontwarn okio.**

-dontwarn com.squareup.okhttp.*

-dontwarn retrofit.appengine.UrlFetchClient

-keepattributes *Annotation*

-keep class retrofit.** { *; }

-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

-keepattributes Signature