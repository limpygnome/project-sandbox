package com.limpygnome.projectsandbox.server.packet.datatype;

/**
 * Used to indicate the number of bytes per character.
 *
 * TODO: consider if this can be replaced with character encoding of some kind.
 */
public enum StringCharSize
{
    /**
     * One byte per character / 8 bits.
     */
    LENGTH_8_BITS,

    /**
     * Two bytes per character / 16 bits.
     */
    LENGTH_16_BITS
}
