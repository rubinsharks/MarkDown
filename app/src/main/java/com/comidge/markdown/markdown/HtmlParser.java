package com.comidge.markdown.markdown;

import java.util.ArrayList;

/**
 * Created by rubin on 2017. 4. 16..
 */

public class HtmlParser {
    // its just p
    public static String getReturnString(String html) {
        StringBuilder stringBuilder = new StringBuilder("");
        ArrayList<Integer> pStartIndexes = new ArrayList<>();
        int index = 0;
        int end = html.indexOf("</p>");
        for(int i = 0; i < html.length(); i++) {
            index = i;
            i = html.indexOf("<p>", i);
            if(i == -1) {
                break;
            } else {
                if(i > end) {
                    int start = pStartIndexes.get(pStartIndexes.size() - 1);
                    stringBuilder.append(html.substring(start + 3, end));
                    stringBuilder.append("\n");
                    pStartIndexes.remove(pStartIndexes.size() - 1);
                    end = html.indexOf("</p>", end + 1);
                    if(i > end) {
                        i -= 1;
                        continue;
                    }
                }
                pStartIndexes.add(i);
            }
        }
        for(int i = index; i < html.length(); i++) {
            i = html.indexOf("</p>", i);
            if(pStartIndexes.size() == 0) {
                break;
            }
            if(i == -1) {
                break;
            } else {
                int start = pStartIndexes.get(pStartIndexes.size() - 1);
                stringBuilder.append(html.substring(start + 3, i));
                stringBuilder.append("\n");
                pStartIndexes.remove(pStartIndexes.size() - 1);
            }
        }
        return stringBuilder.length() == 0 ? html : stringBuilder.toString();
    }
}
