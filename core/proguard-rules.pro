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
-dontoptimize
-keeppackagenames org.ituns.network.core

-keep class org.ituns.network.core.NetworkCallback { *; }
-keep class org.ituns.network.core.NetworkClient {
    protected <fields>;
    protected <methods>;
    public <methods>;
}
-keep class org.ituns.network.core.NetworkCode { *; }
-keep class org.ituns.network.core.NetworkMethod { *; }
-keep class org.ituns.network.core.NetworkRequest { public <methods>; }
-keep class org.ituns.network.core.NetworkRequest$Builder { public <methods>; }
-keep class org.ituns.network.core.NetworkResponse { public <methods>; }
-keep class org.ituns.network.core.NetworkResponse$Builder { public <methods>; }


-keep class org.ituns.network.core.R{ *; }
-keep class org.ituns.network.core.R$* { public static <fields>; }