#include <jni.h>
#include <com_reader_document_txt_TxtDocument.h>
#include <vector>
#include <assert.h>
#include "cr3java.h"
#include "lvstring.h"
#include "lvstream.h"
#include "crtxtenc.h"
#include "cssdef.h"
#include "lvthread.h"

typedef struct {
	unsigned short indx; /* index into big table */
	unsigned short used; /* bitmask of used entries */
} Summary16;

typedef unsigned int ucs4_t;
#if GBK_ENCODING_SUPPORT == 1
#include "../include/encodings/gbkext1.h"
#include "../include/encodings/gbkext2.h"
#include "../include/encodings/gb2312.h"
#include "../include/encodings/cp936ext.h"
#endif
#if JIS_ENCODING_SUPPORT == 1
#include "../include/encodings/jisx0213.h"
#endif
#if BIG5_ENCODING_SUPPORT == 1
#include "../include/encodings/big5.h"
#include "../include/encodings/big5_2003.h"
#endif
#if EUC_KR_ENCODING_SUPPORT == 1
#include "../include/encodings/ksc5601.h"
#endif

static int charToHex(lUInt8 ch) {
	if (ch >= '0' && ch <= '9')
		return ch - '0';
	if (ch >= 'a' && ch <= 'f')
		return ch - 'a' + 10;
	if (ch >= 'A' && ch <= 'F')
		return ch - 'A' + 10;
	return -1;
}

#if GBK_ENCODING_SUPPORT == 1
// based on code from libiconv
static lChar16 cr3_gb2312_mbtowc(const unsigned char *s) {
	unsigned char c1 = s[0];
	if ((c1 >= 0x21 && c1 <= 0x29) || (c1 >= 0x30 && c1 <= 0x77)) {
		unsigned char c2 = s[1];
		if (c2 >= 0x21 && c2 < 0x7f) {
			unsigned int i = 94 * (c1 - 0x21) + (c2 - 0x21);
			if (i < 1410) {
				if (i < 831)
					return gb2312_2uni_page21[i];
			} else {
				if (i < 8178)
					return gb2312_2uni_page30[i - 1410];
			}
		}
	}
	return 0;
}

// based on code from libiconv
static lChar16 cr3_cp936ext_mbtowc(const unsigned char *s) {
	unsigned char c1 = s[0];
	if ((c1 == 0xa6) || (c1 == 0xa8)) {
		unsigned char c2 = s[1];
		if ((c2 >= 0x40 && c2 < 0x7f) || (c2 >= 0x80 && c2 < 0xff)) {
			unsigned int i = 190 * (c1 - 0x81)
					+ (c2 - (c2 >= 0x80 ? 0x41 : 0x40));
			if (i < 7410) {
				if (i >= 7189 && i < 7211)
					return cp936ext_2uni_pagea6[i - 7189];
			} else {
				if (i >= 7532 && i < 7538)
					return cp936ext_2uni_pagea8[i - 7532];
			}
		}
	}
	return 0;
}

// based on code from libiconv
static lChar16 cr3_gbkext1_mbtowc(lChar16 c1, lChar16 c2) {
	if ((c1 >= 0x81 && c1 <= 0xa0)) {
		if ((c2 >= 0x40 && c2 < 0x7f) || (c2 >= 0x80 && c2 < 0xff)) {
			unsigned int i = 190 * (c1 - 0x81)
					+ (c2 - (c2 >= 0x80 ? 0x41 : 0x40));
			if (i < 6080)
				return gbkext1_2uni_page81[i];
		}
	}
	return 0;
}

// based on code from libiconv
static lChar16 cr3_gbkext2_mbtowc(lChar16 c1, lChar16 c2) {
	if ((c1 >= 0xa8 && c1 <= 0xfe)) {
		if ((c2 >= 0x40 && c2 < 0x7f) || (c2 >= 0x80 && c2 < 0xa1)) {
			unsigned int i = 96 * (c1 - 0x81)
					+ (c2 - (c2 >= 0x80 ? 0x41 : 0x40));
			if (i < 12016)
				return gbkext2_2uni_pagea8[i - 3744];
		}
	}
	return 0;
}
#endif

