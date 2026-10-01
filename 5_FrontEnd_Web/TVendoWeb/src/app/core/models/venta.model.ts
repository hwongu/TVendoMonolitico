import { DetalleVenta } from './detalle-venta.model';
import { FechaApi } from './fecha-api.model';
import { Pago } from './pago.model';

export interface Venta {
  codigo: string;
  fecha: FechaApi;
  subtotal: number;
  costoEnvio: number;
  total: number;
  estado: string;
  observacion?: string;
  detalles: DetalleVenta[];
  pagos: Pago[];
}
