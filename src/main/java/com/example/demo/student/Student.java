/**
 * This is Student Model
 */

package com.example.demo.student;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * lombok from POM permit to use annotations for not specifying
 *  explicitly the constructors,getters,setters
 *  All of this could be replaced only with
 *  @Data, but we should make all attributes to final for JPA using
 * */
@ToString
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class Student {
    @Id
    @SequenceGenerator(
            name = "student_sequence",
            sequenceName = "student_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "student_sequence",
            strategy = GenerationType.SEQUENCE)
    private Long id;
    @NotBlank(message = "The name must not be blank.")
    @Column(nullable = false) // database constraint
    private String name;
    @Email(message = "The email format is invalid, try something like: aaa@bbb.ccc")  // can be customized with a regular expression
    @Column(nullable = false, unique = true)
    private String email;
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    public Student(String name, String email, Gender gender) {
        this.name = name;
        this.email = email;
        this.gender = gender;
    }
}

