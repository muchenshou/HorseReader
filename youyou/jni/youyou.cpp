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
#define CP_AUTODETECT_BUF_SIZE 0x20000

struct TxtNode {
	long start;
	long end;
};

struct TxtPage {
	TxtPage() :
			startNode(0), startNodeOffset(0), endNode(0), endNodeOffset(0) {

	}
	lUInt32 startNode;
	int startNodeOffset;
	lUInt32 endNode;
	int endNodeOffset;
};

static int charToHex(lUInt8 ch) {
	if (ch >= '0' && ch <= '9')
		return ch - '0';
	if (ch >= 'a' && ch <= 'f')
		return ch - 'a' + 10;
	if (ch >= 'A' && ch <= 'F')
		return ch - 'A' + 10;
	return -1;
}
class TxtRender {
	LVFontRef mFontRef;
	lUInt32 mWidth;
	lUInt32 mHeight;
	std::vector<lUInt32> _lines;
public:
	TxtRender(int w, int h) :
			mWidth(w), mHeight(h) {
		mFontRef = fontMan->GetFont(1<<6, 400+70, false, css_ff_sans_serif,
				cs8("Droid Sans Fallback"),0);
		CRLog::debug("song txtrender %s", mFontRef->getTypeFace().c_str());
		mFontRef->setBitmapMode(false);
	}
	bool measureText(lString16& node) {
		lUInt16 widths[1024 * 8];
		lUInt8 flags[1024 * 8];
		_lines.clear();
		mFontRef->measureText(node.c_str(), node.length(), widths, flags,
				0x7FFF, '?', 0, false);
		int pre_index = -1;
		lUInt16 pre_width;
		_lines.push_back(0);
		for (int i = 0; i < node.length(); i++) {
			pre_width = pre_index == -1 ? 0:widths[pre_index];
			if ((widths[i] -  pre_width)< (lUInt32)mWidth ) {
				continue;
			} else {
				_lines.push_back(i);
				pre_index = i-1;
			}
		}
		return true;
	}

	lUInt32 getLineCount() {
		return _lines.size();
	}

	lUInt32 getLinePos(int index) {
		return _lines[index];
	}

	lUInt32 screenLines() {
		return mHeight / mFontRef->getHeight();
	}

	inline LVFontRef& getFont() {
		return mFontRef;
	}
	inline lUInt32 getWidth() {
		return mWidth;
	}
	inline lUInt32 getHeight() {
		return mHeight;
	}
};
class TxtBook: public LVFileParserBase {
	std::vector<TxtNode> mNodes;
	std::vector<TxtPage> mPages;
	lString16 m_lang_name;
	lString16 m_encoding_name;
	char_encoding_type m_enc_type;
	lChar16 * m_conv_table; // charset conversion table for 8-bit encodings
	TxtRender mRender;
public:
	TxtBook(LVStreamRef& path, int w, int h) :
			LVFileParserBase(path), m_enc_type(ce_unknown), m_conv_table(NULL), mRender(
					TxtRender(w, h)), _linesInPage(0) {
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
						// mNodes;

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
//		std::vector<TxtNode>::iterator it;
//
//		for (it = mNodes.begin(); it != mNodes.end();it++) {
//			CRLog::debug("song node start:%d end:%d", (*it).start,(*it).end);
//		}
//		std::vector<TxtPage>::iterator it;
//		CRLog::debug("song pages size:%d", mPages.size());
//		for (it = mPages.begin(); it != mPages.end(); it++) {
//			CRLog::debug("song node start:%d startoffset:%d end:%d endoffset:%d", (*it).startNode,
//					(*it).startNodeOffset,(*it).endNode,(*it).endNodeOffset);
//		}
		return false;
	}
	lUInt32 _linesInPage;
	TxtPage _page;
	void render(lString16& text, lUInt32 nodeid) {

		lUInt32 screenLines = mRender.screenLines();

		mRender.measureText(text);
		CRLog::debug("song render node id %d lines %d",nodeid, mRender.getLineCount());
		CRLog::debug("song render node text %s",UnicodeToUtf8(text).c_str());
		lUInt32 lineCount = 0;
		while(lineCount < mRender.getLineCount()) {
			if (_linesInPage+1<=screenLines){
				CRLog::debug("song render lines %d", _linesInPage);
				if (_linesInPage == 0) {
					_page.startNode = nodeid;
					_page.startNodeOffset = mRender.getLinePos(lineCount);
				}
				_linesInPage++;
				if (_linesInPage == screenLines) {
					_linesInPage = 0;
					_page.endNode = nodeid;
//					_page.endNodeOffset = 0;
					this->mPages.push_back(_page);
				}
			} else {
				assert(false);
			}
			lineCount++;
		}
	}
	TxtRender& getRender() {
		return mRender;
	}
	lString16 readNode(lvpos_t pos, int len) {
		Reset();

		Seek(pos, len);
		lChar16 buf[1024*8];
		int count = ReadChars(buf, 1024*8);
		lString16 str;
		str.clear();
		for (int i=0; i<1024*8;i++) {
			if (buf[i] == '\r' || buf[i] == '\n')
				break;
			str.append(1,buf[i]);
		}
		str.pack();
		return str;
	}

