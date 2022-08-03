package com.auth0.gateway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.auth0.gateway.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RotaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Rota.class);
        Rota rota1 = new Rota();
        rota1.setId(1L);
        Rota rota2 = new Rota();
        rota2.setId(rota1.getId());
        assertThat(rota1).isEqualTo(rota2);
        rota2.setId(2L);
        assertThat(rota1).isNotEqualTo(rota2);
        rota1.setId(null);
        assertThat(rota1).isNotEqualTo(rota2);
    }
}
