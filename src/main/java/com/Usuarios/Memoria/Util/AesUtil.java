
package com.Usuarios.Memoria.Util;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class AesUtil {
 
    private static final String SECRET_KEY = "MyS3cur3K3y12345678901234567890AB";
    private static final String INIT_VECTOR = "InitVector123456";
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    
    public String encrypt(String plainText) throws Exception{
    
        IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes("UTF-8"));
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes("UTF-8"), "AES");
        
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
        
        byte[] encryted = cipher.doFinal(plainText.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encryted);
    }
    
    public String decryp(String encryptedText) throws Exception{
    IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes("UTF-8"));
    SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes("UFT-8"), "AES");
    
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE,keySpec, iv);
    
    byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
    return new String(original);
    
    }
    
}
