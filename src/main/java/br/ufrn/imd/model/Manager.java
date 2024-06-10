package br.ufrn.imd.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a manager, storing manager-specific data in the "managers" MongoDB collection.
 * Inherits common user properties from the User class.
 */
@Document(collection = "managers")
public class Manager extends User {

    // List of events managed by this manager
    private List<Event> events;

    /**
     * Constructs a new Manager with specified user details and initializes an empty list of events.
     * @param name the manager's name
     * @param username the manager's username
     * @param email the manager's email
     * @param password the manager's password
     */
    public Manager(String name, String username, String email, String password) {
        super(name, username, email, password, Role.ROLE_MANAGER);
        this.events = new ArrayList<>();
    }

    /**
     * Gets the list of events associated with the manager.
     * @return a list of events
     */
    public List<Event> getEvents() {
        return events;
    }

    /**
     * Sets the list of events managed by the manager.
     * @param events a list of events to set
     */
    public void setEvents(List<Event> events) {
        this.events = new ArrayList<>(events);
    }

    /**
     * Adds an event to the manager's list of events.
     * @param event the event to add
     */
    public void addEvent(Event event) {
        this.events.add(event);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
