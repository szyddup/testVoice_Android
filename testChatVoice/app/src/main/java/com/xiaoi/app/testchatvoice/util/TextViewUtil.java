package com.xiaoi.app.testchatvoice.util;

import android.widget.TextView;

/**
 * @author: Gary.shen
 * Date: 2016/8/10
 * Time: 14:57
 * des:TextView工具类
 */


public class TextViewUtil {
    /**
     * 获取TextView对象的值
     *
     * @param tv
     * @return String型，空白时为空字符串
     */
    public static String getText(TextView tv) {
        if (tv == null || tv.getText() == null) {
            return "";
        }
        return tv.getText().toString();
    }


}
