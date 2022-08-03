package com.auth0.gateway.domain;

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
 * A Rota.
 */
@Table("rota")
public class Rota implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("client")
    private String client;

    @NotNull(message = "must not be null")
    @Column("carer")
    private String carer;

    @NotNull(message = "must not be null")
    @Column("time")
    private Instant time;

    @NotNull(message = "must not be null")
    @Column("duration")
    private Duration duration;

    @Transient
    private Visit visit;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Rota id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClient() {
        return this.client;
    }

    public Rota client(String client) {
        this.setClient(client);
        return this;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getCarer() {
        return this.carer;
    }

    public Rota carer(String carer) {
        this.setCarer(carer);
        return this;
    }

    public void setCarer(String carer) {
        this.carer = carer;
    }

    public Instant getTime() {
        return this.time;
    }

    public Rota time(Instant time) {
        this.setTime(time);
        return this;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public Duration getDuration() {
        return this.duration;
    }

    public Rota duration(Duration duration) {
        this.setDuration(duration);
        return this;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Visit getVisit() {
        return this.visit;
    }

    public void setVisit(Visit visit) {
        if (this.visit != null) {
            this.visit.setRota(null);
        }
        if (visit != null) {
            visit.setRota(this);
        }
        this.visit = visit;
    }

    public Rota visit(Visit visit) {
        this.setVisit(visit);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Rota)) {
            return false;
        }
        return id != null && id.equals(((Rota) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Rota{" +
            "id=" + getId() +
            ", client='" + getClient() + "'" +
            ", carer='" + getCarer() + "'" +
            ", time='" + getTime() + "'" +
            ", duration='" + getDuration() + "'" +
            "}";
    }
}
