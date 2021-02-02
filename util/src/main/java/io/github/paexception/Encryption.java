package io.github.paexception;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Encryption {

	private KeyPair keyPair;
	private SecretKey key;
	private Cipher cipher;

	public Encryption(int bitAES, int bitRSA) throws NoSuchAlgorithmException {
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(bitAES);
		this.key = keyGen.generateKey();
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		keyPairGen.initialize(bitRSA);
		this.keyPair = keyPairGen.generateKeyPair();
	}

	public byte[] encryptAES(String bytes) throws GeneralSecurityException {
		return this.encryptAES(bytes.getBytes());
	}

	public byte[] encryptAES(byte[] bytes) throws GeneralSecurityException {
		return this.encryptAES(bytes, this.key);
	}

	public byte[] encryptAES(String bytes, SecretKey key) throws GeneralSecurityException {
		return this.encryptAES(bytes.getBytes(), key);
	}

	public byte[] encryptAES(byte[] bytes, SecretKey key) throws GeneralSecurityException {
		this.cipher = Cipher.getInstance("AES");
		this.cipher.init(Cipher.ENCRYPT_MODE, key);
		bytes = this.cipher.doFinal(bytes);

		return bytes;
	}

	public byte[] decryptAES(byte[] bytes) throws GeneralSecurityException {
		return this.decryptAES(bytes, this.key);
	}

	public byte[] decryptAES(byte[] bytes, SecretKey key) throws GeneralSecurityException {
		this.cipher = Cipher.getInstance("AES");
		this.cipher.init(Cipher.DECRYPT_MODE, key);
		bytes = this.cipher.doFinal(bytes);

		return bytes;
	}


	public byte[] encryptRSA(String bytes) throws GeneralSecurityException {
		return this.encryptRSA(bytes.getBytes());
	}

	public byte[] encryptRSA(byte[] bytes) throws GeneralSecurityException {
		return this.encryptRSA(bytes, this.keyPair.getPublic());
	}

	public byte[] encryptRSA(String bytes, PublicKey key) throws GeneralSecurityException {
		return this.encryptRSA(bytes.getBytes(), key);
	}

	public byte[] encryptRSA(byte[] bytes, PublicKey key) throws GeneralSecurityException {
		this.cipher = Cipher.getInstance("RSA");
		this.cipher.init(Cipher.ENCRYPT_MODE, key);
		bytes = this.cipher.doFinal(bytes);

		return bytes;
	}

	public byte[] decryptRSA(byte[] bytes) throws GeneralSecurityException {
		return this.decryptRSA(bytes, this.keyPair.getPrivate());
	}

	public byte[] decryptRSA(byte[] bytes, PrivateKey key) throws GeneralSecurityException {
		this.cipher = Cipher.getInstance("RSA");
		this.cipher.init(Cipher.DECRYPT_MODE, key);
		bytes = this.cipher.doFinal(bytes);

		return bytes;
	}


	public SecretKey getKey() {
		return this.key;
	}

	public void setKey(SecretKey key) {
		this.key = key;
	}

	public KeyPair getKeyPair() {
		return this.keyPair;
	}

	public void setKeyPair(KeyPair keyPair) {
		this.keyPair = keyPair;
	}

}
