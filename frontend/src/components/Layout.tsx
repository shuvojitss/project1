import React from 'react';
import { Outlet } from 'react-router-dom';
import { Navbar } from './Navbar';
import { Sidebar } from './Sidebar';

export const Layout: React.FC = () => {
  return (
    <div className="min-vh-100 d-flex flex-column">
      <Navbar />
      <div className="d-flex flex-grow-1">
        <Sidebar />
        <main className="main-content flex-grow-1">
          <Outlet />
        </main>
      </div>
    </div>
  );
};
