package br.ufal.ic.academico;

import br.ufal.ic.academico.control.Register;
import br.ufal.ic.academico.model.*;
import io.dropwizard.testing.junit5.DAOTestExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mock;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(DropwizardExtensionsSupport.class)
public class RequisitesTest {

    public DAOTestExtension daoTesting = DAOTestExtension.newBuilder()
            .addEntityClass(Subject.class)
            .addEntityClass(Course.class)
            .addEntityClass(Student.class)
            .addEntityClass(Department.class)
            .addEntityClass(Secretary.class)
            .addEntityClass(Professor.class)
            .addEntityClass(Offer.class)
            .build();

    private SessionFactory sessionFactory = daoTesting.getSessionFactory();
    private Database dao = new Database(sessionFactory);

    @Test
    @Order(1)
    public void subjectAlreadyEnrolledTest() {

        Register r = new Register(dao, sessionFactory);

        Transaction tr = sessionFactory.getCurrentSession().beginTransaction();

        Secretary ic_secretary = new Secretary("ic_secretary", ConfigApp.Type.GRADUATE);

        Student lucas = new Student("Lucas", "06519361445", ConfigApp.Type.GRADUATE);

        Subject p1 = new Subject("Programacao 1", "COMP201");
        Subject p2 = new Subject("Programacao 2", "COMP202");
        Subject p3 = new Subject("Programacao 3", "COMP203");

        Professor willy = new Professor("Willy", "0000");

        Offer p1_offer = new Offer("Offer p1", p1, willy, ic_secretary);
        Offer p2_offer = new Offer("Offer p2", p2, willy, ic_secretary);
        Offer p3_offer = new Offer("Offer p3", p3, willy, ic_secretary);

        lucas.enrollSubject(p1_offer);
        lucas.enrollSubject(p3_offer);

        dao.persist(Secretary.class, ic_secretary);
        dao.persist(Student.class, lucas);
        dao.persist(Subject.class, p1);
        dao.persist(Subject.class, p2);
        dao.persist(Subject.class, p3);
        dao.persist(Professor.class, willy);
        dao.persist(Offer.class, p1_offer);
        dao.persist(Offer.class, p2_offer);
        dao.persist(Offer.class, p3_offer);

        tr.commit();

        assertFalse(r.enrollStudent(lucas, p1_offer));
        assertFalse(r.enrollStudent(lucas, p3_offer));
        assertTrue(r.enrollStudent(lucas, p2_offer));
    }

    @Test
    @Order(2)
    public void subjectAlreadyApprovedTest() {

        Transaction tr = sessionFactory.getCurrentSession().beginTransaction();

        Secretary ic_secretary = new Secretary("ic_secretary", ConfigApp.Type.GRADUATE);

        Student lucas = new Student("Lucas", "06519361445", ConfigApp.Type.GRADUATE);

        Subject p1 = new Subject("Programacao 1", "COMP201");
        Subject p2 = new Subject("Programacao 2", "COMP202");
        Subject p3 = new Subject("Programacao 3", "COMP203");

        Professor willy = new Professor("Willy", "0000");

        Offer p1_offer = new Offer("Offer p1", p1, willy, ic_secretary);
        Offer p2_offer = new Offer("Offer p2", p2, willy, ic_secretary);
        Offer p3_offer = new Offer("Offer p3", p3, willy, ic_secretary);

        lucas.addCompletedSubject(p1_offer);
        lucas.addCompletedSubject(p2_offer);

        dao.persist(Secretary.class, ic_secretary);
        dao.persist(Student.class, lucas);
        dao.persist(Subject.class, p1);
        dao.persist(Subject.class, p2);
        dao.persist(Subject.class, p3);
        dao.persist(Professor.class, willy);
        dao.persist(Offer.class, p1_offer);
        dao.persist(Offer.class, p2_offer);
        dao.persist(Offer.class, p3_offer);

        tr.commit();

        Register r = new Register(dao, sessionFactory);

        assertFalse(r.enrollStudent(lucas, p1_offer));
        assertFalse(r.enrollStudent(lucas, p2_offer));
        assertTrue(r.enrollStudent(lucas, p3_offer));
    }

