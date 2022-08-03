import { IClient } from 'app/shared/model/client.model';
import { Days } from 'app/shared/model/enumerations/days.model';

export interface ICarer {
  id?: number;
  name?: string;
  phone?: number | null;
  daysavailable?: Days;
  client?: IClient | null;
}

export const defaultValue: Readonly<ICarer> = {};
