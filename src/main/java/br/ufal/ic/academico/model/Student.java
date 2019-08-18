package br.ufal.ic.academico.model;

import br.ufal.ic.academico.ConfigApp;
import br.ufal.ic.academico.model.Subject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@RequiredArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String registration;
    @Setter private int credits;

    @Enumerated(EnumType.STRING)
    private ConfigApp.Type type;

    @ManyToMany
    @JoinTable(name = "STUDENT_SUBJECT_COMPLETED")
    private List<Offer> completedSubjects = new ArrayList<Offer>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "STUDENT_SUBJECT_ENROLLED")
    private List<Offer> enrolledSubjects = new ArrayList<Offer>();

    public Student(String name, String registration, ConfigApp.Type type){
        this.name = name;
        this.registration = registration;
        this.credits = 0;
        this.type = type;
    }

    @ManyToOne
    @JoinTable(name="COURSE_STUDENT",
                joinColumns={@JoinColumn(name="students_id")},
                inverseJoinColumns = {@JoinColumn(name="course_id")})
    private Course course;

    public void enrollSubject(Offer offer) {
        enrolledSubjects.add(offer);
    }

    @Override
    public String toString(){
        return name + " " + registration;
    }

    public void registerCourse(Course course){ this.course = course;}

    public void addCompletedSubject(Offer e){
        this.completedSubjects.add(e);
    }
}
