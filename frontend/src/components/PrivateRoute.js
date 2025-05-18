import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

const PrivateRoute = ({ children, requiredPermission }) => {
    const { user, loading, hasPermission } = useAuth();

    if (loading) {
        return <div>Loading...</div>;
    }

    if (!user) {
        return <Navigate to="/login" />;
    }

    if (requiredPermission && !hasPermission(requiredPermission)) {
        return <Navigate to="/" />;
    }

    return children;
};

export default PrivateRoute; 