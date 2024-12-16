package aepolite;

import net.epolite.EPOLITE;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {
        //create a new keypair
        EPOLITE.EPOLITEKeypair kp = EPOLITE.createEpoliteKeypair();
        
        //initial message (must be a byte array)
        byte[] message_to_encrypt = "Secret Message".getBytes(StandardCharsets.UTF_8);
        
        //encrypt using the public key (which is a byte[])
        byte[] encrypted_message = EPOLITE.encrypt(message_to_encrypt, kp.publicKey);
        
        System.out.printf("Encrypted message length: %d\n", encrypted_message.length);
        
        //decrypt using the private key (which is a byte[])
        byte[] decrypted_message = EPOLITE.decrypt(encrypted_message, kp.privateKey);
        
        System.out.printf("Decrypted: %s\n", new String(decrypted_message));
        
        //Signing and verifying messages
        byte[] message_to_sign = "Awesome signed message".getBytes(StandardCharsets.UTF_8);
        
        byte[] signed_message = EPOLITE.sign(message_to_sign, kp.privateKey);
        
        System.out.printf("Signed message: %s...\n", Arrays.toString(signed_message).substring(0, 64));
        
        //verify message using the signer's public key
        EPOLITE.VerifiedState vs = EPOLITE.verify(signed_message, kp.publicKey);

        System.out.printf("Verified successfully: %b; content: %s\n", vs.verified, new String(vs.signedMessage));
    }
}