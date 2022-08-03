import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { DurationFormat } from 'app/shared/DurationFormat';

import { IRota } from 'app/shared/model/rota.model';
import { getEntities } from './rota.reducer';

export const Rota = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const rotaList = useAppSelector(state => state.gateway.rota.entities);
  const loading = useAppSelector(state => state.gateway.rota.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  return (
    <div>
      <h2 id="rota-heading" data-cy="RotaHeading">
        <Translate contentKey="gatewayApp.rota.home.title">Rotas</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="gatewayApp.rota.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/rota/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="gatewayApp.rota.home.createLabel">Create new Rota</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {rotaList && rotaList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="gatewayApp.rota.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.rota.client">Client</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.rota.carer">Carer</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.rota.time">Time</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.rota.duration">Duration</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {rotaList.map((rota, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/rota/${rota.id}`} color="link" size="sm">
                      {rota.id}
                    </Button>
                  </td>
                  <td>{rota.client}</td>
                  <td>{rota.carer}</td>
                  <td>{rota.time ? <TextFormat type="date" value={rota.time} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{rota.duration ? <DurationFormat value={rota.duration} /> : null}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/rota/${rota.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/rota/${rota.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/rota/${rota.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="gatewayApp.rota.home.notFound">No Rotas found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Rota;
