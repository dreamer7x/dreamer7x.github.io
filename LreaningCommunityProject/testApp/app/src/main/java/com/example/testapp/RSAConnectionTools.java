package com.example.testapp;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSAConnectionTools {

    //内存数据
    private static byte[] privateKey;
    private static byte[] publicKey;
    private static boolean onInit;

    //配置参数
    private String encryptType; //加密格式
    private String encryptPadding; //填充方式
    private int keySize; //密钥长度
    private int bufferSize;

    //参数列表
    //默认密钥长度
    public static final int DEFAULT_KEY_SIZE = 2048; // 512 ~ 2048
    //填充方式
    public static final String ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";
    //加密格式
    public static final String RSA = "RSA";
    //分块间隔符
    public static final byte[] DEFAULT_SPLIT = "#PART#".getBytes();
    //默认支持加密最大字节数
    public static final int DEFAULT_BUFFER_SIZE = (DEFAULT_KEY_SIZE / 8) - 11;

    //初始化
    public RSAConnectionTools(){
        encryptType = RSA;
        encryptPadding = ECB_PKCS1_PADDING;
        keySize = DEFAULT_KEY_SIZE;
        bufferSize = DEFAULT_BUFFER_SIZE;
        if(!onInit) {
            initRSAConnectionToll();
            onInit = true;
        }
    }

    //初始化 获取密钥对
    private void initRSAConnectionToll(){
        try{
            //获取密钥对
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(encryptType);
            keyPairGenerator.initialize(keySize);
            KeyPair keyPair =  keyPairGenerator.genKeyPair();
            privateKey = keyPair.getPrivate().toString().getBytes();
            publicKey = keyPair.getPublic().toString().getBytes();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    //公钥加密
    public byte[] encryptByPublicKey(byte[] data){
        try {
            // 获取公钥
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            // 数据加密
            Cipher cipher = Cipher.getInstance(ECB_PKCS1_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //私钥加密
    public byte[] encryptByPrivateKey(byte[] data){
        try {
            KeyFactory keyFactory = null;
            PrivateKey keyPrivate = null;
            // 获取私钥
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
            keyFactory = KeyFactory.getInstance(RSA);
            keyPrivate = keyFactory.generatePrivate(keySpec);
            // 数据加密
            Cipher cipher = Cipher.getInstance(ECB_PKCS1_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, keyPrivate);
            return cipher.doFinal(data);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //公钥解密工具
    public byte[] decryptByPublicKey(byte[] encryptData){
        try {
            // 得到公钥
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            PublicKey keyPublic = keyFactory.generatePublic(keySpec);
            // 数据解密
            Cipher cipher = Cipher.getInstance(ECB_PKCS1_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, keyPublic);
            return cipher.doFinal(encryptData);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //私钥解密工具
    public byte[] decryptByPrivateKey(byte[] encryptData) {
        try {
            // 获取私钥
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            PrivateKey keyPrivate = keyFactory.generatePrivate(keySpec);
            // 解密数据
            Cipher cipher = Cipher.getInstance(ECB_PKCS1_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, keyPrivate);
            return cipher.doFinal(encryptData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
