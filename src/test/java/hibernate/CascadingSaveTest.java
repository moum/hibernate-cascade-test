package hibernate;

import junit.framework.Assert;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.Properties;

import static junit.framework.Assert.*;

public class CascadingSaveTest {

    private SessionFactory sessionFactory;

    @Before
    public void before() {
        Configuration configuration = new Configuration();
        final Properties props = new Properties();
        props.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
        props.setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:testdb");
        props.setProperty("hibernate.connection.username", "sa");
        props.setProperty("hibernate.connection.password", "");
        props.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
        props.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        props.setProperty("hibernate.show_sql", "true");
        configuration.setProperties(props);
        configuration.addAnnotatedClass(Child.class);
        configuration.addAnnotatedClass(Parent.class);
        sessionFactory = configuration.buildSessionFactory();
    }

    @Test
    public void createSimpleParentChildRelationship() {
        final Session s1 = sessionFactory.openSession();
        final Parent p1 = createParentWithChildren();
        final Serializable p1Id = s1.save(p1);
        s1.flush();
        s1.close();
        final Session s2 = sessionFactory.openSession();
        final Parent p2 = (Parent) s2.get(Parent.class, p1Id);
        assertEquals(p1Id, p2.id);
        assertEquals(2, p2.children.size());
    }

    @Test
    public void reattachAndSaveWithChildren() {
        final Session s1 = sessionFactory.openSession();
        final Parent p1 = createParentWithChildren();
        final Serializable p1Id = s1.save(p1);

    }

    private Parent createParentWithChildren() {
        final Parent p1 = new Parent();
        p1.id = 1L;
        p1.description = "p1desc";
        final Child c1 = new Child();
        c1.id = 1L;
        c1.description = "c1desc";
        final Child c2 = new Child();
        c2.id = 2L;
        c2.description = "c2desc";
        p1.add(c1, c2);
        return p1;
    }

}
