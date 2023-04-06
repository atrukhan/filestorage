package com.company;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

public class RSA {


    private static long privateKey = 0;
    private static long publicKey = 0;
    private static long r = 0;

    public static long getPrivateKey() {
        return privateKey;
    }
    public RSA() {
        createKeys();
    }
    public RSA(long privateKey, long publicKey, long r) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.r = r;

    }

    public static void setPrivateKey(long privateKey) {
        RSA.privateKey = privateKey;
    }

    public static long getPublicKey() {
        return publicKey;
    }

    public static void setPublicKey(long publicKey) {
        RSA.publicKey = publicKey;
    }

    public static long getR() {
        return r;
    }

    public static void setR(long r) {
        RSA.r = r;
    }

    private static long filParamP(long p){
        long res = p;

        while (!BigInteger.valueOf(res).isProbablePrime((int) Math.log(res)) || res > 256*256/5 ){
            if(res > 256*256/5)
                res--;
            else
                res++;

        }

        return res;
    }

    private static long filParamQ(long p, long q){
        long res = q;

        while (!BigInteger.valueOf(res).isProbablePrime((int) Math.log(res)) || res * p > 256*256 || res * p < 256){
            if(res * p > 256*256)
                res = res/2;
            else
                res+= res/2;

        }

        return res;
    }

    public static boolean isCoprime(long a, long b){
        if (a % b == 0) {
            return false;
        } else {
            return true;
        }
    }

//    public static boolean isCoprime(long a, long b){
//        if (a == b) {
//            return a == 1;
//        } else {
//            if (a > b) {
//                return isCoprime(a - b, b);
//            } else {
//                return isCoprime(b - a, a);
//            }
//        }
//    }

    private static boolean isPrime(long a){
        for (long i = 2; i <= Math.sqrt(a); i++) {
            if (a % i == 0) {
                return false;
            }
        }
        return true;
    }

    private static long getE(long f){
        ArrayList<Long> valArr = new ArrayList<Long>();
        long e = f - 1;
        for (int i = 2; i < f; i++) {
            if (isPrime(e) && isCoprime(e, f)) {
                valArr.add(e);
            }
            e--;
        }
        Random random = new Random();
        int index = random.nextInt(valArr.size());
        return valArr.get(index);
//        return valArr.get(valArr.size()-2);
//        return 3;
    }


    public static long getExtendGcd(long a, long b) {
        long x0 = 1; long x1 = 0;
        long d0 = a; long d1 = b;
        long y0 = 0; long y1 = 1;
        long q = 0, d2 = 0, x2 = 0, y2 = 0;

        while (d1 > 1) {
            q = d0 / d1;
            d2 = d0 % d1;
            x2 = x0 - q * x1;
            y2 = y0 - q * y1;
            d0 = d1;
            d1 = d2;
            x0 = x1;
            x1 = x2;
            y0 = y1;
            y1 = y2;
        }
        return y1;

    }

    public static long power(long x, long y, long N) {
        if (y == 0) return 1;
        long z = power(x, y / 2, N);
        if (y % 2 == 0)
            return (z * z) % N;
        else
            return (x * z * z) % N;
    }

    public static byte[] rsaEncrypt(byte[] bytes, long e, long r) {
        String res = "";

        byte[] resBytes = new byte[bytes.length*2];
        byte[] tempByte = new byte[2];
        int index = 0;
        for (int i = 0; i < bytes.length; i++) {
            long temp = power(bytes[i], e, r);

            res = Long.toString(temp);
            tempByte = ByteBuffer.allocate(2).putShort((short)temp).array();
            resBytes[index++] = tempByte[0];
            resBytes[index++] = tempByte[1];
            System.out.println(tempByte[0] + "    " + tempByte[1]);
        }

        return resBytes;
    }

    public static byte[] rsaDecrypt(byte[] bytes, long d, long r) {
        byte[] res = new byte[bytes.length/2];
        byte[] tempBytes = new byte[2];
//        for(int i = 0; i < bytes.length; i++){
//            System.out.print(bytes[i]+" ");
//
//        }
//        System.out.println();
        int index =0;
        short num = 0;
        for (int i = 0; i < res.length; i++) {
            tempBytes[0] = bytes[index++];
            tempBytes[1] = bytes[index++];
//            System.out.println(tempBytes[0] + "    " + tempBytes[1]);
            num = ByteBuffer.wrap(tempBytes).getShort(0);
//            System.out.println("num= "+num);
            long temp = power(num, d, r);
            res[i] = (byte) (temp);
//            System.out.println(res[i]);
        }

        return res;
    }

    private static void createKeys() {
        Random random = new Random();

        long p = random.nextInt(100);
        long q = random.nextInt(100);

//            p = findSimpleNum(p, 256);
//            q = findSimpleNum(q, p);
        p = filParamP(p);
        q = filParamQ(p, q);


        r = p * q;
        long f = (p - 1) * (q - 1);
        publicKey = getE(f);
        privateKey = getExtendGcd(f, publicKey);
        if (privateKey < 0) {
            privateKey += f;
        }
    }
}
