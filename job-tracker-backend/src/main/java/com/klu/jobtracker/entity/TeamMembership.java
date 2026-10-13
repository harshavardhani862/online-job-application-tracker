package com.klu.jobtracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "team_memberships", uniqueConstraints = {
        @UniqueConstraint(name = "uk_team_membership_user", columnNames = "user_id")
})
public class TeamMembership {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TeamRole role;

    @Column(nullable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();

    protected TeamMembership() {}

    public TeamMembership(Organization organization, User user, TeamRole role) {
        this.organization = organization;
        this.user = user;
        this.role = role;
    }

    public Long getId() { return id; }
    public Organization getOrganization() { return organization; }
    public User getUser() { return user; }
    public TeamRole getRole() { return role; }
    public LocalDateTime getJoinedAt() { return joinedAt; }
}
