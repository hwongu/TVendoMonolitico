import { Location } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';
import { Venta } from '../../../core/models/venta.model';
import { VentaService } from '../../../core/services/venta.service';
import { VentaDetailComponent } from './venta-detail.component';

describe('VentaDetailComponent', () => {
  let fixture: ComponentFixture<VentaDetailComponent>;
  let ventaService: jasmine.SpyObj<VentaService>;
  let location: jasmine.SpyObj<Location>;

  const venta: Venta = {
    codigo: 'V-002',
    fecha: [2026, 9, 30, 18, 27, 23],
    subtotal: 17900,
    costoEnvio: 191,
    total: 18091,
    estado: 'REGISTRADA',
    observacion: 'Registro de venta',
    detalles: [
      { productoId: 1, cantidad: 5, precioUnitario: 3500, subtotal: 17500 },
      { productoId: 2, cantidad: 5, precioUnitario: 80, subtotal: 400 }
    ],
    pagos: [
      {
        tipo: 'YAPE',
        monto: 18091,
        estado: 'APROBADO',
        codigoExterno: 'YAPE-BFE5CB81',
        fecha: [2026, 9, 30, 18, 27, 23],
        observacion: 'PAGO'
      }
    ]
  };

  beforeEach(async () => {
    ventaService = jasmine.createSpyObj<VentaService>('VentaService', ['buscarVenta', 'anularVenta']);
    location = jasmine.createSpyObj<Location>('Location', ['go']);
    ventaService.buscarVenta.and.returnValue(of(venta));

    await TestBed.configureTestingModule({
      imports: [VentaDetailComponent],
      providers: [
        provideNoopAnimations(),
        { provide: VentaService, useValue: ventaService },
        { provide: Location, useValue: location },
        { provide: MatSnackBar, useValue: jasmine.createSpyObj<MatSnackBar>('MatSnackBar', ['open']) },
        { provide: ActivatedRoute, useValue: { paramMap: of(convertToParamMap({})) } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(VentaDetailComponent);
    fixture.detectChanges();
  });

  it('consulta y renderiza la venta al hacer clic en Consultar', () => {
    const input: HTMLInputElement = fixture.nativeElement.querySelector('input');
    input.value = 'V-002';
    input.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    const button: HTMLButtonElement = fixture.nativeElement.querySelector('button[type="submit"]');
    button.click();
    fixture.detectChanges();

    expect(ventaService.buscarVenta).toHaveBeenCalledOnceWith('V-002');
    expect(location.go).toHaveBeenCalledOnceWith('/ventas/V-002');
    expect(fixture.nativeElement.textContent).toContain('Venta V-002');
    expect(fixture.nativeElement.textContent).toContain('APROBADO');
    expect(fixture.nativeElement.textContent).toContain('YAPE-BFE5CB81');

    const respuestaCompleta: HTMLTextAreaElement = fixture.nativeElement.querySelector('.raw-response textarea');
    expect(respuestaCompleta.readOnly).toBeTrue();
    expect(respuestaCompleta.value).toContain('"codigo": "V-002"');
  });
});
