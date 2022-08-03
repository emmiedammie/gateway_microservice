import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { DurationFormat } from 'app/shared/DurationFormat';

import { IVisit } from 'app/shared/model/visit.model';
import { getEntities } from './visit.reducer';

export const Visit = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const visitList = useAppSelector(state => state.gateway.visit.entities);
  const loading = useAppSelector(state => state.gateway.visit.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  return (
    <div>
      <h2 id="visit-heading" data-cy="VisitHeading">
        <Translate contentKey="gatewayApp.visit.home.title">Visits</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="gatewayApp.visit.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/visit/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="gatewayApp.visit.home.createLabel">Create new Visit</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {visitList && visitList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="gatewayApp.visit.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.visit.client">Client</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.visit.address">Address</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.visit.carer">Carer</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.visit.accesscode">Accesscode</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.visit.timein">Timein</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.visit.status">Status</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.visit.timespent">Timespent</Translate>
                </th>
                <th>
                  <Translate contentKey="gatewayApp.visit.rota">Rota</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {visitList.map((visit, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/visit/${visit.id}`} color="link" size="sm">
                      {visit.id}
                    </Button>
                  </td>
                  <td>{visit.client}</td>
                  <td>{visit.address}</td>
                  <td>{visit.carer}</td>
                  <td>{visit.accesscode}</td>
                  <td>{visit.timein ? <TextFormat type="date" value={visit.timein} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>
                    <Translate contentKey={`gatewayApp.Status.${visit.status}`} />
                  </td>
                  <td>{visit.timespent ? <DurationFormat value={visit.timespent} /> : null}</td>
                  <td>{visit.rota ? <Link to={`/rota/${visit.rota.id}`}>{visit.rota.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/visit/${visit.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/visit/${visit.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/visit/${visit.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
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
              <Translate contentKey="gatewayApp.visit.home.notFound">No Visits found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Visit;
