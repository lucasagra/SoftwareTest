package br.ufal.ic.academico.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String registration;

    @Setter @ManyToOne private Department department;

    public Professor(String name, String registration){
        this.name = name;
        this.registration = registration;
    }

    public Professor() {}

    @Override
    public String toString() {
        return "Professor: " + name + "\n" +
                "   Registration: " + registration + "\n";

    }
}