#if JIS_ENCODING_SUPPORT == 1
// based on code from libiconv
static lChar16 cr3_jisx0213_to_ucs4(unsigned int row, unsigned int col) {
	lChar16 val;

	if (row >= 0x121 && row <= 0x17e)
		row -= 289;
	else if (row == 0x221)
		row -= 451;
	else if (row >= 0x223 && row <= 0x225)
		row -= 452;
	else if (row == 0x228)
		row -= 454;
	else if (row >= 0x22c && row <= 0x22f)
		row -= 457;
	else if (row >= 0x26e && row <= 0x27e)
		row -= 519;
	else
		return 0x0000;

	if (col >= 0x21 && col <= 0x7e)
		col -= 0x21;
	else
		return 0x0000;

	val = (lChar16) jisx0213_to_ucs_main[row * 94 + col];
	val = (lChar16) jisx0213_to_ucs_pagestart[val >> 8] + (val & 0xff);
	if (val == 0xfffd)
		val = 0x0000;
	return val;
}
#endif

#if BIG5_ENCODING_SUPPORT == 1
// based on code from libiconv
static lUInt16 cr3_big5_mbtowc(lChar16 c1, lChar16 c2) {
	if ((c1 >= 0xa1 && c1 <= 0xc7) || (c1 >= 0xc9 && c1 <= 0xf9)) {
		if ((c2 >= 0x40 && c2 < 0x7f) || (c2 >= 0xa1 && c2 < 0xff)) {
			unsigned int i = 157 * (c1 - 0xa1)
					+ (c2 - (c2 >= 0xa1 ? 0x62 : 0x40));
			unsigned short wc = 0xfffd;
			if (i < 6280) {
				if (i < 6121)
					wc = big5_2uni_pagea1[i];
			} else {
				if (i < 13932)
					wc = big5_2uni_pagec9[i - 6280];
			}
			if (wc != 0xfffd) {
				return wc;
			}
		}
	}
	return 0;
}

#endif

#if EUC_KR_ENCODING_SUPPORT == 1
// based on code from libiconv
static lChar16 cr3_ksc5601_mbtowc(lChar16 c1, lChar16 c2) {
	if ((c1 >= 0x21 && c1 <= 0x2c) || (c1 >= 0x30 && c1 <= 0x48)
			|| (c1 >= 0x4a && c1 <= 0x7d)) {
		if (c2 >= 0x21 && c2 < 0x7f) {
			unsigned int i = 94 * (c1 - 0x21) + (c2 - 0x21);
			unsigned short wc = 0xfffd;
			if (i < 1410) {
				if (i < 1115)
					wc = ksc5601_2uni_page21[i];
			} else if (i < 3854) {
				if (i < 3760)
					wc = ksc5601_2uni_page30[i - 1410];
			} else {
				if (i < 8742)
					wc = ksc5601_2uni_page4a[i - 3854];
			}
			if (wc != 0xfffd) {
				return wc;
			}
		}
	}
	return 0;
}
#endif

#define CP_AUTODETECT_BUF_SIZE 0x20000

struct TxtNode {
	long start;
	long end;
};

struct TxtPage {
	TxtPage() :
			startNode(0), startLineNum(0), endNode(0), endNodeLineNum(0) {

	}
	lUInt32 startNode;
	int startLineNum;
	lUInt32 endNode;
	int endNodeLineNum;
};
class TxtBookCache {
	// {文件内容：[4:文件的最后修改时间][4:储存内容的crc]{储存内容：[4:nodes的size][:nodes][4:pages的size][pages]}}
};

class TxtBook: public LVFileParserBase {
	std::vector<TxtNode> mNodes;
	std::vector<TxtPage> mPages;
	lString16 m_lang_name;
	lString16 m_encoding_name;
	char_encoding_type m_enc_type;
	lChar16 * m_conv_table; // charset conversion table for 8-bit encodings
	LVMutex _mutex;
	int _left_margin;
	int _right_margin;
	int _top_margin;
	int _bottom_margin;

