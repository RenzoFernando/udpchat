package udpchat.network;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.zip.CRC32;

public final class SecurityUtils {

    private static final byte[] KEY = "1234567890123456".getBytes();  // 128 bit DEMO

    private SecurityUtils(){
        // util static
    }

    /*  AES (simétrico)  */
    public static String encryptAES(String plain){
        try{
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE,new SecretKeySpec(KEY,"AES"));
            return Base64.getEncoder().encodeToString(c.doFinal(plain.getBytes()));
        }catch(Exception e){ throw new RuntimeException(e); }
    }
    public static String decryptAES(String cipher){
        try{
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE,new SecretKeySpec(KEY,"AES"));
            return new String(c.doFinal(Base64.getDecoder().decode(cipher)));
        }catch(Exception e){ throw new RuntimeException(e); }
    }

    /*  HMAC SHA-256  */
    public static String hmacSHA256(String msg){
        try{
            Mac m=Mac.getInstance("HmacSHA256");
            m.init(new SecretKeySpec(KEY,"HmacSHA256"));
            return Base64.getEncoder().encodeToString(m.doFinal(msg.getBytes()));
        }catch(Exception e){ throw new RuntimeException(e); }
    }

    /*  CRC-32 para verificar integridad  */
    public static long crc32(byte[] data){
        CRC32 c=new CRC32(); c.update(data); return c.getValue();
    }
}
