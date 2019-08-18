package br.ufal.ic.academico.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    @Setter
    @OneToOne private Secretary graduateSecretary;

    @Setter
    @OneToOne private Secretary postGraduateSecretary;

    public Department(String name) {
        this.name = name;
    }
    public Department() {}
}
