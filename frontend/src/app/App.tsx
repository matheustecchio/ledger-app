import { Navigate, NavLink, Route, Routes } from "react-router-dom";
import { AccountsPage } from "../features/accounts/AccountsPage";

export function App() {
  return (
    <div className="app-shell">
      <aside className="sidebar">
        <a className="brand" href="/accounts" aria-label="Ledger home">
          <span className="brand-mark" aria-hidden="true">L</span>
          Ledger
        </a>
        <nav aria-label="Main navigation">
          <NavLink to="/accounts">Accounts</NavLink>
        </nav>
        <p className="milestone-label">Architecture milestone 01</p>
      </aside>
      <main>
        <Routes>
          <Route path="/accounts" element={<AccountsPage />} />
          <Route path="*" element={<Navigate to="/accounts" replace />} />
        </Routes>
      </main>
    </div>
  );
}
