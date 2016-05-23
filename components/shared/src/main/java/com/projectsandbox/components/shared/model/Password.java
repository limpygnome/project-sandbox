package com.projectsandbox.components.shared.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
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
public class Password implements Serializable
{
    private static final long serialVersionUID = 1L;

    private static final int SALT_LENGTH = 64;

    @Column(name = "password_salt", nullable = false)
    private String passwordSalt;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    public Password() { }

    public Password(String globalPasswordSalt, String password)
    {
        // Check global password salt is working correctly
        if (globalPasswordSalt == null)
        {
            throw new RuntimeException("Global password salt cannot be null");
        }

        // Generate random salt
        byte[] rawSalt = randomSalt(SALT_LENGTH);

        // Generate hash using password and salt data
        this.passwordHash = generateHash(globalPasswordSalt.getBytes(), rawSalt, password);
        this.passwordSalt = byte2Hex(rawSalt);
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
        // Check global password salt is working correctly
        if (globalPasswordSalt == null)
        {
            throw new RuntimeException("Global password salt cannot be null");
        }

        // Check provided password is at least valid
        if (password == null || password.length() == 0)
        {
            return false;
        }

        // Generate hash for specified password
        String hash = generateHash(globalPasswordSalt.getBytes(), hex2byte(this.passwordSalt), password);

        // Compare generated hash to current hash
        return hash.equals(passwordHash);
    }

    private static String generateHash(byte[] globalPasswordSalt, byte[] passwordSalt, String password)
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

    private static byte[] generateByteMess(byte[] bytesGlobalPasswordSalt, byte[] bytesPasswordSalt, String password)
    {
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
        StringBuilder buffer = new StringBuilder(data.length * 2);

        for(byte inputByte : data)
        {
            buffer.append(
                String.format("%02X", inputByte)
            );
        }

        return buffer.toString();
    }

    private static byte[] hex2byte(String data)
    {
        byte[] buffer = new byte[data.length() / 2];

        for (int i = 0; i < data.length(); i+= 2)
        {
            buffer[i / 2] = (byte) (
                (Character.digit(data.charAt(i), 16) << 4) +
                (Character.digit(data.charAt(i+1), 16))
            );
        }

        return buffer;
    }

    private static byte[] randomSalt(int length)
    {
        // Use time and rnd as seed
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.putLong(System.currentTimeMillis());
        byteBuffer.putDouble(Math.random());

        SecureRandom secureRandom = new SecureRandom(byteBuffer.array());

        // Create random array
        byte[] salt = new byte[length];

        for (int i = 0; i < length; i++)
        {
            salt[i] = (byte) secureRandom.nextInt(256);
        }

        return salt;
    }

}
