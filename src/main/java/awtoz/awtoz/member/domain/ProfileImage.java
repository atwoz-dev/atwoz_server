package awtoz.awtoz.member.domain;

import jakarta.persistence.*;

@Entity
public class ProfileImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long member_id;

    private String url;

    private Boolean is_primary;

    private Long sort_order;
}
