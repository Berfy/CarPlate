package com.wlb.pndecoder.utils;

import android.text.method.ReplacementTransformationMethod;

/** @author Berfy
 * Edittext自动大写的识别类
 * */
public class AllCapTransformationMethod extends ReplacementTransformationMethod {
  
    private char[] lower = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};  
    private char[] upper = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};  
    private boolean allUpper = false;  
  
    public AllCapTransformationMethod(boolean needUpper) {  
        this.allUpper = needUpper;  
    }  
  
    @Override  
    protected char[] getOriginal() {  
        if (allUpper) {  
            return lower;  
        } else {  
            return upper;  
        }  
    }  
  
    @Override  
    protected char[] getReplacement() {  
        if (allUpper) {  
            return upper;  
        } else {  
            return lower;  
        }  
    }  
}  