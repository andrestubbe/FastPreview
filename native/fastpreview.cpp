#include <jni.h>
#include <windows.h>
#include <fpdfview.h>
#include <fpdf_formfill.h>
#include <string.h>

static bool g_pdfiumInitialized = false;

// "Fast & Beautiful" render flags:
// FPDF_ANNOT:    Render highlight, markup, comment, stamp annotations
// FPDF_LCD_TEXT: ClearType subpixel text antialiasing
// FPDF_PRINTING: Enable transparency groups, blend modes, soft masks, color overlays
static const int RENDER_FLAGS = FPDF_ANNOT | FPDF_LCD_TEXT | FPDF_PRINTING;

// Persistent minimal form fill info struct.
// PDFium requires this to remain valid while any FPDF_FORMHANDLE created from it is alive.
static FPDF_FORMFILLINFO g_formFillInfo;
static bool g_formFillInfoReady = false;

static void ensure_pdfium_init() {
    if (!g_pdfiumInitialized) {
        FPDF_InitLibrary();
        g_pdfiumInitialized = true;
    }
    if (!g_formFillInfoReady) {
        memset(&g_formFillInfo, 0, sizeof(g_formFillInfo));
        g_formFillInfo.version = 1;
        g_formFillInfoReady = true;
    }
}

// Create a form fill handle for read-only rendering of form fields and widgets.
// The caller MUST call FPDFDOC_ExitFormFillEnvironment() when done.
static FPDF_FORMHANDLE create_form_handle(FPDF_DOCUMENT doc) {
    FPDF_FORMHANDLE handle = FPDFDOC_InitFormFillEnvironment(doc, &g_formFillInfo);
    if (handle) {
        // Highlight form fields with a subtle yellow overlay
        FPDF_SetFormFieldHighlightColor(handle, 0, 0xFFEEDD00);
        FPDF_SetFormFieldHighlightAlpha(handle, 100);
    }
    return handle;
}

// Render form fields and widget annotations on top of the already-rendered page content.
// This handles: text inputs, checkboxes, radio buttons, signatures, popup annotations.
static void render_form_layer(FPDF_FORMHANDLE formHandle, FPDF_BITMAP bitmap,
                              FPDF_PAGE page, int startX, int startY,
                              int sizeX, int sizeY) {
    if (!formHandle) return;
    FORM_OnAfterLoadPage(page, formHandle);
    FPDF_FFLDraw(formHandle, bitmap, page, startX, startY, sizeX, sizeY, 0, RENDER_FLAGS);
    FORM_OnBeforeClosePage(page, formHandle);
}

