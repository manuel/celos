package com.collective.celos;

import static com.collective.celos.SlotState.Status.FAILURE;
import static com.collective.celos.SlotState.Status.READY;
import static com.collective.celos.SlotState.Status.RUNNING;
import static com.collective.celos.SlotState.Status.SUCCESS;
import static com.collective.celos.SlotState.Status.WAITING;

import java.util.Arrays;
import java.util.List;

import com.collective.celos.api.ScheduledTime;
import com.collective.celos.api.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SerialSchedulingStrategyTest {

    private SerialSchedulingStrategy makeSerialSchedulingStrategy() {
        ObjectNode node = Util.newObjectNode();
        node.put(SerialSchedulingStrategy.SLOTS_NUMBER_PROP, 1);
        return new SerialSchedulingStrategy(node);
    }

    private SerialSchedulingStrategy makeSerialSchedulingStrategyConcurrent() {
        ObjectNode node = Util.newObjectNode();
        node.put(SerialSchedulingStrategy.SLOTS_NUMBER_PROP, 3);
        return new SerialSchedulingStrategy(node);
    }


    private WorkflowID workflow;
    private SlotID slot1;
    private SlotID slot2;
    private SlotID slot3;
    private SlotID slot4;
    private SlotID slot5;
    private SlotID slot6;

    @Before
    public void setUp() {
        workflow = new WorkflowID("foo");
        slot1 = new SlotID(workflow, new ScheduledTime(
                "2013-11-18T20:00Z"));
        slot2 = new SlotID(workflow, new ScheduledTime(
                "2013-11-18T21:00Z"));
        slot3 = new SlotID(workflow, new ScheduledTime(
                "2013-11-18T22:00Z"));
        slot4 = new SlotID(workflow, new ScheduledTime(
                "2013-11-18T23:00Z"));
        slot5 = new SlotID(workflow, new ScheduledTime(
                "2013-11-19T00:00Z"));
        slot6 = new SlotID(workflow, new ScheduledTime(
                "2013-11-19T01:00Z"));
    }

    @Test
    public void testHasNoSlots() {
        List<SlotState> input = Arrays
                .asList(new SlotState[] {});
        List<SlotState> expected = Arrays.asList(new SlotState[] {});

        SchedulingStrategy strategy = makeSerialSchedulingStrategy();
        Assert.assertEquals(expected, strategy.getSchedulingCandidates(input));
    }

    @Test
    public void testHasRunningSlot() {
        List<SlotState> input = Arrays
                .asList(new SlotState[] {
                        new SlotState(slot1, FAILURE),
                        new SlotState(slot1, READY),
                        new SlotState(slot2, RUNNING),
                        new SlotState(slot3, SUCCESS),
                        new SlotState(slot4, READY),
                        new SlotState(slot5, WAITING), });
        List<SlotState> expected = Arrays.asList(new SlotState[] {});

        SchedulingStrategy strategy = makeSerialSchedulingStrategy();
        Assert.assertEquals(expected, strategy.getSchedulingCandidates(input));
    }

    @Test
    public void testHasReadySlot() {
        SlotState scheduledSlot = new SlotState(slot1, READY);
        List<SlotState> input = Arrays
                .asList(new SlotState[] {
                        new SlotState(slot1, FAILURE),
                        scheduledSlot,
                        new SlotState(slot3, SUCCESS),
                        new SlotState(slot4, WAITING),
                        new SlotState(slot5, WAITING), });
        List<SlotState> expected = Arrays.asList(new SlotState[] { scheduledSlot });

        SchedulingStrategy strategy = makeSerialSchedulingStrategy();
        Assert.assertEquals(expected, strategy.getSchedulingCandidates(input));
    }

    @Test
    public void testHasMultipleReadySlots() {
        SlotState scheduledSlot = new SlotState(slot1, READY);
        List<SlotState> input = Arrays
                .asList(new SlotState[] {
                        new SlotState(slot1, FAILURE),
                        scheduledSlot,
                        new SlotState(slot3, SUCCESS),
                        new SlotState(slot4, READY),
                        new SlotState(slot5, READY), });
        List<SlotState> expected = Arrays.asList(new SlotState[] { scheduledSlot });

        SchedulingStrategy strategy = makeSerialSchedulingStrategy();
        Assert.assertEquals(expected, strategy.getSchedulingCandidates(input));
    }

    @Test
    public void testHasNoReadySlots() {
        List<SlotState> input = Arrays
                .asList(new SlotState[] {
                        new SlotState(slot1, FAILURE),
                        new SlotState(slot1, WAITING),
                        new SlotState(slot3, SUCCESS),
                        new SlotState(slot4, WAITING),
                        new SlotState(slot5, SUCCESS), });
        List<SlotState> expected = Arrays.asList(new SlotState[] {});

        SchedulingStrategy strategy = makeSerialSchedulingStrategy();
        Assert.assertEquals(expected, strategy.getSchedulingCandidates(input));
    }

    @Test
    public void testHasNoSlotsConcurrent() {
        List<SlotState> input = Arrays
                .asList(new SlotState[] {});
        List<SlotState> expected = Arrays.asList(new SlotState[] {});

        SchedulingStrategy strategy = makeSerialSchedulingStrategyConcurrent();
        Assert.assertEquals(expected, strategy.getSchedulingCandidates(input));
    }

    @Test
    public void testHasRunningSlotConcurrent() {
        SlotState slotState1 = new SlotState(slot1, READY);
        SlotState slotState2 = new SlotState(slot4, READY);
        List<SlotState> input = Arrays
                .asList(new SlotState[] {
                        new SlotState(slot1, FAILURE),
                        slotState1,
                        new SlotState(slot2, RUNNING),
                        new SlotState(slot3, SUCCESS),
                        slotState2,
                        new SlotState(slot5, WAITING), });
        List<SlotState> expected = Arrays.asList(new SlotState[] {slotState1, slotState2});

        SchedulingStrategy strategy = makeSerialSchedulingStrategyConcurrent();
        Assert.assertEquals(expected, strategy.getSchedulingCandidates(input));
    }

    @Test
    public void testHasReadySlotConcurrent() {
        SlotState scheduledSlot = new SlotState(slot1, READY);
        List<SlotState> input = Arrays
                .asList(new SlotState[] {
                        new SlotState(slot1, FAILURE),
                        scheduledSlot,
                        new SlotState(slot3, SUCCESS),
                        new SlotState(slot4, WAITING),
                        new SlotState(slot5, WAITING), });
        List<SlotState> expected = Arrays.asList(new SlotState[] { scheduledSlot });

        SchedulingStrategy strategy = makeSerialSchedulingStrategyConcurrent();
        Assert.assertEquals(expected, strategy.getSchedulingCandidates(input));
    }

    @Test
    public void testHasMultipleReadySlotsConcurrent() {
        SlotState scheduledSlot1 = new SlotState(slot1, READY);
        SlotState scheduledSlot2 = new SlotState(slot4, READY);
        SlotState scheduledSlot3 = new SlotState(slot5, READY);
        List<SlotState> input = Arrays
                .asList(new SlotState[] {
                        new SlotState(slot1, FAILURE),
                        scheduledSlot1,
                        new SlotState(slot3, SUCCESS),
                        scheduledSlot2,
                        scheduledSlot3});
        List<SlotState> expected = Arrays.asList(new SlotState[] { scheduledSlot1, scheduledSlot2, scheduledSlot3 });

        SchedulingStrategy strategy = makeSerialSchedulingStrategyConcurrent();
        Assert.assertEquals(expected, strategy.getSchedulingCandidates(input));
    }

    @Test
    public void testHasMultipleReadySlotsConcurrentMoreThanReady() {
        SlotState scheduledSlot1 = new SlotState(slot1, READY);
        SlotState scheduledSlot2 = new SlotState(slot4, READY);
        SlotState scheduledSlot3 = new SlotState(slot5, READY);
        SlotState scheduledSlot6 = new SlotState(slot6, READY);
        List<SlotState> input = Arrays
                .asList(new SlotState[] {
                        new SlotState(slot1, FAILURE),
                        scheduledSlot1,
                        new SlotState(slot3, SUCCESS),
                        scheduledSlot2,
                        scheduledSlot3,
                        scheduledSlot6
                });
        List<SlotState> expected = Arrays.asList(new SlotState[] { scheduledSlot1, scheduledSlot2, scheduledSlot3});

        SchedulingStrategy strategy = makeSerialSchedulingStrategyConcurrent();
        Assert.assertEquals(expected, strategy.getSchedulingCandidates(input));
    }


    @Test
    public void testHasNoReadySlotsConcurrent() {
        List<SlotState> input = Arrays
                .asList(new SlotState[] {
                        new SlotState(slot1, FAILURE),
                        new SlotState(slot1, WAITING),
                        new SlotState(slot3, SUCCESS),
                        new SlotState(slot4, WAITING),
                        new SlotState(slot5, SUCCESS), });
        List<SlotState> expected = Arrays.asList(new SlotState[] {});

        SchedulingStrategy strategy = makeSerialSchedulingStrategyConcurrent();
        Assert.assertEquals(expected, strategy.getSchedulingCandidates(input));
    }

}
