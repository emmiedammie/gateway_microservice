import { ICarer } from 'app/shared/model/carer.model';

export interface IClient {
  id?: number;
  name?: string;
  phone?: number | null;
  age?: number;
  address?: string;
  accesscode?: number;
  task?: string | null;
  carerassigned?: string;
  carers?: ICarer[] | null;
}

export const defaultValue: Readonly<IClient> = {};
