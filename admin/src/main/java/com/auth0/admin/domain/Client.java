package com.auth0.admin.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Client.
 */
@Entity
@Table(name = "client")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Client implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone")
    private Long phone;

    @NotNull
    @Max(value = 200)
    @Column(name = "age", nullable = false)
    private Integer age;

    @NotNull
    @Column(name = "address", nullable = false)
    private String address;

    @NotNull
    @Column(name = "accesscode", nullable = false)
    private Integer accesscode;

    @Column(name = "task")
    private String task;

    @NotNull
    @Column(name = "carerassigned", nullable = false)
    private String carerassigned;

    @OneToMany(mappedBy = "client")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "client" }, allowSetters = true)
    private Set<Carer> carers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Client id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Client name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPhone() {
        return this.phone;
    }

    public Client phone(Long phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }

    public Integer getAge() {
        return this.age;
    }

    public Client age(Integer age) {
        this.setAge(age);
        return this;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getAddress() {
        return this.address;
    }

    public Client address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getAccesscode() {
        return this.accesscode;
    }

    public Client accesscode(Integer accesscode) {
        this.setAccesscode(accesscode);
        return this;
    }

    public void setAccesscode(Integer accesscode) {
        this.accesscode = accesscode;
    }

    public String getTask() {
        return this.task;
    }

    public Client task(String task) {
        this.setTask(task);
        return this;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getCarerassigned() {
        return this.carerassigned;
    }

    public Client carerassigned(String carerassigned) {
        this.setCarerassigned(carerassigned);
        return this;
    }

    public void setCarerassigned(String carerassigned) {
        this.carerassigned = carerassigned;
    }

    public Set<Carer> getCarers() {
        return this.carers;
    }

    public void setCarers(Set<Carer> carers) {
        if (this.carers != null) {
            this.carers.forEach(i -> i.setClient(null));
        }
        if (carers != null) {
            carers.forEach(i -> i.setClient(this));
        }
        this.carers = carers;
    }

    public Client carers(Set<Carer> carers) {
        this.setCarers(carers);
        return this;
    }

    public Client addCarer(Carer carer) {
        this.carers.add(carer);
        carer.setClient(this);
        return this;
    }

    public Client removeCarer(Carer carer) {
        this.carers.remove(carer);
        carer.setClient(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Client)) {
            return false;
        }
        return id != null && id.equals(((Client) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Client{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", phone=" + getPhone() +
            ", age=" + getAge() +
            ", address='" + getAddress() + "'" +
            ", accesscode=" + getAccesscode() +
            ", task='" + getTask() + "'" +
            ", carerassigned='" + getCarerassigned() + "'" +
            "}";
    }
}
