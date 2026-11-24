import { ReactNode } from 'react';
import { Table, Spinner, Alert, Pagination } from 'react-bootstrap';

export interface Column<T> {
  key: string;
  header: string;
  render?: (row: T) => ReactNode;
  width?: string;
}

interface DataTableProps<T> {
  columns: Column<T>[];
  data: T[];
  isLoading?: boolean;
  error?: string | null;
  onRetry?: () => void;
  page?: number;
  totalPages?: number;
  onPageChange?: (page: number) => void;
  emptyMessage?: string;
}

export function DataTable<T extends Record<string, any>>({
  columns,
  data,
  isLoading,
  error,
  onRetry,
  page = 0,
  totalPages = 1,
  onPageChange,
  emptyMessage = 'No records found.',
}: DataTableProps<T>) {
  if (isLoading) {
    return (
      <div className="text-center py-5">
        <Spinner animation="border" variant="primary" role="status">
          <span className="visually-hidden">Loading data...</span>
        </Spinner>
        <p className="text-muted small mt-2">Loading data, please wait...</p>
      </div>
    );
  }

  if (error) {
    return (
      <Alert variant="danger" className="my-3">
        <div className="d-flex justify-content-between align-items-center">
          <div>{error}</div>
          {onRetry && (
            <button className="btn btn-sm btn-outline-danger" onClick={onRetry}>
              Retry
            </button>
          )}
        </div>
      </Alert>
    );
  }

  if (!data || data.length === 0) {
    return (
      <div className="text-center py-5 bg-white border rounded">
        <p className="text-muted mb-0">{emptyMessage}</p>
      </div>
    );
  }

  return (
    <div className="table-responsive bg-white rounded shadow-sm border">
      <Table hover className="mb-0 align-middle">
        <thead className="table-light">
          <tr>
            {columns.map((c) => (
              <th key={c.key} style={c.width ? { width: c.width } : undefined}>
                {c.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {data.map((row, idx) => (
            <tr key={row.id || idx}>
              {columns.map((c) => (
                <td key={c.key}>{c.render ? c.render(row) : (row as any)[c.key]}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </Table>

      {totalPages > 1 && onPageChange && (
        <div className="d-flex justify-content-between align-items-center p-3 border-top">
          <span className="small text-muted">
            Page {page + 1} of {totalPages}
          </span>
          <Pagination className="mb-0 pagination-sm">
            <Pagination.Prev disabled={page === 0} onClick={() => onPageChange(page - 1)} />
            <Pagination.Item active>{page + 1}</Pagination.Item>
            <Pagination.Next disabled={page >= totalPages - 1} onClick={() => onPageChange(page + 1)} />
          </Pagination>
        </div>
      )}
    </div>
  );
}
