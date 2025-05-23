import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

const PrivateRoute = ({ children, requiredPermission }) => {
    const { user, loading, hasPermission } = useAuth();
    const location = useLocation();

    if (loading) {
        return <div>Loading...</div>;
    }

    // Not authenticated
    if (!user) {
        return <Navigate to="/unauthenticated" state={{ from: location }} replace />;
    }

    // Check for required permission
    if (requiredPermission && !hasPermission(requiredPermission)) {
        return <Navigate to="/unauthorized" state={{ from: location }} replace />;
    }

    return children;
};

export default PrivateRoute; 