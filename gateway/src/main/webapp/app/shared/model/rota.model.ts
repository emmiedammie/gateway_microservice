import dayjs from 'dayjs';
import { IVisit } from 'app/shared/model/visit.model';

export interface IRota {
  id?: number;
  client?: string;
  carer?: string;
  time?: string;
  duration?: string;
  visit?: IVisit | null;
}

export const defaultValue: Readonly<IRota> = {};
