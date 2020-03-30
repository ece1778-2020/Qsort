package com.example.qsort.UxResearcher;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Salt {

    public static byte[] createSalt(){
        byte[] salt = new byte[16];
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.nextBytes(salt);
            return salt;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * 生成摘要
     * @param password
     * @param salt
     * @return
     */
    public static byte[] digest(String password, byte[] salt){

        try {
            MessageDigest msgDigest = MessageDigest.getInstance("SHA");
            if (salt != null && salt.length > 0){
                msgDigest.update(salt);
            }

            byte[] digest = msgDigest.digest(password.getBytes());
            return digest;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String createCredential(String password){
        String s = Base64.getEncoder().encodeToString(digest(password,createSalt()));
        return s;
    }


}
