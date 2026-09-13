import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { StoreService } from './store.service';
import { Store, Storefront } from '../models/store.model';

describe('StoreService', () => {
  let service: StoreService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        StoreService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(StoreService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch active stores', () => {
    const mockStores: Store[] = [
      {
        id: 1,
        name: 'HobbyMotors',
        slug: 'hobbymotors',
        logoUrl: '/logo.webp',
        brandsRepresented: ['Futaba', 'OS'],
        shipsNationwide: true,
        locationProvince: 'Buenos Aires',
        locationCity: 'Vicente López',
        whatsappNumber: '5491145678901',
        isVerified: true,
        activePublicationsCount: 10
      }
    ];

    service.getStores().subscribe(stores => {
      expect(stores.length).toBe(1);
      expect(stores[0].slug).toBe('hobbymotors');
    });

    const req = httpMock.expectOne('/api/v1/client/stores');
    expect(req.request.method).toBe('GET');
    req.flush(mockStores);
  });

  it('should fetch storefront by slug', () => {
    const mockStorefront: Storefront = {
      store: {
        id: 1,
        name: 'HobbyMotors',
        slug: 'hobbymotors',
        logoUrl: '/logo.webp',
        brandsRepresented: ['Futaba'],
        shipsNationwide: true,
        locationProvince: 'Buenos Aires',
        locationCity: 'Vicente López',
        whatsappNumber: '5491145678901',
        isVerified: true,
        activePublicationsCount: 1
      },
      publications: []
    };

    service.getStoreBySlug('hobbymotors').subscribe(sf => {
      expect(sf.store.name).toBe('HobbyMotors');
    });

    const req = httpMock.expectOne('/api/v1/client/stores/hobbymotors');
    expect(req.request.method).toBe('GET');
    req.flush(mockStorefront);
  });
});
