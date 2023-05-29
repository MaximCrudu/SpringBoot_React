/**
 * This is Student Model
 */

package com.example.demo.student;

import lombok.*;

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
public class Student {
    private Long id;
    private String name;
    private String email;
    private Gender gender;


}

