package br.ufrn.imd.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Represents a user in the system, serving as a base class for more specific types of users
 * like Player and Manager. It handles common user attributes.
 */

public abstract class User implements UserDetails {

    @Id
    private String id;  // MongoDB primary key for User objects

    private String name;
    private String username;
    private String email;
    private String password;
    private String phone;
    private String image;
    private String nickname;
    private String address;

    @Field("role")
    private Role role;

    /**
     * Constructs a new User with provided credentials.
     * 
     * @param name     the user's name
     * @param username the user's username
     * @param email    the user's email address
     * @param password the user's password
     */
    public User(String name, String username, String email, String password, Role role) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;  // Set the role here
    }

    public User(String address, String email, String id, String image, String name, String nickname, String password, String phone, Role role, String username) {
        this.address = address;
        this.email = email;
        this.id = id;
        this.image = image;
        this.name = name;
        this.nickname = nickname;
        this.password = password;
        this.phone = phone;
        this.role = role;
        this.username = username;
    }

    // Accessor methods
    public enum Role {
        ROLE_PLAYER, ROLE_MANAGER
    }
    
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
