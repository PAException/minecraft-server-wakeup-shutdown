package io.github.paexception;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

public class EncryptedSocket {

	private final Socket socket;
	private Encryption encryption;
	private boolean handshake;

	public EncryptedSocket(String host, int port) throws IOException {
		this.socket = new Socket(host, port);
		try {
			this.encryption = new Encryption(256, 1024);
		} catch (NoSuchAlgorithmException e) {//Should never be thrown
		}
	}

	public EncryptedSocket(Socket socket) {
		this.socket = socket;
		try {
			this.encryption = new Encryption(256, 1024);
		} catch (NoSuchAlgorithmException e) {//Should never be thrown
		}
	}

	public void handshakeServer() throws IOException, GeneralSecurityException {
		this.writeUnencryptedBytes(this.encryption.getKeyPair().getPublic().getEncoded());
		this.encryption.setKey(new SecretKeySpec(this.encryption
				.decryptRSA(this.readUnencryptedBytes()), "AES"));
		this.handshake = true;
	}

	public void handshakeClient() throws IOException, GeneralSecurityException {
		PublicKey publicKey = KeyFactory.getInstance("RSA")
				.generatePublic(new X509EncodedKeySpec(this.readUnencryptedBytes()));
		this.writeUnencryptedBytes(this.encryption.encryptRSA(this.encryption.getKey().getEncoded(), publicKey));
		this.handshake = true;
	}

	public void write(String msg) throws IOException, GeneralSecurityException {
		if (!this.handshake) throw new IOException("Handshake wasn't made yet, cannot write");

		this.writeBytes(msg.getBytes());
	}

	public String read() throws IOException, GeneralSecurityException {
		if (!this.handshake) throw new IOException("Handshake wasn't made yet, cannot read");

		return new String(this.readBytes());
	}

	public void writeBytes(byte[] bytes) throws IOException, GeneralSecurityException {
		if (!this.handshake) throw new IOException("Handshake wasn't made yet, cannot write");

		this.writeUnencryptedBytes(this.encryption.encryptAES(bytes));
	}

	public byte[] readBytes() throws IOException, GeneralSecurityException {
		if (!this.handshake) throw new IOException("Handshake wasn't made yet, cannot read");

		return this.encryption.decryptAES(this.readUnencryptedBytes());
	}

	private void writeUnencryptedBytes(byte[] bytes) throws IOException {
		this.socket.getOutputStream().write(ByteBuffer.allocate(4).putInt(bytes.length).array());
		this.socket.getOutputStream().write(bytes);
		this.socket.getOutputStream().flush();
	}

	private byte[] readUnencryptedBytes() throws IOException {
		byte[] buffer = new byte[ByteBuffer.wrap(this.socket.getInputStream().readNBytes(4)).getInt()];
		this.socket.getInputStream().readNBytes(buffer, 0, buffer.length);

		return buffer;
	}

	public Socket getSocket() {
		return this.socket;
	}

}
