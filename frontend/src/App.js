import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './hooks/useAuth';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import Layout from './components/Layout';
import PrivateRoute from './components/PrivateRoute';
import Admin from './pages/admin/Admin';
import UserManagement from './pages/admin/UserManagement';
import Metrics from './pages/metrics/Metrics';
import Evaluation from './pages/evaluation/Evaluation';
import Actions from './pages/actions/Actions';
import { PermissionsProvider } from './hooks/usePermissions';
import LoginPage from "./pages/LoginPage";
import SignupPage from "./pages/SignupPage";
import NotFoundPage from './pages/errors/NotFoundPage';
import UnauthorizedPage from './pages/errors/UnauthorizedPage';
import UnauthenticatedPage from './pages/errors/UnauthenticatedPage';
import Dashboard from './pages/Dashboard';

function App() {
  return (
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <AuthProvider>
        <PermissionsProvider>
          <Router>
            <Routes>
              {/* Public Routes */}
              <Route path="/login" element={<LoginPage />} />
              <Route path="/signup" element={<SignupPage />} />
              <Route path="/unauthorized" element={<UnauthorizedPage />} />
              <Route path="/unauthenticated" element={<UnauthenticatedPage />} />
              
              {/* Protected Routes */}
              <Route
                path="/"
                element={
                  <PrivateRoute>
                    <Layout />
                  </PrivateRoute>
                }
              >
                <Route index element={<Dashboard />} />
                
                <Route
                  path="evaluation"
                  element={
                    <PrivateRoute requiredPermission="triggerEvaluation">
                      <Evaluation />
                    </PrivateRoute>
                  }
                />
                
                <Route path="admin">
                  <Route
                    index
                    element={
                      <PrivateRoute requiredPermission="admin">
                        <Admin />
                      </PrivateRoute>
                    }
                  />
                  <Route
                    path="users"
                    element={
                      <PrivateRoute requiredPermission="admin">
                        <UserManagement />
                      </PrivateRoute>
                    }
                  />
                </Route>
                
                <Route
                  path="metrics"
                  element={
                    <PrivateRoute>
                      <Metrics />
                    </PrivateRoute>
                  }
                />

                <Route
                  path="actions"
                  element={
                    <PrivateRoute>
                      <Actions />
                    </PrivateRoute>
                  }
                />
              </Route>

              {/* 404 Route */}
              <Route path="*" element={<NotFoundPage />} />
            </Routes>
          </Router>
        </PermissionsProvider>
      </AuthProvider>
    </LocalizationProvider>
  );
}

export default App;