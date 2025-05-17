import React, { useState, useEffect } from 'react';
import {
    Box,
    Typography,
    Paper,
    Alert,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Chip,
    OutlinedInput,
    Checkbox,
    ListItemText,
    Button,
    Grid,
    Card,
    CardContent,
    Stack,
    CardActions,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { PeopleAlt as PeopleIcon } from '@mui/icons-material';
import { adminApi, usersApi } from '../../services/api';

const ITEM_HEIGHT = 48;
const ITEM_PADDING_TOP = 8;
const MenuProps = {
    PaperProps: {
        style: {
            maxHeight: ITEM_HEIGHT * 4.5 + ITEM_PADDING_TOP,
            width: 250,
        },
    },
};

const Admin = () => {
    const navigate = useNavigate();
    const [users, setUsers] = useState([]);
    const [selectedUser, setSelectedUser] = useState('');
    const [availablePermissions, setAvailablePermissions] = useState([]);
    const [userPermissions, setUserPermissions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    useEffect(() => {
        fetchUsers();
        fetchAvailablePermissions();
    }, []);

    const fetchUsers = async () => {
        try {
            const response = await adminApi.getAllUsers();
            setUsers(response.data);
        } catch (err) {
            setError('Failed to fetch users');
            console.error('Error fetching users:', err);
        }
    };

    const fetchAvailablePermissions = async () => {
        try {
            const response = await adminApi.getAllAvailablePermissions();
            setAvailablePermissions(response.data);
        } catch (err) {
            setError('Failed to fetch available permissions');
            console.error('Error fetching permissions:', err);
        } finally {
            setLoading(false);
        }
    };

    const fetchUserPermissions = async (userId) => {
        try {
            const response = await adminApi.getUserPermissions(userId);
            setUserPermissions(response.data);
        } catch (err) {
            setError('Failed to fetch user permissions');
            console.error('Error fetching user permissions:', err);
        }
    };

    const handleUserChange = (event) => {
        const userId = event.target.value;
        setSelectedUser(userId);
        if (userId) {
            fetchUserPermissions(userId);
        } else {
            setUserPermissions([]);
        }
    };

    const handleGrantPermission = async (permission) => {
        try {
            await adminApi.grantPermissions(selectedUser, [permission]);
            setUserPermissions([...userPermissions, permission]);
            setSuccess(`Successfully granted ${permission} permission`);
            setError('');
        } catch (err) {
            setError('Failed to grant permission');
            console.error('Error granting permission:', err);
        }
    };

    const handleRevokePermission = async (permission) => {
        try {
            await adminApi.revokePermissions(selectedUser, [permission]);
            setUserPermissions(userPermissions.filter(p => p !== permission));
            setSuccess(`Successfully revoked ${permission} permission`);
            setError('');
        } catch (err) {
            setError('Failed to revoke permission');
            console.error('Error revoking permission:', err);
        }
    };

    if (loading) {
        return <Typography>Loading...</Typography>;
    }

    return (
        <Box>
            <Typography variant="h4" gutterBottom>
                Admin Dashboard
            </Typography>

            {error && (
                <Alert severity="error" sx={{ mb: 2 }}>
                    {error}
                </Alert>
            )}

            {success && (
                <Alert severity="success" sx={{ mb: 2 }}>
                    {success}
                </Alert>
            )}

            <Grid container spacing={3}>
                {/* Quick Actions */}
                <Grid item xs={12}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Quick Actions
                            </Typography>
                            <Grid container spacing={2}>
                                <Grid item xs={12} md={4}>
                                    <Card variant="outlined">
                                        <CardContent>
                                            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                                                <PeopleIcon sx={{ fontSize: 40, mr: 2, color: 'primary.main' }} />
                                                <Typography variant="h6">
                                                    User Management
                                                </Typography>
                                            </Box>
                                            <Typography variant="body2" color="text.secondary">
                                                Create, edit, or delete users. Manage user accounts and their information.
                                            </Typography>
                                        </CardContent>
                                        <CardActions>
                                            <Button 
                                                fullWidth 
                                                variant="contained"
                                                onClick={() => navigate('/admin/users')}
                                            >
                                                Manage Users
                                            </Button>
                                        </CardActions>
                                    </Card>
                                </Grid>
                            </Grid>
                        </CardContent>
                    </Card>
                </Grid>

                {/* Permissions Management */}
                <Grid item xs={12}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                User Permissions Management
                            </Typography>
                            
                            <FormControl fullWidth sx={{ mb: 2 }}>
                                <InputLabel>Select User</InputLabel>
                                <Select
                                    value={selectedUser}
                                    onChange={handleUserChange}
                                    label="Select User"
                                >
                                    <MenuItem value="">
                                        <em>None</em>
                                    </MenuItem>
                                    {users.map((user) => (
                                        <MenuItem key={user.id} value={user.id}>
                                            {user.email} ({user.firstName} {user.lastName})
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>

                            {selectedUser && (
                                <>
                                    <Typography variant="subtitle1" gutterBottom>
                                        Current Permissions
                                    </Typography>
                                    <Box sx={{ mb: 2 }}>
                                        {userPermissions.length > 0 ? (
                                            <Stack direction="row" spacing={1} flexWrap="wrap" gap={1}>
                                                {userPermissions.map((permission) => (
                                                    <Chip
                                                        key={permission}
                                                        label={permission}
                                                        onDelete={() => handleRevokePermission(permission)}
                                                        color="primary"
                                                    />
                                                ))}
                                            </Stack>
                                        ) : (
                                            <Typography color="text.secondary">
                                                No permissions assigned
                                            </Typography>
                                        )}
                                    </Box>

                                    <Typography variant="subtitle1" gutterBottom>
                                        Available Permissions
                                    </Typography>
                                    <Stack direction="row" spacing={1} flexWrap="wrap" gap={1}>
                                        {availablePermissions
                                            .filter(permission => !userPermissions.includes(permission))
                                            .map((permission) => (
                                                <Button
                                                    key={permission}
                                                    variant="outlined"
                                                    size="small"
                                                    onClick={() => handleGrantPermission(permission)}
                                                >
                                                    + {permission}
                                                </Button>
                                            ))}
                                    </Stack>
                                </>
                            )}
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>
        </Box>
    );
};

export default Admin; 