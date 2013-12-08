#include "../include/epubfmt.h"
#include "lvstream.h"
#include "lvdocview.h"
#include "lvrend.h"
#include "lvpagesplitter.h"
class EpubItem {
public:
    lString16 href;
    lString16 mediaType;
    lString16 id;
    lString16 title;
    EpubItem()
    { }
    EpubItem( const EpubItem & v )
        : href(v.href), mediaType(v.mediaType), id(v.id)
    { }
    EpubItem & operator = ( const EpubItem & v )
    {
        href = v.href;
        mediaType = v.mediaType;
        id = v.id;
        return *this;
    }
};

class EpubItems : public LVPtrVector<EpubItem> {
public:
    EpubItem * findById( const lString16 & id )
    {
        if ( id.empty() )
            return NULL;
        for ( int i=0; i<length(); i++ )
            if ( get(i)->id == id )
                return get(i);
        return NULL;
    }
};

//static void dumpZip( LVContainerRef arc ) {
//    lString16 arcName = LVExtractFilenameWithoutExtension( arc->GetName() );
//    if ( arcName.empty() )
//        arcName = "unziparc";
//    lString16 outDir = cs16("/tmp/") + arcName;
//    LVCreateDirectory(outDir);
//    for ( int i=0; i<arc->GetObjectCount(); i++ ) {
//        const LVContainerItemInfo * info = arc->GetObjectInfo(i);
//        if ( !info->IsContainer() ) {
//            lString16 outFileName = outDir + "/" + info->GetName();
//            LVCreateDirectory(LVExtractPath(outFileName));
//            LVStreamRef in = arc->OpenStream(info->GetName(), LVOM_READ);
//            LVStreamRef out = LVOpenFileStream(outFileName.c_str(), LVOM_WRITE);
//            if ( !in.isNull() && !out.isNull() ) {
//                CRLog::trace("Writing %s", LCSTR(outFileName));
//                LVPumpStream(out.get(), in.get());
//            }
//        }
//    }
//}

bool DetectEpubFormat( LVStreamRef stream )
{


    LVContainerRef m_arc = LVOpenArchieve( stream );
    if ( m_arc.isNull() )
        return false; // not a ZIP archive

    //dumpZip( m_arc );

    // read "mimetype" file contents from root of archive
    lString16 mimeType;
    {
        LVStreamRef mtStream = m_arc->OpenStream(L"mimetype", LVOM_READ );
        if ( !mtStream.isNull() ) {
            int size = mtStream->GetSize();
            if ( size>4 && size<100 ) {
                LVArray<char> buf( size+1, '\0' );
                if ( mtStream->Read( buf.get(), size, NULL )==LVERR_OK ) {
                    for ( int i=0; i<size; i++ )
                        if ( buf[i]<32 || ((unsigned char)buf[i])>127 )
                            buf[i] = 0;
                    buf[size] = 0;
                    if ( buf[0] )
                        mimeType = Utf8ToUnicode( lString8( buf.get() ) );
                }
            }
        }
    }

    if ( mimeType != L"application/epub+zip" )
        return false;
    return true;
}

void ReadEpubToc( ldomDocument * doc, ldomNode * mapRoot, LVTocItem * baseToc, ldomDocumentFragmentWriter & appender ) {
    if ( !mapRoot || !baseToc)
        return;
    lUInt16 navPoint_id = mapRoot->getDocument()->getElementNameIndex(L"navPoint");
    lUInt16 navLabel_id = mapRoot->getDocument()->getElementNameIndex(L"navLabel");
    lUInt16 content_id = mapRoot->getDocument()->getElementNameIndex(L"content");
    lUInt16 text_id = mapRoot->getDocument()->getElementNameIndex(L"text");
    for ( int i=0; i<5000; i++ ) {
        ldomNode * navPoint = mapRoot->findChildElement(LXML_NS_ANY, navPoint_id, i);
        if ( !navPoint )
            break;
        ldomNode * navLabel = navPoint->findChildElement(LXML_NS_ANY, navLabel_id, -1);
        if ( !navLabel )
            continue;
        ldomNode * text = navLabel->findChildElement(LXML_NS_ANY, text_id, -1);
        if ( !text )
            continue;
        ldomNode * content = navPoint->findChildElement(LXML_NS_ANY, content_id, -1);
        if ( !content )
            continue;
        lString16 href = content->getAttributeValue("src");
        lString16 title = text->getText(' ');
        title.trimDoubleSpaces(false, false, false);
        if ( href.empty() || title.empty() )
            continue;
        //CRLog::trace("TOC href before convert: %s", LCSTR(href));
	href = DecodeHTMLUrlString(href);
        href = appender.convertHref(href);
        //CRLog::trace("TOC href after convert: %s", LCSTR(href));
        if ( href.empty() || href[0]!='#' )
            continue;
        ldomNode * target = doc->getNodeById(doc->getAttrValueIndex(href.substr(1).c_str()));
        if ( !target )
            continue;
        ldomXPointer ptr(target, 0);
        LVTocItem * tocItem = baseToc->addChild(title, ptr, lString16::empty_str);
        ReadEpubToc( doc, navPoint, tocItem, appender );
    }
}

lString16 EpubGetRootFilePath(LVContainerRef m_arc)
{
    // check root media type
    lString16 rootfilePath;
    lString16 rootfileMediaType;
    // read container.xml
    {
        LVStreamRef container_stream = m_arc->OpenStream(L"META-INF/container.xml", LVOM_READ);
        if ( !container_stream.isNull() ) {
            ldomDocument * doc = LVParseXMLStream( container_stream );
            if ( doc ) {
                ldomNode * rootfile = doc->nodeFromXPath( cs16("container/rootfiles/rootfile") );
                if ( rootfile && rootfile->isElement() ) {
                    rootfilePath = rootfile->getAttributeValue("full-path");
                    rootfileMediaType = rootfile->getAttributeValue("media-type");
                }
                delete doc;
            }
        }
    }

    if (rootfilePath.empty() || rootfileMediaType != "application/oebps-package+xml")
        return lString16::empty_str;
    return rootfilePath;
}

/// encrypted font demangling proxy: XORs first 1024 bytes of source stream with key
class FontDemanglingStream : public StreamProxy {
    LVArray<lUInt8> & _key;
public:
    FontDemanglingStream(LVStreamRef baseStream, LVArray<lUInt8> & key) : StreamProxy(baseStream), _key(key) {
    }

    virtual lverror_t Read( void * buf, lvsize_t count, lvsize_t * nBytesRead ) {
        lvpos_t pos = _base->GetPos();
        lverror_t res = _base->Read(buf, count, nBytesRead);
        if (pos < 1024 && _key.length() == 16) {
            for (int i=0; i + pos < 1024; i++) {
                int keyPos = (i + pos) & 15;
                ((lUInt8*)buf)[i] ^= _key[keyPos];
            }
        }
        return res;
    }

};

class EncryptedItem {
public:
    lString16 _uri;
    lString16 _method;
    EncryptedItem(lString16 uri, lString16 method) : _uri(uri), _method(method) {

    }
};

class EncryptedItemCallback {
public:
    virtual void addEncryptedItem(EncryptedItem * item) = 0;
    virtual ~EncryptedItemCallback() {}
};


