import { HttpErrorResponse } from '@angular/common/http';
import { ErrorResponse } from '../models/error-response.model';

const DEFAULT_MESSAGES: Record<number, string> = {
  400: 'Solicitud inválida. Revisa los datos ingresados.',
  404: 'El recurso solicitado no existe.',
  405: 'Método no permitido.',
  409: 'No se pudo completar la operación por un conflicto de negocio.',
  500: 'Ocurrió un error interno en el servidor.'
};

export function getHttpErrorMessage(error: unknown): string {
  if (!(error instanceof HttpErrorResponse)) {
    return 'Ocurrió un error inesperado.';
  }

  const response = error.error as Partial<ErrorResponse> | string | null;
  if (typeof response === 'string' && response.trim()) {
    return response;
  }
  if (response && typeof response === 'object' && typeof response.mensaje === 'string') {
    return response.mensaje;
  }
  return DEFAULT_MESSAGES[error.status] ?? 'No se pudo comunicar con TVendoApi.';
}