    @Test
    @Order(3)
    public void creditsNeededToEnrollTest() {

        Transaction tr = sessionFactory.getCurrentSession().beginTransaction();

        Secretary ic_secretary = new Secretary("ic_secretary", ConfigApp.Type.GRADUATE);

        Student lucas = new Student("Lucas", "06519361445", ConfigApp.Type.GRADUATE);
        lucas.setCredits(60);

        Subject p1 = new Subject("Programacao 1", "COMP201");
        Subject p2 = new Subject("Programacao 2", "COMP202");
        Subject p3 = new Subject("Programacao 3", "COMP203");

        p1.setCreditsRequired(0);
        p2.setCreditsRequired(60);
        p3.setCreditsRequired(61);

        Professor willy = new Professor("Willy", "0000");

        Offer p1_offer = new Offer("Offer p1", p1, willy, ic_secretary);
        Offer p2_offer = new Offer("Offer p2", p2, willy, ic_secretary);
        Offer p3_offer = new Offer("Offer p3", p3, willy, ic_secretary);

        dao.persist(Secretary.class, ic_secretary);
        dao.persist(Student.class, lucas);
        dao.persist(Subject.class, p1);
        dao.persist(Subject.class, p2);
        dao.persist(Subject.class, p3);
        dao.persist(Professor.class, willy);
        dao.persist(Offer.class, p1_offer);
        dao.persist(Offer.class, p2_offer);
        dao.persist(Offer.class, p3_offer);

        tr.commit();

        Register r = new Register(dao, sessionFactory);

        assertTrue(r.enrollStudent(lucas, p1_offer));
        assertTrue(r.enrollStudent(lucas, p2_offer));
        assertFalse(r.enrollStudent(lucas, p3_offer));
    }

    @Test
    @Order(4)
    public void preRequisitesSubjectsToEnrollTest() {

        Transaction tr = sessionFactory.getCurrentSession().beginTransaction();

        Secretary ic_secretary = new Secretary("ic_secretary", ConfigApp.Type.GRADUATE);

        Student lucas = new Student("Lucas", "06519361445", ConfigApp.Type.GRADUATE);
        Student pedro = new Student("Pedro", "06519361442", ConfigApp.Type.GRADUATE);

        Subject p1 = new Subject("Programacao 1", "COMP201");
        Subject p2 = new Subject("Programacao 2", "COMP202");
        Subject p3 = new Subject("Programacao 3", "COMP203");

        p3.addSubjectRequired(p2);
        p3.addSubjectRequired(p1);
        p2.addSubjectRequired(p1);

        Professor willy = new Professor("Willy", "0000");

        Offer p1_offer = new Offer("Offer p1", p1, willy, ic_secretary);
        Offer p2_offer = new Offer("Offer p2", p2, willy, ic_secretary);
        Offer p3_offer = new Offer("Offer p3", p3, willy, ic_secretary);

        lucas.addCompletedSubject(p1_offer);
        lucas.addCompletedSubject(p2_offer);

        dao.persist(Secretary.class, ic_secretary);
        dao.persist(Student.class, lucas);
        dao.persist(Student.class, pedro);
        dao.persist(Subject.class, p1);
        dao.persist(Subject.class, p2);
        dao.persist(Subject.class, p3);
        dao.persist(Professor.class, willy);
        dao.persist(Offer.class, p1_offer);
        dao.persist(Offer.class, p2_offer);
        dao.persist(Offer.class, p3_offer);

        tr.commit();

        Register r = new Register(dao, sessionFactory);

        assertFalse(r.enrollStudent(pedro, p2_offer));
        assertFalse(r.enrollStudent(pedro, p3_offer));
        assertTrue(r.enrollStudent(pedro, p1_offer));

        tr = sessionFactory.getCurrentSession().beginTransaction();
        pedro.addCompletedSubject(p1_offer);
        dao.persist(Student.class, pedro);
        tr.commit();

        assertTrue(r.enrollStudent(pedro, p2_offer));

        tr = sessionFactory.getCurrentSession().beginTransaction();
        pedro.addCompletedSubject(p2_offer);
        dao.persist(Student.class, pedro);
        tr.commit();

        assertTrue(r.enrollStudent(pedro, p3_offer));
    }

