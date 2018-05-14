/**
 * Make a HTML-formatted string with hOCR markup from the internal
 * data structures.
 * page_number is 0-based but will appear in the output as 1-based.
 * Image name/input_file_ can be set by SetInputName before calling
 * GetHOCRText
 * STL removed from original patch submission and refactored by rays.
 * Returned string must be freed with the delete [] operator.
 */
char* TessBaseAPI::GetHOCRText(ETEXT_DESC* monitor, int page_number) {
  if (tesseract_ == nullptr || (page_res_ == nullptr && Recognize(monitor) < 0))
    return nullptr;

  int lcnt = 1, bcnt = 1, pcnt = 1, wcnt = 1;
  int page_id = page_number + 1;  // hOCR uses 1-based page numbers.
  bool para_is_ltr = true;        // Default direction is LTR
  const char* paragraph_lang = nullptr;
  bool font_info = false;
  GetBoolVariable("hocr_font_info", &font_info);

  STRING hocr_str("");

  if (input_file_ == nullptr)
      SetInputName(nullptr);

#ifdef _WIN32
  // convert input name from ANSI encoding to utf-8
  int str16_len =
      MultiByteToWideChar(CP_ACP, 0, input_file_->string(), -1, nullptr, 0);
  wchar_t *uni16_str = new WCHAR[str16_len];
  str16_len = MultiByteToWideChar(CP_ACP, 0, input_file_->string(), -1,
                                  uni16_str, str16_len);
  int utf8_len = WideCharToMultiByte(CP_UTF8, 0, uni16_str, str16_len, nullptr, 0,
                                     nullptr, nullptr);
  char *utf8_str = new char[utf8_len];
  WideCharToMultiByte(CP_UTF8, 0, uni16_str, str16_len, utf8_str,
                      utf8_len, nullptr, nullptr);
  *input_file_ = utf8_str;
  delete[] uni16_str;
  delete[] utf8_str;
#endif

  hocr_str += "  <div class='ocr_page'";
  AddIdTohOCR(&hocr_str, "page", page_id, -1);
  hocr_str += " title='image \"";
  if (input_file_) {
    hocr_str += HOcrEscape(input_file_->string());
  } else {
    hocr_str += "unknown";
  }
  hocr_str.add_str_int("\"; bbox ", rect_left_);
  hocr_str.add_str_int(" ", rect_top_);
  hocr_str.add_str_int(" ", rect_width_);
  hocr_str.add_str_int(" ", rect_height_);
  hocr_str.add_str_int("; ppageno ", page_number);
  hocr_str += "'>\n";

  ResultIterator *res_it = GetIterator();
  while (!res_it->Empty(RIL_BLOCK)) {
    if (res_it->Empty(RIL_WORD)) {
      res_it->Next(RIL_WORD);
      continue;
    }

    // Open any new block/paragraph/textline.
    if (res_it->IsAtBeginningOf(RIL_BLOCK)) {
      para_is_ltr = true;  // reset to default direction
      hocr_str += "   <div class='ocr_carea'";
      AddIdTohOCR(&hocr_str, "block", page_id, bcnt);
      AddBoxTohOCR(res_it, RIL_BLOCK, &hocr_str);
    }
    if (res_it->IsAtBeginningOf(RIL_PARA)) {
      hocr_str += "\n    <p class='ocr_par'";
      para_is_ltr = res_it->ParagraphIsLtr();
      if (!para_is_ltr) {
        hocr_str += " dir='rtl'";
      }
      AddIdTohOCR(&hocr_str, "par", page_id, pcnt);
      paragraph_lang = res_it->WordRecognitionLanguage();
      if (paragraph_lang) {
        hocr_str += " lang='";
        hocr_str += paragraph_lang;
        hocr_str += "'";
      }
      AddBoxTohOCR(res_it, RIL_PARA, &hocr_str);
    }
    if (res_it->IsAtBeginningOf(RIL_TEXTLINE)) {
      hocr_str += "\n     <span class='ocr_line'";
      AddIdTohOCR(&hocr_str, "line", page_id, lcnt);
      AddBoxTohOCR(res_it, RIL_TEXTLINE, &hocr_str);
    }

    // Now, process the word...
    hocr_str += "<span class='ocrx_word'";
    AddIdTohOCR(&hocr_str, "word", page_id, wcnt);
    int left, top, right, bottom;
    bool bold, italic, underlined, monospace, serif, smallcaps;
    int pointsize, font_id;
    const char *font_name;
    res_it->BoundingBox(RIL_WORD, &left, &top, &right, &bottom);
    font_name = res_it->WordFontAttributes(&bold, &italic, &underlined,
                                           &monospace, &serif, &smallcaps,
                                           &pointsize, &font_id);
    hocr_str.add_str_int(" title='bbox ", left);
    hocr_str.add_str_int(" ", top);
    hocr_str.add_str_int(" ", right);
    hocr_str.add_str_int(" ", bottom);
    hocr_str.add_str_int("; x_wconf ", res_it->Confidence(RIL_WORD));
    if (font_info) {
      if (font_name) {
        hocr_str += "; x_font ";
        hocr_str += HOcrEscape(font_name);
      }
      hocr_str.add_str_int("; x_fsize ", pointsize);
    }
    hocr_str += "'";
    const char* lang = res_it->WordRecognitionLanguage();
    if (lang && (!paragraph_lang || strcmp(lang, paragraph_lang))) {
      hocr_str += " lang='";
      hocr_str += lang;
      hocr_str += "'";
    }
    switch (res_it->WordDirection()) {
      // Only emit direction if different from current paragraph direction
      case DIR_LEFT_TO_RIGHT:
        if (!para_is_ltr) hocr_str += " dir='ltr'";
        break;
      case DIR_RIGHT_TO_LEFT:
        if (para_is_ltr) hocr_str += " dir='rtl'";
        break;
      case DIR_MIX:
      case DIR_NEUTRAL:
      default:  // Do nothing.
        break;
    }
    hocr_str += ">";
    bool last_word_in_line = res_it->IsAtFinalElement(RIL_TEXTLINE, RIL_WORD);
    bool last_word_in_para = res_it->IsAtFinalElement(RIL_PARA, RIL_WORD);
    bool last_word_in_block = res_it->IsAtFinalElement(RIL_BLOCK, RIL_WORD);
    if (bold) hocr_str += "<strong>";
    if (italic) hocr_str += "<em>";
    do {
      const std::unique_ptr<const char[]> grapheme(
          res_it->GetUTF8Text(RIL_SYMBOL));
      if (grapheme && grapheme[0] != 0) {
        hocr_str += HOcrEscape(grapheme.get());
      }
      res_it->Next(RIL_SYMBOL);
    } while (!res_it->Empty(RIL_BLOCK) && !res_it->IsAtBeginningOf(RIL_WORD));
    if (italic) hocr_str += "</em>";
    if (bold) hocr_str += "</strong>";
    hocr_str += "</span> ";
    wcnt++;
    // Close any ending block/paragraph/textline.
    if (last_word_in_line) {
      hocr_str += "\n     </span>";
      lcnt++;
    }
    if (last_word_in_para) {
      hocr_str += "\n    </p>\n";
      pcnt++;
      para_is_ltr = true;  // back to default direction
    }
    if (last_word_in_block) {
      hocr_str += "   </div>\n";
      bcnt++;
    }
  }
  hocr_str += "  </div>\n";

  char *ret = new char[hocr_str.length() + 1];
  strcpy(ret, hocr_str.string());
  delete res_it;
  return ret;
}