class EncCallback : public LVXMLParserCallback {
    bool insideEncryption;
    bool insideEncryptedData;
    bool insideEncryptionMethod;
    bool insideCipherData;
    bool insideCipherReference;
public:
    /// called on opening tag <
    virtual ldomNode * OnTagOpen( const lChar16 * nsname, const lChar16 * tagname) {
        if (!lStr_cmp(tagname, "encryption"))
            insideEncryption = true;
        else if (!lStr_cmp(tagname, "EncryptedData"))
            insideEncryptedData = true;
        else if (!lStr_cmp(tagname, "EncryptionMethod"))
            insideEncryptionMethod = true;
        else if (!lStr_cmp(tagname, "CipherData"))
            insideCipherData = true;
        else if (!lStr_cmp(tagname, "CipherReference"))
            insideCipherReference = true;
		return NULL;
    }
    /// called on tag close
    virtual void OnTagClose( const lChar16 * nsname, const lChar16 * tagname ) {
        if (!lStr_cmp(tagname, "encryption"))
            insideEncryption = false;
        else if (!lStr_cmp(tagname, "EncryptedData") && insideEncryptedData) {
            if (!algorithm.empty() && !uri.empty()) {
                _container->addEncryptedItem(new EncryptedItem(uri, algorithm));
            }
            insideEncryptedData = false;
        } else if (!lStr_cmp(tagname, "EncryptionMethod"))
            insideEncryptionMethod = false;
        else if (!lStr_cmp(tagname, "CipherData"))
            insideCipherData = false;
        else if (!lStr_cmp(tagname, "CipherReference"))
            insideCipherReference = false;
    }
    /// called on element attribute
    virtual void OnAttribute( const lChar16 * nsname, const lChar16 * attrname, const lChar16 * attrvalue ) {
        if (!lStr_cmp(attrname, "URI") && insideCipherReference)
            insideEncryption = false;
        else if (!lStr_cmp(attrname, "Algorithm") && insideEncryptionMethod)
            insideEncryptedData = false;
    }
    /// called on text
    virtual void OnText( const lChar16 * text, int len, lUInt32 flags ) {

    }
    /// add named BLOB data to document
    virtual bool OnBlob(lString16 name, const lUInt8 * data, int size) { return false; }

    virtual void OnStop() { }
    /// called after > of opening tag (when entering tag body)
    virtual void OnTagBody() { }

    EncryptedItemCallback * _container;
    lString16 algorithm;
    lString16 uri;
    /// destructor
    EncCallback(EncryptedItemCallback * container) : _container(container) {
        insideEncryption = false;
        insideEncryptedData = false;
        insideEncryptionMethod = false;
        insideCipherData = false;
        insideCipherReference = false;
    }
    virtual ~EncCallback() {}
};

class EncryptedDataContainer : public LVContainer, public EncryptedItemCallback {
    LVContainerRef _container;
    LVPtrVector<EncryptedItem> _list;
public:
    EncryptedDataContainer(LVContainerRef baseContainer) : _container(baseContainer) {

    }

    virtual LVContainer * GetParentContainer() { return _container->GetParentContainer(); }
    //virtual const LVContainerItemInfo * GetObjectInfo(const wchar_t * pname);
    virtual const LVContainerItemInfo * GetObjectInfo(int index) { return _container->GetObjectInfo(index); }
    virtual int GetObjectCount() const { return _container->GetObjectCount(); }
    /// returns object size (file size or directory entry count)
    virtual lverror_t GetSize( lvsize_t * pSize ) { return _container->GetSize(pSize); }


    virtual LVStreamRef OpenStream( const lChar16 * fname, lvopen_mode_t mode ) {

        LVStreamRef res = _container->OpenStream(fname, mode);
        if (res.isNull())
            return res;
        if (isEncryptedItem(fname))
            return LVStreamRef(new FontDemanglingStream(res, _fontManglingKey));
        return res;
    }

    /// returns stream/container name, may be NULL if unknown
    virtual const lChar16 * GetName()
    {
        return _container->GetName();
    }
    /// sets stream/container name, may be not implemented for some objects
    virtual void SetName(const lChar16 * name)
    {
        _container->SetName(name);
    }


    virtual void addEncryptedItem(EncryptedItem * item) {
        _list.add(item);
    }

    EncryptedItem * findEncryptedItem(const lChar16 * name) {
        lString16 n;
        if (name[0] != '/' && name[0] != '\\')
            n << "/";
        n << name;
        for (int i=0; i<_list.length(); i++) {
            lString16 s = _list[i]->_uri;
            if (s[0]!='/' && s[i]!='\\')
                s = "/" + s;
            if (_list[i]->_uri == s)
                return _list[i];
        }
        return NULL;
    }

    bool isEncryptedItem(const lChar16 * name) {
        return findEncryptedItem(name) != NULL;
    }

    LVArray<lUInt8> _fontManglingKey;

    bool setManglingKey(lString16 key) {
        if (key.startsWith("urn:uuid:"))
            key = key.substr(9);
        _fontManglingKey.clear();
        _fontManglingKey.reserve(16);
        lUInt8 b = 0;
        int n = 0;
        for (int i=0; i<key.length(); i++) {
            int d = hexDigit(key[i]);
            if (d>=0) {
                b = (b << 4) | d;
                if (++n > 1) {
                    _fontManglingKey.add(b);
                    n = 0;
                    b = 0;
                }
            }
        }
        return _fontManglingKey.length() == 16;
    }

    bool hasUnsupportedEncryption() {
        for (int i=0; i<_list.length(); i++) {
            lString16 method = _list[i]->_method;
            if (method != "http://ns.adobe.com/pdf/enc#RC") {
                CRLog::debug("unsupported encryption method: %s", LCSTR(method));
                return true;
            }
        }
        return false;
    }

    bool open() {
        LVStreamRef stream = _container->OpenStream(L"META-INF/encryption.xml", LVOM_READ);
        if (stream.isNull())
            return false;
        EncCallback enccallback(this);
        LVXMLParser parser(stream, &enccallback, false, false);
        if (!parser.Parse())
            return false;
        if (_list.length())
            return true;
        return false;
    }
};

void createEncryptedEpubWarningDocument(ldomDocument * m_doc) {
    CRLog::error("EPUB document contains encrypted items");
    ldomDocumentWriter writer(m_doc);
    writer.OnTagOpenNoAttr(NULL, L"body");
    writer.OnTagOpenNoAttr(NULL, L"h3");
    lString16 hdr("Encrypted content");
    writer.OnText(hdr.c_str(), hdr.length(), 0);
    writer.OnTagClose(NULL, L"h3");

    writer.OnTagOpenAndClose(NULL, L"hr");

    writer.OnTagOpenNoAttr(NULL, L"p");
    lString16 txt("This document is encrypted (has DRM protection).");
    writer.OnText(txt.c_str(), txt.length(), 0);
    writer.OnTagClose(NULL, L"p");

    writer.OnTagOpenNoAttr(NULL, L"p");
    lString16 txt2("Cool Reader doesn't support reading of DRM protected books.");
    writer.OnText(txt2.c_str(), txt2.length(), 0);
    writer.OnTagClose(NULL, L"p");

    writer.OnTagOpenNoAttr(NULL, L"p");
    lString16 txt3("To read this book, please use software recommended by book seller.");
    writer.OnText(txt3.c_str(), txt3.length(), 0);
    writer.OnTagClose(NULL, L"p");

    writer.OnTagOpenAndClose(NULL, L"hr");

    writer.OnTagOpenNoAttr(NULL, L"p");
    lString16 txt4("");
    writer.OnText(txt4.c_str(), txt4.length(), 0);
    writer.OnTagClose(NULL, L"p");

    writer.OnTagClose(NULL, L"body");
}

