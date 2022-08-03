package com.auth0.visit.domain;

import com.auth0.visit.domain.enumeration.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Visit.
 */
@Entity
@Table(name = "visit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Visit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "client", nullable = false)
    private String client;

    @NotNull
    @Column(name = "address", nullable = false)
    private String address;

    @NotNull
    @Column(name = "carer", nullable = false)
    private String carer;

    @NotNull
    @Column(name = "accesscode", nullable = false)
    private Integer accesscode;

    @NotNull
    @Column(name = "timein", nullable = false)
    private Instant timein;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @NotNull
    @Column(name = "timespent", nullable = false)
    private Duration timespent;

    @JsonIgnoreProperties(value = { "visit" }, allowSetters = true)
    @OneToOne
    @JoinColumn(unique = true)
    private Rota rota;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Visit id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClient() {
        return this.client;
    }

    public Visit client(String client) {
        this.setClient(client);
        return this;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getAddress() {
        return this.address;
    }

    public Visit address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCarer() {
        return this.carer;
    }

    public Visit carer(String carer) {
        this.setCarer(carer);
        return this;
    }

    public void setCarer(String carer) {
        this.carer = carer;
    }

    public Integer getAccesscode() {
        return this.accesscode;
    }

    public Visit accesscode(Integer accesscode) {
        this.setAccesscode(accesscode);
        return this;
    }

    public void setAccesscode(Integer accesscode) {
        this.accesscode = accesscode;
    }

    public Instant getTimein() {
        return this.timein;
    }

    public Visit timein(Instant timein) {
        this.setTimein(timein);
        return this;
    }

    public void setTimein(Instant timein) {
        this.timein = timein;
    }

    public Status getStatus() {
        return this.status;
    }

    public Visit status(Status status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Duration getTimespent() {
        return this.timespent;
    }

    public Visit timespent(Duration timespent) {
        this.setTimespent(timespent);
        return this;
    }

    public void setTimespent(Duration timespent) {
        this.timespent = timespent;
    }

    public Rota getRota() {
        return this.rota;
    }

    public void setRota(Rota rota) {
        this.rota = rota;
    }

    public Visit rota(Rota rota) {
        this.setRota(rota);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Visit)) {
            return false;
        }
        return id != null && id.equals(((Visit) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Visit{" +
            "id=" + getId() +
            ", client='" + getClient() + "'" +
            ", address='" + getAddress() + "'" +
            ", carer='" + getCarer() + "'" +
            ", accesscode=" + getAccesscode() +
            ", timein='" + getTimein() + "'" +
            ", status='" + getStatus() + "'" +
            ", timespent='" + getTimespent() + "'" +
            "}";
    }
}
