package br.ufal.ic.academico;

import br.ufal.ic.academico.model.*;
import io.dropwizard.testing.junit5.DAOTestExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.hibernate.NonUniqueResultException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.persistence.NoResultException;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.mock;

@ExtendWith(DropwizardExtensionsSupport.class)
public class DBtest {

    public DAOTestExtension dbTesting = DAOTestExtension.newBuilder()
            .addEntityClass(Subject.class)
            .addEntityClass(Course.class)
            .addEntityClass(Student.class)
            .addEntityClass(Department.class)
            .addEntityClass(Secretary.class)
            .addEntityClass(Professor.class)
            .addEntityClass(Offer.class)
            .build();

    private SessionFactory sessionFactory = dbTesting.getSessionFactory();
    private Database dao = new Database(sessionFactory);

    @Test
    public void persistSaveTest() {

        Transaction tr = sessionFactory.getCurrentSession().beginTransaction();

        List<Object> students = dao.getAll(Student.class);
        assertEquals(0, students.size());

        Student lucas = new Student("Lucas", "06519361445", ConfigApp.Type.GRADUATE);
        assertEquals(lucas, dao.persist(Student.class, lucas));

        Student pedro = new Student("Pedro", "06519361441", ConfigApp.Type.GRADUATE);
        assertNotNull(dao.persist(Student.class, pedro));

        List<Object> expected_list = Arrays.asList(lucas, pedro);

        students = dao.getAll(Student.class);
        assertArrayEquals(expected_list.toArray(), students.toArray());

        try {
            dao.persist(Student.class, null);
            fail("Fail at throwing Null Pointer Exception");
        } catch(Exception e) {}

        try {
            System.out.println(dao.persist(null, lucas));
            fail("Fail at throwing Null Pointer Exception");
        } catch(Exception e) {}

        tr.commit();
    }

    @Test
    public void persistUpdateTest() {

        Transaction tr = sessionFactory.getCurrentSession().beginTransaction();

        Student lucas = new Student("Lucas", "06519361445", ConfigApp.Type.GRADUATE);
        Student pedro = new Student("Pedro", "06519361441", ConfigApp.Type.GRADUATE);

        lucas.setCredits(0);
        dao.persist(Student.class, lucas);

        Student lucas_result = dao.getStudent("06519361445");
        assertEquals(0, lucas_result.getCredits());

        lucas.setCredits(60);
        dao.persist(Student.class, lucas);

        lucas_result = dao.getStudent("06519361445");
        assertEquals(60, lucas_result.getCredits());

        dao.persist(Student.class, lucas);
        dao.persist(Student.class, pedro);

        List<Object> students = dao.getAll(Student.class);

        assertEquals(2, students.size());

        tr.commit();
    }


    @Test
    public void getAllTest() {
        Transaction tr = sessionFactory.getCurrentSession().beginTransaction();

        List<Student> expectedStudents = new ArrayList<>();
        for (int i = 0; i < 100; ++i) {

            byte[] array = new byte[7];

            new Random().nextBytes(array);
            String randomName = new String(array, Charset.forName("UTF-8"));

            new Random().nextBytes(array);
            String randomReg = new String(array, Charset.forName("UTF-8"));

            Student randomStd = new Student(randomName, randomReg, ConfigApp.Type.GRADUATE);
            dao.persist(Student.class, randomStd);

            expectedStudents.add(randomStd);
        }

        List<Subject> expectedSubjects = new ArrayList<>();
        for (int i = 0; i < 150; ++i) {

            byte[] array = new byte[7];

            new Random().nextBytes(array);
            String randomName = new String(array, Charset.forName("UTF-8"));

            new Random().nextBytes(array);
            String randomCode = new String(array, Charset.forName("UTF-8"));

            Subject randomSubj = new Subject(randomName, randomCode);
            dao.persist(Subject.class, randomSubj);

            expectedSubjects.add(randomSubj);
        }
        tr.commit();

        tr = sessionFactory.getCurrentSession().beginTransaction();
        List<Object> studentsFromDB = dao.getAll(Student.class);
        List<Object> subjectsFromDB = dao.getAll(Subject.class);
        tr.commit();

        assertArrayEquals(expectedStudents.toArray(), studentsFromDB.toArray());
        assertArrayEquals(expectedSubjects.toArray(), subjectsFromDB.toArray());
    }