LVStreamRef GetEpubCoverpage(LVContainerRef arc)
{
    // check root media type
    lString16 rootfilePath = EpubGetRootFilePath(arc);
    if ( rootfilePath.empty() )
        return LVStreamRef();

    EncryptedDataContainer * decryptor = new EncryptedDataContainer(arc);
    if (decryptor->open()) {
        CRLog::debug("EPUB: encrypted items detected");
    }

    LVContainerRef m_arc = LVContainerRef(decryptor);

    lString16 codeBase = LVExtractPath(rootfilePath, false);
    CRLog::trace("codeBase=%s", LCSTR(codeBase));

    LVStreamRef content_stream = m_arc->OpenStream(rootfilePath.c_str(), LVOM_READ);
    if ( content_stream.isNull() )
        return LVStreamRef();


    LVStreamRef coverPageImageStream;
    // reading content stream
    {
        lString16 coverId;
        ldomDocument * doc = LVParseXMLStream( content_stream );
        if ( !doc )
            return LVStreamRef();

        for ( int i=1; i<20; i++ ) {
            ldomNode * item = doc->nodeFromXPath(lString16("package/metadata/meta[") << fmt::decimal(i) << "]");
            if ( !item )
                break;
            lString16 name = item->getAttributeValue("name");
            lString16 content = item->getAttributeValue("content");
            if (name == "cover")
                coverId = content;
        }

        // items
        for ( int i=1; i<50000; i++ ) {
            ldomNode * item = doc->nodeFromXPath(lString16("package/manifest/item[") << fmt::decimal(i) << "]");
            if ( !item )
                break;
            lString16 href = item->getAttributeValue("href");
            lString16 id = item->getAttributeValue("id");
            if ( !href.empty() && !id.empty() ) {
                if (id == coverId) {
                    // coverpage file
                    lString16 coverFileName = codeBase + href;
                    CRLog::info("EPUB coverpage file: %s", LCSTR(coverFileName));
                    coverPageImageStream = m_arc->OpenStream(coverFileName.c_str(), LVOM_READ);
                }
            }
        }
        delete doc;
    }

    return coverPageImageStream;
}


class EmbeddedFontStyleParser {
    LVEmbeddedFontList & _fontList;
    lString16 _basePath;
    int _state;
    lString8 _face;
    bool _italic;
    bool _bold;
    lString16 _url;
public:
    EmbeddedFontStyleParser(LVEmbeddedFontList & fontList) : _fontList(fontList) { }
    void onToken(char token) {
        // 4,5:  font-family:
        // 6,7:  font-weight:
        // 8,9:  font-style:
        //10,11: src:
        //   10   11    12   13
        //   src   :   url    (
        //CRLog::trace("state==%d: %c ", _state, token);
        switch (token) {
        case ':':
            if (_state < 2) {
                _state = 0;
            } else if (_state == 4 || _state == 6 || _state == 8 || _state == 10) {
                _state++;
            } else if (_state != 3) {
                _state = 2;
            }
            break;
        case ';':
            if (_state < 2) {
                _state = 0;
            } else if (_state != 3) {
                _state = 2;
            }
            break;
        case '{':
            if (_state == 1) {
                _state = 2; // inside @font {
                _face.clear();
                _italic = false;
                _bold = false;
                _url.clear();
            } else
                _state = 3; // inside other {
            break;
        case '}':
            if (_state == 2) {
                if (!_url.empty()) {
//                    CRLog::trace("@font { face: %s; bold: %s; italic: %s; url: %s", _face.c_str(), _bold ? "yes" : "no",
//                                 _italic ? "yes" : "no", LCSTR(_url));
                    _fontList.add(_url, _face, _bold, _italic);
                }
            }
            _state = 0;
            break;
        case '(':
            if (_state == 12) {
                _state = 13;
            } else {
                if (_state > 3)
                    _state = 2;
            }
            break;
        }
    }
    void onToken(lString8 & token) {
        if (token.empty())
            return;
        lString8 t = token;
        token.clear();
        //CRLog::trace("state==%d: %s", _state, t.c_str());
        if (t == "@font-face") {
            if (_state == 0)
                _state = 1; // right after @font
            return;
        }
        if (_state == 1)
            _state = 0;
        if (_state == 2) {
            if (t == "font-family")
                _state = 4;
            else if (t == "font-weight")
                _state = 6;
            else if (t == "font-style")
                _state = 8;
            else if (t == "src")
                _state = 10;
        } else if (_state == 5) {
            _face = t;
            _state = 2;
        } else if (_state == 7) {
            if (t == "bold")
                _bold = true;
            _state = 2;
        } else if (_state == 9) {
            if (t == "italic")
                _italic = true;
            _state = 2;
        } else if (_state == 11) {
            if (t == "url")
                _state = 12;
            else
                _state = 2;
        }
    }
    void onQuotedText(lString8 & token) {
        //CRLog::trace("state==%d: \"%s\"", _state, token.c_str());
        if (_state == 11 || _state == 13) {
            if (!token.empty()) {
                _url = LVCombinePaths(_basePath, Utf8ToUnicode(token));
            }
            _state = 2;
        } else if (_state == 5) {
            if (!token.empty()) {
                _face = token;
            }
            _state = 2;
        }
        token.clear();
    }

    void parse(lString16 basePath, const lString8 & css) {
        _state = 0;
        _basePath = basePath;
        lString8 token;
        char insideQuotes = 0;
        for (int i=0; i<css.length(); i++) {
            char ch = css[i];
            if (insideQuotes || _state == 13) {
                if (ch == insideQuotes || (_state == 13 && ch == ')')) {
                    onQuotedText(token);
                    insideQuotes =  0;
                    if (_state == 13)
                        onToken(ch);
                } else {
                    if (_state == 13 && token.empty() && (ch == '\'' || ch=='\"')) {
                        insideQuotes = ch;
                    } else if (ch != ' ' || _state != 13)
                        token << ch;
                }
                continue;
            }
            if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n') {
                onToken(token);
            } else if (ch == '@' || ch=='-' || ch=='_' || ch=='.' || (ch>='a' && ch <='z') || (ch>='A' && ch <='Z') || (ch>='0' && ch <='9')) {
                token << ch;
            } else if (ch == ':' || ch=='{' || ch == '}' || ch=='(' || ch == ')' || ch == ';') {
                onToken(token);
                onToken(ch);
            } else if (ch == '\'' || ch == '\"') {
                onToken(token);
                insideQuotes = ch;
            }
        }
    }
};

