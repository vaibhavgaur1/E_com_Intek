package com.e_commerce.entity;

import com.e_commerce.Dto.AdminDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter @Setter
@Entity @Table @Builder @AllArgsConstructor @NoArgsConstructor
public class Admin implements UserDetails {

    @Id
    @GeneratedValue
    private Long id;

    private String serviceNo;
    private String name;
    private String email;
    private String password;
    private String contact;
    private boolean isActive;

//    @OneToOne(fetch = FetchType.LAZY)
//    @JsonIgnore
//    private Store store;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinTable(
            name = "admin_role",
            joinColumns = {
                    @JoinColumn(name = "admin_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "role")
            }
    )
    private Set<Role> roles;
//    private Role role;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Set<GrantedAuthority> authorities= new HashSet<>();
        getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        });
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public AdminDto generateDto(){
//        Set<Role> roleSet= new HashSet<>();
        Role role = roles.stream().findFirst().get();
        return AdminDto.builder().id(id)
            .serviceNo(serviceNo).email(email).password(password)
            .contact(contact).isActive(isActive).role(role.getRoleName()).name(name)
            .build();
    }
}
