package pl.aurora.projections.domain.sample.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.aurora.projections.domain.sample.service.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/sample")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SampleController {

    private final SampleService sampleService;

    private static final int OBJECTS_PER_ITERATION = 100000;
    private static final int ITERATIONS = 100;

    @GetMapping("/init")
    public void init() {
        sampleService.init(OBJECTS_PER_ITERATION);
    }

    @GetMapping("/test2")
    public void testTimes2() {

        List<Long> dynamicTimesByClass = new ArrayList<>();

        List<Long> dynamicTimesByInterface = new ArrayList<>();

        for (int x = 0; x < ITERATIONS; x++) {

            long classBefore = System.currentTimeMillis();
            sampleService.testDynamicProjection();
            long classAfter = System.currentTimeMillis();
            dynamicTimesByClass.add(classAfter - classBefore);

            long dynamicBefore = System.currentTimeMillis();
            sampleService.testDynamicProjectionByInterface();
            long dynamicAfter = System.currentTimeMillis();
            dynamicTimesByInterface.add(dynamicAfter - dynamicBefore);
        }

        printTime(dynamicTimesByClass, "Dynamic projections By Class took");
        printTime(dynamicTimesByInterface, "Dynamic projections By Interface took");
        printObjectCount();
    }

    @GetMapping("/test")
    public void testTimes() {
        List<Long> entityTimes = new ArrayList<>();
        List<Long> constructorTimes = new ArrayList<>();
        List<Long> interfaceTimes = new ArrayList<>();
        List<Long> tupleTimes = new ArrayList<>();
        List<Long> dynamicTimes = new ArrayList<>();

        for (int x = 0; x < ITERATIONS; x++) {
            //Entity projections
            long entityBefore = System.currentTimeMillis();
            sampleService.testEntityProjection();
            long entityAfter = System.currentTimeMillis();
            entityTimes.add(entityAfter - entityBefore);

            //Constructor projections
            long constructorBefore = System.currentTimeMillis();
            sampleService.testConstructorProjection();
            long constructorAfter = System.currentTimeMillis();
            constructorTimes.add(constructorAfter - constructorBefore);

            //Interface projections
            long interfaceBefore = System.currentTimeMillis();
            sampleService.testInterfaceProjection();
            long interfaceAfter = System.currentTimeMillis();
            interfaceTimes.add(interfaceAfter - interfaceBefore);

            //Tuple projections
            long tupleBefore = System.currentTimeMillis();
            sampleService.testTupleProjection();
            long tupleAfter = System.currentTimeMillis();
            tupleTimes.add(tupleAfter - tupleBefore);

            //Dynamic projections
            long dynamicBefore = System.currentTimeMillis();
            sampleService.testDynamicProjection();
            long dynamicAfter = System.currentTimeMillis();
            dynamicTimes.add(dynamicAfter - dynamicBefore);
        }

        printTime(entityTimes, "Entity projections took");
        printTime(constructorTimes, "Constructor projections took");
        printTime(interfaceTimes, "Interface projections took");
        printTime(tupleTimes, "Tuple projections took");
        printTime(dynamicTimes, "Dynamic projections took");

        printObjectCount();
    }

    private void printObjectCount(){
        log.info("-----------------------------------------------------------------------");
        log.info("One iteration retrieved (from DB) and projected {} objects.", OBJECTS_PER_ITERATION);
        log.info("-----------------------------------------------------------------------");
    }

    private void printTime(List<Long> millisList, String title) {

        double millis = millisList.stream().mapToDouble(x -> x).average().orElseThrow(IllegalArgumentException::new);
        log.info("{} is {} ms", title, millis);
        printSeconds(millis);
    }

    private void printSeconds(double millis) {
        double seconds = (double) ((millis / 1000) % 60);
        log.info("{} s", seconds);
        log.info("-------------------");
    }
}