bool ImportEpubDocument( LVStreamRef stream, ldomDocument * m_doc, LVDocViewCallback * progressCallback, CacheLoadingCallback * formatCallback )
{
    LVContainerRef arc = LVOpenArchieve( stream );
    if ( arc.isNull() )
        return false; // not a ZIP archive

    // check root media type
    lString16 rootfilePath = EpubGetRootFilePath(arc);
    if ( rootfilePath.empty() )
    	return false;

    EncryptedDataContainer * decryptor = new EncryptedDataContainer(arc);
    if (decryptor->open()) {
        CRLog::debug("EPUB: encrypted items detected");
    }

    LVContainerRef m_arc = LVContainerRef(decryptor);

    if (decryptor->hasUnsupportedEncryption()) {
        // DRM!!!
        createEncryptedEpubWarningDocument(m_doc);
        return true;
    }

    m_doc->setContainer(m_arc);

    // read content.opf
    EpubItems epubItems;
    //EpubItem * epubToc = NULL; //TODO
    LVArray<EpubItem*> spineItems;
    lString16 codeBase;
    //lString16 css;

    //
    {
        codeBase=LVExtractPath(rootfilePath, false);
        CRLog::trace("codeBase=%s", LCSTR(codeBase));
    }

    LVStreamRef content_stream = m_arc->OpenStream(rootfilePath.c_str(), LVOM_READ);
    if ( content_stream.isNull() )
        return false;


    lString16 ncxHref;
    lString16 coverId;

    LVEmbeddedFontList fontList;
    EmbeddedFontStyleParser styleParser(fontList);

    // reading content stream
    {
        ldomDocument * doc = LVParseXMLStream( content_stream );
        if ( !doc )
            return false;

//        // for debug
//        {
//            LVStreamRef out = LVOpenFileStream("/tmp/content.xml", LVOM_WRITE);
//            doc->saveToStream(out, NULL, true);
//        }

        CRPropRef m_doc_props = m_doc->getProps();
        lString16 author = doc->textFromXPath( cs16("package/metadata/creator"));
        lString16 title = doc->textFromXPath( cs16("package/metadata/title"));
        lString16 language = doc->textFromXPath( cs16("package/metadata/language"));
        m_doc_props->setString(DOC_PROP_TITLE, title);
        m_doc_props->setString(DOC_PROP_LANGUAGE, language);
        m_doc_props->setString(DOC_PROP_AUTHORS, author );

        for ( int i=1; i<50; i++ ) {
            ldomNode * item = doc->nodeFromXPath(lString16("package/metadata/identifier[") << fmt::decimal(i) << "]");
            if (!item)
                break;
            lString16 key = item->getText();
            if (decryptor->setManglingKey(key)) {
                CRLog::debug("Using font mangling key %s", LCSTR(key));
                break;
            }
        }

        CRLog::info("Author: %s Title: %s", LCSTR(author), LCSTR(title));
        for ( int i=1; i<20; i++ ) {
            ldomNode * item = doc->nodeFromXPath(lString16("package/metadata/meta[") << fmt::decimal(i) << "]");
            if ( !item )
                break;
            lString16 name = item->getAttributeValue("name");
            lString16 content = item->getAttributeValue("content");
            if (name == "cover")
                coverId = content;
            else if (name == "calibre:series")
                m_doc_props->setString(DOC_PROP_SERIES_NAME, content );
            else if (name == "calibre:series_index")
                m_doc_props->setInt(DOC_PROP_SERIES_NUMBER, content.atoi() );
        }

        // items
        for ( int i=1; i<50000; i++ ) {
            ldomNode * item = doc->nodeFromXPath(lString16("package/manifest/item[") << fmt::decimal(i) << "]");
            if ( !item )
                break;
            lString16 href = item->getAttributeValue("href");
            lString16 mediaType = item->getAttributeValue("media-type");
            lString16 id = item->getAttributeValue("id");
            if ( !href.empty() && !id.empty() ) {
                href = DecodeHTMLUrlString(href);
                if ( id==coverId ) {
                    // coverpage file
                    lString16 coverFileName = codeBase + href;
                    CRLog::info("EPUB coverpage file: %s", LCSTR(coverFileName));
                    LVStreamRef stream = m_arc->OpenStream(coverFileName.c_str(), LVOM_READ);
                    if ( !stream.isNull() ) {
                        LVImageSourceRef img = LVCreateStreamImageSource(stream);
                        if ( !img.isNull() ) {
                            CRLog::info("EPUB coverpage image is correct: %d x %d", img->GetWidth(), img->GetHeight() );
                            m_doc_props->setString(DOC_PROP_COVER_FILE, coverFileName);
                        }
                    }
                }
                EpubItem * epubItem = new EpubItem;
                epubItem->href = href;
                epubItem->id = id;
                epubItem->mediaType = mediaType;
                epubItems.add( epubItem );

//                // register embedded document fonts
//                if (mediaType == L"application/vnd.ms-opentype"
//                        || mediaType == L"application/x-font-otf"
//                        || mediaType == L"application/x-font-ttf") { // TODO: more media types?
//                    // TODO:
//                    fontList.add(codeBase + href);
//                }
            }
            if (mediaType == "text/css") {
                lString16 name = LVCombinePaths(codeBase, href);
                LVStreamRef cssStream = m_arc->OpenStream(name.c_str(), LVOM_READ);
                if (!cssStream.isNull()) {
                    lString8 cssFile = UnicodeToUtf8(LVReadTextFile(cssStream));
                    lString16 base = name;
                    LVExtractLastPathElement(base);
                    //CRLog::trace("style: %s", cssFile.c_str());
                    styleParser.parse(base, cssFile);
                }
            }
        }

        // spine == itemrefs
        if ( epubItems.length()>0 ) {
            ldomNode * spine = doc->nodeFromXPath( cs16("package/spine") );
            if ( spine ) {

                EpubItem * ncx = epubItems.findById( spine->getAttributeValue("toc") ); //TODO
                //EpubItem * ncx = epubItems.findById(cs16("ncx"));
                if ( ncx!=NULL )
                    ncxHref = codeBase + ncx->href;

                for ( int i=1; i<50000; i++ ) {
                    ldomNode * item = doc->nodeFromXPath(lString16("package/spine/itemref[") << fmt::decimal(i) << "]");
                    if ( !item )
                        break;
                    EpubItem * epubItem = epubItems.findById( item->getAttributeValue("idref") );
                    if ( epubItem ) {
                        // TODO: add to document
                        spineItems.add( epubItem );
                    }
                }
            }
        }
        delete doc;
    }

    if ( spineItems.length()==0 )
        return false;


#if BUILD_LITE!=1
    if ( m_doc->openFromCache(formatCallback) ) {
        if ( progressCallback ) {
            progressCallback->OnLoadFileEnd( );
        }
        return true;
    }
#endif

    lUInt32 saveFlags = m_doc->getDocFlags();
    m_doc->setDocFlags( saveFlags );
    m_doc->setContainer( m_arc );

    ldomDocumentWriter writer(m_doc);
#if 0
    m_doc->setNodeTypes( fb2_elem_table );
    m_doc->setAttributeTypes( fb2_attr_table );
    m_doc->setNameSpaceTypes( fb2_ns_table );
#endif
    //m_doc->setCodeBase( codeBase );

    ldomDocumentFragmentWriter appender(&writer, cs16("body"), cs16("DocFragment"), lString16::empty_str );
    writer.OnStart(NULL);
    writer.OnTagOpenNoAttr(L"", L"body");
    int fragmentCount = 0;
    for ( int i=0; i<spineItems.length(); i++ ) {
        if (spineItems[i]->mediaType == "application/xhtml+xml") {
            lString16 name = codeBase + spineItems[i]->href;
            lString16 subst = cs16("_doc_fragment_") + fmt::decimal(i);
            appender.addPathSubstitution( name, subst );
            //CRLog::trace("subst: %s => %s", LCSTR(name), LCSTR(subst));
        }
    }
    for ( int i=0; i<spineItems.length(); i++ ) {
        if (spineItems[i]->mediaType == "application/xhtml+xml") {
            lString16 name = codeBase + spineItems[i]->href;
            {
                CRLog::debug("Checking fragment: %s", LCSTR(name));
                LVStreamRef stream = m_arc->OpenStream(name.c_str(), LVOM_READ);
                if ( !stream.isNull() ) {
                    appender.setCodeBase( name );
                    lString16 base = name;
                    LVExtractLastPathElement(base);
                    //CRLog::trace("base: %s", LCSTR(base));
                    //LVXMLParser
                    LVHTMLParser parser(stream, &appender);
                    if ( parser.CheckFormat() && parser.Parse() ) {
                        // valid
                        fragmentCount++;
                        lString8 headCss = appender.getHeadStyleText();
                        //CRLog::trace("style: %s", headCss.c_str());
                        styleParser.parse(base, headCss);
                    } else {
                        CRLog::error("Document type is not XML/XHTML for fragment %s", LCSTR(name));
                    }
                }
            }
        }
    }

    if ( !ncxHref.empty() ) {
        LVStreamRef stream = m_arc->OpenStream(ncxHref.c_str(), LVOM_READ);
        lString16 codeBase = LVExtractPath( ncxHref );
        if ( codeBase.length()>0 && codeBase.lastChar()!='/' )
            codeBase.append(1, L'/');
        appender.setCodeBase(codeBase);
        if ( !stream.isNull() ) {
            ldomDocument * ncxdoc = LVParseXMLStream( stream );
            if ( ncxdoc!=NULL ) {
                ldomNode * navMap = ncxdoc->nodeFromXPath( cs16("ncx/navMap"));
                if ( navMap!=NULL )
                    ReadEpubToc( m_doc, navMap, m_doc->getToc(), appender );
                delete ncxdoc;
            }
        }
    }

    writer.OnTagClose(L"", L"body");
    writer.OnStop();
    CRLog::debug("EPUB: %d documents merged", fragmentCount);

    if (!fontList.empty()) {
        // set document font list, and register fonts
        m_doc->getEmbeddedFontList().set(fontList);
        m_doc->registerEmbeddedFonts();
        m_doc->forceReinitStyles();
    }

    if ( fragmentCount==0 )
        return false;

#if 0
    // set stylesheet
    //m_doc->getStyleSheet()->clear();
    m_doc->setStyleSheet( NULL, true );
    //m_doc->getStyleSheet()->parse(m_stylesheet.c_str());
    if ( !css.empty() && m_doc->getDocFlag(DOC_FLAG_ENABLE_INTERNAL_STYLES) ) {

        m_doc->setStyleSheet( "p.p { text-align: justify }\n"
            "svg { text-align: center }\n"
            "i { display: inline; font-style: italic }\n"
            "b { display: inline; font-weight: bold }\n"
            "abbr { display: inline }\n"
            "acronym { display: inline }\n"
            "address { display: inline }\n"
            "p.title-p { hyphenate: none }\n"
//abbr, acronym, address, blockquote, br, cite, code, dfn, div, em, h1, h2, h3, h4, h5, h6, kbd, p, pre, q, samp, span, strong, var
        , false);
        m_doc->setStyleSheet( UnicodeToUtf8(css).c_str(), false );
        //m_doc->getStyleSheet()->parse(UnicodeToUtf8(css).c_str());
    } else {
        //m_doc->getStyleSheet()->parse(m_stylesheet.c_str());
        //m_doc->setStyleSheet( m_stylesheet.c_str(), false );
    }
#endif
#if 0
    LVStreamRef out = LVOpenFileStream( L"c:\\doc.xml" , LVOM_WRITE );
    if ( !out.isNull() )
        m_doc->saveToStream( out, "utf-8" );
#endif

    // DONE!
    if ( progressCallback ) {
        progressCallback->OnLoadFileEnd( );
        m_doc->compact();
        m_doc->dumpStatistics();
    }

    // save compound XML document, for testing:
    m_doc->saveToStream(LVOpenFileStream("/sdcard//epub_dump.xml", LVOM_WRITE), NULL, true);

    return true;

}