	LFormattedText _fmt;
	LVFontRef mFontRef;
public:
	int view_width;
	int view_height;
	lUInt32 mWidth;
	lUInt32 mHeight;
	TxtBook(LVStreamRef& path, int w, int h, int font_size) :
			LVFileParserBase(path), m_enc_type(ce_unknown), m_conv_table(NULL), _linePosInPage(
					0) {
		view_width = w;
		view_height = h;
		_left_margin = 50;
		_right_margin = 30;
		_top_margin = 50;
		_bottom_margin = 50;
		mWidth = w - _left_margin - _right_margin;
		mHeight = h - _top_margin - _bottom_margin;
		mFontRef = fontMan->GetFont(font_size, 400 + 70, false,
				css_ff_sans_serif, cs8("Droid Sans Fallback"), 0);
		mFontRef->setBitmapMode(false);

	}
	/// returns 8-bit charset conversion table (128 items, for codes 128..255)
	virtual lChar16 * GetCharsetTable() {
		return m_conv_table;
	}
	void SetCharset(const lChar16 * name) {
		m_encoding_name = lString16(name);
		if (m_encoding_name == "utf-8") {
			m_enc_type = ce_utf8;
			SetCharsetTable(NULL);
		} else if (m_encoding_name == "utf-16") {
			m_enc_type = ce_utf16_le;
			SetCharsetTable(NULL);
#if GBK_ENCODING_SUPPORT == 1
		} else if (m_encoding_name == "gbk" || m_encoding_name == "cp936"
				|| m_encoding_name == "cp-936") {
			m_enc_type = ce_gbk;
			SetCharsetTable(NULL);
#endif
#if JIS_ENCODING_SUPPORT == 1
		} else if (m_encoding_name == "shift-jis"
				|| m_encoding_name == "shift_jis" || m_encoding_name == "sjis"
				|| m_encoding_name == "ms_kanji"
				|| m_encoding_name == "csshiftjis"
				|| m_encoding_name == "shift_jisx0213"
				|| m_encoding_name == "shift_jis-2004"
				|| m_encoding_name == "cp932") {
			m_enc_type = ce_shift_jis;
			SetCharsetTable(NULL);
		} else if (m_encoding_name == "euc-jisx0213"
				|| m_encoding_name == "euc-jis-2004"
				|| m_encoding_name == "euc-jis" || m_encoding_name == "euc-jp"
				|| m_encoding_name == "eucjp") {
			m_enc_type = ce_euc_jis;
			SetCharsetTable(NULL);
#endif
#if BIG5_ENCODING_SUPPORT == 1
		} else if (m_encoding_name == "big5" || m_encoding_name == "big5-2003"
				|| m_encoding_name == "big-5" || m_encoding_name == "big-five"
				|| m_encoding_name == "bigfive" || m_encoding_name == "cn-big5"
				|| m_encoding_name == "csbig5" || m_encoding_name == "cp950") {
			m_enc_type = ce_big5;
			SetCharsetTable(NULL);
#endif
#if EUC_KR_ENCODING_SUPPORT == 1
		} else if (m_encoding_name == "euc_kr" || m_encoding_name == "euc-kr"
				|| m_encoding_name == "euckr" || m_encoding_name == "cseuckr"
				|| m_encoding_name == "cp51949" || m_encoding_name == "cp949") {
			m_enc_type = ce_euc_kr;
			SetCharsetTable(NULL);
#endif
		} else if (m_encoding_name == "utf-16le") {
			m_enc_type = ce_utf16_le;
			SetCharsetTable(NULL);
		} else if (m_encoding_name == "utf-16be") {
			m_enc_type = ce_utf16_be;
			SetCharsetTable(NULL);
		} else if (m_encoding_name == "utf-32") {
			m_enc_type = ce_utf32_le;
			SetCharsetTable(NULL);
		} else if (m_encoding_name == "utf-32le") {
			m_enc_type = ce_utf32_le;
			SetCharsetTable(NULL);
		} else if (m_encoding_name == "utf-32be") {
			m_enc_type = ce_utf32_be;
			SetCharsetTable(NULL);
		} else {
			m_enc_type = ce_8bit_cp;
			//CRLog::trace("charset: %s", LCSTR(lString16(name)));
			const lChar16 * table = GetCharsetByte2UnicodeTable(name);
			if (table)
				SetCharsetTable(table);
		}
	}

