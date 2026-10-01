import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_CONFIG } from '../config/api.config';
import { Comprobante } from '../models/comprobante.model';
import { RegistrarVentaRequest } from '../models/registrar-venta-request.model';
import { Venta } from '../models/venta.model';

@Injectable({ providedIn: 'root' })
export class VentaService {
  private readonly ventasUrl = `${API_CONFIG.apiUrl}/ventas`;

  constructor(private readonly http: HttpClient) {}

  registrarVenta(request: RegistrarVentaRequest): Observable<Venta> {
    return this.http.post<Venta>(this.ventasUrl, request);
  }

  buscarVenta(codigo: string): Observable<Venta> {
    return this.http.get<Venta>(`${this.ventasUrl}/${encodeURIComponent(codigo)}`);
  }

  anularVenta(codigo: string): Observable<void> {
    return this.http.put<void>(`${this.ventasUrl}/${encodeURIComponent(codigo)}/anular`, null);
  }

  obtenerComprobante(codigo: string): Observable<Comprobante> {
    return this.http.get<Comprobante>(`${this.ventasUrl}/${encodeURIComponent(codigo)}/comprobante`);
  }
}
