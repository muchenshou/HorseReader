#ifndef EPUBFMT_H
#define EPUBFMT_H

#include "../include/crsetup.h"
#include "../include/lvtinydom.h"
#include "lvthread.h"
#include "lvpagesplitter.h"
bool DetectEpubFormat( LVStreamRef stream );
bool ImportEpubDocument( LVStreamRef stream, ldomDocument * doc, LVDocViewCallback * progressCallback, CacheLoadingCallback * formatCallback );
lString16 EpubGetRootFilePath( LVContainerRef m_arc );
LVStreamRef GetEpubCoverpage(LVContainerRef arc);
void testEpub(LVStreamRef stream,LVDrawBuf& drawbuf);


extern lString16 mergeCssMacros(CRPropRef props);
extern lString8 substituteCssMacros(lString8 src, CRPropRef props);

class EpubDocument{
	LVRendPageList m_pages;
	ldomDocument *m_doc;
	LVMutex _mutex;
	lvRect m_pageRects[2];
	lvRect m_pageMargins;
	int m_dx;
	int m_dy;

	font_ref_t     m_font;
	int m_font_size;
	lString8 m_defaultFontFace;

	// options
	CRPropRef m_props;
	CRPropRef m_doc_props;
	int m_def_interline_space;
	lString8 m_stylesheet;
	bool m_is_rendered;
	LVImageSourceRef m_defaultCover;
public:
	EpubDocument();
	/// return view mutex
	LVMutex & getMutex() { return _mutex; }
	void updateLayout() {
		lvRect rc(0, 0, m_dx, m_dy);
		m_pageRects[0] = rc;
		m_pageRects[1] = rc;
	}
	void updateDocStyleSheet() {
	    CRPropRef p = m_props->getSubProps("styles.");
	    m_doc->setStyleSheet(substituteCssMacros(m_stylesheet, p).c_str(), true);
	}
	void setRenderProps(int dx, int dy);
	/// returns book title
	lString16 getTitle() {
		return m_doc_props->getStringDef(DOC_PROP_TITLE);
	}
	/// returns book language
	lString16 getLanguage() {
		return m_doc_props->getStringDef(DOC_PROP_LANGUAGE);
	}
	/// returns book author(s)
	lString16 getAuthors() {
		return m_doc_props->getStringDef(DOC_PROP_AUTHORS);
	}
	/// returns book series name and number (series name #1)
	lString16 getSeries() {
		lString16 name = m_doc_props->getStringDef(DOC_PROP_SERIES_NAME);
		lString16 number = m_doc_props->getStringDef(DOC_PROP_SERIES_NUMBER);
		if (!name.empty() && !number.empty())
			name << " #" << number;
		return name;
	}
public :
	/// get current default cover image
	    LVImageSourceRef getDefaultCover() const { return m_defaultCover; }
	/// returns cover page image source, if any
	LVImageSourceRef getCoverPageImage() {

//		lUInt16 path[] = { el_FictionBook, el_description, el_title_info,
//				el_coverpage, 0 };
//		ldomNode * cover_el = m_doc->getRootNode()->findChildElement(path);
//
//		if (cover_el) {
//			ldomNode * cover_img_el = cover_el->findChildElement(LXML_NS_ANY,
//					el_image, 0);
//			if (cover_img_el) {
//				LVImageSourceRef imgsrc = cover_img_el->getObjectImageSource();
//				return imgsrc;
//			}
//		}
		return LVImageSourceRef(); // not found: return NULL ref
	}
	/// draws coverpage to image buffer
	void drawCoverTo(LVDrawBuf * drawBuf, lvRect & rc) ;
	void drawPageTo(LVDrawBuf * drawbuf, LVRendPageInfo & page,
			lvRect * pageRect, int pageCount, int basePage) ;

	void Draw(LVDrawBuf & drawbuf, int page) {
		drawPageTo(&drawbuf, *m_pages[page], &m_pageRects[0],
							m_pages.length(), 1);
	}
	/// render (format) document
    void Render( int dx=0, int dy=0, LVRendPageList * pages=NULL );
    void loadDocument(LVStreamRef stream);
    inline int getWidth() {return m_dx;}
    inline int getHeight() {return m_dy;}
};
#endif // EPUBFMT_H
