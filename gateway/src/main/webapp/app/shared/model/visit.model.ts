import dayjs from 'dayjs';
import { IRota } from 'app/shared/model/rota.model';
import { Status } from 'app/shared/model/enumerations/status.model';

export interface IVisit {
  id?: number;
  client?: string;
  address?: string;
  carer?: string;
  accesscode?: number;
  timein?: string;
  status?: Status;
  timespent?: string;
  rota?: IRota | null;
}

export const defaultValue: Readonly<IVisit> = {};