	bool drawPage(LVDrawBuf* draw, lUInt32 index) {

		lUInt32 start_node = mPages[index].startNode;
		lUInt32 start_node_offset = mPages[index].startNodeOffset;
		lUInt32 end_node = mPages[index].endNode;
		lUInt32 end_node_offset = mPages[index].endNodeOffset;
		LVFontRef font = mRender.getFont();

		lUInt16 widths[1024*8];
		lUInt8 flags[1024*8];
		lChar16 buf[1024*8];
		lUInt32 buf_pos = 0;
		int y = 10;
			CRLog::debug("song node drag page index %d",index);
		for(lUInt32 i= start_node;i<=end_node;i++) {
			// node
			lString16 node = readNode(mNodes[i].start, mNodes[i].end-mNodes[i].start);
			font->measureText(node.c_str(), node.length(), widths, flags,0x7FFF,'?',0);


			const lChar16 *str = node.c_str();
			CRLog::debug("song node %d %s", i,UnicodeToUtf8(node).c_str());
			int pre = -1;
			for(int j=0;j<node.length();j++) {
				int prelen;
				if (pre == -1) {
					prelen = 0;
				} else {
					prelen = widths[pre];
				}
				// line
				if (widths[j] > mRender.getWidth()+prelen) {
					pre = pre == -1 ? 0 : pre;
					font->DrawTextString(draw, 10, y,
							str+pre,j-pre-1, L'?', NULL, false,  0);
					pre = j -1;
					y += font->getHeight();
				}
				if (j == node.length()-1) {
					font->DrawTextString(draw, 10, y,
												str+pre+1,j - pre, L'?', NULL, false,  0);

					y += font->getHeight();
				}
			}
		}

		return false;
	}
protected:

};

/*
 * Class:     com_reader_document_txt_TxtDocument
 * Method:    pageCount
 * Signature: ()I
 */JNIEXPORT jint JNICALL Java_com_reader_document_txt_TxtDocument_pageCount(
		JNIEnv *, jobject) {
	return 0;
}

LVRef<TxtBook> txt_book;
/*
 * Class:     com_reader_document_txt_TxtDocument
 * Method:    loadDocument
 * Signature: (Ljava/lang/String;II)I
 */JNIEXPORT jint JNICALL Java_com_reader_document_txt_TxtDocument_loadDocument(
		JNIEnv *e, jobject self, jstring bookPath, jint width, jint height) {
	CRJNIEnv env(e);
	lString16 path = env.fromJavaString(bookPath);
	LVStreamRef stream = LVOpenFileStream(path.c_str(), LVOM_READ);
	txt_book = new TxtBook(stream, width, height);
	CRLog::setLogger(new JNICDRLogger());
	CRLog::setLogLevel(CRLog::LL_TRACE);
	CRLog::debug("song loaddocument");
	txt_book->Parse();
	return 0;
}
 LVMutex _mutex;
/*
 * Class:     com_reader_document_txt_TxtDocument
 * Method:    getPage
 * Signature: (Landroid/graphics/Bitmap;)I
 */JNIEXPORT jint JNICALL Java_com_reader_document_txt_TxtDocument_getPage(
		JNIEnv *env, jobject self, jint index,jobject bitmap) {
	 LVLock lock(_mutex);
	LVDrawBuf * drawbuf = BitmapAccessorInterface::getInstance()->lock(env,
			bitmap);
	if (drawbuf != NULL) {
		drawbuf->FillRect(0, 0, txt_book->getRender().getWidth(),
				txt_book->getRender().getHeight(), 0x00ffeeee);
		drawbuf->SetTextColor( 0x00000000 );
		txt_book->drawPage(drawbuf, index);
		//CRLog::trace("getPageImageInternal calling bitmap->unlock");
		BitmapAccessorInterface::getInstance()->unlock(env, bitmap, drawbuf);
	} else {
		CRLog::error("bitmap accessor is invalid");
	}
	return 0;
}
