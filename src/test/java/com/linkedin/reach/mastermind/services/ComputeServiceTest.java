package com.linkedin.reach.mastermind.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComputeServiceTest {

    private ComputeService computeService;

    @BeforeEach
    void setUp() {
        computeService = new ComputeService();
    }

    @Test
    void test_countCorrectNumbers_allOverlapPermutation() {
        assertEquals(4, computeService.countCorrectNumbers("0123", "3210"));
    }

    @Test
    void test_countCorrectNumbers_withDuplicates() {
        assertEquals(4, computeService.countCorrectNumbers("0011", "1010"));
    }

    @Test
    void test_countCorrectNumbers_partialOverlap() {
        assertEquals(1, computeService.countCorrectNumbers("0123", "0456"));
    }

    @Test
    void test_countCorrectNumbers_noOverlap() {
        assertEquals(0, computeService.countCorrectNumbers("0123", "4567"));
    }

    @Test
    void test_countCorrectLocations_allMatch() {
        assertEquals(4, computeService.countCorrectLocations("0123", "0123"));
    }

    @Test
    void test_countCorrectLocations_noneMatchPermutation() {
        assertEquals(0, computeService.countCorrectLocations("0123", "3210"));
    }

    @Test
    void test_countCorrectLocations_someMatch() {
        assertEquals(3, computeService.countCorrectLocations("0123", "0523"));
    }

    @Test
    void test_countCorrectLocations_duplicates() {
        assertEquals(4, computeService.countCorrectLocations("0011", "0011"));
    }
}
