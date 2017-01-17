package com.xiaoi.app.testchatvoice.bean;

/**
 * @author: Gary.shen
 * Date: 2016/12/1
 * Time: 11:17
 * des:
 */

public class VoiceImfo {
    String request;
    String answer;
    int isLast;//0不是，1是

    public VoiceImfo(String request, String answer, int isLast) {
        this.request = request;
        this.answer = answer;
        this.isLast = isLast;
    }

    public VoiceImfo() {

    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getIsLast() {
        return isLast;
    }

    public void setIsLast(int isLast) {
        this.isLast = isLast;
    }
}
