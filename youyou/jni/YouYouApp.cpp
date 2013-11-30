#include <jni.h>
#include "cr3java.h"
#include "com_reader_app_YouYouApplication.h"
/*
 * Class:     com_reader_app_YouYouApplication
 * Method:    initInternal
 * Signature: ([Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_reader_app_YouYouApplication_initInternal
(JNIEnv * penv, jclass obj, jobjectArray fontArray) {

	CRJNIEnv env(penv);

	// to catch crashes and remove current cache file on crash (SIGSEGV etc.)
	crSetSignalHandler();

	LOGI("initInternal called");
	// set fatal error handler
	LOGD("Redirecting CDRLog to Android");
	CRLog::setLogger( new JNICDRLogger() );
	CRLog::setLogLevel( CRLog::LL_TRACE );
	CRLog::info("CREngine log redirected");

	CRLog::info("initializing hyphenation manager");
    HyphMan::initDictionaries(lString16::empty_str); //don't look for dictionaries
	HyphMan::activateDictionary(lString16(HYPH_DICT_ID_NONE));
	CRLog::info("creating font manager");
    InitFontManager(lString8::empty_str);
	CRLog::debug("converting fonts array: %d items", (int)env->GetArrayLength(fontArray));
	lString16Collection fonts;
	env.fromJavaStringArray(fontArray, fonts);
	int len = fonts.length();
	CRLog::debug("registering fonts: %d fonts in list", len);
	for ( int i=0; i<len; i++ ) {
		lString8 fontName = UnicodeToUtf8(fonts[i]);
		CRLog::debug("registering font %s", fontName.c_str());
		if ( !fontMan->RegisterFont( fontName ) )
			CRLog::error("cannot load font %s", fontName.c_str());
	}

    CRLog::info("%d fonts registered", fontMan->GetFontCount());
	return fontMan->GetFontCount() ? JNI_TRUE : JNI_FALSE;

}
