package com.acme.coffeshop;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class InputRulesTest {
    @Test
    void enforcesUsernameAndPasswordBounds() {
        assertTrue(InputRules.validUsername("shop_user-1"));
        assertFalse(InputRules.validUsername("ab"));
        assertFalse(InputRules.validUsername("bad name"));
        assertTrue(InputRules.validPassword("long enough password"));
        assertFalse(InputRules.validPassword("short"));
    }
}
