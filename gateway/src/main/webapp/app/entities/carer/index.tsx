import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Carer from './carer';
import CarerDetail from './carer-detail';
import CarerUpdate from './carer-update';
import CarerDeleteDialog from './carer-delete-dialog';

const CarerRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Carer />} />
    <Route path="new" element={<CarerUpdate />} />
    <Route path=":id">
      <Route index element={<CarerDetail />} />
      <Route path="edit" element={<CarerUpdate />} />
      <Route path="delete" element={<CarerDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CarerRoutes;
