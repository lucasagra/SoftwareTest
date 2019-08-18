package br.ufal.ic.academico.model;

import br.ufal.ic.academico.ConfigApp;
import br.ufal.ic.academico.model.Professor;
import br.ufal.ic.academico.model.Secretary;
import br.ufal.ic.academico.model.Subject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Setter
    private String offerName;

    @ManyToOne
    private Subject subject;
    @ManyToOne
    private Professor professor;
    @ManyToOne
    private Secretary secretary;

    public Offer(String offerName, Subject subject, Professor professor, Secretary secretary) {
        this.offerName = offerName;
        this.subject = subject;
        this.professor = professor;
        this.secretary = secretary;
    }

    public Offer() {}

    @Override
    public String toString() {
        return "Offer: " + offerName + "\n" +
                "Name: " + subject.getName() + "\n" +
                "Code: " + subject.getCode() + "\n" +
                "Type: " + secretary.getType() + "\n" +
                "Credits: " + subject.getCredits() + "\n" +
                subject.getSubjectsReq() +
                "Credits Required: " + subject.getCreditsRequired() + "\n" +
                professor.toString();
    }
}
