#include <jni.h>
#include <windows.h>

// Placeholder for native rendering logic
// To be implemented: PDFium integration, WebView2 integration, etc.

extern "C" {
    JNIEXPORT jboolean JNICALL Java_fastpreview_api_FastPreview_initNative(JNIEnv* env, jobject obj) {
        return JNI_TRUE;
    }
}
