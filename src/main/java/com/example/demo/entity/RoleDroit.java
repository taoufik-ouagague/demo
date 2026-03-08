package com.example.demo.entity;
import jakarta.persistence.*;
@Entity
@Table(name = "role_droit")
public class RoleDroit extends EntityClass {
      @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

        @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "droit_id", nullable = false)
    private Droit droit;

        @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

        public void setId(Integer id) {
            this.id = id;
        }

        public void setDroit(Droit droit) {
            this.droit = droit;
        }

        public void setRole(Role role) {
            this.role = role;
        }

        public Integer getId() {
            return id;
        }

        public Droit getDroit() {
            return droit;
        }

        public Role getRole() {
            return role;
        }


}
