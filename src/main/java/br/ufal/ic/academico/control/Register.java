package br.ufal.ic.academico.control;

import br.ufal.ic.academico.ConfigApp;
import br.ufal.ic.academico.Database;
import br.ufal.ic.academico.model.Course;
import br.ufal.ic.academico.model.Offer;
import br.ufal.ic.academico.model.Student;
import br.ufal.ic.academico.model.Subject;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.stream.Collectors;

public class Register {

    protected Database db;
    protected SessionFactory sessionFac;
    private Format format = new Format();

    public Register(Database db, SessionFactory sessionFac) {
        this.db = db;
        this.sessionFac = sessionFac;
    }

    public void register() {
        Student selected = selectStudent();
        if(selected == null) return;

        Course course = selected.getCourse();

        Subject subject = selectSubject(course);
        if(subject == null) return;

        Offer offer = getOffer(subject);
        if(offer == null) return;

        try {
            enrollStudent(selected, offer);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private Student selectStudent() {
        Transaction tr = sessionFac.getCurrentSession().beginTransaction();
        List<Object> students = db.getAll(Student.class);
        tr.commit();

        return (Student) format.choose(students);
    }

    private Subject selectSubject(Course course) {
        Transaction tr = sessionFac.getCurrentSession().beginTransaction();
        List<Object> availableSubjects = db.getAvailableSubjects(course);
        tr.commit();

        return (Subject) format.choose(availableSubjects);
    }

    private Offer getOffer(Subject subject) {
        Transaction tr = sessionFac.getCurrentSession().beginTransaction();

        Offer offer = null;

        try {
            offer = (Offer) db.getOfferSubject(subject).get(0);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("There is no available offer for: " + subject.getName());
            return null;
        } finally {
            tr.commit();
        }

        return offer;

    }
    public boolean enrollStudent(Student selected, Offer offer) {
        Transaction tr = sessionFac.getCurrentSession().beginTransaction();

        List<Long> requiredSubjectsID = (db.getRequiredSubjects(offer.getSubject()))
                .stream().map(s -> ((Subject)s).getId()).collect(Collectors.toList());
        tr.commit();

        tr = sessionFac.getCurrentSession().beginTransaction();
        List<Long> completedSubjectsID = (db.getCompletedSubjects(selected))
                .stream().map(s -> ((Offer)s).getSubject().getId()).collect(Collectors.toList());
        tr.commit();

/*
        tr = sessionFac.getCurrentSession().beginTransaction();
        List<Long> enrolledSubjectsID = (db.getEnrolledSubjects(selected))
                .stream().map(s -> ((Offer)s).getSubject().getId()).collect(Collectors.toList());
        tr.commit();
*/

        List<Long> enrolledSubjectsID = (selected.getEnrolledSubjects())
                .stream().map(s -> ((Offer)s).getSubject().getId()).collect(Collectors.toList());

        if (enrolledSubjectsID.contains(offer.getSubject().getId())){
            System.out.println("Student already enrolled");

        } else if (completedSubjectsID.contains(offer.getSubject().getId())) {
            System.out.println("Student already approved in this subject");

        } else if (!completedSubjectsID.containsAll(requiredSubjectsID)) {
            System.out.println("Student doesn't have subject pre-requesites");

        } else if (selected.getCredits() < offer.getSubject().getCreditsRequired()) {
            System.out.println("Student doesn't have enough credits");

        } else if (selected.getType() == ConfigApp.Type.POST_GRADUATE && offer.getSecretary().getType() == ConfigApp.Type.GRADUATE) {
            System.out.println("Post-Graduate students can't enroll to graduate subjects");

        } else if (offer.getSecretary().getType() == ConfigApp.Type.POST_GRADUATE && selected.getType() == ConfigApp.Type.GRADUATE && selected.getCredits() < 170) {
            System.out.println("Graduate students must have at least 170 credits to attend to Post-Graduate subjects");

        } else {
            tr = sessionFac.getCurrentSession().beginTransaction();
            selected.enrollSubject(offer);
            System.out.println("Enrolled");
            db.persist(Student.class, selected);
            tr.commit();
            return true;
        }

        return false;
    }
}
