package com.auth0.gateway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.auth0.gateway.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CarerTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Carer.class);
        Carer carer1 = new Carer();
        carer1.setId(1L);
        Carer carer2 = new Carer();
        carer2.setId(carer1.getId());
        assertThat(carer1).isEqualTo(carer2);
        carer2.setId(2L);
        assertThat(carer1).isNotEqualTo(carer2);
        carer1.setId(null);
        assertThat(carer1).isNotEqualTo(carer2);
    }
}
