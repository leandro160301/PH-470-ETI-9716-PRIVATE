package com.jws.jwsapi;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;

import com.jws.jwsapi.caliber.CaliberRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class CaliberRepositoryTest {


    @Mock
    private Context mockContext;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCalibers_ReturnsFixedList() {
        List<String> expectedCalibers = Arrays.asList("Caliber1", "Caliber2", "Caliber3");

        // Asume que el método getCalibers retorna una lista fija
        when(CaliberRepository.getCalibers(mockContext)).thenReturn(expectedCalibers);

        // Llama al método y verifica el resultado
        List<String> actualCalibers = CaliberRepository.getCalibers(mockContext);
        assertEquals(expectedCalibers, actualCalibers);
    }
}