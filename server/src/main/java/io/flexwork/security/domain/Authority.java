package io.flexwork.security.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;
import org.springframework.data.domain.Persistable;

/** A Authority. */
@Entity
@Table(name = "fw_authority")
@JsonIgnoreProperties(value = {"new", "id"})
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Authority implements Serializable, Persistable<String> {

    private static final long serialVersionUID = 1L;

    @NotNull @Size(max = 50)
    @Id
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @NotNull @Size(max = 50)
    @Column(name = "descriptive_name", length = 50, nullable = false, unique = true)
    private String descriptiveName;

    @Transient private boolean isPersisted;

    public String getName() {
        return this.name;
    }

    public Authority name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    @PostLoad
    @PostPersist
    public void updateEntityState() {
        this.setIsPersisted();
    }

    @Override
    public String getId() {
        return this.name;
    }

    @Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public Authority setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public @NotNull @Size(max = 50) String getDescriptiveName() {
        return descriptiveName;
    }

    public void setDescriptiveName(@NotNull @Size(max = 50) String roleName) {
        this.descriptiveName = roleName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Authority)) {
            return false;
        }
        return getName() != null && getName().equals(((Authority) o).getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Authority{" + "name=" + getName() + "}";
    }
}