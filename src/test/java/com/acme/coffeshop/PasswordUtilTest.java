package com.acme.coffeshop;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PasswordUtilTest {
    @Test
    void createsDistinctSaltsAndVerifiesOnlyTheMatchingPassword() throws Exception {
        PasswordUtil.Credential first = PasswordUtil.create("correct horse battery");
        PasswordUtil.Credential second = PasswordUtil.create("correct horse battery");

        assertNotEquals(java.util.Arrays.toString(first.getSalt()), java.util.Arrays.toString(second.getSalt()));
        assertTrue(PasswordUtil.verify("correct horse battery", first.getHash(), first.getSalt()));
        assertFalse(PasswordUtil.verify("incorrect password", first.getHash(), first.getSalt()));
    }
}
