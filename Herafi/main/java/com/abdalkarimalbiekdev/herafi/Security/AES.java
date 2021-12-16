package com.abdalkarimalbiekdev.herafi.Security;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import ru.bullyboo.encoder.Encoder;

public class AES {

    public static String ENCRYPTION_KEY = "......................";
    private static String INITIALIZATIO_VECTOR = ".....................";


    public static String encrypt(String plainText) throws Exception {
//
//        String encrypt = Encoder.BuilderAES()
//                .message(plainText)
//                .method(ru.bullyboo.encoder.methods.AES.Method.AES_CBC_PKCS5PADDING)
//                .key(ENCRYPTION_KEY)
//                .keySize(ru.bullyboo.encoder.methods.AES.Key.SIZE_128)
//                .iVector(INITIALIZATIO_VECTOR)
//                .encrypt();
//
//        return encrypt;

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(ENCRYPTION_KEY.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.ENCRYPT_MODE , key , new IvParameterSpec(INITIALIZATIO_VECTOR.getBytes("UTF-8")));
        byte[] encryptedMsg = cipher.doFinal(plainText.getBytes("UTF-8"));
        String cipherText = Base64.encodeToString(encryptedMsg , Base64.DEFAULT);

        return cipherText;
    }



    public static String decrypt(String cipherText) throws Exception{

        String decrypt = Encoder.BuilderAES()
                .message(cipherText)
                .method(ru.bullyboo.encoder.methods.AES.Method.AES_CBC_PKCS5PADDING)
                .key(ENCRYPTION_KEY)
                .keySize(ru.bullyboo.encoder.methods.AES.Key.SIZE_128)
                .iVector(INITIALIZATIO_VECTOR)
                .decrypt();

        return decrypt;


//        byte[] base64Encrypted = Base64.decode(cipherText , Base64.DEFAULT);

//        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//        SecretKeySpec key = new SecretKeySpec(ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8), "AES");
//        cipher.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(INITIALIZATIO_VECTOR.getBytes(StandardCharsets.UTF_8)));
//
//        String plainText = new String(cipher.doFinal(base64Encrypted),StandardCharsets.UTF_8);
//
//        return plainText;

    }
}
