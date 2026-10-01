import { Location } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ActivatedRoute } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { Comprobante } from '../../../core/models/comprobante.model';
import { VentaService } from '../../../core/services/venta.service';
import { getHttpErrorMessage } from '../../../core/utils/http-error.util';

@Component({
  selector: 'app-comprobante-view',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  templateUrl: './comprobante-view.component.html',
  styleUrl: './comprobante-view.component.scss'
})
export class ComprobanteViewComponent implements OnInit, OnDestroy {
  readonly codigoControl = new FormControl('', { nonNullable: true, validators: [Validators.required] });
  comprobante: Comprobante | null = null;
  cargando = false;
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
        this.cargarComprobante(codigo);
      } else {
        this.comprobante = null;
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
    this.location.go(`/comprobantes/${encodeURIComponent(codigo)}`);
    this.cargarComprobante(codigo);
  }

  private cargarComprobante(codigo: string): void {
    this.cargando = true;
    this.comprobante = null;
    this.ventaService.obtenerComprobante(codigo).subscribe({
      next: (comprobante) => {
        this.comprobante = comprobante;
        this.cargando = false;
      },
      error: (error) => {
        this.cargando = false;
        this.snackBar.open(getHttpErrorMessage(error), 'Cerrar', { duration: 6000 });
      }
    });
  }
}
