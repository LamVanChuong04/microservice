package com.example.authservice.entity;

import com.example.authservice.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AddressEntity extends BaseEntity {
    @Id
    @GeneratedValue(generator = "uuid")
    private String id;

    private String province;
    private String district;
    private String ward;
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
