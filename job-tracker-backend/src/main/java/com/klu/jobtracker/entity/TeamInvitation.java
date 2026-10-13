package com.klu.jobtracker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "team_invitations", indexes = {
        @Index(name = "idx_team_invitation_org", columnList = "organization_id")
})
public class TeamInvitation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 36)
    private String token = UUID.randomUUID().toString();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invited_by", nullable = false)
    private User invitedBy;

    @Column(nullable = false, length = 254)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TeamRole role;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime expiresAt = LocalDateTime.now().plusDays(7);

    @Column
    private LocalDateTime acceptedAt;

    protected TeamInvitation() {}

    public TeamInvitation(Organization organization, User invitedBy, String email, TeamRole role) {
        this.organization = organization;
        this.invitedBy = invitedBy;
        this.email = email;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getToken() { return token; }
    public Organization getOrganization() { return organization; }
    public User getInvitedBy() { return invitedBy; }
    public String getEmail() { return email; }
    public TeamRole getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public LocalDateTime getAcceptedAt() { return acceptedAt; }
    public void accept() { this.acceptedAt = LocalDateTime.now(); }
}
