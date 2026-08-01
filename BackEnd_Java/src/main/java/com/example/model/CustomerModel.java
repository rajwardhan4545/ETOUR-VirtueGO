package com.example.model;

import com.example.entities.BookingHeader;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.Set;


@NoArgsConstructor
@AllArgsConstructor
public class CustomerModel {

    private Integer id;

    private String email;

    private String phone;

    private String address;

    private String firstName;

    private String lastName;

    private String password;

    private String customerRole;

    private String authProvider;

    private Boolean profileCompleted;

//    private Set<BookingHeader> bookingHeaders;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCustomerRole() {
        return customerRole;
    }

    public void setCustomerRole(String customerRole) {
        this.customerRole = customerRole;
    }

    public String getAuthProvider() {
        return authProvider;
    }

    public void setAuthProvider(String authProvider) {
        this.authProvider = authProvider;
    }

    public Boolean getProfileCompleted() {
        return profileCompleted;
    }

    public void setProfileCompleted(Boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
    }

//    public Set<BookingHeader> getBookingHeaders() {
//        return bookingHeaders;
//    }
//
//    public void setBookingHeaders(Set<BookingHeader> bookingHeaders) {
//        this.bookingHeaders = bookingHeaders;
//    }
}
