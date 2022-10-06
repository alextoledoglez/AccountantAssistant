# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ### DAOs ###
-keep class com.personal.accountantAssistant.data.dao.** { *; }

# ### Entities ###
-keep class com.personal.accountantAssistant.data.entities.** { *; }

# ### Room ###
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder

## ### Gson ###
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# Gson specific classes
-dontwarn sun.misc.**
-keep class com.google.gson.stream.** { *; }
-keep public class com.google.gson.**
-keep public class com.google.gson.** {public private protected *;}

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

##---------------End: proguard configuration for Gson  ----------

-keepclassmembers,allowoptimization enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}

-keep class Google.Impl.* { *; }
-keep class Google.** { *; }
-keep class com.google.unity.** {*;}

-keepattributes Annotation

## ### Google ###

-dontnote com.google.**
-keep class com.google.googlesignin.** { *; }
-keepnames class com.google.googlesignin.* { *; }
-keep class persistence.** {
  *;
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
@com.google.android.gms.common.annotation.KeepName *;
}

##---------------End: proguard configuration for Google  ----------

## ### GMS library ###
-keep class com.google.android.gms.security.ProviderInstaller { *; }
-keep public class com.google.android.gms.* { public *; }
-keep class com.google.android.gms.auth.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

## ### Ads ###
-keep public class com.google.android.gms.ads.*{public *;}
-keep public class com.google.ads.*{public *;}

# ### Firebase ###
-keep class com.firebase.** { *; }

# Keep the annotations
-keepattributes *Annotation*

# If you are using custom exceptions, add this line so that custom exception types are skipped during obfuscation:
-keep public class * extends java.lang.Exception

# ### Parcel ###
-keepclassmembers class * implements android.os.Parcelable { static ** CREATOR; }

-keepclassmembers class com.personal.accountantAssistant.** extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class com.microsoft.** { *; }
-keep class com.microsoft.**
-keep interface com.microsoft.** { *; }
-keepclasseswithmembernames class com.microsoft.** { *; }

-ignorewarnings

-dontobfuscate
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-dontwarn javax.**
-keep class **$$ViewBinder { *; }
-dontwarn java.lang.invoke**
-dontwarn okhttp3.internal.**
-dontwarn java.nio.file.**
-dontwarn okio.**
-dontwarn org.**

-dontwarn android.support.**

-keep class androidx.work.** { *; }