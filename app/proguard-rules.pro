-optimizationpasses 5
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-keepattributes Signature
-keepattributes *Annotation*,InnerClasses
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-keepattributes *Annotation*,InnerClasses,Signature,EnclosingMethod

-keep public class * extends android.app.Application
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Service
-keep class kotlinx.coroutines.android.** {*;}
-keep class **.R$* {*;}
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#保持bean不被混淆
-keep class com.jintao.vipmanager.bean.** { *; }
-keep class com.jintao.vipmanager.database.bean.** { *; }
-keep class com.jintao.vipmanager.network.HttpData { *; }
#保持Android与JavaScript进行交互的类不被混淆
-keep class **.AndroidJavaScript { *; }
-keepclassmembers class * extends android.webkit.WebViewClient {
     public void *(android.webkit.WebView,java.lang.String,android.graphics.Bitmap);
     public boolean *(android.webkit.WebView,java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebChromeClient {
     public void *(android.webkit.WebView,java.lang.String);
}
-keepattributes JavascriptInterface

#保持本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}


#保持okhttp不被混淆
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *; }
-dontwarn okio.**
#保持gson不被混淆
-keep class com.google.gson.** { *; }
-keep class com.tencent.mmkv.** { *; }

-keep class com.hjq.gson.http.**{*;}
-keep class com.hjq.gson.factory.**{*;}
-keep class com.hjq.toast.**{*;}
-keep class com.hjq.shape.**{*;}

#阿里云
-keep class com.alibaba.sdk.android.oss.** { *; }

# 图片压缩
-keep class top.zibin.luban.** { *; }
# UI库
-dontwarn com.lxj.xpopup.widget.**
-keep class com.lxj.xpopup.widget.**{*;}
-keep class com.lxj.xpopupext.bean.** {}

-keep class com.gyf.immersionbar.** { *; }
-keep class me.jingbin.library.** { *; }

#GreenDAO数据库
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties { *; }

# If you DO use SQLCipher:
-keep class org.greenrobot.greendao.database.SqlCipherEncryptedHelper { *; }

-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}




