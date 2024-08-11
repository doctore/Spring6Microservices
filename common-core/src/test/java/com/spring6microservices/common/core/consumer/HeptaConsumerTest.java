package com.spring6microservices.common.core.consumer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HeptaConsumerTest {

    @Test
    @DisplayName("accept: then the defined operation is performed based on provided arguments")
    public void accept_thenTheDefinedOperationIsPerformedBasedOnProvidedArguments() {
        List<Object> list = asList(
                10,
                "abc",
                true,
                15,
                "12",
                false,
                20
        );
        HeptaConsumer<Integer, String, Boolean, Integer, String, Boolean, Integer> plusNAndAddSAndChangeB = (i1, s1, b1, i2, s2, b2, i3) -> {
            list.set(0, (Integer)list.get(0) + i1);
            list.set(1, list.get(1) + s1);
            list.set(2, b1);
            list.set(3, (Integer)list.get(3) + i2);
            list.set(4, list.get(4) + s2);
            list.set(5, b2);
            list.set(6, (Integer)list.get(6) + i3);
        };
        plusNAndAddSAndChangeB.accept(
                5,
                "V2",
                false,
                7,
                "34",
                true,
                9
        );

        assertEquals(15, list.get(0));
        assertEquals("abcV2", list.get(1));
        assertEquals(false, list.get(2));
        assertEquals(22, list.get(3));
        assertEquals("1234", list.get(4));
        assertEquals(true, list.get(5));
        assertEquals(29, list.get(6));
    }


    @Test
    @DisplayName("andThen: when after is null then NullPointerException is thrown")
    public void andThen_whenAfterIsNull_thenNullPointerExceptionIsThrown() {
        List<Object> list = asList(
                10,
                "abc",
                true,
                15,
                "12",
                false,
                20
        );
        HeptaConsumer<Integer, String, Boolean, Integer, String, Boolean, Integer> plusNAndAddSAndChangeB = (i1, s1, b1, i2, s2, b2, i3) -> {
            list.set(0, (Integer)list.get(0) + i1);
            list.set(1, list.get(1) + s1);
            list.set(2, b1);
            list.set(3, (Integer)list.get(3) + i2);
            list.set(4, list.get(4) + s2);
            list.set(5, b2);
            list.set(6, (Integer)list.get(6) + i3);
        };

        assertThrows(
                NullPointerException.class,
                () -> plusNAndAddSAndChangeB.andThen(null)
        );
    }


    @Test
    @DisplayName("andThen: when after is not null then after is applied after current consumer")
    public void andThen_whenAfterIsNotNull_thenAfterIsAppliedAfterCurrentConsumer() {
        List<Object> list = asList(
                10,
                "abc",
                true,
                15,
                "12",
                false,
                20
        );
        HeptaConsumer<Integer, String, Boolean, Integer, String, Boolean, Integer> plusNAndAddSAndChangeB = (i1, s1, b1, i2, s2, b2, i3) -> {
            list.set(0, (Integer)list.get(0) + i1);
            list.set(1, list.get(1) + s1);
            list.set(2, b1);
            list.set(3, (Integer)list.get(3) + i2);
            list.set(4, list.get(4) + s2);
            list.set(5, b2);
            list.set(6, (Integer)list.get(6) + i3);
        };
        plusNAndAddSAndChangeB.andThen(
                plusNAndAddSAndChangeB
        ).accept(
                5,
                "V2",
                false,
                7,
                "34",
                true,
                9
        );

        assertEquals(20, list.get(0));
        assertEquals("abcV2V2", list.get(1));
        assertEquals(false, list.get(2));
        assertEquals(29, list.get(3));
        assertEquals("123434", list.get(4));
        assertEquals(true, list.get(5));
        assertEquals(38, list.get(6));
    }

}
