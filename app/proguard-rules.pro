# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }
-keep,includedescriptorclasses class com.funpixelnest.slots.**$$serializer { *; }
-keepclassmembers class com.funpixelnest.slots.** { *** Companion; }
-keepclasseswithmembers class com.funpixelnest.slots.** { kotlinx.serialization.KSerializer serializer(...); }
