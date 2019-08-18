package br.ufal.ic.academico.control;

import br.ufal.ic.academico.ConfigApp;
import br.ufal.ic.academico.Database;
import br.ufal.ic.academico.model.*;
import br.ufal.ic.academico.view.Menu;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.InputMismatchException;
import java.util.List;
import java.util.stream.Collectors;

public class Control {

    private Menu menu = new Menu();
    private Database db;
    private SessionFactory sessionFac;
    private Format format = new Format();

    public Control(Database db, SessionFactory session) {
        this.db = db;
        this.sessionFac = session;
    }

    public void main() {
        Register r = new Register(db, sessionFac);

        int option = -1;
        while (option != 0) {
            try {
                option = menu.main();

                if (option == 1) {
                    r.register();
                } else if (option == 2) {
                    secretaries();
                } else if (option == 3) {
                    subjects();
                } else if (option == 4) {
                    students();
                }
            } catch (InputMismatchException e) {
                new Format().invalidInput();
            }
        }
    }

    public void secretaries() {
        Transaction tr = sessionFac.getCurrentSession().beginTransaction();
        List<Object> secretaryList = db.getAll(Secretary.class);
        tr.commit();

        Secretary selected = (Secretary) format.choose(secretaryList);
        if (selected == null) return;

        tr = sessionFac.getCurrentSession().beginTransaction();
        List<Object> offerList = db.getOffers(selected);

        offerList.stream().forEach(o -> System.out.println(((Offer) o).toString()));
        tr.commit();
    }


    public void subjects() {
        Transaction tr = sessionFac.getCurrentSession().beginTransaction();
        List<Object> subjectsList = db.getAll(Subject.class);

        Subject selected = (Subject) format.choose(subjectsList);
        if (selected == null) {
            tr.commit();
            return;
        }

        System.out.println(selected.getInfo());
        tr.commit();

        tr = sessionFac.getCurrentSession().beginTransaction();
        List<Object> offerList = db.getOfferSubject(selected);
        tr.commit();

        try {
            Offer offer = (Offer) offerList.get(0);
            System.out.println("Professor: " + offer.getProfessor().getName());

            tr = sessionFac.getCurrentSession().beginTransaction();
            List<Object> studentsEnrolled = db.getEnrolledStudents(offer);
            tr.commit();

            System.out.println("Students Enrolled: ");
            studentsEnrolled.stream().forEach(s -> System.out.println("    " + s.toString()));
            System.out.println();

        } catch (IndexOutOfBoundsException e) {
            System.out.println("There is no active offer for this subject");
        }
    }

    public void students() {
        Transaction tr = sessionFac.getCurrentSession().beginTransaction();
        List<Object> studentList = db.getAll(Student.class);

        Student selected = (Student) format.choose(studentList);
        if (selected == null) {
            tr.commit();
            return;
        }

        List<Offer> offerList = selected.getEnrolledSubjects();

        System.out.println(selected.toString());
        System.out.println("Enrolled Subjects:");
        offerList.stream().forEach(o -> {
            System.out.println("   " + ((Offer) o).getOfferName());
            System.out.println("   " + ((Offer) o).getSubject().toString());
        });
        System.out.println();

        tr.commit();
    }
}