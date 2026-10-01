import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { Producto } from '../../../core/models/producto.model';
import { ProductoService } from '../../../core/services/producto.service';
import { getHttpErrorMessage } from '../../../core/utils/http-error.util';

@Component({
  selector: 'app-producto-list',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatCardModule, MatProgressSpinnerModule, MatSnackBarModule, MatTableModule],
  templateUrl: './producto-list.component.html',
  styleUrl: './producto-list.component.scss'
})
export class ProductoListComponent implements OnInit {
  readonly displayedColumns = ['codigo', 'nombre', 'descripcion', 'precio', 'stock', 'estado'];
  productos: Producto[] = [];
  cargando = false;

  constructor(
    private readonly productoService: ProductoService,
    private readonly snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.cargarProductos();
  }

  cargarProductos(): void {
    this.cargando = true;
    this.productoService.listarProductos().subscribe({
      next: (productos) => {
        this.productos = productos;
        this.cargando = false;
      },
      error: (error) => {
        this.cargando = false;
        this.snackBar.open(getHttpErrorMessage(error), 'Cerrar', { duration: 5000 });
      }
    });
  }
}
