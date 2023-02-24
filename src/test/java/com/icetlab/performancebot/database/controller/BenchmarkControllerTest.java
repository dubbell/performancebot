package com.icetlab.performancebot.database.controller;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Optional;
import com.icetlab.performancebot.database.model.Benchmark;
import com.icetlab.performancebot.database.repository.BenchmarkRepository;

public class BenchmarkControllerTest {

    private BenchmarkRepository mockRepository;
    private BenchmarkController controller;

    @BeforeEach
    public void setup() {
        mockRepository = Mockito.mock(BenchmarkRepository.class);
        controller = new BenchmarkController();
        ReflectionTestUtils.setField(controller, "benchmarkRepository", mockRepository);
    }

    @Test
    public void testGetBenchmarkById() {
        Benchmark expected = new Benchmark("1", "{\"result\": 123}", "project-1");
        Mockito.when(mockRepository.findById("1")).thenReturn(Optional.of(expected));
        Benchmark actual = controller.getBenchmarkById("1");
        assertEquals(expected, actual);
    }

    @Test
    public void testGetBenchmarkByIdNotFound() {
        Mockito.when(mockRepository.findById("1")).thenReturn(Optional.empty());
        Benchmark actual = controller.getBenchmarkById("1");
        assertNull(actual);
    }

    @Test
    public void testAddBenchmark() {
        String runData = "{\"result\": 123}";
        String projectId = "project-1";
        Benchmark expected = new Benchmark("0", runData, projectId);
        Mockito.when(mockRepository.count()).thenReturn(0L);
        Benchmark b = controller.addBenchmark(runData, projectId);
        assertEquals(expected.getId(), b.getId());
    }
}
