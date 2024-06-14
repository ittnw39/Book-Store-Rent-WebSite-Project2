package io.elice.shoppingmall.product.test;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.Test;

public class JasyptConfigTest {

    private final String SECRET_KEY = ""; //환경변수 값을 입력해야 함

    @Test
    void 복호화테스트() {
        //given
        String accessKey = "9MUkbayVHHOq3oeMzxvIxdo7hpmtrIsPMYtww8v1as8=";
        String secretKey = "T4aRO1YKrEgnNjjj/gxLdoWV9XOx243XNY5LPk9qRiqPTwc3c/6x7l/vUAq3CS5RoyE/X3Amq5s=";

        //when
        String decryptAccessKey = jasyptDecoding(accessKey);
        String decryptSecretKey = jasyptDecoding(secretKey);

        //then
        System.out.println("decryptAccessKey = " + decryptAccessKey);
        System.out.println("decryptSecretKey = " + decryptSecretKey);
    }

    public String jasyptDecoding(String value) {

        StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
        pbeEnc.setAlgorithm("PBEWithMD5AndDES");
        pbeEnc.setPassword(SECRET_KEY);
        return pbeEnc.decrypt(value);
    }
}