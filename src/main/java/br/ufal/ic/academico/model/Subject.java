package br.ufal.ic.academico.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String code;

    @Setter
    private int credits;

    @Setter
    private int creditsRequired;

    @Setter
    @ManyToOne
    private Department department;

    @ManyToMany//(fetch = FetchType.EAGER)
    @JoinTable(name = "SUBJECT_SUBJECT_REQUIRED")
    private List<Subject> subjectsRequired = new ArrayList<Subject>();


    public Subject(String name, String code){
        this.name = name;
        this.code = code;
        this.credits = 0;
    }
    public Subject(){}

    @Override
    public String toString(){
        return code + " - " + name;
    }

    public String getSubjectsReq () {
        StringBuilder str = new StringBuilder();

        str.append("Subjects Required: \n");

        List<Subject> req = this.getSubjectsRequired();

        for (Subject s: req) {
            str.append("    " + s.getCode() + "\n");
        }

        return str.toString();
    }

    public String getInfo() {
        return "Code: " + code + "\n" +
                "Name: " + name + "\n" +
                "Credits: " + credits + "\n" +
                "Credits Required: " + creditsRequired + "\n" +
                getSubjectsReq();
    }

    public void addSubjectRequired(Subject e){
        subjectsRequired.add(e);
    }
}
