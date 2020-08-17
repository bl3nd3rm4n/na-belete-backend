package com.blndr.nabeletebackend.model;



import javax.persistence.*;

@Entity
@Table(name = "tickets")
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer ticketId;
    @ManyToOne
    @JoinColumn(name = "ticket_category_id", nullable = false)
    private TicketCategory ticketCategory;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Integer getTicketId() {
        return ticketId;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
