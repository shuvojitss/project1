import React, { useState, useEffect } from 'react';
import { Row, Col, Form, Button, Modal, Alert } from 'react-bootstrap';
import { PageHeader } from '@/components/PageHeader';
import { DataTable, Column } from '@/components/DataTable';
import { StatusBadge } from '@/components/StatusBadge';
import { catalogApi } from '@/api/catalogApi';
import { usePagination } from '@/hooks/usePagination';
import { useDebounce } from '@/hooks/useDebounce';
import { formatCurrency } from '@/utils/formatters';
import { Product, Category } from '@/types';

export const CatalogPage: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [totalPages, setTotalPages] = useState(1);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 300);

  const { page, size, setPage } = usePagination(0, 10);

  // Create Product Modal State
  const [showModal, setShowModal] = useState(false);
  const [newSku, setNewSku] = useState('');
  const [newName, setNewName] = useState('');
  const [newDesc, setNewDesc] = useState('');
  const [newCategoryId, setNewCategoryId] = useState('');
  const [newUom] = useState('EACH');
  const [newCost, setNewCost] = useState<number>(10);
  const [newWeight, setNewWeight] = useState<number>(1);
  const [modalSubmitting, setModalSubmitting] = useState(false);
  const [modalError, setModalError] = useState<string | null>(null);

  const loadData = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const res = await catalogApi.getProducts(page, size, debouncedSearch, selectedCategory);
      setProducts(res.content);
      setTotalPages(res.totalPages);
    } catch {
      // Demo seed data fallback
      setProducts([
        {
          id: 'prod-001',
          sku: 'SKU-ELEC-001',
          name: 'Microcontroller Cortex M4',
          description: 'High-performance embedded controller',
          categoryId: 'cat-elec',
          categoryName: 'Electronics',
          uom: 'PCS',
          weightKg: 0.15,
          unitCost: 14.5,
          status: 'ACTIVE',
        },
        {
          id: 'prod-002',
          sku: 'SKU-ELEC-002',
          name: 'Optocoupler High Speed DIP-8',
          description: 'Optically coupled isolator',
          categoryId: 'cat-elec',
          categoryName: 'Electronics',
          uom: 'PCS',
          weightKg: 0.05,
          unitCost: 3.2,
          status: 'ACTIVE',
        },
        {
          id: 'prod-003',
          sku: 'SKU-MECH-001',
          name: 'Aluminium Extrusion Frame 40x40',
          description: 'Structural framing beam 1m',
          categoryId: 'cat-mech',
          categoryName: 'Mechanical',
          uom: 'BAR',
          weightKg: 1.85,
          unitCost: 28.0,
          status: 'ACTIVE',
        },
        {
          id: 'prod-004',
          sku: 'SKU-PACK-001',
          name: 'Corrugated Heavy Duty Box XL',
          description: 'Double walled shipping carton',
          categoryId: 'cat-pack',
          categoryName: 'Packaging',
          uom: 'BOX',
          weightKg: 0.45,
          unitCost: 2.1,
          status: 'ACTIVE',
        },
      ]);
      setTotalPages(1);
    } finally {
      setIsLoading(false);
    }
  };

  const loadCategories = async () => {
    try {
      const cats = await catalogApi.getCategories();
      setCategories(cats);
    } catch {
      setCategories([
        { id: 'cat-elec', code: 'ELEC', name: 'Electronics' },
        { id: 'cat-mech', code: 'MECH', name: 'Mechanical' },
        { id: 'cat-pack', code: 'PACK', name: 'Packaging' },
      ]);
    }
  };

  useEffect(() => {
    loadCategories();
  }, []);

  useEffect(() => {
    loadData();
  }, [page, debouncedSearch, selectedCategory]);

  const handleCreateProduct = async (e: React.FormEvent) => {
    e.preventDefault();
    setModalSubmitting(true);
    setModalError(null);
    try {
      const created = await catalogApi.createProduct({
        sku: newSku,
        name: newName,
        description: newDesc,
        categoryId: newCategoryId || categories[0]?.id || 'cat-elec',
        uom: newUom,
        unitCost: Number(newCost),
        weightKg: Number(newWeight),
        status: 'ACTIVE',
      });
      setProducts([created, ...products]);
      setShowModal(false);
      setNewSku('');
      setNewName('');
      setNewDesc('');
    } catch (err: any) {
      setModalError(err?.detail || 'Failed to create product.');
    } finally {
      setModalSubmitting(false);
    }
  };

  const columns: Column<Product>[] = [
    { key: 'sku', header: 'SKU', render: (p) => <span className="fw-semibold text-primary">{p.sku}</span> },
    { key: 'name', header: 'Product Name' },
    { key: 'categoryName', header: 'Category', render: (p) => p.categoryName || 'General' },
    { key: 'uom', header: 'UOM' },
    { key: 'unitCost', header: 'Unit Cost', render: (p) => formatCurrency(p.unitCost) },
    { key: 'weightKg', header: 'Weight', render: (p) => `${p.weightKg} kg` },
    { key: 'status', header: 'Status', render: (p) => <StatusBadge status={p.status} /> },
  ];

  return (
    <div>
      <PageHeader
        title="Product Master Catalog"
        subtitle="Manage product master data, classifications and physical characteristics"
        actions={
          <Button variant="primary" size="sm" onClick={() => setShowModal(true)}>
            ➕ Create Product
          </Button>
        }
      />

      <Row className="g-3 mb-3">
        <Col md={5}>
          <Form.Control
            type="text"
            placeholder="Search by SKU or name..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </Col>
        <Col md={4}>
          <Form.Select value={selectedCategory} onChange={(e) => setSelectedCategory(e.target.value)}>
            <option value="">All Categories</option>
            {categories.map((c) => (
              <option key={c.id} value={c.id}>
                {c.name}
              </option>
            ))}
          </Form.Select>
        </Col>
        <Col md={3} className="text-end">
          <Button variant="outline-secondary" size="sm" onClick={loadData}>
            🔄 Refresh
          </Button>
        </Col>
      </Row>

      <DataTable
        columns={columns}
        data={products}
        isLoading={isLoading}
        error={error}
        onRetry={loadData}
        page={page}
        totalPages={totalPages}
        onPageChange={setPage}
      />

      {/* Create Product Modal */}
      <Modal show={showModal} onHide={() => setShowModal(false)} centered>
        <Form onSubmit={handleCreateProduct}>
          <Modal.Header closeButton>
            <Modal.Title className="h5">Create New Product</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            {modalError && <Alert variant="danger">{modalError}</Alert>}
            <Form.Group className="mb-3">
              <Form.Label>SKU Code</Form.Label>
              <Form.Control
                type="text"
                placeholder="e.g. SKU-AUTO-001"
                value={newSku}
                onChange={(e) => setNewSku(e.target.value)}
                required
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Product Name</Form.Label>
              <Form.Control
                type="text"
                placeholder="Product title"
                value={newName}
                onChange={(e) => setNewName(e.target.value)}
                required
              />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Category</Form.Label>
              <Form.Select value={newCategoryId} onChange={(e) => setNewCategoryId(e.target.value)}>
                {categories.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.name}
                  </option>
                ))}
              </Form.Select>
            </Form.Group>
            <Row>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Unit Cost ($)</Form.Label>
                  <Form.Control
                    type="number"
                    step="0.01"
                    value={newCost}
                    onChange={(e) => setNewCost(Number(e.target.value))}
                    required
                  />
                </Form.Group>
              </Col>
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label>Weight (kg)</Form.Label>
                  <Form.Control
                    type="number"
                    step="0.01"
                    value={newWeight}
                    onChange={(e) => setNewWeight(Number(e.target.value))}
                    required
                  />
                </Form.Group>
              </Col>
            </Row>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowModal(false)}>
              Cancel
            </Button>
            <Button variant="primary" type="submit" disabled={modalSubmitting}>
              {modalSubmitting ? 'Creating...' : 'Save Product'}
            </Button>
          </Modal.Footer>
        </Form>
      </Modal>
    </div>
  );
};