    @Test
    public void getStudentTest() {
        String lucasid = "111111";
        String pedroid = "111112";

        Transaction tr = sessionFactory.getCurrentSession().beginTransaction();

        Student lucas = new Student("Lucas", lucasid, ConfigApp.Type.GRADUATE);
        Student pedro = new Student("Pedro", pedroid, ConfigApp.Type.GRADUATE);

        dao.persist(Student.class, lucas);
        dao.persist(Student.class, pedro);

        tr.commit();

        tr = sessionFactory.getCurrentSession().beginTransaction();

        Student lucasFromDB = dao.getStudent(lucasid);
        assertEquals(lucas, lucasFromDB);
        Student pedroFromDB = dao.getStudent(pedroid);
        assertEquals(pedro, pedroFromDB);

//        Student lucas_hacker = new Student("LucasHacker", lucasid, ConfigApp.Type.GRADUATE);
//        dao.persist(Student.class, lucas_hacker);

        tr.commit();

//        tr = sessionFactory.getCurrentSession().beginTransaction();
//
//        try {
//            lucasFromDB = dao.getStudent(lucasid);
//            assertEquals(lucas, lucasFromDB);
//        } catch (NonUniqueResultException e){
//            fail("Returned more than one student with same registration");
//        }
//        tr.commit();

        try {
            dao.getStudent(null);
            fail("Registration null returned something");
        } catch (NoResultException e) {
            assert true;
        }

        try {
            dao.getStudent("0");
            fail("Method returned unexpected registration");
        } catch (NoResultException e) {
            assert true;
        }
    }

    @Test
    public void getAvailableSubjectsFromCourseTest() {
        Transaction tr = sessionFactory.getCurrentSession().beginTransaction();

        Secretary sec = new Secretary("a", ConfigApp.Type.GRADUATE);

        Course courseTest1 = new Course("CourseTest1", sec);
        Course courseTest2 = new Course("CourseTest2", sec);
        Course courseTest3 = new Course("CourseTest3", sec);
        Course courseTest4 = new Course("CourseTest4", sec);

        Subject s1 = new Subject("s1", "s1");
        Subject s2 = new Subject("s2", "s2");
        Subject s3 = new Subject("s3", "s3");
        Subject s4 = new Subject("s4", "s4");
        Subject s5 = new Subject("s5", "s5");
        Subject s6 = new Subject("s6", "s6");

        List<Subject> subjectList = Arrays.asList(s1, s2, s3, s4, s5, s6);

        courseTest1.addRegularSubject(s1);
        courseTest1.addRegularSubject(s2);
        courseTest1.addRegularSubject(s3);
        courseTest1.addRegularSubject(s4);
        courseTest1.addOptionalSubject(s5);
        courseTest1.addOptionalSubject(s6);

        courseTest2.addRegularSubject(s1);
        courseTest2.addRegularSubject(s2);
        courseTest2.addRegularSubject(s3);
        courseTest2.addRegularSubject(s4);
        courseTest2.addRegularSubject(s5);
        courseTest2.addRegularSubject(s6);

        courseTest3.addOptionalSubject(s1);
        courseTest3.addOptionalSubject(s2);
        courseTest3.addOptionalSubject(s3);
        courseTest3.addOptionalSubject(s4);
        courseTest3.addOptionalSubject(s5);
        courseTest3.addOptionalSubject(s6);

        dao.persist(Secretary.class, sec);
        dao.persist(Course.class, courseTest1);
        dao.persist(Course.class, courseTest2);
        dao.persist(Course.class, courseTest3);
        dao.persist(Course.class, courseTest4);
        dao.persist(Subject.class, s1);
        dao.persist(Subject.class, s2);
        dao.persist(Subject.class, s3);
        dao.persist(Subject.class, s4);
        dao.persist(Subject.class, s5);
        dao.persist(Subject.class, s6);

        tr.commit();

        tr = sessionFactory.getCurrentSession().beginTransaction();

        List<Object> list1 = dao.getAvailableSubjects(courseTest1);
        assertArrayEquals(subjectList.toArray(), list1.toArray());

        List<Object> list2 = dao.getAvailableSubjects(courseTest2);
        assertArrayEquals(subjectList.toArray(), list2.toArray());

        List<Object> list3 = dao.getAvailableSubjects(courseTest3);
        assertArrayEquals(subjectList.toArray(), list3.toArray());

        List<Object> list4 = dao.getAvailableSubjects(courseTest4);
        assertEquals(0, list4.size());

        try {
            dao.getAvailableSubjects(null);
            fail("Something returned from null");
        } catch (NullPointerException e) {
            assert true;
        }

        tr.commit();
    }

