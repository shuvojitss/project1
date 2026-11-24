import { useState, useCallback } from 'react';

export interface PaginationState {
  page: number;
  size: number;
  setPage: (page: number) => void;
  setSize: (size: number) => void;
  nextPage: () => void;
  prevPage: () => void;
}

export function usePagination(initialPage = 0, initialSize = 20): PaginationState {
  const [page, setPage] = useState(initialPage);
  const [size, setSize] = useState(initialSize);

  const nextPage = useCallback(() => setPage((p) => p + 1), []);
  const prevPage = useCallback(() => setPage((p) => Math.max(0, p - 1)), []);

  return {
    page,
    size,
    setPage,
    setSize,
    nextPage,
    prevPage,
  };
}
