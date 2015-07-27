package com.limpygnome.projectsandbox.shared.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * Controls access to the password.
 *
 * The idea is that it's impossible to release the hash and salt, since once the password is created,
 * they cannot be accessed.
 */
@Embeddable
public class Password
{
    private static final int SALT_LENGTH = 64;

    @Column(name = "password_salt", nullable = false)
    private String passwordSalt;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    public Password() { }

    public Password(String globalPasswordSalt, String password)
    {
        // Generate random salt
        this.passwordSalt = randomSalt(SALT_LENGTH);

        // Generate hash using password and salt data
        this.passwordHash = generateHash(globalPasswordSalt, passwordSalt, password);
    }

    /**
     * Indicates if the provided password is the same as the internally stored password.
     *
     * @param globalPasswordSalt The global salt
     * @param password The password to test
     * @return True = valid/same, false = invalid/not the same
     */
    public boolean isValid(String globalPasswordSalt, String password)
    {
        String hash = generateHash(globalPasswordSalt, this.passwordSalt, password);

        return hash.equals(passwordHash);
    }

    private static String generateHash(String globalPasswordSalt, String passwordSalt, String password)
    {
        byte[] input = generateByteMess(globalPasswordSalt, passwordSalt, password);

        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            byte[] output = messageDigest.digest(input);

            return byte2Hex(output);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to do crypto to generate password");
        }
    }

    private static byte[] generateByteMess(String globalPasswordSalt, String passwordSalt, String password)
    {
        byte[] bytesGlobalPasswordSalt = globalPasswordSalt.getBytes();
        byte[] bytesPasswordSalt = passwordSalt.getBytes();
        byte[] bytesPassword = password.getBytes();

        // Copy salt and password together
        byte[] mess = new byte[bytesGlobalPasswordSalt.length + bytesPasswordSalt.length + bytesPassword.length];

        System.arraycopy(bytesGlobalPasswordSalt, 0, mess, 0, bytesGlobalPasswordSalt.length);
        System.arraycopy(bytesPasswordSalt, 0, mess, bytesGlobalPasswordSalt.length, bytesPasswordSalt.length);
        System.arraycopy(bytesPassword, 0, mess, bytesGlobalPasswordSalt.length + bytesPasswordSalt.length, bytesPassword.length);

        // Do some arithmetic
        mess = generateByteMessArithmeticScramblePass(mess, bytesPasswordSalt);
        mess = generateByteMessArithmeticScramblePass(mess, bytesPassword);

        return mess;
    }

    private static byte[] generateByteMessArithmeticScramblePass(byte[] input, byte[] salt)
    {
        byte inputByte;

        for (int i = 0; i < input.length; i++)
        {
            inputByte = input[i];

            if (i % 2 == 0)
            {
                inputByte += salt[circularRound(salt.length, i)];
            }
            else
            {
                inputByte -= salt[circularRound(salt.length, i)];
            }

            input[i] = inputByte;
        }

        return input;
    }

    private static int circularRound(int upperBound, int value)
    {
        while (value >= upperBound)
        {
            value -= upperBound;
        }

        return value;
    }

    private static String byte2Hex(byte[] data)
    {
        StringBuilder buffer = new StringBuilder();

        for(byte inputByte : data)
        {
            buffer.append(
                Integer.toHexString(0xFF & inputByte)
            );
        }

        return buffer.toString();
    }

    private static String randomSalt(int length)
    {
        StringBuilder buffer = new StringBuilder();

        // Use time and rnd as seed
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.putLong(System.currentTimeMillis());
        byteBuffer.putDouble(Math.random());

        SecureRandom secureRandom = new SecureRandom(byteBuffer.array());

        int charCode;
        for (int i = 0; i < length; i++)
        {
            charCode = 33 + secureRandom.nextInt(93);
            buffer.append((char) charCode);
        }

        return buffer.toString();
    }

}