    @Test
    public void getCompletedSubjectsFromStudentTest() {
        Transaction tr = sessionFactory.getCurrentSession().beginTransaction();

        Secretary sec = new Secretary("a", ConfigApp.Type.GRADUATE);

        Student lucas = new Student("Lucas", "0", ConfigApp.Type.GRADUATE);
        Student pedro = new Student("Pedro", "1", ConfigApp.Type.GRADUATE);

        Professor willy = new Professor();

        Subject s1 = new Subject("s1", "s1");
        Subject s2 = new Subject("s2", "s2");
        Subject s3 = new Subject("s3", "s3");
        Subject s4 = new Subject("s4", "s4");
        Offer o1 = new Offer("o1", s1, willy, sec);
        Offer o2 = new Offer("o2", s2, willy, sec);
        Offer o3 = new Offer("o3", s3, willy, sec);
        Offer o4 = new Offer("o4", s4, willy, sec);

        List<Offer> offerList = Arrays.asList(o1, o2, o3, o4);

        lucas.addCompletedSubject(o1);
        lucas.addCompletedSubject(o2);
        lucas.addCompletedSubject(o3);
        lucas.addCompletedSubject(o4);

        dao.persist(Secretary.class, sec);
        dao.persist(Student.class, lucas);
        dao.persist(Student.class, pedro);
        dao.persist(Professor.class, willy);
        dao.persist(Subject.class, s1);
        dao.persist(Subject.class, s2);
        dao.persist(Subject.class, s3);
        dao.persist(Subject.class, s4);
        dao.persist(Offer.class, o1);
        dao.persist(Offer.class, o2);
        dao.persist(Offer.class, o3);
        dao.persist(Offer.class, o4);

        tr.commit();


        tr = sessionFactory.getCurrentSession().beginTransaction();

        List<Object> list1 = dao.getCompletedSubjects(lucas);
        assertArrayEquals(offerList.toArray(), list1.toArray());

        List<Object> list2 = dao.getCompletedSubjects(pedro);
        assertEquals(0, list2.size());

        try {
            dao.getCompletedSubjects(null);
            fail("Something returned from null");
        } catch (NullPointerException e) {
            assert true;
        }

        tr.commit();
    }

    @Test
    public void getRequiredSubjectsFromSubjectTest() {

        Transaction tr = sessionFactory.getCurrentSession().beginTransaction();

        Subject s1 = new Subject("s1", "s1");
        Subject s2 = new Subject("s2", "s2");
        Subject s3 = new Subject("s3", "s3");
        Subject s4 = new Subject("s4", "s4");

        s1.addSubjectRequired(s2);
        s1.addSubjectRequired(s3);
        s1.addSubjectRequired(s4);

        List<Subject> subjectList = Arrays.asList(s2, s3, s4);

        dao.persist(Subject.class, s1);
        dao.persist(Subject.class, s2);
        dao.persist(Subject.class, s3);
        dao.persist(Subject.class, s4);

        tr.commit();

        tr = sessionFactory.getCurrentSession().beginTransaction();

        List<Object> fromDB = dao.getRequiredSubjects(s1);
        assertArrayEquals(subjectList.toArray(), fromDB.toArray());

        fromDB = dao.getRequiredSubjects(s2);
        assertEquals(0, fromDB.size());

        try {
            dao.getRequiredSubjects(null);
            fail("Something returned from null");
        } catch (NullPointerException e) {
            assert true;
        }

        tr.commit();
    }

