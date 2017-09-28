package com.benyamephrem.contactmgr;

import com.benyamephrem.contactmgr.model.Contact;
import com.benyamephrem.contactmgr.model.Contact.ContactBuilder;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;


public class Application {
    //Hold a reusable reference of a SessionFactory (since we only need one)
    public static final SessionFactory sessionFactory = buildSessionFactory();

    //Static members are always loaded as soon as they are needed by the JVM so this is ran first before main is run
    //therefore initializing session factory before any other code is ran
    private static SessionFactory buildSessionFactory() {
        //Create a StandardServiceRegistry object
        //Makes builder object, .configure(*path*) loads the .cfg file (we already made that), and build builds the object
        final ServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();

        //Load the entity objects from the .cfg file and build SessionFactory
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public static void main(String[] args) {
        Contact contact = new ContactBuilder("Yams", "Galore")
                .withEmail("ben@gmail.com")
                .withPhone(4438213821L)
                .build();

        save(contact);

        fetchAllContacts().stream().forEach(System.out::println);

    }

    @SuppressWarnings("unchecked")
    public static List<Contact> fetchAllContacts(){
        //Open a session
        Session session = sessionFactory.openSession();

        // Create Criteria Builder
        CriteriaBuilder builder = session.getCriteriaBuilder();

        //Create Criteria Query
        CriteriaQuery<Contact> criteria = builder.createQuery(Contact.class);

        //Specify Criteria Root
        criteria.from(Contact.class);

        //Execute a query and get a list of contact objects based on criteria
        List<Contact> contacts = session.createQuery(criteria).getResultList();

        //Close the session
        session.close();

        return contacts;
    }

    public static void save(Contact contact){
        //Open a session
        Session session = sessionFactory.openSession();

        //Begin a transaction
        session.beginTransaction(); //To support hibernate transactions we have to add a Gradle dependency

        //Use the session to save the contact object
        session.save(contact);

        //Commit the transaction
        session.getTransaction().commit();

        //Close the session
        session.close();
    }

}
