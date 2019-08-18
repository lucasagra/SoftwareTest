package br.ufal.ic.academico;

import br.ufal.ic.academico.control.Control;

import br.ufal.ic.academico.model.Department;
import br.ufal.ic.academico.model.Professor;
import br.ufal.ic.academico.model.Student;
import br.ufal.ic.academico.model.Subject;
import br.ufal.ic.academico.model.Course;
import br.ufal.ic.academico.model.Secretary;
import br.ufal.ic.academico.model.Offer;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Environment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.Application;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;


@Slf4j
public class AcademicoApp extends Application<ConfigApp> {

    private Control control;

    public static void main(String[] args) throws Exception {
        new AcademicoApp().run(args);
    }

    @Override
    public void initialize(Bootstrap<ConfigApp> bootstrap) {
        log.info("initialize");
        bootstrap.addBundle(hibernate);
    }

    @Override
    public void run(ConfigApp config, Environment environment) {

        SessionFactory sessionFactory = hibernate.getSessionFactory();

        final Database db = new Database(sessionFactory);

//      final MyResource resource = new MyResource(db);
//      environment.jersey().register(resource);

        control = new Control(db, sessionFactory);
        control.main();
    }


    private final HibernateBundle<ConfigApp> hibernate = new HibernateBundle<ConfigApp>(Student.class, Subject.class, Department.class,
            Course.class, Secretary.class, Professor.class, Offer.class) {
        
        @Override
        public DataSourceFactory getDataSourceFactory(ConfigApp configuration) {
            return configuration.getDatabase();
        }
    };
}
