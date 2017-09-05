/*     */
package cn.berfy.framework.service.bluetooth.lib;
/*     */ 
/*     */

/*     */
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class StringByte
/*     */ {
    /*  15 */   private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c',
/*  16 */     'd', 'e', 'f'};
    /*     */
/*     */ 
/*     */ 
/*  20 */   private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
/*  21 */     'D', 'E', 'F'};

    /*     */
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */
    public static char[] encodeHex(byte[] data)
/*     */ {
/*  31 */
        return encodeHex(data, true);
/*     */
    }

    /*     */
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */
    public static char[] encodeHex(byte[] data, boolean toLowerCase)
/*     */ {
/*  44 */
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
/*     */
    }
    protected static char[] encodeHex(byte[] data, char[] toDigits)
/*     */ {
/*  57 */
        int l = data.length;
/*  58 */
        char[] out = new char[l << 1];
/*     */     
/*  60 */
        int i = 0;
        for (int j = 0; i < l; i++) {
/*  61 */
            out[(j++)] = toDigits[((0xF0 & data[i]) >>> 4)];
/*  62 */
            out[(j++)] = toDigits[(0xF & data[i])];
/*     */
        }
/*  64 */
        return out;
/*     */
    }

    public static String encodeHexStr(byte[] data)
/*     */ {
/*  75 */
        return encodeHexStr(data, true);
/*     */
    }

    /*     */
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */
    public static String encodeHexStr(byte[] data, boolean toLowerCase)
/*     */ {
/*  88 */
        return encodeHexStr(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
/*     */
    }

    /*     */
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */
    protected static String encodeHexStr(byte[] data, char[] toDigits)
/*     */ {
/* 101 */
        return new String(encodeHex(data, toDigits));
/*     */
    }

    /*     */
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */
    public static byte[] decodeHex(char[] data)
/*     */ {
/* 114 */
        int len = data.length;
/* 115 */
        if ((len & 0x1) != 0) {
/* 116 */
            throw new RuntimeException("Odd number of characters.");
/*     */
        }
/* 118 */
        byte[] out = new byte[len >> 1];
/*     */     
/* 120 */
        int i = 0;
        for (int j = 0; j < len; i++) {
/* 121 */
            int f = toDigit(data[j], j) << 4;
/* 122 */
            j++;
/* 123 */
            f |= toDigit(data[j], j);
/* 124 */
            j++;
/* 125 */
            out[i] = ((byte) (f & 0xFF));
/*     */
        }
/* 127 */
        return out;
/*     */
    }

    /*     */
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */
    protected static int toDigit(char ch, int index)
/*     */ {
/* 142 */
        int digit = Character.digit(ch, 16);
/* 143 */
        if (digit == -1) {
/* 144 */
            throw new RuntimeException("Illegal hexadecimal character " + ch + " at index " + index);
/*     */
        }
/* 146 */
        return digit;
/*     */
    }

    /*     */
/*     */
    public static void main(String[] args) {
/* 150 */
        String srcStr = "待转换字符串";
/* 151 */
        String encodeStr = encodeHexStr(srcStr.getBytes());
/* 152 */
        String decodeStr = new String(decodeHex(encodeStr.toCharArray()));
/* 153 */
        System.out.println("转换前：" + srcStr);
/* 154 */
        System.out.println("转换后：" + encodeStr);
/* 155 */
        System.out.println("还原后：" + decodeStr);
/*     */
    }
/*     */
}


/* Location:              E:\1.jar!\com\lanya\open\StringByte.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       0.7.1
 */