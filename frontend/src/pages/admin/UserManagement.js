import React, { useState, useEffect } from 'react';
import {
    Box,
    Typography,
    Button,
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    Alert,
    Paper,
} from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import { adminApi } from '../../services/api';

const UserManagement = () => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [openDialog, setOpenDialog] = useState(false);
    const [selectedUser, setSelectedUser] = useState(null);
    const [formData, setFormData] = useState({
        email: '',
        firstName: '',
        lastName: '',
        password: '',
    });

    useEffect(() => {
        fetchUsers();
    }, []);

    const fetchUsers = async () => {
        try {
            const response = await adminApi.getAllUsers();
            setUsers(response.data);
            setError('');
        } catch (err) {
            setError('Failed to fetch users');
            console.error('Error fetching users:', err);
        } finally {
            setLoading(false);
        }
    };

    const handleOpenDialog = (user = null) => {
        if (user) {
            setSelectedUser(user);
            setFormData({
                email: user.email,
                firstName: user.firstName,
                lastName: user.lastName,
                password: '',
            });
        } else {
            setSelectedUser(null);
            setFormData({
                email: '',
                firstName: '',
                lastName: '',
                password: '',
            });
        }
        setOpenDialog(true);
    };

    const handleCloseDialog = () => {
        setOpenDialog(false);
        setSelectedUser(null);
        setFormData({
            email: '',
            firstName: '',
            lastName: '',
            password: '',
        });
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (selectedUser) {
                await adminApi.updateUser(selectedUser.id, formData);
                setSuccess('User updated successfully');
            } else {
                await adminApi.createUser(formData);
                setSuccess('User created successfully');
            }
            handleCloseDialog();
            fetchUsers();
            setError('');
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to save user');
        }
    };

    const handleDeleteUser = async (userId) => {
        try {
            await adminApi.deleteUser(userId);
            setSuccess('User deleted successfully');
            fetchUsers();
            setError('');
        } catch (err) {
            setError('Failed to delete user');
            console.error('Error deleting user:', err);
        }
    };

    const columns = [
        { field: 'email', headerName: 'Email', flex: 1 },
        { field: 'firstName', headerName: 'First Name', flex: 1 },
        { field: 'lastName', headerName: 'Last Name', flex: 1 },
        {
            field: 'actions',
            headerName: 'Actions',
            flex: 1,
            renderCell: (params) => (
                <Box>
                    <Button
                        onClick={() => handleOpenDialog(params.row)}
                        variant="outlined"
                        size="small"
                        sx={{ mr: 1 }}
                    >
                        Edit
                    </Button>
                    <Button
                        onClick={() => handleDeleteUser(params.row.id)}
                        variant="outlined"
                        color="error"
                        size="small"
                    >
                        Delete
                    </Button>
                </Box>
            ),
        },
    ];

    return (
        <Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
                <Typography variant="h4">User Management</Typography>
                <Button
                    variant="contained"
                    onClick={() => handleOpenDialog()}
                >
                    Add New User
                </Button>
            </Box>

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

            <Paper sx={{ height: 400, width: '100%' }}>
                <DataGrid
                    rows={users}
                    columns={columns}
                    pageSize={5}
                    rowsPerPageOptions={[5]}
                    disableSelectionOnClick
                    loading={loading}
                />
            </Paper>

            <Dialog open={openDialog} onClose={handleCloseDialog}>
                <DialogTitle>
                    {selectedUser ? 'Edit User' : 'Add New User'}
                </DialogTitle>
                <DialogContent>
                    <Box component="form" onSubmit={handleSubmit} sx={{ mt: 2 }}>
                        <TextField
                            fullWidth
                            margin="normal"
                            label="Email"
                            name="email"
                            value={formData.email}
                            onChange={handleInputChange}
                            required
                        />
                        <TextField
                            fullWidth
                            margin="normal"
                            label="First Name"
                            name="firstName"
                            value={formData.firstName}
                            onChange={handleInputChange}
                            required
                        />
                        <TextField
                            fullWidth
                            margin="normal"
                            label="Last Name"
                            name="lastName"
                            value={formData.lastName}
                            onChange={handleInputChange}
                            required
                        />
                        <TextField
                            fullWidth
                            margin="normal"
                            label="Password"
                            name="password"
                            type="password"
                            value={formData.password}
                            onChange={handleInputChange}
                            required={!selectedUser}
                            helperText={selectedUser ? "Leave blank to keep current password" : ""}
                        />
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleCloseDialog}>Cancel</Button>
                    <Button onClick={handleSubmit} variant="contained">
                        {selectedUser ? 'Update' : 'Create'}
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default UserManagement; 