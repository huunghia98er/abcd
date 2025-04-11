package org.mos.uaa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
    private Long id;

    @Column(unique = true, nullable = false)
    private String phone;

    private String password;

    private Boolean isVerified = false;

    private Boolean isRequiredChangePw = true;

    public User(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }
}
