package br.ufal.ic.academico.model;

import br.ufal.ic.academico.ConfigApp;
import br.ufal.ic.academico.model.Secretary;
import br.ufal.ic.academico.model.Student;
import br.ufal.ic.academico.model.Subject;
import com.sun.xml.internal.ws.api.FeatureConstructor;
import lombok.Getter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToMany
    @JoinTable(name = "GRADUATECOURSE_SUBJECTS")
    private List<Subject> regularSubjects = new ArrayList<Subject>();

    @ManyToMany
    @JoinTable(name = "GRADUATECOURSE_OPTIONAL_SUBJECTS")
    private List<Subject> optionalSubjects = new ArrayList<Subject>();

    @OneToMany(mappedBy = "course")
    @Fetch(FetchMode.JOIN)
    private List<Student> students = new ArrayList<Student>();

    @ManyToOne
    private Secretary secretary;

    public Course(String name, Secretary secretary) {
        this.name = name;
        this.secretary = secretary;
    }

    public Course() {}

    public void enrollStudent(Student e){
        students.add(e);
    }
    public void addRegularSubject(Subject e) {
        regularSubjects.add(e);
    }
    public void addOptionalSubject(Subject e) {
        optionalSubjects.add(e);
    }

    @Override
    public String toString(){

        return "Id: " + id + "\n" +
                "Name: " + name + "\n" +
                secretary.toString();

    }
}
