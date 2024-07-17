package com.tupa.restaurante.entidades;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "tb_roles")
public class Role {

    @Id
    private String id;
    private String role; // Nome da role (ex: ROLE_GARCOM)

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public enum Values {
        ADMIN("ROLE_ADMIN"),
        BASIC("ROLE_BASIC"),
        GARCOM("ROLE_GARCOM");

        private final String roleName;

        Values(String roleName) {
            this.roleName = roleName;
        }

        public String getRoleName() {
            return roleName;
        }
    }
}
