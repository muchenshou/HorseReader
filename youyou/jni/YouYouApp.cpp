#include <jni.h>
#include "cr3java.h"
#include "com_reader_app_YouYouApplication.h"
/*
 * Class:     com_reader_app_YouYouApplication
 * Method:    initInternal
 * Signature: ([Ljava/lang/String;)Z
 */JNIEXPORT jboolean JNICALL Java_com_reader_app_YouYouApplication_initInternal(
		JNIEnv * penv, jclass obj, jobjectArray fontArray) {

	CRJNIEnv env(penv);

	// to catch crashes and remove current cache file on crash (SIGSEGV etc.)
	crSetSignalHandler();

	LOGI("initInternal called");
	// set fatal error handler
	LOGD("Redirecting CDRLog to Android");
	CRLog::setLogger(new JNICDRLogger());
	CRLog::setLogLevel(CRLog::LL_TRACE);
	CRLog::info("CREngine log redirected");

	CRLog::info("initializing hyphenation manager");
	HyphMan::initDictionaries(lString16::empty_str); //don't look for dictionaries
	HyphMan::activateDictionary(lString16(HYPH_DICT_ID_NONE));
	CRLog::info("creating font manager");
	InitFontManager(lString8::empty_str);
	CRLog::debug("converting fonts array: %d items",
			(int) env->GetArrayLength(fontArray));
	lString16Collection fonts;
	env.fromJavaStringArray(fontArray, fonts);
	int len = fonts.length();
	CRLog::debug("registering fonts: %d fonts in list", len);
	for (int i = 0; i < len; i++) {
		lString8 fontName = UnicodeToUtf8(fonts[i]);
//		CRLog::debug("registering font %s", fontName.c_str());
		if (!fontMan->RegisterFont(fontName))
//			CRLog::error("cannot load font %s", fontName.c_str());
			;
	}

	CRLog::info("%d fonts registered", fontMan->GetFontCount());
	return fontMan->GetFontCount() ? JNI_TRUE : JNI_FALSE;

}
#define NO_BATTERY_GAUGE 1