// song

static const char * DEFAULT_FONT_NAME = "Droid Sans Fallback"; //Times New Roman";
static const char * DEFAULT_STATUS_FONT_NAME =
		"Droid Sans Fallback"; //Times New Roman";
static css_font_family_t DEFAULT_FONT_FAMILY = css_ff_sans_serif;
EpubDocument::EpubDocument(){
		m_doc = NULL;
		m_dx = 0;
		m_dy = 0;
		m_font_size = 1<<6;
		m_defaultFontFace=lString8(DEFAULT_FONT_NAME);
		m_props = LVCreatePropsContainer();
		m_doc_props =LVCreatePropsContainer();
		m_is_rendered = false;
		m_def_interline_space = 100;
	}
void EpubDocument::setRenderProps(int dx, int dy) {
	if (!m_doc || m_doc->getRootNode() == NULL)
		return;
	m_dx = dx;
	m_dy = dy;
	updateLayout();

	if (dx == 0)
		dx = m_pageRects[0].width() - m_pageMargins.left - m_pageMargins.right;
	if (dy == 0)
		dy = m_pageRects[0].height() - m_pageMargins.top - m_pageMargins.bottom;

	lString8 fontName = lString8(DEFAULT_FONT_NAME);
	m_font = fontMan->GetFont(m_font_size, 400 + LVRendGetFontEmbolden(),
			false, DEFAULT_FONT_FAMILY, m_defaultFontFace);
	//m_font = LVCreateFontTransform( m_font, LVFONT_TRANSFORM_EMBOLDEN );
	if (!m_font)
		return;

    updateDocStyleSheet();

    m_doc->setRenderProps(dx, dy, false,  0, m_font, m_def_interline_space, m_props);
    text_highlight_options_t h;
    h.bookmarkHighlightMode = m_props->getIntDef(PROP_HIGHLIGHT_COMMENT_BOOKMARKS, highlight_mode_underline);
    h.selectionColor = (m_props->getColorDef(PROP_HIGHLIGHT_SELECTION_COLOR, 0xC0C0C0) & 0xFFFFFF);
    h.commentColor = (m_props->getColorDef(PROP_HIGHLIGHT_BOOKMARK_COLOR_COMMENT, 0xA08000) & 0xFFFFFF);
    h.correctionColor = (m_props->getColorDef(PROP_HIGHLIGHT_BOOKMARK_COLOR_CORRECTION, 0xA00000) & 0xFFFFFF);
    m_doc->setHightlightOptions(h);
}

