package com.example.demo.student;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private StudentRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    /**
     * In order to avoid introducing the entities created for tests
     * into the persistent database, we will have to work with
     * H2 in memory database.
     * */
    @Test
    void shouldTestIfStudentExistsEmail() {
        // given
        String email = "johnny@mail.com";
        Student student = new Student(
                "Johnny",
                email,
                Gender.MALE
        );
        underTest.save(student);

        // when
        boolean expected = underTest.selectExistsEmail(email);

        // then
        assertThat(expected).isTrue();
    }

    @Test
    void shouldCheckWhenStudentEmailDoesNotExists() {
        // given
        String email = "johnny@mail.com";

        // when
        boolean expected = underTest.selectExistsEmail(email);

        // then
        assertThat(expected).isFalse();
    }
}