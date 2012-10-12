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
        assertEquals(p1Id, p2.id);
        assertEquals(2, p2.children.size());
        s2.flush();
        s2.close();
    }

    /**
     * Recreate the object state after creating a graph in a session and that session is flushed and closed. This is in
     * order to wipe out the Hibernate proprietary collection types that are used for the Child elements. With a changed
     * parent, it doesn't matter whether or not the children are modified, they will always be deleted and inserted. This
     * goes for all the elements in the children collection.
     *
     * Note that this only happens with a Session.saveOrUpdate. A merge will handle this without delete and insert, only
     * deleting and inserting elements that are actually touched.
     */
    @Test
    public void saveWithNativeCollectionTypesWithUnmodifiedChildren() {
        final Session s1 = sessionFactory.openSession();
        final Parent p1 = createParentWithChildren();
        p1.id = 1L;
        p1.description = "desc1";
        s1.save(p1);
        s1.flush();
        s1.close();
        System.out.println("saved first time");

        final Session s2 = sessionFactory.openSession();
        final Parent p2 = createParentWithChildren();
        p2.id = 1L;
        p2.description = "modified";
        s2.saveOrUpdate(p2);
        s2.flush();
        s2.close();
    }

    /*
        Child updates are only triggered if the child elements do not implement equals and hashCode properly.
     */
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
        assertEquals(p1.id, mergedParent.id);
    }

    /*
        Funny how collections of components (i.e. Embeddables) referred to by an entity causes all Child elements to be
        deleted and inserted on the below merge. Doesn't matter whether any child has been modified at all or not.

        Funny update: Implementation of proper equals and hashCode in the Child type seems to solve this problem. I.e.
        when implemented properly and the element has a unique id, delete-insert combinations are not triggered for all
        elements in the embedabble collection, but a delete-insert combination *is* triggered for modified elements,
        rather than update. However, the end result wouldn be the same.

        BTW: Hibernate does not accept @Id on the @Embeddable type, neither does it allow optimistic locking on the
        child element.

        The JPA documentation suggests that this is more of a feature than a bug, ref:
        http://en.wikibooks.org/wiki/Java_Persistence/ElementCollection#Primary_keys_in_CollectionTable.


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
        final Child child = p1.children.iterator().next();
        child.description = "modifiedChild";
        final Parent mergedParent = (Parent) s2.merge(p1);
        s2.flush();
        s2.close();
        assertEquals(p1.id, mergedParent.id);
    }

    private Parent createParentWithChildren() {
        final Parent p1 = new Parent();
        p1.id = 1L;
        p1.description = "p1desc";
        final Child c1 = new Child();
        c1.description = "c1desc";
        c1.id = 1L;
        final Child c2 = new Child();
        c2.description = "c2desc";
        c2.id = 2L;
        final Set<Child> children = new HashSet<Child>();
        children.add(c1);
        children.add(c2);
        p1.children = children;
        return p1;
    }

}