void EpubDocument::drawCoverTo(LVDrawBuf * drawBuf, lvRect & rc) {
		if (rc.width() < 130 || rc.height() < 130)
			return;
		int base_font_size = 16;
		int w = rc.width();
		if (w < 200)
			base_font_size = 16;
		else if (w < 300)
			base_font_size = 18;
		else if (w < 500)
			base_font_size = 20;
		else if (w < 700)
			base_font_size = 22;
		else
			base_font_size = 24;
		//CRLog::trace("drawCoverTo() - loading fonts...");
		LVFontRef author_fnt(fontMan->GetFont(base_font_size, 700, false,
	            css_ff_serif, cs8("Times New Roman")));
		LVFontRef title_fnt(fontMan->GetFont(base_font_size + 4, 700, false,
	            css_ff_serif, cs8("Times New Roman")));
		LVFontRef series_fnt(fontMan->GetFont(base_font_size - 3, 400, true,
	            css_ff_serif, cs8("Times New Roman")));
		lString16 authors = getAuthors();
		lString16 title = getTitle();
		lString16 series = getSeries();
		if (title.empty())
	        title = "no title";
		LFormattedText txform;
		if (!authors.empty())
			txform.AddSourceLine(authors.c_str(), authors.length(), 0xFFFFFFFF,
					0xFFFFFFFF, author_fnt.get(), LTEXT_ALIGN_CENTER, 18);
		txform.AddSourceLine(title.c_str(), title.length(), 0xFFFFFFFF, 0xFFFFFFFF,
				title_fnt.get(), LTEXT_ALIGN_CENTER, 18);
		if (!series.empty())
			txform.AddSourceLine(series.c_str(), series.length(), 0xFFFFFFFF,
					0xFFFFFFFF, series_fnt.get(), LTEXT_ALIGN_CENTER, 18);
		int title_w = rc.width() - rc.width() / 4;
		int h = txform.Format((lUInt16)title_w, (lUInt16)rc.height());

		lvRect imgrc = rc;

		//CRLog::trace("drawCoverTo() - getting cover image");
		LVImageSourceRef imgsrc = getCoverPageImage();
		LVImageSourceRef defcover = getDefaultCover();
		if (!imgsrc.isNull() && imgrc.height() > 30) {
	#ifdef NO_TEXT_IN_COVERPAGE
			h = 0;
	#endif
			if (h)
				imgrc.bottom -= h + 16;
			//fprintf( stderr, "Writing coverpage image...\n" );
			int src_dx = imgsrc->GetWidth();
			int src_dy = imgsrc->GetHeight();
			int scale_x = imgrc.width() * 0x10000 / src_dx;
			int scale_y = imgrc.height() * 0x10000 / src_dy;
			if (scale_x < scale_y)
				scale_y = scale_x;
			else
				scale_x = scale_y;
			int dst_dx = (src_dx * scale_x) >> 16;
			int dst_dy = (src_dy * scale_y) >> 16;
	        if (dst_dx > rc.width() * 6 / 8)
				dst_dx = imgrc.width();
	        if (dst_dy > rc.height() * 6 / 8)
				dst_dy = imgrc.height();
			//CRLog::trace("drawCoverTo() - drawing image");
	        LVColorDrawBuf buf2(src_dx, src_dy, 32);
	        buf2.Draw(imgsrc, 0, 0, src_dx, src_dy, true);
	        drawBuf->DrawRescaled(&buf2, imgrc.left + (imgrc.width() - dst_dx) / 2,
	                imgrc.top + (imgrc.height() - dst_dy) / 2, dst_dx, dst_dy, 0);
		} else if (!defcover.isNull()) {
			if (h)
				imgrc.bottom -= h + 16;
			// draw default cover with title at center
			imgrc = rc;
			int src_dx = defcover->GetWidth();
			int src_dy = defcover->GetHeight();
			int scale_x = imgrc.width() * 0x10000 / src_dx;
			int scale_y = imgrc.height() * 0x10000 / src_dy;
			if (scale_x < scale_y)
				scale_y = scale_x;
			else
				scale_x = scale_y;
			int dst_dx = (src_dx * scale_x) >> 16;
			int dst_dy = (src_dy * scale_y) >> 16;
			if (dst_dx > rc.width() - 10)
				dst_dx = imgrc.width();
			if (dst_dy > rc.height() - 10)
				dst_dy = imgrc.height();
			//CRLog::trace("drawCoverTo() - drawing image");
			drawBuf->Draw(defcover, imgrc.left + (imgrc.width() - dst_dx) / 2,
					imgrc.top + (imgrc.height() - dst_dy) / 2, dst_dx, dst_dy);
			//CRLog::trace("drawCoverTo() - drawing text");
			txform.Draw(drawBuf, (rc.right + rc.left - title_w) / 2, (rc.bottom
					+ rc.top - h) / 2, NULL);
			//CRLog::trace("drawCoverTo() - done");
			return;
		} else {
			imgrc.bottom = imgrc.top;
		}
		rc.top = imgrc.bottom;
		//CRLog::trace("drawCoverTo() - drawing text");
		if (h)
			txform.Draw(drawBuf, (rc.right + rc.left - title_w) / 2, (rc.bottom
					+ rc.top - h) / 2, NULL);
		//CRLog::trace("drawCoverTo() - done");
	}
void EpubDocument::drawPageTo(LVDrawBuf * drawbuf, LVRendPageInfo & page,
			lvRect * pageRect, int pageCount, int basePage) {
		CRLog::debug("song epub drawpage %d %d %d %d %d %d",page.start,page.height, pageRect->width(),pageRect->height(), pageCount,basePage);
		int start = page.start;
		int height = page.height;
		//CRLog::trace("drawPageTo(%d,%d)", start, height);
		lvRect fullRect(0, 0, drawbuf->GetWidth(), drawbuf->GetHeight());
		if (!pageRect)
			pageRect = &fullRect;
	    //drawbuf->setHidePartialGlyphs(getViewMode()==DVM_PAGES);
		drawbuf->setHidePartialGlyphs(true);
		//int offset = (pageRect->height() - m_pageMargins.top - m_pageMargins.bottom - height) / 3;
		//if (offset>16)
		//    offset = 16;
		//if (offset<0)
		//    offset = 0;
		int offset = 0;
		lvRect clip;
		clip.left = pageRect->left + m_pageMargins.left;
		clip.top = pageRect->top + m_pageMargins.top + offset;
		clip.bottom = pageRect->top + m_pageMargins.top + height
				+ offset;
		clip.right = pageRect->left + pageRect->width() - m_pageMargins.right;
		if (page.type == PAGE_TYPE_COVER)
			clip.top = pageRect->top + m_pageMargins.top;

		drawbuf->SetClipRect(&clip);
		if (m_doc) {
			if (page.type == PAGE_TYPE_COVER) {
				lvRect rc = *pageRect;
				drawbuf->SetClipRect(&rc);
				//if ( m_pageMargins.bottom > m_pageMargins.top )
				//    rc.bottom -= m_pageMargins.bottom - m_pageMargins.top;
				/*
				 rc.left += m_pageMargins.left / 2;
				 rc.top += m_pageMargins.bottom / 2;
				 rc.right -= m_pageMargins.right / 2;
				 rc.bottom -= m_pageMargins.bottom / 2;
				 */
				//CRLog::trace("Entering drawCoverTo()");
				drawCoverTo(drawbuf, rc);
			} else {
				// draw main page text
				//CRLog::trace("Entering DrawDocument()");
				if (page.height)
					DrawDocument(*drawbuf, m_doc->getRootNode(), pageRect->left
							+ m_pageMargins.left, clip.top, pageRect->width()
							- m_pageMargins.left - m_pageMargins.right, height, 0,
	                                                -start + offset, m_dy, NULL, NULL);
				//CRLog::trace("Done DrawDocument() for main text");
				// draw footnotes
	#define FOOTNOTE_MARGIN 8
				int fny = clip.top + (page.height ? page.height + FOOTNOTE_MARGIN
						: FOOTNOTE_MARGIN);
				int fy = fny;
				bool footnoteDrawed = false;
				for (int fn = 0; fn < page.footnotes.length(); fn++) {
					int fstart = page.footnotes[fn].start;
					int fheight = page.footnotes[fn].height;
					clip.top = fy + offset;
					clip.left = pageRect->left + m_pageMargins.left;
					clip.right = pageRect->right - m_pageMargins.right;
					clip.bottom = fy + offset + fheight;
					drawbuf->SetClipRect(&clip);
					DrawDocument(*drawbuf, m_doc->getRootNode(), pageRect->left
							+ m_pageMargins.left, fy + offset, pageRect->width()
							- m_pageMargins.left - m_pageMargins.right, fheight, 0,
							-fstart + offset, m_dy, NULL);
					footnoteDrawed = true;
					fy += fheight;
				}
				if (footnoteDrawed) { // && page.height
					fny -= FOOTNOTE_MARGIN / 2;
					drawbuf->SetClipRect(NULL);
	                lUInt32 cl = drawbuf->GetTextColor();
	                cl = (cl & 0xFFFFFF) | (0x55000000);
					drawbuf->FillRect(pageRect->left + m_pageMargins.left, fny,
							pageRect->right - m_pageMargins.right, fny + 1,
	                        cl);
				}
			}
		}
		drawbuf->SetClipRect(NULL);
	#ifdef SHOW_PAGE_RECT
		drawbuf->FillRect(pageRect->left, pageRect->top, pageRect->left+1, pageRect->bottom, 0xAAAAAA);
		drawbuf->FillRect(pageRect->left, pageRect->top, pageRect->right, pageRect->top+1, 0xAAAAAA);
		drawbuf->FillRect(pageRect->right-1, pageRect->top, pageRect->right, pageRect->bottom, 0xAAAAAA);
		drawbuf->FillRect(pageRect->left, pageRect->bottom-1, pageRect->right, pageRect->bottom, 0xAAAAAA);
		drawbuf->FillRect(pageRect->left+m_pageMargins.left, pageRect->top+m_pageMargins.top+headerHeight, pageRect->left+1+m_pageMargins.left, pageRect->bottom-m_pageMargins.bottom, 0x555555);
		drawbuf->FillRect(pageRect->left+m_pageMargins.left, pageRect->top+m_pageMargins.top+headerHeight, pageRect->right-m_pageMargins.right, pageRect->top+1+m_pageMargins.top+headerHeight, 0x555555);
		drawbuf->FillRect(pageRect->right-1-m_pageMargins.right, pageRect->top+m_pageMargins.top+headerHeight, pageRect->right-m_pageMargins.right, pageRect->bottom-m_pageMargins.bottom, 0x555555);
		drawbuf->FillRect(pageRect->left+m_pageMargins.left, pageRect->bottom-1-m_pageMargins.bottom, pageRect->right-m_pageMargins.right, pageRect->bottom-m_pageMargins.bottom, 0x555555);
	#endif

	#if 0
		lString16 pagenum = lString16::itoa( page.index+1 );
		m_font->DrawTextString(drawbuf, 5, 0 , pagenum.c_str(), pagenum.length(), '?', NULL, false); //drawbuf->GetHeight()-m_font->getHeight()
	#endif
	}