extern "C" {
    JNIEXPORT jlong JNICALL Java_fastpreview_api_FastPreview_getPageSizeNative(
        JNIEnv* env, 
        jclass clazz, 
        jstring filePath, 
        jint pageIndex, 
        jint dpi
    ) {
        ensure_pdfium_init();

        const char* pathStr = env->GetStringUTFChars(filePath, NULL);
        if (!pathStr) return 0;

        FPDF_DOCUMENT doc = FPDF_LoadDocument(pathStr, NULL);
        env->ReleaseStringUTFChars(filePath, pathStr);

        if (!doc) return 0;

        FPDF_PAGE page = FPDF_LoadPage(doc, pageIndex);
        if (!page) {
            FPDF_CloseDocument(doc);
            return 0;
        }

        double pageWidth = FPDF_GetPageWidth(page);
        double pageHeight = FPDF_GetPageHeight(page);

        FPDF_ClosePage(page);
        FPDF_CloseDocument(doc);

        // Calculate dimensions in pixels based on DPI (1 point = 1/72 inch)
        int w = (int)(pageWidth * dpi / 72.0);
        int h = (int)(pageHeight * dpi / 72.0);

        return ((jlong)w << 32) | (h & 0xFFFFFFFFL);
    }

    JNIEXPORT jboolean JNICALL Java_fastpreview_api_FastPreview_renderPDFNative(
        JNIEnv* env, 
        jclass clazz, 
        jstring filePath, 
        jint pageIndex, 
        jint width, 
        jint height, 
        jobject bufferObj
    ) {
        ensure_pdfium_init();

        unsigned char* buffer = (unsigned char*)env->GetDirectBufferAddress(bufferObj);
        if (!buffer) return JNI_FALSE;

        const char* pathStr = env->GetStringUTFChars(filePath, NULL);
        if (!pathStr) return JNI_FALSE;

        FPDF_DOCUMENT doc = FPDF_LoadDocument(pathStr, NULL);
        env->ReleaseStringUTFChars(filePath, pathStr);
        if (!doc) return JNI_FALSE;

        FPDF_PAGE page = FPDF_LoadPage(doc, pageIndex);
        if (!page) {
            FPDF_CloseDocument(doc);
            return JNI_FALSE;
        }

        // Zero-copy bitmap wrapping the Java DirectByteBuffer
        FPDF_BITMAP bitmap = FPDFBitmap_CreateEx(width, height, FPDFBitmap_BGRA, buffer, width * 4);
        if (!bitmap) {
            FPDF_ClosePage(page);
            FPDF_CloseDocument(doc);
            return JNI_FALSE;
        }

        // White background
        FPDFBitmap_FillRect(bitmap, 0, 0, width, height, 0xFFFFFFFF);

        // 1. Render page content (text, images, vector graphics, annotations, transparency)
        FPDF_RenderPageBitmap(bitmap, page, 0, 0, width, height, 0, RENDER_FLAGS);

        // 2. Render form fields + widget annotations on top
        FPDF_FORMHANDLE formHandle = create_form_handle(doc);
        render_form_layer(formHandle, bitmap, page, 0, 0, width, height);
        if (formHandle) FPDFDOC_ExitFormFillEnvironment(formHandle);

        FPDFBitmap_Destroy(bitmap);
        FPDF_ClosePage(page);
        FPDF_CloseDocument(doc);

        return JNI_TRUE;
    }

    JNIEXPORT jboolean JNICALL Java_fastpreview_api_FastPreview_renderPDFNativeEx(
        JNIEnv* env, 
        jclass clazz, 
        jstring filePath, 
        jint pageIndex, 
        jint viewWidth, 
        jint viewHeight, 
        jdouble offsetX, 
        jdouble offsetY, 
        jint pageWidth, 
        jint pageHeight, 
        jobject bufferObj
    ) {
        ensure_pdfium_init();

        unsigned char* buffer = (unsigned char*)env->GetDirectBufferAddress(bufferObj);
        if (!buffer) return JNI_FALSE;

        const char* pathStr = env->GetStringUTFChars(filePath, NULL);
        if (!pathStr) return JNI_FALSE;

        FPDF_DOCUMENT doc = FPDF_LoadDocument(pathStr, NULL);
        env->ReleaseStringUTFChars(filePath, pathStr);
        if (!doc) return JNI_FALSE;

        FPDF_PAGE page = FPDF_LoadPage(doc, pageIndex);
        if (!page) {
            FPDF_CloseDocument(doc);
            return JNI_FALSE;
        }

        // Viewport-sized bitmap wrapping the Java DirectByteBuffer
        FPDF_BITMAP bitmap = FPDFBitmap_CreateEx(viewWidth, viewHeight, FPDFBitmap_BGRA, buffer, viewWidth * 4);
        if (!bitmap) {
            FPDF_ClosePage(page);
            FPDF_CloseDocument(doc);
            return JNI_FALSE;
        }

        // Transparent viewport background (areas outside the page)
        FPDFBitmap_FillRect(bitmap, 0, 0, viewWidth, viewHeight, 0x00000000);

        // White background only within the page bounds
        FPDFBitmap_FillRect(bitmap, (int)offsetX, (int)offsetY, pageWidth, pageHeight, 0xFFFFFFFF);

        // 1. Render page content with viewport clipping (PDFium auto-culls off-screen elements)
        FPDF_RenderPageBitmap(bitmap, page, (int)offsetX, (int)offsetY, pageWidth, pageHeight, 0, RENDER_FLAGS);

        // 2. Render form fields + widget annotations on top
        FPDF_FORMHANDLE formHandle = create_form_handle(doc);
        render_form_layer(formHandle, bitmap, page, (int)offsetX, (int)offsetY, pageWidth, pageHeight);
        if (formHandle) FPDFDOC_ExitFormFillEnvironment(formHandle);

        FPDFBitmap_Destroy(bitmap);
        FPDF_ClosePage(page);
        FPDF_CloseDocument(doc);

        return JNI_TRUE;
    }
}
