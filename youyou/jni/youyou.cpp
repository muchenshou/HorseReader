#include <jni.h>
#include <com_reader_document_txt_TxtDocument.h>
#include "lvstring.h"
#include "lvstream.h"
#include "cr3java.h"
#include "crtxtenc.h"

class JNICDRLogger : public CRLog
{
public:
    JNICDRLogger()
    {
    	curr_level = CRLog::LL_DEBUG;
    }
protected:

	virtual void log( const char * lvl, const char * msg, va_list args)
	{
	    #define MAX_LOG_MSG_SIZE 1024
		static char buffer[MAX_LOG_MSG_SIZE+1];
		vsnprintf(buffer, MAX_LOG_MSG_SIZE, msg, args);
		int level = ANDROID_LOG_DEBUG;
		//LOGD("CRLog::log is called with LEVEL %s, pattern %s", lvl, msg);
		if ( !strcmp(lvl, "FATAL") )
			level = ANDROID_LOG_FATAL;
		else if ( !strcmp(lvl, "ERROR") )
			level = ANDROID_LOG_ERROR;
		else if ( !strcmp(lvl, "WARN") )
			level = ANDROID_LOG_WARN;
		else if ( !strcmp(lvl, "INFO") )
			level = ANDROID_LOG_INFO;
		else if ( !strcmp(lvl, "DEBUG") )
			level = ANDROID_LOG_DEBUG;
		else if ( !strcmp(lvl, "TRACE") )
			level = ANDROID_LOG_VERBOSE;
		__android_log_write(level, LOG_TAG, buffer);
	}
};

class TxtNode {
	long start;
	long end;
};

class TxtPage {
	TxtNode *startNode;
	int startNodeOffset;
	TxtNode *endNode;
	int endNodeOffset;
};

