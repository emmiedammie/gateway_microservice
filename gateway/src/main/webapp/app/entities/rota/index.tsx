import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Rota from './rota';
import RotaDetail from './rota-detail';
import RotaUpdate from './rota-update';
import RotaDeleteDialog from './rota-delete-dialog';

const RotaRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Rota />} />
    <Route path="new" element={<RotaUpdate />} />
    <Route path=":id">
      <Route index element={<RotaDetail />} />
      <Route path="edit" element={<RotaUpdate />} />
      <Route path="delete" element={<RotaDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default RotaRoutes;
