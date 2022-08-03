import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './carer.reducer';

export const CarerDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const carerEntity = useAppSelector(state => state.gateway.carer.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="carerDetailsHeading">
          <Translate contentKey="gatewayApp.carer.detail.title">Carer</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{carerEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="gatewayApp.carer.name">Name</Translate>
            </span>
          </dt>
          <dd>{carerEntity.name}</dd>
          <dt>
            <span id="phone">
              <Translate contentKey="gatewayApp.carer.phone">Phone</Translate>
            </span>
          </dt>
          <dd>{carerEntity.phone}</dd>
          <dt>
            <span id="daysavailable">
              <Translate contentKey="gatewayApp.carer.daysavailable">Daysavailable</Translate>
            </span>
          </dt>
          <dd>{carerEntity.daysavailable}</dd>
          <dt>
            <Translate contentKey="gatewayApp.carer.client">Client</Translate>
          </dt>
          <dd>{carerEntity.client ? carerEntity.client.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/carer" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/carer/${carerEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CarerDetail;
