package de.njsm.movielist.server.util;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PasswordEncoderTest {

    @Test
    public void test() {
        PasswordEncoder uut = new PasswordEncoder(120000, "hycGj7LfAyx1",
        new byte[]{
                (byte) 0xbe, (byte) 0x99, (byte) 0x10, (byte) 0xf0, (byte) 0x81, (byte) 0x77, (byte) 0xfc, (byte) 0x30, (byte) 0x53, (byte) 0xbc, (byte) 0x78, (byte) 0x02, (byte) 0x6d, (byte) 0x60, (byte) 0x65, (byte) 0xf5, (byte) 0xba, (byte) 0x96, (byte) 0x23, (byte) 0x9a, (byte) 0x5c, (byte) 0xa3, (byte) 0x4c, (byte) 0xdc, (byte) 0xb3, (byte) 0x44, (byte) 0xe6, (byte) 0xd4, (byte) 0xa0, (byte) 0x81, (byte) 0x61, (byte) 0xb2
        });

        assertTrue(uut.matches("testtesttest"));

    }
}
