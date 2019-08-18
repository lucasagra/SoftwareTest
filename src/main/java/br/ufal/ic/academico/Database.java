package br.ufal.ic.academico;

import br.ufal.ic.academico.model.*;
import io.dropwizard.hibernate.AbstractDAO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;



@Slf4j
public class Database extends AbstractDAO<Object> {
    
    public Database(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public <T> T get(Class<T> clazz, Serializable id) throws HibernateException {
        log.info("getting {}: id={}", clazz.getSimpleName(), id);
        return currentSession().get(clazz, id);
    }

    public List<Object> getAll(Class clazz) throws HibernateException {
        log.info("getting all " + clazz.getSimpleName());
        return super.list(query("from " + clazz.getSimpleName()));
    }

    public Student getStudent(String registration) throws HibernateException {
        log.info("getting Students with cpf");
        return (Student) currentSession().createQuery("Select std From Student std where std.registration = " + "'" + registration + "'").getSingleResult();
    }


    public Course getStudentCourse(Student student) throws HibernateException {
        log.info("getting Course");
        Course course = (Course) currentSession().createQuery("SELECT c FROM Course as c JOIN c.students stds where stds.id =" + student.getId()).getSingleResult();

        return course;
    }

    public List<Object> getAvailableSubjects(Course course) throws HibernateException {
        log.info("getting Available subjects for course: " + course.getName());

        List<Object> subjects = currentSession().createQuery("Select c.regularSubjects From Course c where c.id = " + course.getId()).list();
        subjects.addAll(currentSession().createQuery("Select c.optionalSubjects From Course c where c.id = " + course.getId()).list());

        return subjects;
    }

    public List<Object> getCompletedSubjects(Student student) throws HibernateException {
        log.info("getting completed Subjects for student: " + student.getName());

        List<Object> subjects = currentSession().createQuery("Select st.completedSubjects From Student st where st.id = " + student.getId()).list();

        return subjects;
    }

    public List<Object> getRequiredSubjects(Subject subject) throws HibernateException {
        log.info("getting required Subjects for: " + subject.getName());

        List<Object> subjects = currentSession().createQuery("Select s.subjectsRequired From Subject s where s.id = " + subject.getId()).list();

        return subjects;
    }

    public List<Object> getOfferSubject(Subject subject) throws HibernateException {
        log.info("getting Offer for: " + subject.getName());

        List<Object> offers = currentSession().createQuery("Select o From Offer o where o.subject.id = " + subject.getId()).list();

        return offers;
    }

    public List<Object> getOffers(Secretary secretary) throws HibernateException {
        log.info("getting Offer for: " + secretary.getName());

        List<Object> offers = currentSession().createQuery("Select o From Offer o where o.secretary.id = " + secretary.getId()).list();

        return offers;
    }

    public List<Object> getEnrolledStudents(Offer offer) throws HibernateException {
        log.info("getting Students enrolled in offer: " + offer.getOfferName());

        List<Object> students = currentSession().createQuery("Select std From Student std JOIN std.enrolledSubjects ofrs where ofrs.id = " + offer.getId()).list();

        return students;
    }


    public <T> T persist(Class<T> clazz, T entity) throws HibernateException {
        if (clazz == null) throw new NullPointerException();
        return (T) super.persist(entity);
    }
}
