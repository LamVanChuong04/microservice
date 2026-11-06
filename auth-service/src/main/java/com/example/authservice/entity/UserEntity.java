package com.example.authservice.entity;

import com.example.authservice.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class UserEntity extends BaseEntity {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String username;
    private String password;


    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<AddressEntity> address;
}
