package com.example.demo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DemoApplicationTests {

    /**
     * JUnit test template. BEGIN
     */
    Calculator testCalculator = new Calculator();

    @Test
    void itShouldAddTwoNumbers() {
        // given (the inputs)
        int firstNumber = 15;
        int secondNumber = 8;

        // when (invoke the method to test, here is add method)
        int result = testCalculator.add(firstNumber, secondNumber);

        // then, perform assertion (here is AssertJ method)
        int expected = 23;
        assertThat(result).isEqualTo(expected);
    }

    class Calculator {
        int add(int a, int b) {
            return a + b;
        }
    }
    /**
     * JUnit test template. END
     * */
}