void EpubDocument::Render( int dx, int dy, LVRendPageList * pages ) {

    	LVLock lock(getMutex());
    	{	CRLog::debug("song epub render1");
    		if (!m_doc || m_doc->getRootNode() == NULL)
    			return;
    		CRLog::debug("song epub render2");
    		if (dx == 0)
    			dx = m_pageRects[0].width() - m_pageMargins.left
    					- m_pageMargins.right;
    		if (dy == 0)
    			dy = m_pageRects[0].height() - m_pageMargins.top
    					- m_pageMargins.bottom;
    		CRLog::debug("song epub render3 %d %d",dx,dy);
    		setRenderProps(dx, dy);
    		CRLog::debug("song epub render4");
    		if (pages == NULL)
    			pages = &m_pages;
    		CRLog::debug("song epub render5");
    		if (!m_font)
    			return;
    		CRLog::debug("song epub render6");
    		CRLog::debug("Render(width=%d, height=%d, fontSize=%d)", dx, dy,
    				m_font_size);
    		//CRLog::trace("calling render() for document %08X font=%08X", (unsigned int)m_doc, (unsigned int)m_font.get() );
    		CRLog::debug("song epub render height %d",m_doc->render(pages, NULL, dx, dy,
                    false, 0,
                    m_font, m_def_interline_space, m_props));

    #if 0
    		FILE * f = fopen("/sdcard/pagelist.log", "wt");
    		if (f) {
    			for (int i=0; i<m_pages.length(); i++)
    			{
    				fprintf(f, "%4d:   %7d .. %-7d [%d]\n", i, m_pages[i].start, m_pages[i].start+m_pages[i].height, m_pages[i].height);
    			}
    			fclose(f);
    		}
    #endif
    		fontMan->gc();
    		m_is_rendered = true;
    		//CRLog::debug("Making TOC...");
    		//makeToc();
    		CRLog::debug("Updating selections...");
//    		updateSelections();
    		CRLog::debug("Render is finished %d",m_pages.size());

    	}

    }
