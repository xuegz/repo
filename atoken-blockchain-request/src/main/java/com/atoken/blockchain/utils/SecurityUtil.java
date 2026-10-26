package com.atoken.blockchain.utils;

import com.atoken.blockchain.exception.ParseException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;

/**
 * @author Qingyun Yu
 * @Description
 * @create 2017-10-16 10:39
 **/
public class SecurityUtil {
    private static Logger logger = LoggerFactory.getLogger(SecurityUtil.class);
    private static final int MAX_ENCODE_SIZE = 64;

    public static PublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        logger.debug("generate publicKey:{}", publicKey);
        byte[] keyBytes = Base64.encodeBase64(publicKey.getBytes());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    public static PublicKey getPublicKey(String modulusStr, String exponentStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        logger.debug("generate publicKey [modulusStr:{},exponentStr:{}]", modulusStr, exponentStr);
        BigInteger modulus = new BigInteger(modulusStr, 16);
        BigInteger exponent = new BigInteger(exponentStr, 16);
        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(modulus, exponent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(publicKeySpec);
    }

    public static PrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        logger.debug("generate privateKey:{}", privateKey);
        byte[] keyBytes = Base64.decodeBase64(privateKey.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    public static PrivateKey getPrivateKey(String modulusStr, String exponentStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        logger.debug("generate privateKey [modulusStr:{},exponentStr:{}]", modulusStr, exponentStr);
        BigInteger modulus = new BigInteger(modulusStr, 16);
        BigInteger exponent = new BigInteger(exponentStr, 16);
        RSAPrivateKeySpec privateKeySpec = new RSAPrivateKeySpec(modulus, exponent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(privateKeySpec);
    }

    public static byte[] encryptByRSA(String content, String publicKeyStr) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        logger.debug("encrypt [content:{},publicKeyStr:{}]", content, publicKeyStr);
        PublicKey publicKey = getPublicKey(publicKeyStr);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(content.getBytes());
    }

    public static byte[] encryptByRSA(String content, String modulusStr, String exponentStr) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        logger.debug("encrypt [content:{},modulusStr:{},exponentStr:{}]", content, modulusStr, exponentStr);
        PublicKey publicKey = getPublicKey(modulusStr, exponentStr);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(content.getBytes());
    }

    public static byte[] decryptByRSA(String content, String privateKeyStr) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        logger.debug("encrypt [content:{},privateKeyStr:{}]", content, privateKeyStr);
        PrivateKey privateKey = getPrivateKey(privateKeyStr);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(content.getBytes());
    }

    public static String decryptByRSA(String content, String modulusStr, String exponentStr) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        logger.debug("encrypt [content:{},modulusStr:{},exponentStr:{}]", content, modulusStr, exponentStr);
        byte[] contentByte = DataUtil.toByteArray(content);
        PrivateKey privateKey = getPrivateKey(modulusStr, exponentStr);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(contentByte));
    }

    public static String encoderByMD5(String content) throws NoSuchAlgorithmException {
        logger.debug("md5 encrypt [content:{}]", content);
        MessageDigest md5Digest = MessageDigest.getInstance("MD5");
        md5Digest.update(content.getBytes());
        return new BigInteger(1, md5Digest.digest()).toString(16);
    }

    public static String encoderBySha256(String content) {
        logger.debug("md5 encrypt [content:{}]", content);
        MessageDigest sha256Digest = null;
        try {
            sha256Digest = MessageDigest.getInstance("SHA-256");

            sha256Digest.update(content.getBytes());
        } catch (Exception e) {
            throw new ParseException("sha256 encode failed" ,"sha256 encode failed");
        }
        return new BigInteger(1, sha256Digest.digest()).toString(16);
    }

    public static String encoderBySha256(byte[] content) {
        logger.debug("md5 encrypt [content:{}]", content);
        MessageDigest sha256Digest = null;
        try {
            sha256Digest = MessageDigest.getInstance("SHA-256");

            sha256Digest.update(content);
        } catch (Exception e) {
            throw new ParseException("sha256 encode failed" ,"sha256 encode failed");
        }
        return Hex.encodeHexString(sha256Digest.digest());
    }

    public static String encoderByAES(String value, String privateKey) throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        logger.debug("aes param is [value:{},privateKey:{}]", value, privateKey);
        byte[] iv = privateKey.getBytes("utf-8");
        Key key = new SecretKeySpec(iv, "AES");
        String multiple_16 = value + 0;
        int length = multiple_16.length();
        int rest = length % 16;

        if (rest != 0) {
            for (int i = 0; i < 16 - rest; i++) {
                multiple_16 += " ";
            }
        }

        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] encodeValue = cipher.doFinal(multiple_16.getBytes());
        BASE64Encoder encoder = new BASE64Encoder();
        String base64Value = encoder.encode(encodeValue);
        String result = base64Value.replace("\n", "").replace("\r", "");
        logger.debug("encrypt is [{}]", result);
        return result;
    }

    public static String decoderByAES(String value, String privateKey) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        byte[] iv = privateKey.getBytes("utf-8");
        Key key = new SecretKeySpec(iv, "AES");
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] multiple16 = decoder.decodeBuffer(value);
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        byte[] decodeValue = cipher.doFinal(multiple16);
        String decodeString = new String(decodeValue, "utf-8");
        return decodeString.substring(0, decodeString.lastIndexOf('0'));
    }
}
