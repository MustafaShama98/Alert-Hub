import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './hooks/useAuth';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import Login from './pages/auth/Login';
import Layout from './components/Layout';
import PrivateRoute from './components/PrivateRoute';
import Admin from './pages/admin/Admin';
import UserManagement from './pages/admin/UserManagement';
import Metrics from './pages/metrics/Metrics';
import Evaluation from './pages/evaluation/Evaluation';
import Actions from './pages/actions/Actions';
import { PermissionsProvider } from './hooks/usePermissions';

// Lazy load other components
const Dashboard = () => <div>Dashboard Page</div>;

function App() {
  return (
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <AuthProvider>
        <PermissionsProvider>
          <Router>
            <Routes>
              <Route path="/login" element={<Login />} />
              
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

              {/* Catch all route - redirect to home */}
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </Router>
        </PermissionsProvider>
      </AuthProvider>
    </LocalizationProvider>
  );
}

export default App;