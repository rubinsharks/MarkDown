package com.comidge.markdown.markdown;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.text.style.QuoteSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;

import com.comidge.markdown.StringUtil;

import java.util.HashMap;

/**
 * Created by rubin on 2017. 4. 7..
 */

public class MarkString implements MarkDown {

    private SpannableStringBuilder builder;
    private boolean isParsing = false;
    private int hLevel = 0;
    private boolean isBlockQuote = false;
    private boolean isList = false;
    private String orderedNumber = null;

    public MarkString(String text) {
        this.builder = new SpannableStringBuilder(text);
    }

    public SpannableStringBuilder getRawBuilder() throws ParsingException {
        return this.builder;
    }

    public SpannableStringBuilder getBuilder() throws ParsingException {
        if(!isParsing) {
            throw new ParsingException();
        }
        return this.builder;
    }

    public void startParsing(HashMap<String, String> links) {
        isParsing = true;
        if(builder.length() < 2) return;
        addHeader(builder);
        addBlockQuote(builder);
        addList(builder);
        addOrderedList((builder));
        addLink(builder, links);
        addBoldAndItalic(builder);
        addBold(builder);
        addItalic(builder);
        if(hLevel != 0) {
            float offset = 1.4f - (0.2f * hLevel);
            RelativeSizeSpan sizeSpan = new RelativeSizeSpan(1.0f + offset);
            builder.setSpan(sizeSpan, 0, builder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        } else if(isBlockQuote) {
            QuoteSpan quoteSpan = new QuoteSpan();
            builder.setSpan(quoteSpan, 0, builder.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        } else if(isList) {
            builder.insert(0, "    " + '\u2022' + "   ");
        } else if(orderedNumber != null) {
            builder.insert(0, "    " + '\u2022' + "   ");
        }
    }

    /*
    # ~ ######
     */
    private void addHeader(SpannableStringBuilder builder) {
        if(builder.charAt(0) == '#') {
            for(int i = 1; i < (builder.length() > 6 ? 6 : builder.length()); i++) {
                char c = builder.charAt(i);
                if(c != '#') {
                    if(c == ' ') {
                        hLevel = i;
                        builder.delete(0, i + 1);
                        return;
                    } else {
                        return;
                    }
                }
            }
            if(builder.length() > 6) {
                if(builder.charAt(6) == ' ') {
                    hLevel = 6;
                    builder.delete(0, 7);
                }
            }
        }
    }

    /*
    >
     */
    private void addBlockQuote(SpannableStringBuilder builder) {
        if(builder.charAt(0) == '>') {
            isBlockQuote = true;
            builder.delete(0, 1);
        }
    }

    /*
    *, +, -
     */
    private void addList(SpannableStringBuilder builder) {
        if(builder.length() < 2) return;
        char first = builder.charAt(0);
        if(first == '*' || first == '+' || first =='-') {
            if(builder.charAt(1) == ' ') {
                isList = true;
                builder.delete(0, 2);
            }
        }
    }

    private void addOrderedList(SpannableStringBuilder builder) {
        if(builder.length() < 3) return;
        if(Character.isDigit(builder.charAt(0))) {
            for(int i = 1; i < builder.length(); i++) {
                if(!Character.isDigit(builder.charAt(i))) {
                    if(builder.charAt(i) == '.') {
                        if(builder.length() == i + 1) return;
                        if(builder.charAt(i + 1) == ' ') {
                            orderedNumber = builder.subSequence(0, i).toString();
                        } else {
                            return;
                        }
                    } else {
                        return;
                    }
                }
            }
        }
    }

    private void addLink(SpannableStringBuilder builder, HashMap<String, String> links) {
        int start = -1;
        String title = "";
        String link = "";
        String url = "";
        for(int i = 0; i < builder.length(); i++) {
            char c = builder.charAt(i);
            if(start == -1) {
                i = StringUtil.indexOf(builder, "[", i);
            } else {
                i = StringUtil.indexOf(builder, "]", i);
            }
            if(i == -1) return;
            if(start == -1) {
                start = i;
                i = i + 1;
            } else {
                if(StringUtil.checkNextCharacter(builder, i) == '[') {
                    int substart = StringUtil.indexOf(builder, "[", i);
                    int subend = StringUtil.indexOf(builder, "]", i + 1);
                    if(subend - substart > 1) {
                        link = builder.subSequence(substart + 1, subend).toString();
                        String match = links.get(link);
                        if(match != null) {
                            title = builder.subSequence(start + 1, i).toString();
                            url = match;
                            builder.delete(start, subend + 1);
                            i = i - (i - start);
                        }
                    }
                } else if(StringUtil.checkNextCharacter(builder, i) == '('){
                    int substart = StringUtil.indexOf(builder, "(", i);
                    int subend = StringUtil.indexOf(builder, ")", i + 1);
                    if(subend - substart > 1) {
                        url = builder.subSequence(substart + 1, subend).toString();
                        title = builder.subSequence(start + 1, i).toString();
                        builder.delete(start, subend + 1);
                        i = i - (i - start);
                    }
                } else {
                    link = builder.subSequence(start + 1, i).toString();
                    url = link;
                    title = link;
                    builder.delete(start, i + 1);
                    i = i - (i - start);
                }
                if(!title.equals("")) {
                    builder.insert(i, title);
                    URLSpan urlSpan = new URLSpan(url);
                    builder.setSpan(urlSpan, i, i + title.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                }
                title = "";
                url = "";
                start = -1;
            }
        }
    }

    /*
    ***
     */
    private void addBoldAndItalic(SpannableStringBuilder builder) {

        int start = -1;
        for(int i = 0; i < builder.length(); i++) {
            i = StringUtil.indexOf(builder, "***", i);
            if(i == -1) return;
            if(start == -1) {
                if(i + 4 > builder.length()) return;
                if(builder.charAt(i + 3) != ' ') {
                    start = i;
                    i = i + 2;
                }
            } else {
                if(builder.charAt(i - 1) != ' ') {
                    builder.delete(i, i + 3);
                    builder.delete(start, start + 3);
                    // start ~ i - 3
                    StyleSpan styleSpan = new StyleSpan(Typeface.BOLD_ITALIC);
                    builder.setSpan(styleSpan, start, i - 3, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    start = -1;
                    i = i - 4;
                } else {
                    if(i + 4 > builder.length()) return;
                    if(builder.charAt(i + 3) != ' ') {
                        start = i;
                    }
                }
            }
        }
    }

    /*
    **
     */
    private void addBold(SpannableStringBuilder builder) {
        int start = -1;
        for(int i = 0; i < builder.length(); i++) {
            i = StringUtil.indexOf(builder, "**", i);
            if(i == -1) return;
            if(start == -1) {
                if(i + 3 > builder.length()) return;
                if(builder.charAt(i + 2) != ' ') {
                    start = i;
                    i = i + 1;
                }
            } else {
                if(builder.charAt(i - 1) != ' ') {
                    builder.delete(i, i + 2);
                    builder.delete(start, start + 2);
                    // start ~ i - 3
                    StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
                    builder.setSpan(styleSpan, start, i - 2, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    start = -1;
                    i = i - 3;
                } else {
                    if(i + 3 > builder.length()) return;
                    if(builder.charAt(i + 2) != ' ') {
                        start = i;
                    }
                }
            }
        }
    }

    /*
    *
     */
    private void addItalic(SpannableStringBuilder builder) {
        int start = -1;
        for(int i = 0; i < builder.length(); i++) {
            i = StringUtil.indexOf(builder, "*", i);
            if(i == -1) return;
            if(start == -1) {
                if(i + 2 > builder.length()) return;
                if(builder.charAt(i + 1) != ' ') {
                    start = i;
                }
            } else {
                if(builder.charAt(i - 1) != ' ') {
                    builder.delete(i, i + 1);
                    builder.delete(start, start + 1);
                    // start ~ i - 3
                    StyleSpan styleSpan = new StyleSpan(Typeface.ITALIC);
                    builder.setSpan(styleSpan, start, i - 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    start = -1;
                    i = i - 2;
                } else {
                    if(i + 2 > builder.length()) return;
                    if(builder.charAt(i + 1) != ' ') {
                        start = i;
                    }
                }
            }
        }
    }

    private class ParsingException extends RuntimeException {
        public ParsingException() {super();}
    }
}