    @Test
    public void getOfferFromSubjectTest() {
        Transaction tr = sessionFactory.getCurrentSession().beginTransaction();

        Secretary sec = new Secretary("a", ConfigApp.Type.GRADUATE);

        Professor willy = new Professor();

        Subject s1 = new Subject("s1", "s1");
        Offer o1 = new Offer("o1", s1, willy, sec);
        Offer o11 = new Offer("o11", s1, willy, sec);
        Offer o111 = new Offer("o111", s1, willy, sec);

        Subject s2 = new Subject("s2", "s2");
        Offer o2 = new Offer("o2", s2, willy, sec);

        Subject s3 = new Subject("s3", "s3");
        Offer o3 = new Offer("o3", s3, willy, sec);

        Subject s4 = new Subject("s4", "s4");

        List<Subject> subjectList = Arrays.asList(s1, s2, s3, s4);

        dao.persist(Secretary.class, sec);
        dao.persist(Professor.class, willy);
        dao.persist(Subject.class, s1);
        dao.persist(Subject.class, s2);
        dao.persist(Subject.class, s3);
        dao.persist(Subject.class, s4);
        dao.persist(Offer.class, o1);
        dao.persist(Offer.class, o11);
        dao.persist(Offer.class, o111);
        dao.persist(Offer.class, o2);
        dao.persist(Offer.class, o3);

        tr.commit();

        tr = sessionFactory.getCurrentSession().beginTransaction();

        /*
            For each subject S,
                query all offers from it and for Each offer O
                    assertEquals O.subject to subject S
        * */
        subjectList.stream().forEach(s -> {
            dao.getOfferSubject(s).stream().forEach(o -> {
                assertEquals(s, ((Offer)o).getSubject(),
                        "Failed at " + s.toString() + " different from " + ((Offer)o).getSubject().toString());
            });
        });


        try {
            dao.getOfferSubject(null);
            fail("Something returned from null");
        } catch (NullPointerException e) {
            assert true;
        }

        tr.commit();
    }

    @Test
    public void getOfferFromSecretaryTest() {
        Transaction tr = sessionFactory.getCurrentSession().beginTransaction();

        // 3 offers for s1
        // 1 offer for s2
        Secretary sec1 = new Secretary("1", ConfigApp.Type.GRADUATE);
        // 1 offer for s3
        Secretary sec2 = new Secretary("2", ConfigApp.Type.POST_GRADUATE);
        // 0 offers
        Secretary sec3 = new Secretary("3", ConfigApp.Type.POST_GRADUATE);

        Professor willy = new Professor();

        Subject s1 = new Subject("s1", "s1");
        Offer o1 = new Offer("o1", s1, willy, sec1);
        Offer o11 = new Offer("o11", s1, willy, sec1);
        Offer o111 = new Offer("o111", s1, willy, sec1);

        Subject s2 = new Subject("s2", "s2");
        Offer o2 = new Offer("o2", s2, willy, sec1);

        Subject s3 = new Subject("s3", "s3");
        Offer o3 = new Offer("o3", s3, willy, sec2);

        Subject s4 = new Subject("s4", "s4");

        List<Secretary> secretaryList = Arrays.asList(sec1, sec2, sec3);

        dao.persist(Secretary.class, sec1);
        dao.persist(Secretary.class, sec2);
        dao.persist(Secretary.class, sec3);
        dao.persist(Professor.class, willy);
        dao.persist(Subject.class, s1);
        dao.persist(Subject.class, s2);
        dao.persist(Subject.class, s3);
        dao.persist(Subject.class, s4);
        dao.persist(Offer.class, o1);
        dao.persist(Offer.class, o11);
        dao.persist(Offer.class, o111);
        dao.persist(Offer.class, o2);
        dao.persist(Offer.class, o3);

        tr.commit();

        tr = sessionFactory.getCurrentSession().beginTransaction();

        /*
            For each secretary S,
                query all offers from it and for Each offer O
                    assertEquals O.secretary to secretary S
        * */
        secretaryList.stream().forEach(s -> {
            dao.getOffers(s).stream().forEach(o -> {
                assertEquals(s, ((Offer)o).getSecretary(),
                        "Failed at " + s.toString() + "different from" + ((Offer)o).getSecretary().toString());
            });
        });


        try {
            dao.getOffers(null);
            fail("Something returned from null");
        } catch (NullPointerException e) {
            assert true;
        }

        tr.commit();
    }

