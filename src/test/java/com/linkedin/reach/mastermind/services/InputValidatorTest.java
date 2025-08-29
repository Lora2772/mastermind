package com.linkedin.reach.mastermind.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputValidatorTest {

    private InputValidator validator;

    @BeforeEach
    void setUp() {
        validator = new InputValidator();
    }

    @Test
    void test_valid_fourDigitsInRange() {
        assertTrue(validator.validate("0123"));
        assertTrue(validator.validate("7777"));
        assertTrue(validator.validate("0007"));
        assertTrue(validator.validate("7061"));
    }

    @Test
    void test_invalid_digitGreaterThanSeven() {
        assertFalse(validator.validate("1289"));
        assertFalse(validator.validate("9876"));
        assertFalse(validator.validate("7708"));
    }

    @Test
    void test_invalid_nonDigit() {
        assertFalse(validator.validate("12a3"));
        assertFalse(validator.validate("!234"));
        assertFalse(validator.validate("7 01"));
    }

    @Test
    void test_invalid_length() {
        assertFalse(validator.validate(""));
        assertFalse(validator.validate("1"));
        assertFalse(validator.validate("123"));
        assertFalse(validator.validate("01234"));
        assertFalse(validator.validate("01234567"));
    }

    @Test
    void test_invalid_null() {
        assertFalse(validator.validate(null));
    }
}