	void SetCharsetTable(const lChar16 * table) {
		if (!table) {
			if (m_conv_table) {
				delete[] m_conv_table;
				m_conv_table = NULL;
			}
			return;
		}
		m_enc_type = ce_8bit_cp;
		if (!m_conv_table)
			m_conv_table = new lChar16[128];
		lStr_memcpy(m_conv_table, table, 128);
	}

	void checkEof() {
		if (m_buf_fpos + m_buf_len >= this->m_stream_size - 4)
			m_buf_pos = m_buf_len = m_stream_size - m_buf_fpos; //force eof
		//m_buf_pos = m_buf_len = m_stream_size - (m_buf_fpos+m_buf_len);
	}
	int ReadChars(lChar16 * buf, int maxsize) {
		if (m_buf_pos >= m_buf_len)
			return 0;
		int count = 0;
		switch (m_enc_type) {
		case ce_8bit_cp:
		case ce_utf8:
			if (m_conv_table != NULL) {
				for (; count < maxsize && m_buf_pos < m_buf_len; count++) {
					lUInt16 ch = m_buf[m_buf_pos++];
					buf[count] =
							((ch & 0x80) == 0) ? ch : m_conv_table[ch & 0x7F];
				}
				return count;
			} else {
				int srclen = m_buf_len - m_buf_pos;
				int dstlen = maxsize;
				Utf8ToUnicode(m_buf + m_buf_pos, srclen, buf, dstlen);
				m_buf_pos += srclen;
				if (dstlen == 0) {
					checkEof();
				}
				return dstlen;
			}
		case ce_utf16_be: {
			for (; count < maxsize; count++) {
				if (m_buf_pos + 1 >= m_buf_len) {
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
		case ce_gbk: {
			// based on ICONV code, gbk.h
			for (; count < maxsize; count++) {
				if (m_buf_pos >= m_buf_len) {
					checkEof();
					return count;
				}
				lUInt16 ch = m_buf[m_buf_pos++];
				int twoBytes = ch >= 0x81 && ch < 0xFF ? 1 : 0;
				if (m_buf_pos + twoBytes >= m_buf_len) {
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
						buf[0] = (lUInt8) (ch - 0x80);
						buf[1] = (lUInt8) (ch2 - 0x80);
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
		case ce_shift_jis: {
			// based on ICONV code, gbk.h
			for (; count < maxsize - 1; count++) {
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
					if ((ch >= 0x81 && ch <= 0x9f)
							|| (ch >= 0xe0 && ch <= 0xfc)) {
						/* Two byte character. */
						if (m_buf_pos + 1 >= m_buf_len) {
							checkEof();
							return count;
						}
						lUInt16 ch2 = 0;
						ch2 = m_buf[m_buf_pos++];
						if ((ch2 >= 0x40 && ch2 <= 0x7e)
								|| (ch2 >= 0x80 && ch2 <= 0xfc)) {
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
							lChar16 wc = cr3_jisx0213_to_ucs4(0x121 + ch1, ch2);
							if (wc) {
								if (wc < 0x80) {
									/* It's a combining character. */
									lChar16 wc1 = jisx0213_to_ucs_combining[wc
											- 1][0];
									lChar16 wc2 = jisx0213_to_ucs_combining[wc
											- 1][1];
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
		case ce_euc_jis: {
			// based on ICONV code, gbk.h
			for (; count < maxsize - 1; count++) {
				lUInt16 ch = m_buf[m_buf_pos++];
				lUInt16 res = 0;
				if (ch < 0x80) {
					/* Plain ASCII character. */
					res = ch;
				} else {
					if ((ch >= 0xa1 && ch <= 0xfe) || ch == 0x8e
							|| ch == 0x8f) {
						/* Two byte character. */
						if (m_buf_pos + 1 >= m_buf_len) {
							checkEof();
							return count;
						}
						lUInt16 ch2 = m_buf[m_buf_pos++];
						if (ch2 >= 0xa1 && ch2 <= 0xfe && ch == 0x8f
								&& m_buf_pos + 2 >= m_buf_len) {
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
									wc = cr3_jisx0213_to_ucs4(
											0x200 - 0x80 + ch2, ch3 ^ 0x80);
								} else {
									/* JISX 0213 plane 1. */
									wc = cr3_jisx0213_to_ucs4(0x100 - 0x80 + ch,
											ch2 ^ 0x80);
								}
								if (wc) {
									if (wc < 0x80) {
										/* It's a combining character. */
										ucs4_t wc1 =
												jisx0213_to_ucs_combining[wc - 1][0];
										ucs4_t wc2 =
												jisx0213_to_ucs_combining[wc - 1][1];
										/* We cannot output two Unicode characters at once. So,
										 output the first character and buffer the second one. */
										buf[count++] = (lChar16) wc1;
										res = (lChar16) wc2;
									} else
										res = (lChar16) wc;
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
		case ce_big5: {
			// based on ICONV code, gbk.h
			for (; count < maxsize - 1; count++) {
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
						if ((ch2 >= 0x40 && ch2 < 0x7f)
								|| (ch2 >= 0xa1 && ch2 < 0xff)) {
							if (ch >= 0xa1) {
								if (ch < 0xa3) {
									unsigned int i =
											157 * (ch - 0xa1)
													+ (ch2
															- (ch2 >= 0xa1 ?
																	0x62 : 0x40));
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
												if (ch2 >= 0xc0
														&& ch2 <= 0xe1) {
													res =
															(ch2 == 0xe1 ?
																	0x20ac :
																ch2 == 0xe0 ?
																		0x2421 :
																		0x2340
																				+ ch2);
												}
											} else if (ch == 0xf9) {
												if (ch2 >= 0xd6) {
													res =
															big5_2003_2uni_pagef9[ch2
																	- 0xd6];
												}
											} else if (ch >= 0xfa) {
												res = 0xe000 + 157 * (ch - 0xfa)
														+ (ch2
																- (ch2 >= 0xa1 ?
																		0x62 :
																		0x40));
											}
										}
									} else {
										/* c == 0xc2 && c2 == 0x55. */
										res = 0x5f5e;
									}
								} else {
									/* (c == 0xc6 && c2 >= 0xa1) || c == 0xc7. */
									unsigned int i =
											157 * (ch - 0xc6)
													+ (ch2
															- (ch2 >= 0xa1 ?
																	0x62 : 0x40));
									if (i < 133) {
										/* 63 <= i < 133. */
										lChar16 wc = big5_2003_2uni_pagec6[i
												- 63];
										if (wc != 0xfffd) {
											res = wc;
										}
									} else if (i < 216) {
										/* 133 <= i < 216. Hiragana. */
										res = (lChar16) (0x3041 - 133 + i);
									} else if (i < 302) {
										/* 216 <= i < 302. Katakana. */
										res = (lChar16) (0x30a1 - 216 + i);
									}
								}
							} else {
								/* 0x81 <= c < 0xa1. */
								res = (ch >= 0x8e ? 0xdb18 : 0xeeb8)
										+ 157 * (ch - 0x81)
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
		case ce_euc_kr: {
			// based on ICONV code, gbk.h
			for (; count < maxsize - 1; count++) {
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
						res = cr3_ksc5601_mbtowc(ch - 0x80, ch2 - 0x80);
					}
				}

				if (res == 0)
					res = '?'; // replace invalid chars with ?
				buf[count] = res;
			}
			return count;
		}
#endif

		case ce_utf16_le: {
			for (; count < maxsize; count++) {
				if (m_buf_pos + 1 >= m_buf_len) {
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
			for (; count < maxsize; count++) {
				if (m_buf_pos + 3 >= m_buf_len) {
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
			for (; count < maxsize; count++) {
				if (m_buf_pos + 3 >= m_buf_len) {
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
	/// tries to autodetect text encoding
	bool AutodetectEncoding(bool utfOnly) {
		char enc_name[32];
		char lang_name[32];
		lvpos_t oldpos = m_stream->GetPos();
		unsigned sz = CP_AUTODETECT_BUF_SIZE;
		m_stream->SetPos(0);
		if (sz > m_stream->GetSize())
			sz = m_stream->GetSize();
		if (sz < 16)
			return false;
		unsigned char * buf = new unsigned char[sz];
		lvsize_t bytesRead = 0;
		if (m_stream->Read(buf, sz, &bytesRead) != LVERR_OK) {
			delete[] buf;
			m_stream->SetPos(oldpos);
			return false;
		}

		int res = 0;
		bool hasTags = hasXmlTags(buf, sz);
		if (utfOnly)
			res = AutodetectCodePageUtf(buf, sz, enc_name, lang_name);
		else
			res = AutodetectCodePage(buf, sz, enc_name, lang_name, hasTags);
		delete[] buf;
		m_stream->SetPos(oldpos);
		if (res) {
			//CRLog::debug("Code page decoding results: encoding=%s, lang=%s", enc_name, lang_name);
			m_lang_name = lString16(lang_name);
			SetCharset(lString16(enc_name).c_str());
		}

		// restore state
		return res != 0 || utfOnly;
	}
	/// reads one character from buffer in RTF format
	lChar16 ReadRtfChar(int, const lChar16 * conv_table) {
		lChar16 ch = m_buf[m_buf_pos++];
		lChar16 ch2 = m_buf[m_buf_pos];
		if (ch == '\\' && ch2 != '\'') {
		} else if (ch == '\\') {
			m_buf_pos++;
			int digit1 = charToHex(m_buf[0]);
			int digit2 = charToHex(m_buf[1]);
			m_buf_pos += 2;
			if (digit1 >= 0 && digit2 >= 0) {
				ch = ((lChar8) ((digit1 << 4) | digit2));
				if (ch & 0x80)
					return conv_table[ch & 0x7F];
				else
					return ch;
			} else {
				return '?';
			}
		} else {
			if (ch >= ' ') {
				if (ch & 0x80)
					return conv_table[ch & 0x7F];
				else
					return ch;
			}
		}
		return ' ';
	}

	/// reads specified number of bytes, converts to characters and saves to buffer
	int ReadTextBytes(lvpos_t pos, int bytesToRead, lChar16 * buf, int buf_size,
			int flags) {
		if (!Seek(pos, bytesToRead)) {
			CRLog::error(
					"LVTextFileBase::ReadTextBytes seek error! cannot set pos to %d to read %d bytes",
					(int) pos, (int) bytesToRead);
			return 0;
		}
		int chcount = 0;
		int max_pos = m_buf_pos + bytesToRead;
		if (max_pos > m_buf_len)
			max_pos = m_buf_len;
		if ((flags & TXTFLG_RTF) != 0) {
			char_encoding_type enc_type = ce_utf8;
			lChar16 * conv_table = NULL;
			if (flags & TXTFLG_ENCODING_MASK) {
				// set new encoding
				int enc_id = (flags & TXTFLG_ENCODING_MASK)
						>> TXTFLG_ENCODING_SHIFT;
				if (enc_id >= ce_8bit_cp) {
					conv_table = (lChar16 *) GetCharsetByte2UnicodeTableById(
							enc_id);
					enc_type = ce_8bit_cp;
				} else {
					conv_table = NULL;
					enc_type = (char_encoding_type) enc_id;
				}
			}
			while (m_buf_pos < max_pos && chcount < buf_size) {
				*buf++ = ReadRtfChar(enc_type, conv_table);
				chcount++;
			}
		} else {
			return ReadChars(buf, buf_size);
		}
		return chcount;
	}

	bool CheckFormat() {
		LVFileParserBase::Reset();
		// encoding test
		if (!AutodetectEncoding(false))
			return false;
#define TEXT_PARSER_DETECT_SIZE 16384
		LVFileParserBase::Reset();
		lChar16 * chbuf = new lChar16[TEXT_PARSER_DETECT_SIZE];
		FillBuffer(TEXT_PARSER_DETECT_SIZE);
		int charsDecoded = ReadTextBytes(0, m_buf_len, chbuf,
				TEXT_PARSER_DETECT_SIZE - 1, 0);
		bool res = false;
		if (charsDecoded > 16) {
			int illegal_char_count = 0;
			int crlf_count = 0;
			int space_count = 0;
			for (int i = 0; i < charsDecoded; i++) {
				if (chbuf[i] <= 32) {
					switch (chbuf[i]) {
					case ' ':
					case '\t':
						space_count++;
						break;
					case 10:
					case 13:
						crlf_count++;
						break;
					case 12:
						//case 9:
					case 8:
					case 7:
					case 30:
					case 0x14:
					case 0x15:
						break;
					default:
						illegal_char_count++;
					}
				}
			}
			if (illegal_char_count == 0
					&& (space_count >= charsDecoded / 16 || crlf_count > 0))
				res = true;
			if (illegal_char_count > 0)
				CRLog::error("illegal characters detected: count=%d",
						illegal_char_count);
		}
		delete[] chbuf;
		LVFileParserBase::Reset();
		return res;
	}

	bool Parse() {
//		_bookpath->Read(buf, count)
		CheckFormat();
		LVFileParserBase::Reset();
		lChar16 buf[1024 * 16];
		int pos = 0;
		TxtNode node;
		node.start = 0;

		while (true) {

			if (m_buf_len - m_buf_pos < 8) {
				FillBuffer(4096);
			}
			if (ReadChars(&buf[pos], 1) > 0) {
				int ch = buf[pos];
				if ((buf[pos] == '\r' || buf[pos] == '\n')) {
					if (pos != 0) {
						buf[pos] = 0;
						lString16 n(buf);

						node.end = m_buf_fpos + m_buf_pos;

						mNodes.push_back(node);

						// render
						render(n, (lUInt32) (mNodes.size() - 1));
					}
					node.start = m_buf_fpos + m_buf_pos;
					pos = 0;
				} else {
					pos++;
				}
			} else {
				buf[pos] = 0;
				if (pos != 0) {
					buf[pos] = 0;
					CRLog::debug("song %d %d %s", buf[pos], pos,
							UnicodeToUtf8(lString16(buf)).c_str());

					pos = 0;
				}
				break;
			}
		}
		lString16 n("");
		render(n, (lUInt32) (mNodes.size() - 1), true);
		return false;
	}
private:
	lUInt32 _linePosInPage;
	TxtPage _page;
public:
	int getPagesCount() {
		return mPages.size();
	}
	void render(lString16& text, lUInt32 nodeid, bool end = false) {
		LVLock lock(_mutex);
		if (end) {
			_page.endNode = nodeid;
			this->mPages.push_back(_page);
			CRLog::debug("song render page %d", mPages.size());
			return;
		}
		_fmt.Clear();
		_fmt.AddSourceLine(text.c_str(), text.length(), 0xFF, 0x0,
				mFontRef.get(),LTEXT_ALIGN_LEFT|LTEXT_FLAG_OWNTEXT,
		           20, /* interline space, *16 (16=single, 32=double) */
		           100    /* first line margin */
		           );
		_fmt.Format(mWidth, mHeight);

		for (int i=0; i<_fmt.GetLineCount(); i++) {
			const formatted_line_t *line = _fmt.GetLineInfo(i);
			if (_linePosInPage+(line->height) < mHeight) {
				if (_linePosInPage == 0) {
					_page.startNode = nodeid;
					_page.startLineNum = i;
				} else {
					_page.endNode = nodeid;
					_page.endNodeLineNum = i;
				}
				_linePosInPage+= line->height;
			} else {
				_linePosInPage = line->height;

				this->mPages.push_back(_page);
				_page.startNode = nodeid;
				_page.startLineNum = i;
			}
		}

	}

	lString16 readNode(lvpos_t pos, int len) {
		Reset();

		Seek(pos, len);
		lChar16 buf[1024 * 8];
		int count = ReadChars(buf, 1024 * 8);
		lString16 str;
		str.clear();
		for (int i = 0; i < 1024 * 8; i++) {
			if (buf[i] == '\r' || buf[i] == '\n')
				break;
			str.append(1, buf[i]);
		}
		str.pack();
		return str;
	}

	bool drawPage(LVDrawBuf* draw, lUInt32 index) {
		LVLock lock(_mutex);
		lUInt32 start_node = mPages[index].startNode;
		lUInt32 start_node_offset = mPages[index].startLineNum;
		lUInt32 end_node = mPages[index].endNode;

		_fmt.Clear();
		for (lUInt32 i = start_node; i <= end_node; i++) {
			lString16 node = readNode(mNodes[i].start,
					mNodes[i].end - mNodes[i].start);
			_fmt.AddSourceLine(node.c_str(), node.length(), 0x000000,
					0xFF000000, mFontRef.get(),LTEXT_ALIGN_LEFT|LTEXT_FLAG_OWNTEXT,
			           20, /* interline space, *16 (16=single, 32=double) */
			           100    /* first line margin */
			           );
		}
		_fmt.Format(mWidth, mHeight);
		int screenLines = mHeight / (mFontRef->getHeight());
		const formatted_line_t *line = _fmt.GetLineInfo(
							 start_node_offset);
		int start = line->y;
		draw->setHidePartialGlyphs(true);
		lvRect r(_left_margin,_top_margin,_left_margin+mWidth, screenLines*mFontRef->getHeight());
		draw->SetClipRect(&r);
		_fmt.Draw(draw,_left_margin,-start+_top_margin,NULL);
		draw->SetClipRect(NULL);
		return false;
	}
};
LVRef<TxtBook> txt_book;
static LVImageSourceRef _currentImage; //bg

/*
 * Class:     com_reader_document_txt_TxtDocument
 * Method:    setBg
 * Signature: ([B)I
 */JNIEXPORT jint JNICALL Java_com_reader_document_txt_TxtDocument_setBg(
		JNIEnv *_env, jobject self, jbyteArray jdata) {
	CRJNIEnv env(_env);
	LVImageSourceRef img;
	if (jdata != NULL) {
		LVStreamRef stream = env.jbyteArrayToStream(jdata);
		if (!stream.isNull()) {
			img = LVCreateStreamImageSource(stream);
		}
	}
	_currentImage = img;
	return 0;
}
/*
 * Class:     com_reader_document_txt_TxtDocument
 * Method:    pageCount
 * Signature: ()I
 */JNIEXPORT jint JNICALL Java_com_reader_document_txt_TxtDocument_pageCount(
		JNIEnv *, jobject) {
	return txt_book->getPagesCount();
}

/*
 * Class:     com_reader_document_txt_TxtDocument
 * Method:    loadDocument
 * Signature: (Ljava/lang/String;II)I
 */JNIEXPORT jint JNICALL Java_com_reader_document_txt_TxtDocument_loadDocument(
		JNIEnv *e, jobject self, jstring bookPath, jint width, jint height,
		jint font_size) {
	CRJNIEnv env(e);
	lString16 path = env.fromJavaString(bookPath);
	LVStreamRef stream = LVOpenFileStream(path.c_str(), LVOM_READ);
	txt_book = new TxtBook(stream, width, height, font_size);
	CRLog::setLogger(new JNICDRLogger());
	CRLog::setLogLevel(CRLog::LL_TRACE);
	CRLog::debug("song loaddocument");
	txt_book->Parse();
	return 0;
}

/*
 * Class:     com_reader_document_txt_TxtDocument
 * Method:    getPage
 * Signature: (Landroid/graphics/Bitmap;)I
 */JNIEXPORT jint JNICALL Java_com_reader_document_txt_TxtDocument_getPage(
		JNIEnv *env, jobject self, jint index, jobject bitmap) {
	LVDrawBuf * drawbuf = BitmapAccessorInterface::getInstance()->lock(env,
			bitmap);
	if (drawbuf != NULL) {
//		drawbuf->FillRect(0, 0, txt_book->getRender().getWidth(),
//				txt_book->getRender().getHeight(), 0x00ffeeee);
//		drawbuf->Clear(0xFF000000);
		if (_currentImage.get() != NULL) {
			drawbuf->Draw(_currentImage, 0, 0, txt_book->view_width,
					txt_book->view_height);
		}
		drawbuf->SetTextColor(0x00000000);
		txt_book->drawPage(drawbuf, index);
		//CRLog::trace("getPageImageInternal calling bitmap->unlock");
		BitmapAccessorInterface::getInstance()->unlock(env, bitmap, drawbuf);
	} else {
		CRLog::error("bitmap accessor is invalid");
	}
	return 0;
}
