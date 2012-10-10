package hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static junit.framework.Assert.assertEquals;

public class CascadingSaveTest {

    private SessionFactory sessionFactory;

    @Before
    public void before() {
        final Configuration configuration = new Configuration();
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

    @After
    public void after() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
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
        assertEquals(p1Id, p2.getId());
        assertEquals(2, p2.children.size());
        s2.flush();
        s2.close();
    }

    @Test
    public void reattachAndSaveWithUnmodifiedChildren() {
        final Session s1 = sessionFactory.openSession();
        final Parent p1 = createParentWithChildren();
        s1.save(p1);
        s1.flush();
        s1.close();
        final Session s2 = sessionFactory.openSession();
        p1.description = "modified";
        final Parent mergedParent = (Parent) s2.merge(p1);
        s2.flush();
        s2.close();
        assertEquals(p1.getId(), mergedParent.getId());
    }

    /*
        Funny how collections of components (i.e. Embeddables) referred to by an entity causes all Child elements to be
        deleted and inserted on the below merge. Doesn't matter whether any child has been modified at all or not.
     */
    @Test
    public void reattachAndSaveWithModifiedChildren() {
        final Session s1 = sessionFactory.openSession();
        final Parent p1 = createParentWithChildren();
        s1.save(p1);
        s1.flush();
        s1.close();
        final Session s2 = sessionFactory.openSession();
        p1.description = "modified";
        p1.children.iterator().next().description = "modifiedChild";
        final Parent mergedParent = (Parent) s2.merge(p1);
        s2.flush();
        s2.close();
        assertEquals(p1.getId(), mergedParent.getId());
    }

    private Parent createParentWithChildren() {
        final Parent p1 = new Parent();
        p1.description = "p1desc";
        final Child c1 = new Child();
        c1.description = "c1desc";
        final Child c2 = new Child();
        c2.description = "c2desc";
        final Set<Child> children = new HashSet<Child>();
        children.add(c1);
        children.add(c2);
        p1.children = children;
        return p1;
    }

}
