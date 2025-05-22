import React, { createContext, useContext, useState, useEffect } from 'react';
import { useAuth } from './useAuth';

// All permissions from backend Permission enum
export const PERMISSIONS = {
    // Action permissions
    CREATE_ACTION: 'createAction',
    UPDATE_ACTION: 'updateAction',
    DELETE_ACTION: 'deleteAction',
    
    // Metric permissions
    CREATE_METRIC: 'createMetric',
    UPDATE_METRIC: 'updateMetric',
    DELETE_METRIC: 'deleteMetric',
    
    // Process permissions
    TRIGGER_SCAN: 'triggerScan',
    TRIGGER_PROCESS: 'triggerProcess',
    TRIGGER_EVALUATION: 'triggerEvaluation',
    
    // Basic permissions
    READ: 'read',
    
    // Admin permissions
    CREATE_USER: 'createUser',
    DELETE_USER: 'deleteUser',
    GRANT_PERMISSION: 'grantPermission',
    REVOKE_PERMISSION: 'revokePermission',
    ADMIN: 'admin'
};

const DEFAULT_PERMISSIONS = {
    // Action permissions
    canCreateAction: false,
    canUpdateAction: false,
    canDeleteAction: false,
    
    // Metric permissions
    canCreateMetric: false,
    canUpdateMetric: false,
    canDeleteMetric: false,
    
    // Process permissions
    canTriggerScan: false,
    canTriggerProcess: false,
    canTriggerEvaluation: false,
    
    // Basic permissions
    canRead: false,
    
    // Admin permissions
    canCreateUser: false,
    canDeleteUser: false,
    canGrantPermission: false,
    canRevokePermission: false,
    isAdmin: false
};

const ALL_PERMISSIONS = {
    // Action permissions
    canCreateAction: true,
    canUpdateAction: true,
    canDeleteAction: true,
    
    // Metric permissions
    canCreateMetric: true,
    canUpdateMetric: true,
    canDeleteMetric: true,
    
    // Process permissions
    canTriggerScan: true,
    canTriggerProcess: true,
    canTriggerEvaluation: true,
    
    // Basic permissions
    canRead: true,
    
    // Admin permissions
    canCreateUser: true,
    canDeleteUser: true,
    canGrantPermission: true,
    canRevokePermission: true,
    isAdmin: true
};

const PermissionsContext = createContext(null);

export const PermissionsProvider = ({ children }) => {
    const { user } = useAuth();
    const [permissions, setPermissions] = useState(DEFAULT_PERMISSIONS);

    useEffect(() => {
        if (user && user.permissions) {
            // Check if user is admin first
            const isAdmin = user.permissions.includes(PERMISSIONS.ADMIN);
            
            if (isAdmin) {
                // If user is admin, grant all permissions
                setPermissions(ALL_PERMISSIONS);
            } else {
                // If not admin, check individual permissions
                setPermissions({
                    // Action permissions
                    canCreateAction: user.permissions.includes(PERMISSIONS.CREATE_ACTION),
                    canUpdateAction: user.permissions.includes(PERMISSIONS.UPDATE_ACTION),
                    canDeleteAction: user.permissions.includes(PERMISSIONS.DELETE_ACTION),
                    
                    // Metric permissions
                    canCreateMetric: user.permissions.includes(PERMISSIONS.CREATE_METRIC),
                    canUpdateMetric: user.permissions.includes(PERMISSIONS.UPDATE_METRIC),
                    canDeleteMetric: user.permissions.includes(PERMISSIONS.DELETE_METRIC),
                    
                    // Process permissions
                    canTriggerScan: user.permissions.includes(PERMISSIONS.TRIGGER_SCAN),
                    canTriggerProcess: user.permissions.includes(PERMISSIONS.TRIGGER_PROCESS),
                    canTriggerEvaluation: user.permissions.includes(PERMISSIONS.TRIGGER_EVALUATION),
                    
                    // Basic permissions
                    canRead: user.permissions.includes(PERMISSIONS.READ),
                    
                    // Admin permissions
                    canCreateUser: user.permissions.includes(PERMISSIONS.CREATE_USER),
                    canDeleteUser: user.permissions.includes(PERMISSIONS.DELETE_USER),
                    canGrantPermission: user.permissions.includes(PERMISSIONS.GRANT_PERMISSION),
                    canRevokePermission: user.permissions.includes(PERMISSIONS.REVOKE_PERMISSION),
                    isAdmin: false
                });
            }
        } else {
            // If no user or no permissions, reset to default
            setPermissions(DEFAULT_PERMISSIONS);
        }
    }, [user]);

    return (
        <PermissionsContext.Provider value={permissions}>
            {children}
        </PermissionsContext.Provider>
    );
};

export const usePermissions = () => {
    const context = useContext(PermissionsContext);
    if (context === null) {
        throw new Error('usePermissions must be used within a PermissionsProvider');
    }
    return context;
}; 