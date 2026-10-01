import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { Router } from '@angular/router';
import { Producto } from '../../../core/models/producto.model';
import { RegistrarVentaRequest } from '../../../core/models/registrar-venta-request.model';
import { ProductoService } from '../../../core/services/producto.service';
import { VentaService } from '../../../core/services/venta.service';
import { getHttpErrorMessage } from '../../../core/utils/http-error.util';

interface ProductoSeleccionado {
  producto: Producto;
  cantidad: number;
}

@Component({
  selector: 'app-venta-create',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatRadioModule,
    MatSelectModule,
    MatSnackBarModule,
    MatTableModule
  ],
  templateUrl: './venta-create.component.html',
  styleUrl: './venta-create.component.scss'
})
export class VentaCreateComponent implements OnInit {
  readonly tiposEnvio = [
    { valor: 'ESTANDAR', etiqueta: 'Estándar' },
    { valor: 'EXPRESS', etiqueta: 'Express' },
    { valor: 'PROGRAMADO', etiqueta: 'Programado' }
  ];
  readonly tiposPago = ['YAPE', 'PLIN'];
  readonly displayedColumns = ['codigo', 'producto', 'precio', 'cantidad', 'subtotal', 'accion'];

  productos: Producto[] = [];
  detalle: ProductoSeleccionado[] = [];
  cargandoProductos = false;
  guardando = false;
  detalleInvalido = false;

  readonly ventaForm = new FormGroup({
    codigoVenta: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    tipoEnvio: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    tipoPago: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    numeroDestino: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    titular: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    descripcionPago: new FormControl('', { nonNullable: true })
  });

  readonly productoForm = new FormGroup({
    codigo: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    cantidad: new FormControl(1, { nonNullable: true, validators: [Validators.required, Validators.min(1)] })
  });

  constructor(
    private readonly productoService: ProductoService,
    private readonly ventaService: VentaService,
    private readonly router: Router,
    private readonly snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.cargarProductos();
  }

  get productosDisponibles(): Producto[] {
    return this.productos.filter((producto) => producto.activo && producto.stock > 0);
  }

  cargarProductos(): void {
    this.cargandoProductos = true;
    this.productoService.listarProductos().subscribe({
      next: (productos) => {
        this.productos = productos;
        this.cargandoProductos = false;
      },
      error: (error) => {
        this.cargandoProductos = false;
        this.snackBar.open(getHttpErrorMessage(error), 'Cerrar', { duration: 5000 });
      }
    });
  }

  agregarProducto(): void {
    if (this.productoForm.invalid) {
      this.productoForm.markAllAsTouched();
      return;
    }

    const { codigo, cantidad } = this.productoForm.getRawValue();
    const producto = this.productosDisponibles.find((item) => item.codigo === codigo);
    if (!producto) {
      this.snackBar.open('Selecciona un producto disponible.', 'Cerrar', { duration: 3500 });
      return;
    }

    const existente = this.detalle.find((item) => item.producto.codigo === codigo);
    this.detalle = existente
      ? this.detalle.map((item) => item === existente ? { ...item, cantidad: item.cantidad + cantidad } : item)
      : [...this.detalle, { producto, cantidad }];
    this.detalleInvalido = false;
    this.productoForm.reset({ codigo: '', cantidad: 1 });
  }

  eliminarProducto(codigo: string): void {
    this.detalle = this.detalle.filter((item) => item.producto.codigo !== codigo);
  }

  registrarVenta(): void {
    this.detalleInvalido = this.detalle.length === 0;
    if (this.ventaForm.invalid || this.detalleInvalido) {
      this.ventaForm.markAllAsTouched();
      this.snackBar.open('Completa los campos obligatorios y agrega al menos un producto.', 'Cerrar', { duration: 4500 });
      return;
    }

    const formulario = this.ventaForm.getRawValue();
    const productos = this.detalle.reduce<Record<string, number>>((resultado, item) => {
      resultado[item.producto.codigo] = item.cantidad;
      return resultado;
    }, {});
    const request: RegistrarVentaRequest = { ...formulario, productos };

    this.guardando = true;
    this.ventaService.registrarVenta(request).subscribe({
      next: () => {
        this.guardando = false;
        this.snackBar.open('Venta registrada correctamente', 'Cerrar', { duration: 3500 });
        void this.router.navigate(['/ventas', formulario.codigoVenta]);
      },
      error: (error) => {
        this.guardando = false;
        this.snackBar.open(getHttpErrorMessage(error), 'Cerrar', { duration: 6000 });
      }
    });
  }
}
