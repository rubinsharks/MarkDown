package com.comidge.markdown;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.comidge.markdown.markdown.HtmlParser;
import com.comidge.markdown.markdown.MarkDown;
import com.comidge.markdown.markdown.MarkImage;
import com.comidge.markdown.markdown.MarkLine;
import com.comidge.markdown.markdown.MarkString;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rubin on 2017. 4. 4..
 */

public class MarkDownView extends LinearLayout {

    private ArrayList<MarkDown> markdowns;
    private HashMap<String, String> links;

    private int textColor = 0xFF000000;

    public MarkDownView(Context context) {
        this(context, null);
    }

    public MarkDownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    public void setTextColor(@ColorInt int color) {
        this.textColor = color;
    }

    public void setMarkDownText(String text) {
        text = HtmlParser.getReturnString(text);
        text = text.replace("\n", " \n");
        String[] splited = text.split("\\r?\\n");
        markdowns = new ArrayList<>();
        links = new HashMap<>();
        for(String split : splited) {
            if(split.equals(" ")) {
                markdowns.add(new MarkString(split));
                continue;
            }
            split = split.trim();
            if(isLine(split)) {
                markdowns.add(new MarkLine());
            } else if(isLink(split)) {
            } else {
                markdowns.add(new MarkString(split));
            }
        }
        extractImage(markdowns);
        drawMarkDownText(markdowns);
    }

    private void drawMarkDownText(ArrayList<MarkDown> markDowns) {
        removeAllViews();
        for(MarkDown markDown : markDowns) {
            if(markDown instanceof MarkString) {
                addView(getMarkStringView((MarkString) markDown));
            } else if(markDown instanceof MarkLine) {
                View lineView = getMarkLineView();
                addView(lineView);
                LayoutParams params = (LayoutParams) lineView.getLayoutParams();
                params.setMargins(0, LayoutUtil.getDp(getContext(), 20), 0, LayoutUtil.getDp(getContext(), 20));
            } else if(markDown instanceof MarkImage) {
                View imageView = getMarkImageView((MarkImage) markDown);
                addView(imageView);
            }
        }
    }

    private View getMarkStringView(MarkString markString) {
        TextView markView = new TextView(getContext());
        markString.startParsing(links);
        markView.setText(markString.getBuilder());
        markView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        markView.setTextColor(textColor);
        markView.setMovementMethod(LinkMovementMethod.getInstance());
        return markView;
    }

    private View getMarkLineView() {
        View view = new View(getContext());
        LayoutUtil.inflateLayoutParams(view, ViewGroup.LayoutParams.MATCH_PARENT, LayoutUtil.getDp(getContext(), 1));
        view.setBackgroundColor(0xFFDDDDDD);
        return view;
    }

    private View getMarkImageView(MarkImage markImage) {
        ImageView imageView = new ImageView(getContext());
        LayoutUtil.inflateLayoutParams(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if(mOnImage != null) {
            mOnImage.onImage(imageView, markImage.getUrl());
        }
        return imageView;
    }

    private boolean isLine(String text) {
        if(text.length() == 0) return false;
        char first = '\u0000';
        for(int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if(first == '\u0000') {
                if(!(c == '*' || c == '-' || c == '_')) {
                    return false;
                }
                first = c;
            } else {
                if(c != ' ' && first != c) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isLink(String text) {
        int startIndex = text.indexOf('[');
        if(startIndex == -1) return false;
        int endIndex = text.indexOf("]:");
        if(endIndex == -1) return false;
        if(endIndex <= startIndex) return false;
        for(int i = startIndex + 1; i < endIndex; i++) {
            char c = text.charAt(i);
            if(c == '[' || c == ']') return false;
        }
        links.put(text.substring(startIndex + 1, endIndex), text.substring(endIndex + 2, text.length()).trim());
        return true;
    }

    private boolean extractImage(ArrayList<MarkDown> markdowns) {
        for(int i = 0; i < markdowns.size(); i++) {
            MarkDown markDown = markdowns.get(i);
            if(markDown instanceof MarkString) {
                SpannableStringBuilder builder = ((MarkString) markDown).getRawBuilder();
                extractImageLink(builder, links, markdowns, i);
            }
        }
        return true;
    }

    private void extractImageLink(SpannableStringBuilder builder, HashMap<String, String> links, ArrayList<MarkDown> markdowns, int markdownsIndex) {
        int start = -1;
        int markdownsAddIndex = 0;
        String title = "";
        String link = "";
        String url = "";
        markdowns.remove(markdownsIndex);
        for(int i = 0; i < builder.length(); i++) {
            if(start == -1) {
                i = StringUtil.indexOf(builder, "![", i);
            } else {
                i = StringUtil.indexOf(builder, "]", i);
            }
            if(i == -1) {
                if(builder.length() - markdownsAddIndex != 0) {
                    markdowns.add(markdownsIndex, new MarkString(builder.subSequence(markdownsAddIndex, builder.length()).toString()));
                }
                return;
            }
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
                            url = match;
                            if(!url.startsWith("http")) {
                                url = "";
                                continue;
                            }
                            title = builder.subSequence(start + 1, i).toString();
                            if(start - markdownsAddIndex != 0) {
                                markdowns.add(markdownsIndex++, new MarkString(builder.subSequence(markdownsAddIndex, start).toString()));
                                markdownsAddIndex = start;
                            }
                            builder.delete(start, subend + 1);
                            i = i - (i - start);
                        }
                    }
                } else if(StringUtil.checkNextCharacter(builder, i) == '('){
                    int substart = StringUtil.indexOf(builder, "(", i);
                    int subend = StringUtil.indexOf(builder, ")", i + 1);
                    if(subend - substart > 1) {
                        url = builder.subSequence(substart + 1, subend).toString();
                        if(!url.startsWith("http")) {
                            url = "";
                            continue;
                        }
                        title = builder.subSequence(start + 1, i).toString();
                        if(start - markdownsAddIndex != 0) {
                            markdowns.add(markdownsIndex++, new MarkString(builder.subSequence(markdownsAddIndex, start).toString()));
                            markdownsAddIndex = start;
                        }
                        builder.delete(start, subend + 1);
                        i = i - (i - start);
                    }
                } else {
                    link = builder.subSequence(start + 2, i).toString();
                    url = link;
                    if(!url.startsWith("http")) {
                        url = "";
                        continue;
                    }
                    title = link;
                    if(start - markdownsAddIndex != 0) {
                        markdowns.add(markdownsIndex++, new MarkString(builder.subSequence(markdownsAddIndex, start).toString()));
                        markdownsAddIndex = start;
                    }
                    builder.delete(start, i + 1);
                    i = i - (i - start);
                }
                if(!title.equals("")) {
                    markdowns.add(markdownsIndex++, new MarkImage(title, url));
                }
                title = "";
                url = "";
                start = -1;
            }
        }
        if(builder.length() - markdownsAddIndex != 0) {
            markdowns.add(markdownsIndex, new MarkString(builder.subSequence(markdownsAddIndex, builder.length()).toString()));
        }
    }

    public OnImage mOnImage;
    public void setOnImage(OnImage listener) {
        this.mOnImage = listener;
    }
    public interface OnImage {
        void onImage(ImageView imageView, String url);
    }
}