    @Test
    public void getEnrolledStudentsFromOfferTest() {
        Transaction tr = sessionFactory.getCurrentSession().beginTransaction();

        Secretary sec = new Secretary("a", ConfigApp.Type.GRADUATE);

        Student lucas = new Student("Lucas", "0", ConfigApp.Type.GRADUATE);
        Student pedro = new Student("Pedro", "1", ConfigApp.Type.GRADUATE);
        Student luiz = new Student("Luiz", "2", ConfigApp.Type.GRADUATE);
        Student marcos = new Student("Marcos", "3", ConfigApp.Type.GRADUATE);

        Professor willy = new Professor();

        Subject s1 = new Subject("s1", "s1");
        Subject s2 = new Subject("s2", "s2");
        Subject s3 = new Subject("s3", "s3");

        // 4 students
        Offer o1 = new Offer("o1", s1, willy, sec);
        // 2 students
        Offer o2 = new Offer("o2", s2, willy, sec);
        // 0 students
        Offer o3 = new Offer("o3", s3, willy, sec);


        List<Student> expectedStudentList1 = Arrays.asList(lucas, pedro, luiz, marcos);
        lucas.enrollSubject(o1);
        pedro.enrollSubject(o1);
        luiz.enrollSubject(o1);
        marcos.enrollSubject(o1);

        List<Student> expectedStudentList2 = Arrays.asList(lucas, pedro);
        lucas.enrollSubject(o2);
        pedro.enrollSubject(o2);


        dao.persist(Secretary.class, sec);
        dao.persist(Student.class, lucas);
        dao.persist(Student.class, pedro);
        dao.persist(Student.class, luiz);
        dao.persist(Student.class, marcos);
        dao.persist(Professor.class, willy);
        dao.persist(Subject.class, s1);
        dao.persist(Subject.class, s2);
        dao.persist(Subject.class, s3);
        dao.persist(Offer.class, o1);
        dao.persist(Offer.class, o2);
        dao.persist(Offer.class, o3);

        tr.commit();

        tr = sessionFactory.getCurrentSession().beginTransaction();

        List<Object> fromDB = dao.getEnrolledStudents(o1);
        assertArrayEquals(expectedStudentList1.toArray(), fromDB.toArray());

        fromDB = dao.getEnrolledStudents(o2);
        assertArrayEquals(expectedStudentList2.toArray(), fromDB.toArray());

        fromDB = dao.getEnrolledStudents(o3);
        assertEquals(0, fromDB.size());

        try {
            dao.getEnrolledStudents(null);
            fail("Something returned from null");
        } catch (NullPointerException e) {
            assert true;
        }

        tr.commit();
    }

    @Test
    public void test() {

        Transaction tr = sessionFactory.getCurrentSession().beginTransaction();


        assertNotNull(dao.persist(Student.class, new Student("NameTest" , "29189283", ConfigApp.Type.GRADUATE)));

        tr.commit();

        tr = sessionFactory.getCurrentSession().beginTransaction();
        List<Object> students = dao.getAll(Student.class);
        tr.commit();

        students.stream().forEach(s -> System.out.println(s));

        System.out.println(students.size());

    }
}
