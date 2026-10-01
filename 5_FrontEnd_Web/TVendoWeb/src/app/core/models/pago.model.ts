import { FechaApi } from './fecha-api.model';

export interface Pago {
  tipo: string;
  monto: number;
  estado: string;
  codigoExterno: string;
  fecha: FechaApi;
  observacion: string;
}
