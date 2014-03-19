#ifndef EPUBFMT_H
#define EPUBFMT_H

#include "../include/crsetup.h"
#include "../include/lvtinydom.h"
#include "lvthread.h"
#include "lvpagesplitter.h"
#include <vector>
#include "lvref.h"

extern lString16 mergeCssMacros(CRPropRef props);
extern lString8 substituteCssMacros(lString8 src, CRPropRef props);
class EpubItem {
public:
	lString16 href;
	lString16 mediaType;
	lString16 id;
	lString16 title;
	EpubItem() {
	}
	EpubItem(const EpubItem & v) :
			href(v.href), mediaType(v.mediaType), id(v.id) {
	}
	EpubItem & operator =(const EpubItem & v) {
		href = v.href;
		mediaType = v.mediaType;
		id = v.id;
		return *this;
	}
};
struct EpubChapterPages:public LVRefCounter {
	EpubChapterPages():m_doc(NULL),start(0),bLoad(false),bRender(false){

	}
	LVRendPageList m_pages;
	ldomDocument *m_doc;
	int start;
	bool bLoad;
	bool bRender;
	EpubItem item;
	~EpubChapterPages() {
		if (m_doc) {
			delete m_doc;
		}
		m_doc = NULL;
	}
};
typedef LVFastRef<EpubChapterPages> EpubChapterPagesRef;
class EpubDocument {
public:
	typedef std::vector<EpubChapterPagesRef> EpubDocPagesContainer;

protected:
	LVMutex _mutex;
	lvRect m_pageRects[2];
	lvRect m_pageMargins;
	int m_dx;
	int m_dy;

	font_ref_t m_font;
	int m_font_size;
	lString8 m_defaultFontFace;

	// options
	CRPropRef m_props;
	CRPropRef m_doc_props;
	int m_def_interline_space;
	lString8 m_stylesheet;
	LVImageSourceRef m_defaultCover;
	// loadchapter
	lString16 codeBase;
	ldomDocument *temp_unknowndoc;
	lString16 ncxHref;
	LVContainerRef m_arc;
	EpubDocPagesContainer mDocumentPages;
public:
	EpubDocument();
	/// return view mutex
	LVMutex & getMutex() {
		return _mutex;
	}
	EpubChapterPagesRef& getChapterPages(int index) {
		EpubChapterPagesRef& p = mDocumentPages[index];
		loadChapter(p);
		Render(m_dx, m_dy, &p);
		return p;
	}
	inline int getChaptersCount() {
		return mDocumentPages.size();
	}
	void updateLayout() {
		lvRect rc(0, 0, m_dx, m_dy);
		m_pageRects[0] = rc;
		m_pageRects[1] = rc;
	}
	void updateDocStyleSheet() {
		CRPropRef p = m_props->getSubProps("styles.");
		//m_doc->setStyleSheet(substituteCssMacros(m_stylesheet, p).c_str(), true);
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
public:
	/// get current default cover image
	LVImageSourceRef getDefaultCover() const {
		return m_defaultCover;
	}
	/// returns cover page image source, if any
	LVImageSourceRef getCoverPageImage() {
		return LVImageSourceRef(); // not found: return NULL ref
	}
	/// draws coverpage to image buffer
	void drawCoverTo(LVDrawBuf * drawBuf, lvRect & rc);
	void drawPageTo(LVDrawBuf * drawbuf, ldomDocument * m_doc,
			LVRendPageInfo & page, lvRect * pageRect, int pageCount,
			int basePage);

	void Draw(LVDrawBuf & drawbuf, int chapterindex, int page) {
		LVLock lock(getMutex());
		std::vector<EpubChapterPages>::iterator it;
		CRLog::debug("song draw 1 %d %d",chapterindex, page);
		EpubChapterPagesRef &p = mDocumentPages[chapterindex];
		CRLog::debug("song draw 2 %d %d",chapterindex, page);
		LVRendPageInfo *pageinfo = p->m_pages[page];
		CRLog::debug("song draw 3");
		drawPageTo(&drawbuf, p->m_doc, *pageinfo, &m_pageRects[0],
				p->m_pages.length(), 1);
		return;
	}
	int getPageCount() {
		return mDocumentPages.back()->start
				+ mDocumentPages.back()->m_pages.length();
	}
	/// render (format) document
	void loadChapter(EpubChapterPagesRef item);
	void Render(int dx = 0, int dy = 0,EpubChapterPagesRef* chapterPages=NULL);
	void loadDocument(lString16& path);
	inline int getWidth() {
		return m_dx;
	}
	inline int getHeight() {
		return m_dy;
	}
	inline void setWidthHeight(int dx, int dy) {
		m_dx = dx;
		m_dy = dy;
	}
};
#endif // EPUBFMT_H
