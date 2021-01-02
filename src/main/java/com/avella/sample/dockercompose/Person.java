package com.avella.sample.dockercompose;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class Person {
    String id;
    String name;
    String city;
}