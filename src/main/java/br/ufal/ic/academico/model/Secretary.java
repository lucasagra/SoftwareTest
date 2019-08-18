package br.ufal.ic.academico.model;

import br.ufal.ic.academico.ConfigApp;
import br.ufal.ic.academico.model.Department;
import lombok.Getter;

import javax.persistence.*;
import java.util.List;


@Getter
@Entity

public class Secretary {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private ConfigApp.Type type;

    public Secretary(String name, ConfigApp.Type type) {
        this.name = name;
        this.type = type;
    }

    public Secretary() {};

    @Override
    public String toString() {
        return "Secretary: " + name + " - " + type;
    }
}
