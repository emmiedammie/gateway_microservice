import carer from 'app/entities/carer/carer.reducer';
import rota from 'app/entities/rota/rota.reducer';
import client from 'app/entities/client/client.reducer';
import visit from 'app/entities/visit/visit.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  carer,
  rota,
  client,
  visit,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
