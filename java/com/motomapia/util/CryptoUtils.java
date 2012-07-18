/*
 */

package com.motomapia.util;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

/**
 * Some static methods to help working with MessageDigest
 * 
 * @author Jeff Schnitzer
 */
public class CryptoUtils
{
	/** */
	private static SecureRandom RANDOM = new SecureRandom();
	
	/** Masks the annoying exceptions */
	public static MessageDigest createDigestSHA256() {
		return createDigest("SHA-256");
	}

	/** Masks the annoying exceptions */
	public static MessageDigest createDigest(String algorithm) {
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/** Masks the annoying exceptions */
	public static Mac createMacHmacSHA256() {
		return createMac("HmacSHA256");
	}

	/** Masks the annoying exceptions */
	public static Mac createMac(String algorithm) {
		try {
			return Mac.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
	/** */
	public static SecretKey createSecretKeyHmacSHA256(byte[] secret) {
		return new SecretKeySpec(secret, "HmacSHA256");
	}
	
	/** Create the mac code for a message and secret */
	public static byte[] macHmacSHA256(String msg, byte[] secret) {
		Mac mac = CryptoUtils.createMacHmacSHA256();
		
		try { mac.init(createSecretKeyHmacSHA256(secret)); }
		catch (InvalidKeyException ex) { throw new RuntimeException(ex); }
		
		return mac.doFinal(StringUtils2.getBytesUTF8(msg));
	}
	
	/** Create the mac code for a message and secret, encoding it as hex */
	public static String macHmacSHA256Hex(String msg, byte[] secret) {
		return Hex.encodeHexString(macHmacSHA256(msg, secret));
	}
	
	/**
	 * Encrypts with a random IV that is prepended to the result 
	 */
	public static byte[] encryptAES(String msg, byte[] secret) {
		byte[] iv = new byte[16];
		RANDOM.nextBytes(iv);

		byte[] cipherText = encrypt(msg, new SecretKeySpec(secret, "AES"), iv, "AES/CBC/PKCS5Padding");
		
		byte[] both = new byte[iv.length + cipherText.length];
		System.arraycopy(iv, 0, both, 0, iv.length);
		System.arraycopy(cipherText, 0, both, iv.length, cipherText.length);
		
		return both;
	}
	
	/** */
	public static byte[] encrypt(String msg, Key secret, byte[] iv, String algorithm) {
		try {
			Cipher c = Cipher.getInstance(algorithm);
			c.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(iv));
			return c.doFinal(StringUtils2.getBytesUTF8(msg));
		} catch (GeneralSecurityException ex) {
			throw new RuntimeException(ex);
		}
	}

	/** 
	 * Expects that an IV is at the beginning (built with encryptAES())
	 */
	public static String decryptAES(byte[] cipherText, byte[] secret) {
		byte[] iv = Arrays.copyOfRange(cipherText, 0, 16);
		byte[] text = Arrays.copyOfRange(cipherText, 16, cipherText.length);
		return decrypt(text, new SecretKeySpec(secret, "AES"), iv, "AES/CBC/PKCS5Padding");
	}
	
	/** */
	public static String decrypt(byte[] cipherText, Key secret, byte[] iv, String algorithm) {
		try {
			Cipher c = Cipher.getInstance(algorithm);
			c.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
			return StringUtils2.newStringUTF8(c.doFinal(cipherText));
		} catch (GeneralSecurityException ex) {
			throw new RuntimeException(ex);
		}
	}
}