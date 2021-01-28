package io.github.paexception.mcsws.server.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Encryption {

	private KeyPair keyPair;
	private SecretKey key;
	private Cipher cipher;

	public Encryption(int bitAES, int bitRSA) {
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(bitAES);
			this.key = keyGen.generateKey();
			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
			keyPairGen.initialize(bitRSA);
			this.keyPair = keyPairGen.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public Encryption() {
		new Encryption(256, 4096);
	}


	public byte[] encryptAES(String bytes) {
		return this.encryptAES(bytes.getBytes());
	}

	public byte[] encryptAES(byte[] bytes) {
		return this.encryptAES(bytes, this.key);
	}

	public byte[] encryptAES(String bytes, SecretKey key) {
		return this.encryptAES(bytes.getBytes(), key);
	}

	public byte[] encryptAES(byte[] bytes, SecretKey key) {
		try {
			this.cipher = Cipher.getInstance("AES");
			this.cipher.init(Cipher.ENCRYPT_MODE, key);
			bytes = this.cipher.doFinal(bytes);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	public byte[] decryptAES(byte[] bytes) {
		return this.decryptAES(bytes, this.key);
	}

	public byte[] decryptAES(byte[] bytes, SecretKey key) {
		try {
			this.cipher = Cipher.getInstance("AES");
			this.cipher.init(Cipher.DECRYPT_MODE, key);
			bytes = this.cipher.doFinal(bytes);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
			e.printStackTrace();
		}
		return bytes;
	}


	public byte[] encryptRSA(String bytes) {
		return this.encryptRSA(bytes.getBytes());
	}

	public byte[] encryptRSA(byte[] bytes) {
		return this.encryptRSA(bytes, this.keyPair.getPublic());
	}

	public byte[] encryptRSA(String bytes, PublicKey key) {
		return this.encryptRSA(bytes.getBytes(), key);
	}

	public byte[] encryptRSA(byte[] bytes, PublicKey key) {
		try {
			this.cipher = Cipher.getInstance("RSA");
			this.cipher.init(Cipher.ENCRYPT_MODE, key);
			bytes = this.cipher.doFinal(bytes);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}

		return bytes;
	}

	public byte[] decryptRSA(byte[] bytes) {
		return this.decryptRSA(bytes, this.keyPair.getPrivate());
	}

	public byte[] decryptRSA(byte[] bytes, PrivateKey key) {
		try {
			this.cipher = Cipher.getInstance("RSA");
			this.cipher.init(Cipher.DECRYPT_MODE, key);
			bytes = this.cipher.doFinal(bytes);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}

		return bytes;
	}


	public SecretKey getKey() {
		return this.key;
	}

	public KeyPair getKeyPair() {
		return this.keyPair;
	}

	public void setKeyPair(KeyPair keyPair) {
		this.keyPair = keyPair;
	}

	public void setKey(SecretKey key) {
		this.key = key;
	}

}