    @Test
    @Order(5)
    public void studentTypeRequisiteToEnrollTest() {

        Transaction tr = sessionFactory.getCurrentSession().beginTransaction();

        Secretary ic_secretary = new Secretary("ic_secretary", ConfigApp.Type.GRADUATE);
        Secretary ic_secretary_post = new Secretary("ic_secretary", ConfigApp.Type.POST_GRADUATE);

        Student lucas = new Student("Lucas", "06519361445", ConfigApp.Type.GRADUATE);
        Student pedro = new Student("Pedro", "06519361442", ConfigApp.Type.GRADUATE);

        Student carlos = new Student("Carlos", "06519361444", ConfigApp.Type.POST_GRADUATE);
        Student luiz = new Student("Luiz", "06329361444", ConfigApp.Type.POST_GRADUATE);

        lucas.setCredits(170);
        pedro.setCredits(169);

        carlos.setCredits(170);
        luiz.setCredits(169);

        Subject p1 = new Subject("Programacao 1", "COMP201");
        Subject p2 = new Subject("Programacao 2", "COMP202");
        Subject p3 = new Subject("Programacao 3", "COMP203");

        Professor willy = new Professor("Willy", "0000");

        Offer p1_offer = new Offer("Offer p1", p1, willy, ic_secretary);
        Offer p2_offer = new Offer("Offer p2", p2, willy, ic_secretary);
        Offer p3_offer = new Offer("Offer p3", p3, willy, ic_secretary_post);

        dao.persist(Secretary.class, ic_secretary);
        dao.persist(Secretary.class, ic_secretary_post);
        dao.persist(Student.class, lucas);
        dao.persist(Student.class, pedro);
        dao.persist(Student.class, carlos);
        dao.persist(Student.class, luiz);
        dao.persist(Subject.class, p1);
        dao.persist(Subject.class, p2);
        dao.persist(Subject.class, p3);
        dao.persist(Professor.class, willy);
        dao.persist(Offer.class, p1_offer);
        dao.persist(Offer.class, p2_offer);
        dao.persist(Offer.class, p3_offer);

        tr.commit();

        Register r = new Register(dao, sessionFactory);

        assertFalse(r.enrollStudent(carlos, p1_offer));
        assertFalse(r.enrollStudent(carlos, p2_offer));
        assertTrue(r.enrollStudent(carlos, p3_offer));

        assertFalse(r.enrollStudent(luiz, p1_offer));
        assertFalse(r.enrollStudent(luiz, p2_offer));
        assertTrue(r.enrollStudent(luiz, p3_offer));

        assertTrue(r.enrollStudent(lucas, p1_offer));
        assertTrue(r.enrollStudent(lucas, p2_offer));
        assertTrue(r.enrollStudent(lucas, p3_offer));

        assertTrue(r.enrollStudent(pedro, p1_offer));
        assertTrue(r.enrollStudent(pedro, p2_offer));
        assertFalse(r.enrollStudent(pedro, p3_offer));
    }


    public class MockRegister extends Register {
        public MockRegister (Database db, SessionFactory sessionFac){
            super(db, sessionFac);
        }

        @Override
        public boolean enrollStudent(Student selected, Offer offer) {
            Transaction tr = sessionFac.getCurrentSession().beginTransaction();

            List<Long> requiredSubjectsID = (db.getRequiredSubjects(offer.getSubject()))
                    .stream().map(s -> ((Subject)s).getId()).collect(Collectors.toList());
            tr.commit();

            tr = sessionFac.getCurrentSession().beginTransaction();
            List<Long> completedSubjectsID = (db.getCompletedSubjects(selected))
                    .stream().map(s -> ((Offer)s).getSubject().getId()).collect(Collectors.toList());
            tr.commit();

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
                return true;
            }

            return false;
        }
    }
}
