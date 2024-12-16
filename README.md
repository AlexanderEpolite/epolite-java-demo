# EPOLITE Java Library

The Java library for EPOLITE allows for Post Quantum encryption using public/private keypairs.

The public key can perform the Verifying and Encrypting operations, whereas the private key can perform the Signing and Decrypting operations.

## Setting up with Maven

Add to your dependencies in the `pom.xml` file:
```xml
    <dependencies>
        <!-- add this to use the EPOLITE library, it is on the Maven Central repository -->
        <!-- Refer to the README.md on how to use the library -->
        <dependency>
            <groupId>net.epolite</groupId>
            <artifactId>epolite-java</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>
```

Sync with Maven and you can start using the library right away.

## Encryption

A user who knows another's public key can use it to encrypt a message.  The returned `byte[]` message can only be decrypted
by one who has the private key (usually and _hopefully_ just the owner of the key).

This encryption allows someone who has never contacted another to fully encrypt a message.  It is similar to locking a box with a letter inside
and sending it to someone, only the key can only be opened with the EPOLITE private key, which is (practically) unbreakable.

```java
import net.epolite.EPOLITE;
//...
public static void main(String[] args) throws Exception {
    EPOLITE.EPOLITEKeypair kp = EPOLITE.createEpoliteKeypair();

    //initial message (must be a byte array)
    //in a production application, the secret message should never be a string
    byte[] message_to_encrypt = "Secret Message".getBytes(StandardCharsets.UTF_8);

    //encrypt using the public key (which is a byte[])
    byte[] encrypted_message = EPOLITE.encrypt(message_to_encrypt, kp.publicKey);

    System.out.printf("Encrypted message length: %d\n", encrypted_message.length);

    //decrypt using the private key (which is a byte[])
    byte[] decrypted_message = EPOLITE.decrypt(encrypted_message, kp.privateKey);

    System.out.printf("Decrypted: %s\n", new String(decrypted_message));
}
```

## Digital Signatures

Think of Digital Signatures as writing your signature on a piece of paper to verify you read it, but instead your signature
is _unforgeable_, so no one can ever forge your signature, as long as others verify with the correct public key.

With the signer's public key, you can verify that their private key signed the message, providing proof of identity
without ever having to have access to their private key.

```java
import net.epolite.EPOLITE;
//...
public static void main(String[] args) throws Exception {
    EPOLITE.EPOLITEKeypair kp = EPOLITE.createEpoliteKeypair();
    
    //Signing and verifying messages
    byte[] message_to_sign = "Awesome signed message".getBytes(StandardCharsets.UTF_8);
    
    byte[] signed_message = EPOLITE.sign(message_to_sign, kp.privateKey);
    
    System.out.printf("Signed message: %s...\n", Arrays.toString(signed_message).substring(0, 64));
    
    //verify message using the signer's public key
    EPOLITE.VerifiedState vs = EPOLITE.verify(signed_message, kp.publicKey);
    
    //will return true along with the message content
    System.out.printf("Verified successfully: %b; content: %s\n", vs.verified, new String(vs.signedMessage));
}
```

Do be aware, that the content in `signedMessage` will be returned, even if the message is not verified!  You must check
if the `verified` parameter from the `verify` method is true to ensure it was signed correctly.


## Storing Keys

Private keys should always be encrypted in-memory before being stored on-disk.  Public keys, depending on the application,
may be stored unencrypted, or uploaded to keyservers online for others to discover.  No such keyserver currently exists for
EPOLITE keys; however, custom ones could be made if need be.

The private key should only be held by the owner.  They should never be shared, and in an enterprise setting, users should
have their own private keys, instead of sharing them.