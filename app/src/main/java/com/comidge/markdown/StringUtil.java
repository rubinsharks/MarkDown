package com.comidge.markdown;

import android.text.SpannableStringBuilder;

/**
 * Created by rubin on 2017. 4. 19..
 */

public class StringUtil {

    public static char checkNextCharacter(SpannableStringBuilder builder, int start) {
        for(int i = start + 1; i < builder.length(); i++) {
            char c = builder.charAt(i);
            if(c != ' ') {
                return c;
            }
        }
        return '\u0000';
    }

    public static int indexOf(SpannableStringBuilder builder, String str, int start) {
        if(builder.length() - start < str.length() || builder.length() == 0) return -1;
        char strFirst = str.charAt(0);
        for(int i = start; i < builder.length(); i++) {
            char c = builder.charAt(i);
            if(strFirst == c) {
                for(int j = 0; j < builder.length() - i; j++) {
                    if (builder.charAt(i + j) != str.charAt(j)) {
                        break;
                    }
                    if (j == str.length() - 1) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
}
