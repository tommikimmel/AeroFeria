import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { CatalogService } from './catalog.service';
import { PublicationDetail, PageResponse, PublicationCard } from '../models/publication.model';

describe('CatalogService', () => {
  let service: CatalogService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        CatalogService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(CatalogService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch publications with filters', () => {
    const mockResponse: PageResponse<PublicationCard> = {
      content: [
        {
          id: 1,
          title: 'Futaba 16IZ',
          slug: 'futaba-16iz',
          price: 650,
          currency: 'USD',
          condition: 'NEW',
          locationProvince: 'Buenos Aires',
          locationCity: 'Vicente López',
          viewsCount: 10,
          createdAt: '2026-09-13T10:00:00'
        }
      ],
      totalElements: 1,
      totalPages: 1,
      size: 20,
      number: 0,
      empty: false
    };

    service.getPublications({ search: 'futaba', currency: 'USD', page: 0, size: 20 }).subscribe(res => {
      expect(res.content.length).toBe(1);
      expect(res.content[0].title).toBe('Futaba 16IZ');
    });

    const req = httpMock.expectOne(r => r.url === '/api/v1/client/publications' && r.params.get('search') === 'futaba');
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should fetch publication by slug', () => {
    const mockDetail: PublicationDetail = {
      id: 1,
      title: 'Futaba 16IZ',
      slug: 'futaba-16iz',
      description: 'Radio completa',
      condition: 'NEW',
      price: 650,
      currency: 'USD',
      locationProvince: 'Buenos Aires',
      locationCity: 'Vicente López',
      status: 'ACTIVE',
      viewsCount: 11,
      whatsappClicksCount: 2,
      images: [],
      seller: {
        id: 10,
        fullName: 'HobbyMotors',
        phoneNumber: '5491145678901',
        isStore: true,
        isVerifiedStore: true
      },
      whatsappUrl: 'https://wa.me/5491145678901',
      createdAt: '2026-09-13T10:00:00',
      updatedAt: '2026-09-13T10:00:00'
    };

    service.getPublicationBySlugOrId('futaba-16iz').subscribe(detail => {
      expect(detail.id).toBe(1);
      expect(detail.slug).toBe('futaba-16iz');
    });

    const req = httpMock.expectOne('/api/v1/client/publications/futaba-16iz');
    expect(req.request.method).toBe('GET');
    req.flush(mockDetail);
  });

  it('should track whatsapp click and return deep link', () => {
    service.recordWhatsAppClick(1).subscribe(res => {
      expect(res.totalClicks).toBe(3);
      expect(res.whatsappUrl).toContain('wa.me');
    });

    const req = httpMock.expectOne('/api/v1/client/publications/1/whatsapp-click');
    expect(req.request.method).toBe('POST');
    req.flush({
      publicationId: 1,
      whatsappUrl: 'https://wa.me/5491145678901?text=Hola',
      totalClicks: 3,
      targetPhoneNumber: '5491145678901',
      message: 'Hola'
    });
  });

  it('should fetch category tree', () => {
    service.getCategories().subscribe(cats => {
      expect(cats.length).toBe(1);
      expect(cats[0].name).toBe('Aviones');
    });

    const req = httpMock.expectOne('/api/v1/client/categories');
    req.flush([{ id: 1, name: 'Aviones', slug: 'aviones', displayOrder: 1, subcategories: [] }]);
  });

  it('should fetch public stats', () => {
    service.getPublicStats().subscribe(stats => {
      expect(stats.totalPublications).toBe(40);
    });

    const req = httpMock.expectOne('/api/v1/client/stats');
    req.flush({ totalPublications: 40, totalStores: 2, totalCategories: 6, totalUsers: 15 });
  });
});
