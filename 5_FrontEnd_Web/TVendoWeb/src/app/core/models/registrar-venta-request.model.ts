export interface RegistrarVentaRequest {
  codigoVenta: string;
  productos: Record<string, number>;
  tipoEnvio: string;
  tipoPago: string;
  numeroDestino: string;
  titular: string;
  descripcionPago: string;
}
