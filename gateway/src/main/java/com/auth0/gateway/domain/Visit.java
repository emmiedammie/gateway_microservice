package com.auth0.gateway.domain;

import com.auth0.gateway.domain.enumeration.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Visit.
 */
@Table("visit")
public class Visit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("client")
    private String client;

    @NotNull(message = "must not be null")
    @Column("address")
    private String address;

    @NotNull(message = "must not be null")
    @Column("carer")
    private String carer;

    @NotNull(message = "must not be null")
    @Column("accesscode")
    private Integer accesscode;

    @NotNull(message = "must not be null")
    @Column("timein")
    private Instant timein;

    @NotNull(message = "must not be null")
    @Column("status")
    private Status status;

    @NotNull(message = "must not be null")
    @Column("timespent")
    private Duration timespent;

    @Transient
    private Rota rota;

    @Column("rota_id")
    private Long rotaId;

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
        this.rotaId = rota != null ? rota.getId() : null;
    }

    public Visit rota(Rota rota) {
        this.setRota(rota);
        return this;
    }

    public Long getRotaId() {
        return this.rotaId;
    }

    public void setRotaId(Long rota) {
        this.rotaId = rota;
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