class TxtBook: public LVFileParserBase{
	LVPtrVector<TxtNode> mNodes;
	LVPtrVector<TxtPage> mPages;
	LVStreamRef _bookpath;
	char_encoding_type m_enc_type;
	lChar16 * m_conv_table; // charset conversion table for 8-bit encodings
public:
	TxtBook(LVStreamRef& path):LVFileParserBase(path){
	}
	void checkEof()
	{
	    if ( m_buf_fpos+m_buf_len >= this->m_stream_size-4 )
	        m_buf_pos = m_buf_len = m_stream_size - m_buf_fpos; //force eof
	        //m_buf_pos = m_buf_len = m_stream_size - (m_buf_fpos+m_buf_len);
	}
	int ReadChar(lChar16& ch) {
		const int maxsize = 1;
		lChar16 * buf = &ch;
	    if (m_buf_pos >= m_buf_len)
	        return 0;
	    int count = 0;
	    switch ( m_enc_type ) {
	    case ce_8bit_cp:
	    case ce_utf8:
	        if ( m_conv_table!=NULL ) {
	            for ( ; count<maxsize && m_buf_pos<m_buf_len; count++ ) {
	                lUInt16 ch = m_buf[m_buf_pos++];
	                buf[count] = ( (ch & 0x80) == 0 ) ? ch : m_conv_table[ch&0x7F];
	            }
	            return count;
	        } else  {
	            int srclen = m_buf_len - m_buf_pos;
	            int dstlen = maxsize;
	            Utf8ToUnicode(m_buf + m_buf_pos, srclen, buf, dstlen);
	            m_buf_pos += srclen;
	            if (dstlen == 0) {
	                checkEof();
	            }
	            return dstlen;
	        }
	    case ce_utf16_be:
	        {
	            for ( ; count<maxsize; count++ ) {
	                if ( m_buf_pos+1>=m_buf_len ) {
	                    checkEof();
	                    return count;
	                }
	                lUInt16 ch = m_buf[m_buf_pos++];
	                lUInt16 ch2 = m_buf[m_buf_pos++];
	                buf[count] = (ch << 8) | ch2;
	            }
	            return count;
	        }

	#if GBK_ENCODING_SUPPORT == 1
	    case ce_gbk:
	    {
	        // based on ICONV code, gbk.h
	        for ( ; count<maxsize; count++ ) {
	            if (m_buf_pos >= m_buf_len) {
	                checkEof();
	                return count;
	            }
	            lUInt16 ch = m_buf[m_buf_pos++];
	            int twoBytes = ch >= 0x81 && ch < 0xFF ? 1 : 0;
	            if ( m_buf_pos + twoBytes>=m_buf_len ) {
	                checkEof();
	                return count;
	            }
	            lUInt16 ch2 = 0;
	            if (twoBytes)
	                ch2 = m_buf[m_buf_pos++];
	            lUInt16 res = twoBytes ? 0 : ch;
	            if (res == 0 && ch >= 0xa1 && ch <= 0xf7) {
	                if (ch == 0xa1) {
	                    if (ch2 == 0xa4) {
	                        res = 0x00b7;
	                    }
	                    if (ch2 == 0xaa) {
	                        res = 0x2014;
	                    }
	                }
	                if (ch2 >= 0xa1 && ch2 < 0xff) {
	                    unsigned char buf[2];
	                    buf[0] = (lUInt8)(ch - 0x80);
						buf[1] = (lUInt8)(ch2 - 0x80);
	                    res = cr3_gb2312_mbtowc(buf);
	                    if (!res)
	                        res = cr3_cp936ext_mbtowc(buf);
	                }
	            }
	            if (res == 0 && ch >= 0x81 && ch <= 0xa0)
	                res = cr3_gbkext1_mbtowc(ch, ch2);
	            if (res == 0 && ch >= 0xa8 && ch <= 0xfe)
	                res = cr3_gbkext2_mbtowc(ch, ch2);
	            if (res == 0 && ch == 0xa2) {
	                if (ch2 >= 0xa1 && ch2 <= 0xaa) {
	                    res = 0x2170 + (ch2 - 0xa1);
	                }
	            }
	            if (res == 0)
	                res = '?'; // replace invalid chars with ?
	            buf[count] = res;
	        }
	        return count;
	    }
	#endif
	#if JIS_ENCODING_SUPPORT == 1
	    case ce_shift_jis:
	    {
	        // based on ICONV code, gbk.h
	        for ( ; count < maxsize - 1; count++ ) {
	            if (m_buf_pos >= m_buf_len) {
	                checkEof();
	                return count;
	            }
	            lUInt16 ch = m_buf[m_buf_pos++];
	            lUInt16 res = 0;
	            if (ch < 0x80) {
	                /* Plain ISO646-JP character. */
	                if (ch == 0x5c)
	                    res = 0x00a5;
	                else if (ch == 0x7e)
	                    res = 0x203e;
	                else
	                    res = ch;
	            } else if (ch >= 0xa1 && ch <= 0xdf) {
	                res = ch + 0xfec0;
	            } else {
	                if ((ch >= 0x81 && ch <= 0x9f) || (ch >= 0xe0 && ch <= 0xfc)) {
	                    /* Two byte character. */
	                    if (m_buf_pos + 1 >= m_buf_len) {
	                        checkEof();
	                        return count;
	                    }
	                    lUInt16 ch2 = 0;
	                    ch2 = m_buf[m_buf_pos++];
	                    if ((ch2 >= 0x40 && ch2 <= 0x7e) || (ch2 >= 0x80 && ch2 <= 0xfc)) {
	                        lChar16 ch1;
	                        /* Convert to row and column. */
	                        if (ch < 0xe0)
	                            ch -= 0x81;
	                        else
	                            ch -= 0xc1;
	                        if (ch2 < 0x80)
	                            ch2 -= 0x40;
	                        else
	                            ch2 -= 0x41;
	                        /* Now 0 <= ch <= 0x3b, 0 <= ch2 <= 0xbb. */
	                        ch1 = 2 * ch;
	                        if (ch2 >= 0x5e)
	                            ch2 -= 0x5e, ch1++;
	                        ch2 += 0x21;
	                        if (ch1 >= 0x5e) {
	                            /* Handling of JISX 0213 plane 2 rows. */
	                            if (ch1 >= 0x67)
	                                ch1 += 230;
	                            else if (ch1 >= 0x63 || ch1 == 0x5f)
	                                ch1 += 168;
	                            else
	                                ch1 += 162;
	                        }
	                        lChar16 wc = cr3_jisx0213_to_ucs4(0x121+ch1, ch2);
	                        if (wc) {
	                            if (wc < 0x80) {
	                                /* It's a combining character. */
	                                lChar16 wc1 = jisx0213_to_ucs_combining[wc - 1][0];
	                                lChar16 wc2 = jisx0213_to_ucs_combining[wc - 1][1];
	                                buf[count++] = wc1;
	                                res = wc2;
	                            } else
	                                res = wc;
	                        }
	                    }
	                }
	            }


	            if (res == 0)
	                res = '?'; // replace invalid chars with ?
	            buf[count] = res;
	        }
	        return count;
	    }
	    case ce_euc_jis:
	    {
	        // based on ICONV code, gbk.h
	        for ( ; count < maxsize-1; count++ ) {
	            lUInt16 ch = m_buf[m_buf_pos++];
	            lUInt16 res = 0;
	            if (ch < 0x80) {
	                /* Plain ASCII character. */
	                res = ch;
	            } else {
	                if ((ch >= 0xa1 && ch <= 0xfe) || ch == 0x8e || ch == 0x8f) {
	                    /* Two byte character. */
	                    if (m_buf_pos + 1 >= m_buf_len) {
	                        checkEof();
	                        return count;
	                    }
	                    lUInt16 ch2 = m_buf[m_buf_pos++];
	                    if (ch2 >= 0xa1 && ch2 <= 0xfe && ch == 0x8f && m_buf_pos + 2 >= m_buf_len) {
	                        checkEof();
	                        return count;
	                    }

	                    if (ch2 >= 0xa1 && ch2 <= 0xfe) {
	                        if (ch == 0x8e) {
	                            /* Half-width katakana. */
	                            if (ch2 <= 0xdf) {
	                              res = ch2 + 0xfec0;
	                            }
	                        } else {
	                            lChar16 wc;
	                            if (ch == 0x8f) {
	                                /* JISX 0213 plane 2. */
	                                lUInt16 ch3 = m_buf[m_buf_pos++];
	                                wc = cr3_jisx0213_to_ucs4(0x200-0x80+ch2,ch3^0x80);
	                            } else {
	                                /* JISX 0213 plane 1. */
	                                wc = cr3_jisx0213_to_ucs4(0x100-0x80+ch,ch2^0x80);
	                            }
	                            if (wc) {
	                                if (wc < 0x80) {
	                                    /* It's a combining character. */
	                                    ucs4_t wc1 = jisx0213_to_ucs_combining[wc - 1][0];
	                                    ucs4_t wc2 = jisx0213_to_ucs_combining[wc - 1][1];
	                                    /* We cannot output two Unicode characters at once. So,
	                                       output the first character and buffer the second one. */
	                                    buf[count++] = (lChar16)wc1;
	                                    res = (lChar16)wc2;
	                                } else
	                                    res = (lChar16)wc;
	                            }
	                        }
	                    }
	                }
	            }

	            if (res == 0)
	                res = '?'; // replace invalid chars with ?
	            buf[count] = res;
	        }
	        return count;
	    }
	#endif
	#if BIG5_ENCODING_SUPPORT == 1
	    case ce_big5:
	    {
	        // based on ICONV code, gbk.h
	        for ( ; count < maxsize - 1; count++ ) {
	            if (m_buf_pos >= m_buf_len) {
	                checkEof();
	                return count;
	            }
	            lUInt16 ch = m_buf[m_buf_pos++];
	            lUInt16 res = 0;
	            /* Code set 0 (ASCII) */
	            if (ch < 0x80) {
	                res = ch;
	            } else if (ch >= 0x81 && ch < 0xff) {
	                /* Code set 1 (BIG5 extended) */
	                {
	                    if (m_buf_pos + 1 >= m_buf_len) {
	                        checkEof();
	                        return count;
	                    }
	                    lUInt16 ch2 = m_buf[m_buf_pos++];
	                    if ((ch2 >= 0x40 && ch2 < 0x7f) || (ch2 >= 0xa1 && ch2 < 0xff)) {
	                        if (ch >= 0xa1) {
	                            if (ch < 0xa3) {
	                                unsigned int i = 157 * (ch - 0xa1) + (ch2 - (ch2 >= 0xa1 ? 0x62 : 0x40));
	                                lChar16 wc = big5_2003_2uni_pagea1[i];
	                                if (wc != 0xfffd) {
	                                    res = wc;
	                                }
	                            }
	                            if (!((ch == 0xc6 && ch2 >= 0xa1) || ch == 0xc7)) {
	                                if (!(ch == 0xc2 && ch2 == 0x55)) {
	                                    res = cr3_big5_mbtowc(ch, ch2);
	                                    if (!res) {
	                                        if (ch == 0xa3) {
	                                            if (ch2 >= 0xc0 && ch2 <= 0xe1) {
	                                                res = (ch2 == 0xe1 ? 0x20ac : ch2 == 0xe0 ? 0x2421 : 0x2340 + ch2);
	                                            }
	                                        } else if (ch == 0xf9) {
	                                            if (ch2 >= 0xd6) {
	                                                res = big5_2003_2uni_pagef9[ch2-0xd6];
	                                            }
	                                        } else if (ch >= 0xfa) {
	                                            res = 0xe000 + 157 * (ch - 0xfa) + (ch2 - (ch2 >= 0xa1 ? 0x62 : 0x40));
	                                        }
	                                    }
	                                } else {
	                                    /* c == 0xc2 && c2 == 0x55. */
	                                    res = 0x5f5e;
	                                }
	                            } else {
	                                /* (c == 0xc6 && c2 >= 0xa1) || c == 0xc7. */
	                                unsigned int i = 157 * (ch - 0xc6) + (ch2 - (ch2 >= 0xa1 ? 0x62 : 0x40));
	                                if (i < 133) {
	                                    /* 63 <= i < 133. */
	                                    lChar16 wc = big5_2003_2uni_pagec6[i-63];
	                                    if (wc != 0xfffd) {
	                                        res = wc;
	                                    }
	                                } else if (i < 216) {
	                                    /* 133 <= i < 216. Hiragana. */
	                                    res = (lChar16)(0x3041 - 133 + i);
	                                } else if (i < 302) {
	                                    /* 216 <= i < 302. Katakana. */
	                                    res = (lChar16)(0x30a1 - 216 + i);
	                                }
	                            }
	                        } else {
	                            /* 0x81 <= c < 0xa1. */
	                            res = (ch >= 0x8e ? 0xdb18 : 0xeeb8) + 157 * (ch - 0x81)
	                                    + (ch2 - (ch2 >= 0xa1 ? 0x62 : 0x40));
	                        }
	                    }
	                }
	            }


	            if (res == 0)
	                res = '?'; // replace invalid chars with ?
	            buf[count] = res;
	        }
	        return count;
	    }
	#endif
	#if EUC_KR_ENCODING_SUPPORT == 1
	    case ce_euc_kr:
	    {
	        // based on ICONV code, gbk.h
	        for ( ; count < maxsize - 1; count++ ) {
	            if (m_buf_pos >= m_buf_len) {
	                checkEof();
	                return count;
	            }
	            lUInt16 ch = m_buf[m_buf_pos++];
	            lUInt16 res = 0;

	            /* Code set 0 (ASCII or KS C 5636-1993) */
	            if (ch < 0x80)
	                res = ch;
	            else if (ch >= 0xa1 && ch < 0xff) {
	                if (m_buf_pos + 1 >= m_buf_len) {
	                    checkEof();
	                    return count;
	                }
	                /* Code set 1 (KS C 5601-1992, now KS X 1001:2002) */
	                lUInt16 ch2 = m_buf[m_buf_pos++];
	                if (ch2 >= 0xa1 && ch2 < 0xff) {
	                    res = cr3_ksc5601_mbtowc(ch-0x80, ch2-0x80);
	                }
	            }

	            if (res == 0)
	                res = '?'; // replace invalid chars with ?
	            buf[count] = res;
	        }
	        return count;
	    }
	#endif

	    case ce_utf16_le:
	        {
	            for ( ; count<maxsize; count++ ) {
	                if ( m_buf_pos+1>=m_buf_len ) {
	                    checkEof();
	                    return count;
	                }
	                lUInt16 ch = m_buf[m_buf_pos++];
	                lUInt16 ch2 = m_buf[m_buf_pos++];
	                buf[count] = (ch2 << 8) | ch;
	            }
	            return count;
	        }
	    case ce_utf32_be:
	        // support 24 bits only
	        {
	            for ( ; count<maxsize; count++ ) {
	                if ( m_buf_pos+3>=m_buf_len ) {
	                    checkEof();
	                    return count;
	                }
	                m_buf_pos++; //lUInt16 ch = m_buf[m_buf_pos++];
	                lUInt16 ch2 = m_buf[m_buf_pos++];
	                lUInt16 ch3 = m_buf[m_buf_pos++];
	                lUInt16 ch4 = m_buf[m_buf_pos++];
	                buf[count] = (ch2 << 16) | (ch3 << 8) | ch4;
	            }
	            return count;
	        }
	    case ce_utf32_le:
	        // support 24 bits only
	        {
	            for ( ; count<maxsize; count++ ) {
	                if ( m_buf_pos+3>=m_buf_len ) {
	                    checkEof();
	                    return count;
	                }
	                lUInt16 ch = m_buf[m_buf_pos++];
	                lUInt16 ch2 = m_buf[m_buf_pos++];
	                lUInt16 ch3 = m_buf[m_buf_pos++];
	                m_buf_pos++; //lUInt16 ch4 = m_buf[m_buf_pos++];
	                buf[count] = (ch3 << 16) | (ch2 << 8) | ch;
	            }
	            return count;
	        }
	    default:
	        return 0;
	    }

	}
	bool Parse() {
//		_bookpath->Read(buf, count)
		return false;
	}

};
/*
 * Class:     com_reader_document_txt_TxtDocument
 * Method:    pageCount
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_reader_document_txt_TxtDocument_pageCount
  (JNIEnv *, jobject) {
	return 0;
}

/*
 * Class:     com_reader_document_txt_TxtDocument
 * Method:    loadDocument
 * Signature: (Ljava/lang/String;II)I
 */
JNIEXPORT jint JNICALL Java_com_reader_document_txt_TxtDocument_loadDocument
  (JNIEnv *e, jobject self, jstring bookPath, jint width, jint height) {
	CRJNIEnv env(e);
	lString16 path = env.fromJavaString(bookPath);
	LVStreamRef stream = LVOpenFileStream(path.c_str(), LVOM_READ);
//	TxtBook book(stream);
	CRLog::setLogger( new JNICDRLogger() );
	CRLog::setLogLevel( CRLog::LL_TRACE );
	CRLog::debug("song loaddocument");

	return 0;
}

/*
 * Class:     com_reader_document_txt_TxtDocument
 * Method:    getPage
 * Signature: (Landroid/graphics/Bitmap;)I
 */
JNIEXPORT jint JNICALL Java_com_reader_document_txt_TxtDocument_getPage
  (JNIEnv *, jobject, jobject) {
	return 0;
}
