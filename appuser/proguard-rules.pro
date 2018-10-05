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
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#-ignorewarnings

-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
   }

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
       native <methods>;
 }
# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
      void set*(***);
      *** get*();
 }
# We want to keep methods in Activity that could be used in the XML attribute onClick
 -keepclassmembers class * extends android.app.Activity {
      public void *(android.view.View);
}
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-dontnote com.android.vending.licensing.ILicensingService
#-keep public class * extends android.support.v4.app.Fragment

-keep public class * extends android.app.Application
-keep public class * extends android.support.multidex.MultiDexApplication

# Keep the support library
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }
# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
#support library
-keep class android.support.** { *; }
-dontwarn android.support.**

# Specific classes that common test libs warn about
-dontwarn java.beans.**
-dontwarn javax.lang.model.element.Modifier
-dontwarn org.apache.tools.ant.**
-dontwarn org.assertj.core.internal.cglib.asm.util.TraceClassVisitor
-dontwarn org.easymock.**
-dontwarn org.jmock.core.**
-dontwarn org.w3c.dom.bootstrap.**
-dontwarn sun.misc.Unsafe
-dontwarn sun.reflect.**

# design support library
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

# appcompact V7
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

#for keep classes in analytics package
-keep class com.google.analytics.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

#Uncomment if using annotations to keep them.
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes SourceFile,LineNumberTable
-keepattributes InnerClasses

#The -optimizations option disables some arithmetic simplifications that Dalvik 1.0 and 1.5 can't handle. Note that the Dalvik VM also can't handle aggressive overloading (of static fields).
#To understand or change this check http://proguard.sourceforge.net/index.html#/manual/optimizations.html
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
-allowaccessmodification

#When not preverifing in a case-insensitive filing system, such as Windows. Because this tool unpacks your processed jars, you should then use:
-dontusemixedcaseclassnames

#Specifies not to ignore non-public library classes. As of version 4.5, this is the default setting
-dontskipnonpubliclibraryclasses

#Preverification is irrelevant for the dex compiler and the Dalvik VM, so we can switch it off with the -dontpreverify option.
-dontpreverify

#Specifies to write out some more information during processing. If the program terminates with an exception, this option will print out the entire stack trace, instead of just the exception message.
-verbose

-dontshrink

-dump class_files.txt
-printseeds seeds.txt
-printusage unused.txt
-printmapping mapping.txt

-keep class android.support.multidex.**
-keepclassmembernames class android.support.multidex.**{*;}
-keepclassmembers class android.support.multidex.** {*;}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
# Preserve the special static methods that are required in all enumeration classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}
# Preserve static fields of inner classes of R classes that might be accessed
# through introspection
# Keep the R
-keepclassmembers class **.R$* {
    public static <fields>;
}
-keep class com.google.android.gcm.** { *; }
-keep class com.android.support.** { *; }
-keep interface com.google.android.gcm.** { *; }
-keep class com.google.analytics.** { *; }
-keep interface com.google.analytics.** { *; }
-keep class com.google.tagmanager.** { *; }
-keep interface com.google.tagmanager.** { *; }
-keep class com.google.gson.** { *; }
-keep interface com.google.gson.** { *; }
-keep class com.googlecode.flickrjandroid.** { *; }
-keep interface com.googlecode.flickrjandroid.** { *; }
-keep class com.google.android.gms.ads.identifier.** { *; }
-keep class com.google.android.gms.** { *; }
-keep class com.google.maps.android.** { *; }
-keep interface com.google.maps.android.** { *; }
#firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
#arch
-keep class android.arch.** { *; }
-keep interface android.arch.** { *; }
-dontwarn android.arch.**
#parce bolts
-keep class com.parce.bolts.** { *; }
-keep interface com.parce.bolts.** { *; }
-dontwarn com.parce.bolts.**
#google
-keep class com.google.** { *; }
-keep interface com.google.** { *; }
-dontwarn com.google.**

##########################External Lib####################
#GSON
# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

#Retrofit
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

#smoothprogressbar:library-circular
-keep class com.github.castorflex.** { *; }

#facebook
-keep class com.facebook.** { *; }
-keep interface com.facebook.** { *; }
-dontwarn com.facebook.**

#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#Android-Image-Cropper
-keep class android.support.v7.widget.** { *; }
-dontwarn com.theartofdev.edmodo.cropper.**
-keep class com.theartofdev.edmodo.cropper.** { *; }
-keep interface com.theartofdev.edmodo.cropper.** { *; }


########################### Project Rules #####################################
-keep class com.biz4solutions.models.** { *; }
-keep interface com.biz4solutions.models.** { *; }
-keep class com.biz4solutions.customs.** { *; }
-keep interface com.biz4solutions.customs.** { *; }
-keep class com.biz4solutions.utilities.** { *; }
-keep interface com.biz4solutions.utilities.** { *; }
-keep class com.biz4solutions.services.** { *; }
-keep interface com.biz4solutions.services.** { *; }
-keep class com.biz4solutions.** { *; }
-keep interface com.biz4solutions.** { *; }

-dontwarn org.conscrypt.**
-dontwarn org.codehaus.mojo.**
-dontnote org.apache.http.**
#-dontnote com.biz4solutions.**
