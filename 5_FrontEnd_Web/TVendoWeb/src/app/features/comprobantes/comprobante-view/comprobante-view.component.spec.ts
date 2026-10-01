import { Location } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBar } from '@angular/material/snack-bar';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { of } from 'rxjs';
import { VentaService } from '../../../core/services/venta.service';
import { ComprobanteViewComponent } from './comprobante-view.component';

describe('ComprobanteViewComponent', () => {
  let fixture: ComponentFixture<ComprobanteViewComponent>;
  let ventaService: jasmine.SpyObj<VentaService>;
  let location: jasmine.SpyObj<Location>;

  beforeEach(async () => {
    ventaService = jasmine.createSpyObj<VentaService>('VentaService', ['obtenerComprobante']);
    location = jasmine.createSpyObj<Location>('Location', ['go']);
    ventaService.obtenerComprobante.and.returnValue(of({
      codigoVenta: 'V-002',
      comprobante: 'TVENDO\nVenta: V-002\nEstado: REGISTRADA'
    }));

    await TestBed.configureTestingModule({
      imports: [ComprobanteViewComponent],
      providers: [
        provideNoopAnimations(),
        { provide: VentaService, useValue: ventaService },
        { provide: Location, useValue: location },
        { provide: MatSnackBar, useValue: jasmine.createSpyObj<MatSnackBar>('MatSnackBar', ['open']) },
        { provide: ActivatedRoute, useValue: { paramMap: of(convertToParamMap({})) } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ComprobanteViewComponent);
    fixture.detectChanges();
  });

  it('consulta y renderiza el comprobante al hacer clic en Consultar', () => {
    const input: HTMLInputElement = fixture.nativeElement.querySelector('input');
    input.value = 'V-002';
    input.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    const button: HTMLButtonElement = fixture.nativeElement.querySelector('button[type="submit"]');
    button.click();
    fixture.detectChanges();

    expect(ventaService.obtenerComprobante).toHaveBeenCalledOnceWith('V-002');
    expect(location.go).toHaveBeenCalledOnceWith('/comprobantes/V-002');
    expect(fixture.nativeElement.textContent).toContain('Venta V-002');
    expect(fixture.nativeElement.textContent).toContain('REGISTRADA');
  });
});