static void replaceColor(char * str, lUInt32 color) {
	// in line like "0 c #80000000",
	// replace value of color
	for (int i = 0; i < 8; i++) {
		str[i + 5] = toHexDigit((color >> 28) & 0xF);
		color <<= 4;
	}
}
static LVRefVec<LVImageSource> getBatteryIcons(lUInt32 color) {
	CRLog::debug("Making list of Battery icon bitmats");

//	#include "battery_icons.h"
	lUInt32 cl1 = 0x00000000 | (color & 0xFFFFFF);
	lUInt32 cl2 = 0x40000000 | (color & 0xFFFFFF);
	lUInt32 cl3 = 0x80000000 | (color & 0xFFFFFF);
	lUInt32 cl4 = 0xF0000000 | (color & 0xFFFFFF);

	static char color1[] = "0 c #80000000";
	static char color2[] = "X c #80000000";
	static char color3[] = "o c #80AAAAAA";
	static char color4[] = ". c #80FFFFFF";
#define BATTERY_HEADER \
			"28 15 5 1", \
			color1, \
			color2, \
			color3, \
			color4, \
			"  c None",

	static const char * battery8[] = {
			BATTERY_HEADER "   .........................",
			"   .00000000000000000000000.",
			"   .0.....................0.",
			"....0.XXXX.XXXX.XXXX.XXXX.0.",
			".0000.XXXX.XXXX.XXXX.XXXX.0.",
			".0..0.XXXX.XXXX.XXXX.XXXX.0.",
			".0..0.XXXX.XXXX.XXXX.XXXX.0.",
			".0..0.XXXX.XXXX.XXXX.XXXX.0.",
			".0..0.XXXX.XXXX.XXXX.XXXX.0.",
			".0..0.XXXX.XXXX.XXXX.XXXX.0.",
			".0000.XXXX.XXXX.XXXX.XXXX.0.",
			"....0.XXXX.XXXX.XXXX.XXXX.0.",
			"   .0.....................0.",
			"   .00000000000000000000000.",
			"   .........................",
		};
	static const char * battery7[] = {
			BATTERY_HEADER "   .........................",
			"   .00000000000000000000000.",
			"   .0.....................0.",
			"....0.oooo.XXXX.XXXX.XXXX.0.",
			".0000.oooo.XXXX.XXXX.XXXX.0.",
			".0..0.oooo.XXXX.XXXX.XXXX.0.",
			".0..0.oooo.XXXX.XXXX.XXXX.0.",
			".0..0.oooo.XXXX.XXXX.XXXX.0.",
			".0..0.oooo.XXXX.XXXX.XXXX.0.",
			".0..0.oooo.XXXX.XXXX.XXXX.0.",
			".0000.oooo.XXXX.XXXX.XXXX.0.",
			"....0.oooo.XXXX.XXXX.XXXX.0.",
			"   .0.....................0.",
			"   .00000000000000000000000.",
			"   .........................",
		};
	static const char * battery6[] = {
			BATTERY_HEADER "   .........................",
			"   .00000000000000000000000.",
			"   .0.....................0.",
			"....0......XXXX.XXXX.XXXX.0.",
			".0000......XXXX.XXXX.XXXX.0.",
			".0..0......XXXX.XXXX.XXXX.0.",
			".0..0......XXXX.XXXX.XXXX.0.",
			".0..0......XXXX.XXXX.XXXX.0.",
			".0..0......XXXX.XXXX.XXXX.0.",
			".0..0......XXXX.XXXX.XXXX.0.",
			".0000......XXXX.XXXX.XXXX.0.",
			"....0......XXXX.XXXX.XXXX.0.",
			"   .0.....................0.",
			"   .00000000000000000000000.",
			"   .........................",
		};
	static const char * battery5[] = {
			BATTERY_HEADER "   .........................",
			"   .00000000000000000000000.",
			"   .0.....................0.",
			"....0......oooo.XXXX.XXXX.0.",
			".0000......oooo.XXXX.XXXX.0.",
			".0..0......oooo.XXXX.XXXX.0.",
			".0..0......oooo.XXXX.XXXX.0.",
			".0..0......oooo.XXXX.XXXX.0.",
			".0..0......oooo.XXXX.XXXX.0.",
			".0..0......oooo.XXXX.XXXX.0.",
			".0000......oooo.XXXX.XXXX.0.",
			"....0......oooo.XXXX.XXXX.0.",
			"   .0.....................0.",
			"   .00000000000000000000000.",
			"   .........................",
		};
	static const char * battery4[] = {
			BATTERY_HEADER "   .........................",
			"   .00000000000000000000000.",
			"   .0.....................0.",
			"....0...........XXXX.XXXX.0.",
			".0000...........XXXX.XXXX.0.",
			".0..0...........XXXX.XXXX.0.",
			".0..0...........XXXX.XXXX.0.",
			".0..0...........XXXX.XXXX.0.",
			".0..0...........XXXX.XXXX.0.",
			".0..0...........XXXX.XXXX.0.",
			".0000...........XXXX.XXXX.0.",
			"....0...........XXXX.XXXX.0.",
			"   .0.....................0.",
			"   .00000000000000000000000.",
			"   .........................",
		};
	static const char * battery3[] = {
			BATTERY_HEADER "   .........................",
			"   .00000000000000000000000.",
			"   .0.....................0.",
			"....0...........oooo.XXXX.0.",
			".0000...........oooo.XXXX.0.",
			".0..0...........oooo.XXXX.0.",
			".0..0...........oooo.XXXX.0.",
			".0..0...........oooo.XXXX.0.",
			".0..0...........oooo.XXXX.0.",
			".0..0...........oooo.XXXX.0.",
			".0000...........oooo.XXXX.0.",
			"....0...........oooo.XXXX.0.",
			"   .0.....................0.",
			"   .00000000000000000000000.",
			"   .........................",
		};
	static const char * battery2[] = {
			BATTERY_HEADER "   .........................",
			"   .00000000000000000000000.",
			"   .0.....................0.",
			"....0................XXXX.0.",
			".0000................XXXX.0.",
			".0..0................XXXX.0.",
			".0..0................XXXX.0.",
			".0..0................XXXX.0.",
			".0..0................XXXX.0.",
			".0..0................XXXX.0.",
			".0000................XXXX.0.",
			"....0................XXXX.0.",
			"   .0.....................0.",
			"   .00000000000000000000000.",
			"   .........................",
		};
	static const char * battery1[] = {
			BATTERY_HEADER "   .........................",
			"   .00000000000000000000000.",
			"   .0.....................0.",
			"   .0................oooo.0.",
			".0000................oooo.0.",
			".0..0................oooo.0.",
			".0..0................oooo.0.",
			".0..0................oooo.0.",
			".0..0................oooo.0.",
			".0..0................oooo.0.",
			".0000................oooo.0.",
			"   .0................oooo.0.",
			"   .0.....................0.",
			"   .00000000000000000000000.",
			"   .........................",
		};
	static const char * battery0[] = {
			BATTERY_HEADER "   .........................",
			"   .00000000000000000000000.",
			"   .0.....................0.",
			"   .0.....................0.",
			".0000.....................0.",
			".0..0.....................0.",
			".0..0.....................0.",
			".0..0.....................0.",
			".0..0.....................0.",
			".0..0.....................0.",
			".0000.....................0.",
			"....0.....................0.",
			"   .0.....................0.",
			"   .00000000000000000000000.",
			"   .........................",
		};
//#endif

	static const char * battery_charge[] = {
			BATTERY_HEADER "   .........................",
			"   .00000000000000000000000.",
			"   .0.....................0.",
			"....0.....................0.",
			".0000............XX.......0.",
			".0..0...........XXXX......0.",
			".0..0..XX......XXXXXX.....0.",
			".0..0...XXX...XXXX..XX....0.",
			".0..0....XXX..XXXX...XX...0.",
			".0..0.....XXXXXXX.....XX..0.",
			".0000.......XXXX..........0.",
			"....0........XX...........0.",
			"   .0.....................0.",
			"   .00000000000000000000000.",
			"   .........................",
		};
	static const char * battery_frame[] = {
			BATTERY_HEADER "   .........................",
			"   .00000000000000000000000.",
			"   .0.....................0.",
			"....0.....................0.",
			".0000.....................0.",
			".0..0.....................0.",
			".0..0.....................0.",
			".0..0.....................0.",
			".0..0.....................0.",
			".0..0.....................0.",
			".0000.....................0.",
			"....0.....................0.",
			"   .0.....................0.",
			"   .00000000000000000000000.",
			"   .........................",
		};

	const char * * icon_bpm[] = { battery_charge, battery0, battery1, battery2,
			battery3, battery4, battery5, battery6, battery7, battery8,
			battery_frame, NULL };

	replaceColor(color1, cl1);
	replaceColor(color2, cl2);
	replaceColor(color3, cl3);
	replaceColor(color4, cl4);

	LVRefVec<LVImageSource> icons;
	for (int i = 0; icon_bpm[i]; i++)
		icons.add(LVCreateXPMImageSource(icon_bpm[i]));

	return icons;
}
#include "com_reader_util_BitmapUtil.h"
int getInfo(JNIEnv* env, jobject jbitmap, AndroidBitmapInfo* info) {
	    //CRLog::trace("JNIGraphicsReplacement::getInfo entered");
		jclass cls = env->GetObjectClass(jbitmap);
		jmethodID mid;
		mid = env->GetMethodID(cls,	"getHeight", "()I");
		info->height = env->CallIntMethod(jbitmap, mid);
		//CRLog::debug("Bitmap height: %d", info->height);
		mid = env->GetMethodID(cls,	"getWidth", "()I");
		info->width = env->CallIntMethod(jbitmap, mid);
		//CRLog::debug("Bitmap width: %d", info->width);
		mid = env->GetMethodID(cls,	"getRowBytes", "()I");
		info->stride = env->CallIntMethod(jbitmap, mid);
		//CRLog::debug("Bitmap stride: %d", info->stride);
		mid = env->GetMethodID(cls,	"getConfig", "()Landroid/graphics/Bitmap$Config;");
		jobject configObj = env->CallObjectMethod(jbitmap, mid);
		jclass configCls = env->GetObjectClass(configObj);
		mid = env->GetMethodID(configCls, "ordinal", "()I");
		int ord = env->CallIntMethod(configObj, mid);
		switch ( ord ) {
		case 1:
			info->format = ANDROID_BITMAP_FORMAT_A_8;
			break;
		case 2:
			info->format = ANDROID_BITMAP_FORMAT_RGBA_4444;
			break;
		case 3:
			info->format = ANDROID_BITMAP_FORMAT_RGBA_8888;
			break;
		case 4:
		case 8:
			info->format = ANDROID_BITMAP_FORMAT_RGB_565;
			break;
		default:
			info->format = ANDROID_BITMAP_FORMAT_NONE;
			break;
		}
		jfieldID fid;
		fid = env->GetFieldID(configCls, "nativeInt", "I");
		//info->format
		int ni = env->GetIntField(configObj, fid);
		//CRLog::debug("Bitmap format: %d (ord=%d, nativeInt=%d)", info->format, ord, ni);
		return ANDROID_BITMAP_RESUT_SUCCESS;
    }
/*
 * Class:     com_reader_util_BitmapUtil
 * Method:    DrawBatteryBitmap
 * Signature: (Landroid/graphics/Bitmap;II)I
 */JNIEXPORT jint JNICALL Java_com_reader_util_BitmapUtil_DrawBatteryBitmap(
		JNIEnv *e, jclass cls, jobject bitmap, jint which, jint color) {
	SET_ENV(e);
	LVDrawBuf * drawbuf = BitmapAccessorInterface::getInstance()->lock(e,
			bitmap);
	AndroidBitmapInfo info;
	getInfo(e,bitmap,&info);
	if (drawbuf != NULL) {
		LVRefVec<LVImageSource> batteryicon = getBatteryIcons(0xCCFF0000);

		drawbuf->FillRect(0, 0, info.width, info.height, 0xFF000000);
		drawbuf->Draw(batteryicon[9], 0, 0, info.width, info.height);
		drawbuf->SetTextColor(0x00000000);
		//		txt_book->drawPage(drawbuf, index);
		//CRLog::trace("getPageImageInternal calling bitmap->unlock");
		BitmapAccessorInterface::getInstance()->unlock(e, bitmap, drawbuf);
	} else {
		CRLog::error("bitmap accessor is invalid");
	}
	return 0;
}
