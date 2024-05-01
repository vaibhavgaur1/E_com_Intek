package com.e_commerce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter@Setter@AllArgsConstructor@NoArgsConstructor@Builder
public class Role {

    @Id
    private String roleName;
    private String roleDescription;
}
