import React from 'react';
import Sidebar from '../components/Sidebar';
import Topbar from '../components/Topbar';

export default function DashboardLayout({ children }) {
  return (
    <div className="layout-container">
      <Sidebar />
      <div className="layout-content">
        <Topbar />
        <main>{children}</main>
      </div>
    </div>
  );
}
