import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IClient } from 'app/shared/model/client.model';
import { getEntities as getClients } from 'app/entities/client/client.reducer';
import { ICarer } from 'app/shared/model/carer.model';
import { Days } from 'app/shared/model/enumerations/days.model';
import { getEntity, updateEntity, createEntity, reset } from './carer.reducer';

export const CarerUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const clients = useAppSelector(state => state.gateway.client.entities);
  const carerEntity = useAppSelector(state => state.gateway.carer.entity);
  const loading = useAppSelector(state => state.gateway.carer.loading);
  const updating = useAppSelector(state => state.gateway.carer.updating);
  const updateSuccess = useAppSelector(state => state.gateway.carer.updateSuccess);
  const daysValues = Object.keys(Days);

  const handleClose = () => {
    navigate('/carer');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getClients({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...carerEntity,
      ...values,
      client: clients.find(it => it.id.toString() === values.client.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          daysavailable: 'MONDAY',
          ...carerEntity,
          client: carerEntity?.client?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="gatewayApp.carer.home.createOrEditLabel" data-cy="CarerCreateUpdateHeading">
            <Translate contentKey="gatewayApp.carer.home.createOrEditLabel">Create or edit a Carer</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="carer-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('gatewayApp.carer.name')}
                id="carer-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField label={translate('gatewayApp.carer.phone')} id="carer-phone" name="phone" data-cy="phone" type="text" />
              <ValidatedField
                label={translate('gatewayApp.carer.daysavailable')}
                id="carer-daysavailable"
                name="daysavailable"
                data-cy="daysavailable"
                type="select"
              >
                {daysValues.map(days => (
                  <option value={days} key={days}>
                    {translate('gatewayApp.Days.' + days)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField id="carer-client" name="client" data-cy="client" label={translate('gatewayApp.carer.client')} type="select">
                <option value="" key="0" />
                {clients
                  ? clients.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/carer" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default CarerUpdate;
