import { CommonModule, Location } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { ActivatedRoute } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { FechaApi } from '../../../core/models/fecha-api.model';
import { Venta } from '../../../core/models/venta.model';
import { VentaService } from '../../../core/services/venta.service';
import { getHttpErrorMessage } from '../../../core/utils/http-error.util';

@Component({
  selector: 'app-venta-detail',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatDividerModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatTableModule
  ],
  templateUrl: './venta-detail.component.html',
  styleUrl: './venta-detail.component.scss'
})
export class VentaDetailComponent implements OnInit, OnDestroy {
  readonly detalleColumns = ['productoId', 'cantidad', 'precioUnitario', 'subtotal'];
  readonly pagoColumns = ['tipo', 'monto', 'estado', 'codigoExterno', 'fecha', 'observacion'];
  readonly codigoControl = new FormControl('', { nonNullable: true, validators: [Validators.required] });

  venta: Venta | null = null;
  cargando = false;
  anulando = false;
  private readonly destroy$ = new Subject<void>();

  constructor(
    private readonly route: ActivatedRoute,
    private readonly location: Location,
    private readonly ventaService: VentaService,
    private readonly snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.route.paramMap.pipe(takeUntil(this.destroy$)).subscribe((params) => {
      const codigo = params.get('codigo');
      if (codigo) {
        this.codigoControl.setValue(codigo);
        this.cargarVenta(codigo);
      } else {
        this.venta = null;
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  consultar(event?: Event): void {
    event?.preventDefault();
    const codigo = this.codigoControl.value.trim();
    if (!codigo) {
      this.codigoControl.markAsTouched();
      return;
    }

    this.codigoControl.setValue(codigo);
    this.location.go(`/ventas/${encodeURIComponent(codigo)}`);
    this.cargarVenta(codigo);
  }

  formatearFecha(fecha: FechaApi): string {
    if (Array.isArray(fecha)) {
      const [anio, mes, dia, hora = 0, minuto = 0, segundo = 0] = fecha;
      if ([anio, mes, dia].some((valor) => !Number.isFinite(valor))) {
        return fecha.join('-');
      }
      const fechaLocal = new Date(anio, mes - 1, dia, hora, minuto, segundo);
      return new Intl.DateTimeFormat('es-PE', {
        dateStyle: 'medium',
        timeStyle: 'medium'
      }).format(fechaLocal);
    }

    const fechaConvertida = new Date(fecha);
    return Number.isNaN(fechaConvertida.getTime())
      ? fecha
      : new Intl.DateTimeFormat('es-PE', {
          dateStyle: 'medium',
          timeStyle: 'medium'
        }).format(fechaConvertida);
  }

  anularVenta(): void {
    if (!this.venta || !confirm(`¿Deseas anular la venta ${this.venta.codigo}?`)) {
      return;
    }

    const codigo = this.venta.codigo;
    this.anulando = true;
    this.ventaService.anularVenta(codigo).subscribe({
      next: () => {
        this.anulando = false;
        this.snackBar.open('Venta anulada correctamente', 'Cerrar', { duration: 3500 });
        this.cargarVenta(codigo);
      },
      error: (error) => {
        this.anulando = false;
        this.snackBar.open(getHttpErrorMessage(error), 'Cerrar', { duration: 6000 });
      }
    });
  }

  private cargarVenta(codigo: string): void {
    this.cargando = true;
    this.venta = null;
    this.ventaService.buscarVenta(codigo).subscribe({
      next: (venta) => {
        this.venta = venta;
        this.cargando = false;
      },
      error: (error) => {
        this.cargando = false;
        this.snackBar.open(getHttpErrorMessage(error), 'Cerrar', { duration: 6000 });
      }
    });
  }
}