void EpubDocument::loadDocument(LVStreamRef stream) {

        LVContainerRef arc = LVOpenArchieve( stream );
        if ( arc.isNull() )
            return; // not a ZIP archive

        // check root media type
        lString16 rootfilePath = EpubGetRootFilePath(arc);
        if ( rootfilePath.empty() )
        	return;

        EncryptedDataContainer * decryptor = new EncryptedDataContainer(arc);
        if (decryptor->open()) {
            CRLog::debug("EPUB: encrypted items detected");
        }

        LVContainerRef m_arc = LVContainerRef(decryptor);
        m_doc = new ldomDocument();
        if (decryptor->hasUnsupportedEncryption()) {
            // DRM!!!
            createEncryptedEpubWarningDocument(m_doc);
            return;
        }
        m_doc->setContainer(m_arc);

        // read content.opf
        EpubItems epubItems;
        //EpubItem * epubToc = NULL; //TODO
        LVArray<EpubItem*> spineItems;
        lString16 codeBase;
        //lString16 css;

        //
        {
            codeBase=LVExtractPath(rootfilePath, false);
            CRLog::trace("codeBase=%s", LCSTR(codeBase));
        }

        LVStreamRef content_stream = m_arc->OpenStream(rootfilePath.c_str(), LVOM_READ);
        if ( content_stream.isNull() )
            return;


        lString16 ncxHref;
        lString16 coverId;

        LVEmbeddedFontList fontList;
        EmbeddedFontStyleParser styleParser(fontList);

        // reading content stream
        {
            ldomDocument * doc = LVParseXMLStream( content_stream );
            if ( !doc )
                return ;

    //        // for debug
    //        {
    //            LVStreamRef out = LVOpenFileStream("/tmp/content.xml", LVOM_WRITE);
    //            doc->saveToStream(out, NULL, true);
    //        }

            m_doc_props = m_doc->getProps();
            lString16 author = doc->textFromXPath( cs16("package/metadata/creator"));
            lString16 title = doc->textFromXPath( cs16("package/metadata/title"));
            lString16 language = doc->textFromXPath( cs16("package/metadata/language"));
            m_doc_props->setString(DOC_PROP_TITLE, title);
            m_doc_props->setString(DOC_PROP_LANGUAGE, language);
            m_doc_props->setString(DOC_PROP_AUTHORS, author );

            for ( int i=1; i<50; i++ ) {
                ldomNode * item = doc->nodeFromXPath(lString16("package/metadata/identifier[") << fmt::decimal(i) << "]");
                if (!item)
                    break;
                lString16 key = item->getText();
                if (decryptor->setManglingKey(key)) {
                    CRLog::debug("Using font mangling key %s", LCSTR(key));
                    break;
                }
            }

            CRLog::info("Author: %s Title: %s", LCSTR(author), LCSTR(title));
            for ( int i=1; i<20; i++ ) {
                ldomNode * item = doc->nodeFromXPath(lString16("package/metadata/meta[") << fmt::decimal(i) << "]");
                if ( !item )
                    break;
                lString16 name = item->getAttributeValue("name");
                lString16 content = item->getAttributeValue("content");
                if (name == "cover")
                    coverId = content;
                else if (name == "calibre:series")
                    m_doc_props->setString(DOC_PROP_SERIES_NAME, content );
                else if (name == "calibre:series_index")
                    m_doc_props->setInt(DOC_PROP_SERIES_NUMBER, content.atoi() );
            }

            // items
            for ( int i=1; i<50000; i++ ) {
                ldomNode * item = doc->nodeFromXPath(lString16("package/manifest/item[") << fmt::decimal(i) << "]");
                if ( !item )
                    break;
                lString16 href = item->getAttributeValue("href");
                lString16 mediaType = item->getAttributeValue("media-type");
                lString16 id = item->getAttributeValue("id");
                if ( !href.empty() && !id.empty() ) {
                    href = DecodeHTMLUrlString(href);
                    if ( id==coverId ) {
                        // coverpage file
                        lString16 coverFileName = codeBase + href;
                        CRLog::info("EPUB coverpage file: %s", LCSTR(coverFileName));
                        LVStreamRef stream = m_arc->OpenStream(coverFileName.c_str(), LVOM_READ);
                        if ( !stream.isNull() ) {
                            LVImageSourceRef img = LVCreateStreamImageSource(stream);
                            if ( !img.isNull() ) {
                                CRLog::info("EPUB coverpage image is correct: %d x %d", img->GetWidth(), img->GetHeight() );
                                m_doc_props->setString(DOC_PROP_COVER_FILE, coverFileName);
                            }
                        }
                    }
                    EpubItem * epubItem = new EpubItem;
                    epubItem->href = href;
                    epubItem->id = id;
                    epubItem->mediaType = mediaType;
                    epubItems.add( epubItem );

    //                // register embedded document fonts
    //                if (mediaType == L"application/vnd.ms-opentype"
    //                        || mediaType == L"application/x-font-otf"
    //                        || mediaType == L"application/x-font-ttf") { // TODO: more media types?
    //                    // TODO:
    //                    fontList.add(codeBase + href);
    //                }
                }
                if (mediaType == "text/css") {
                    lString16 name = LVCombinePaths(codeBase, href);
                    LVStreamRef cssStream = m_arc->OpenStream(name.c_str(), LVOM_READ);
                    if (!cssStream.isNull()) {
                        lString8 cssFile = UnicodeToUtf8(LVReadTextFile(cssStream));
                        lString16 base = name;
                        LVExtractLastPathElement(base);
                        //CRLog::trace("style: %s", cssFile.c_str());
                        styleParser.parse(base, cssFile);
                    }
                }
            }

            // spine == itemrefs
            if ( epubItems.length()>0 ) {
                ldomNode * spine = doc->nodeFromXPath( cs16("package/spine") );
                if ( spine ) {

                    EpubItem * ncx = epubItems.findById( spine->getAttributeValue("toc") ); //TODO
                    //EpubItem * ncx = epubItems.findById(cs16("ncx"));
                    if ( ncx!=NULL )
                        ncxHref = codeBase + ncx->href;

                    for ( int i=1; i<50000; i++ ) {
                        ldomNode * item = doc->nodeFromXPath(lString16("package/spine/itemref[") << fmt::decimal(i) << "]");
                        if ( !item )
                            break;
                        EpubItem * epubItem = epubItems.findById( item->getAttributeValue("idref") );
                        if ( epubItem ) {
                            // TODO: add to document
                            spineItems.add( epubItem );
                        }
                    }
                }
            }
            delete doc;
        }

        if ( spineItems.length()==0 )
            return;

        lUInt32 saveFlags = m_doc->getDocFlags();
        m_doc->setDocFlags( saveFlags );
        m_doc->setContainer( m_arc );

        ldomDocumentWriter writer(m_doc);
    #if 0
        m_doc->setNodeTypes( fb2_elem_table );
        m_doc->setAttributeTypes( fb2_attr_table );
        m_doc->setNameSpaceTypes( fb2_ns_table );
    #endif
        //m_doc->setCodeBase( codeBase );

        ldomDocumentFragmentWriter appender(&writer, cs16("body"), cs16("DocFragment"), lString16::empty_str );
        writer.OnStart(NULL);
        writer.OnTagOpenNoAttr(L"", L"body");
        int fragmentCount = 0;
        for ( int i=0; i<spineItems.length(); i++ ) {
            if (spineItems[i]->mediaType == "application/xhtml+xml") {
                lString16 name = codeBase + spineItems[i]->href;
                lString16 subst = cs16("_doc_fragment_") + fmt::decimal(i);
                appender.addPathSubstitution( name, subst );
                //CRLog::trace("subst: %s => %s", LCSTR(name), LCSTR(subst));
            }
        }
        for ( int i=0; i<spineItems.length(); i++ ) {
        	if (i>100)
        		break;
            if (spineItems[i]->mediaType == "application/xhtml+xml") {
                lString16 name = codeBase + spineItems[i]->href;
                {
                    CRLog::debug("Checking fragment: %s", LCSTR(name));
                    LVStreamRef stream = m_arc->OpenStream(name.c_str(), LVOM_READ);
                    if ( !stream.isNull() ) {
                        appender.setCodeBase( name );
                        lString16 base = name;
                        LVExtractLastPathElement(base);
                        //CRLog::trace("base: %s", LCSTR(base));
                        //LVXMLParser
                        LVHTMLParser parser(stream, &appender);
                        if ( parser.CheckFormat() && parser.Parse() ) {
                            // valid
                            fragmentCount++;
                            lString8 headCss = appender.getHeadStyleText();
                            //CRLog::trace("style: %s", headCss.c_str());
                            styleParser.parse(base, headCss);
                        } else {
                            CRLog::error("Document type is not XML/XHTML for fragment %s", LCSTR(name));
                        }
                    }
                }
            }
        }

        if ( !ncxHref.empty() ) {
            LVStreamRef stream = m_arc->OpenStream(ncxHref.c_str(), LVOM_READ);
            lString16 codeBase = LVExtractPath( ncxHref );
            if ( codeBase.length()>0 && codeBase.lastChar()!='/' )
                codeBase.append(1, L'/');
            appender.setCodeBase(codeBase);
            if ( !stream.isNull() ) {
                ldomDocument * ncxdoc = LVParseXMLStream( stream );
                if ( ncxdoc!=NULL ) {
                    ldomNode * navMap = ncxdoc->nodeFromXPath( cs16("ncx/navMap"));
                    if ( navMap!=NULL )
                        ReadEpubToc( m_doc, navMap, m_doc->getToc(), appender );
                    delete ncxdoc;
                }
            }
        }

        writer.OnTagClose(L"", L"body");
        writer.OnStop();
        CRLog::debug("EPUB: %d documents merged", fragmentCount);

        if (!fontList.empty()) {
            // set document font list, and register fonts
            m_doc->getEmbeddedFontList().set(fontList);
            m_doc->registerEmbeddedFonts();
            m_doc->forceReinitStyles();
        }

        if ( fragmentCount==0 )
            return;

    #if 0
        // set stylesheet
        //m_doc->getStyleSheet()->clear();
        m_doc->setStyleSheet( NULL, true );
        //m_doc->getStyleSheet()->parse(m_stylesheet.c_str());
        if ( !css.empty() && m_doc->getDocFlag(DOC_FLAG_ENABLE_INTERNAL_STYLES) ) {

            m_doc->setStyleSheet( "p.p { text-align: justify }\n"
                "svg { text-align: center }\n"
                "i { display: inline; font-style: italic }\n"
                "b { display: inline; font-weight: bold }\n"
                "abbr { display: inline }\n"
                "acronym { display: inline }\n"
                "address { display: inline }\n"
                "p.title-p { hyphenate: none }\n"
    //abbr, acronym, address, blockquote, br, cite, code, dfn, div, em, h1, h2, h3, h4, h5, h6, kbd, p, pre, q, samp, span, strong, var
            , false);
            m_doc->setStyleSheet( UnicodeToUtf8(css).c_str(), false );
            //m_doc->getStyleSheet()->parse(UnicodeToUtf8(css).c_str());
        } else {
            //m_doc->getStyleSheet()->parse(m_stylesheet.c_str());
            //m_doc->setStyleSheet( m_stylesheet.c_str(), false );
        }
    #endif
    #if 0
        LVStreamRef out = LVOpenFileStream( L"c:\\doc.xml" , LVOM_WRITE );
        if ( !out.isNull() )
            m_doc->saveToStream( out, "utf-8" );
    #endif

        // save compound XML document, for testing:
        m_doc->saveToStream(LVOpenFileStream("/sdcard//epub_dump.xml", LVOM_WRITE), NULL, true);

        return;


    }
void testEpub(LVStreamRef stream,LVDrawBuf& drawbuf) {}
