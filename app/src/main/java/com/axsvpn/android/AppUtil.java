package com.axsvpn.android;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public class AppUtil {
    public static String md5(String data) {
        return new String(Hex.encodeHex(DigestUtils.md5(data)));
    }
}
