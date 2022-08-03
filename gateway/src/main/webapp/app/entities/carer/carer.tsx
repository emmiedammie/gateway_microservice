import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ICarer } from 'app/shared/model/carer.model';
import { getEntities } from './carer.reducer';

export const Carer = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const carerList = useAppSelector(state => state.gateway.carer.entities);
  const loading = useAppSelector(state => state.gateway.carer.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  return (
    <div>
      <h2 id="carer-heading" data-cy="CarerHeading">
        <Translate contentKey="gatewayApp.carer.home.title">Carers</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="gatewayApp.carer.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/carer/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="gatewayApp.carer.home.createLabel">Create new Carer</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {carerList && carerList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="gatewayApp.carer.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.carer.name">Name</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.carer.phone">Phone</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.carer.daysavailable">Daysavailable</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.carer.client">Client</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {carerList.map((carer, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/carer/${carer.id}`} color="link" size="sm">
                      {carer.id}
                    </Button>
                  </td>
                  <td>{carer.name}</td>
                  <td>{carer.phone}</td>
                  <td>
                    <Translate contentKey={`gatewayApp.Days.${carer.daysavailable}`} />
                  </td>
                  <td>{carer.client ? <Link to={`/client/${carer.client.id}`}>{carer.client.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/carer/${carer.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/carer/${carer.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/carer/${carer.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
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
              <Translate contentKey="gatewayApp.carer.home.notFound">No Carers found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Carer;
