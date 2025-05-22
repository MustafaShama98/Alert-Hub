import React, { useState, useEffect } from 'react';
import {
    Box,
    Typography,
    Button,
    Paper,
    IconButton,
    Tooltip,
    Chip,
    Grid,
    Card,
    CardContent,
    Alert,
    Switch,
    FormControlLabel
} from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import { actionsApi } from '../../services/api';
import { useAuth } from '../../hooks/useAuth';
import { usePermissions } from '../../hooks/usePermissions';
import AddActionModal from './AddActionModal';

const Actions = () => {
    const { user } = useAuth();
    const permissions = usePermissions();
    const [actions, setActions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [isAddModalOpen, setIsAddModalOpen] = useState(false);
    const [selectedAction, setSelectedAction] = useState(null);
    const [showOnlyEnabled, setShowOnlyEnabled] = useState(false);

    const fetchActions = async () => {
        try {
            setLoading(true);
            const response = user?.userId 
                ? await actionsApi.getUserActions(user.userId)
                : await actionsApi.getAllActions();
            setActions(response.data);
            setError(null);
        } catch (err) {
            console.error('Error fetching actions:', err);
            setError(err.response?.data?.message || 'Failed to fetch actions');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchActions();
    }, [user]);

    const handleAddAction = () => {
        setSelectedAction(null);
        setIsAddModalOpen(true);
    };

    const handleEditAction = (action) => {
        setSelectedAction(action);
        setIsAddModalOpen(true);
    };

    const handleDeleteAction = async (id) => {
        try {
            await actionsApi.deleteAction(id);
            fetchActions(); // Refresh the list
        } catch (err) {
            console.error('Error deleting action:', err);
            setError(err.response?.data?.message || 'Failed to delete action');
        }
    };

    const handleActionToggle = async (action) => {
        try {
            await actionsApi.updateAction(action.id, {
                ...action,
                isEnabled: !action.isEnabled
            });
            fetchActions(); // Refresh the list
        } catch (err) {
            console.error('Error toggling action:', err);
            setError(err.response?.data?.message || 'Failed to toggle action');
        }
    };

    const columns = [
        { field: 'id', headerName: 'ID', width: 70 },
        { field: 'name', headerName: 'Name', width: 200 },
        { 
            field: 'actionType', 
            headerName: 'Type', 
            width: 120,
            renderCell: (params) => (
                <Chip 
                    label={params.value} 
                    color={params.value === 'EMAIL' ? 'primary' : 'secondary'}
                />
            )
        },
        { 
            field: 'runOnDay', 
            headerName: 'Run Day', 
            width: 120,
            renderCell: (params) => (
                <Chip 
                    label={params.value} 
                    color={params.value === 'ALL' ? 'warning' : 'default'}
                />
            )
        },
        { 
            field: 'runOnTime', 
            headerName: 'Run Time', 
            width: 120,
            renderCell: (params) => (
                <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                    <AccessTimeIcon fontSize="small" />
                    {params.value}
                </Box>
            )
        },
        { field: 'to', headerName: 'Recipient', width: 200 },
        {
            field: 'isEnabled',
            headerName: 'Status',
            width: 120,
            renderCell: (params) => (
                <Switch
                    checked={params.value}
                    onChange={() => handleActionToggle(params.row)}
                    color="primary"
                />
            )
        },
        {
            field: 'actions',
            headerName: 'Actions',
            width: 120,
            sortable: false,
            renderCell: (params) => (
                <Box>
                    <Tooltip title="Edit action">
                        <IconButton
                            onClick={() => handleEditAction(params.row)}
                            size="small"
                        >
                            <EditIcon />
                        </IconButton>
                    </Tooltip>
                    <Tooltip title="Delete action">
                        <IconButton
                            onClick={() => handleDeleteAction(params.row.id)}
                            size="small"
                            color="error"
                        >
                            <DeleteIcon />
                        </IconButton>
                    </Tooltip>
                </Box>
            )
        }
    ];

    const filteredActions = showOnlyEnabled 
        ? actions.filter(action => action.isEnabled)
        : actions;

    return (
        <Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
                <Typography variant="h4">Action Scheduler</Typography>
                <Button
                    variant="contained"
                    startIcon={<AddIcon />}
                    onClick={handleAddAction}
                >
                    Add New Action
                </Button>
            </Box>

            {error && (
                <Alert severity="error" sx={{ mb: 2 }}>
                    {error}
                </Alert>
            )}

            <Grid container spacing={3}>
                <Grid item xs={12}>
                    <Card>
                        <CardContent>
                            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 2 }}>
                                <Typography variant="h6">Scheduled Actions</Typography>
                                <FormControlLabel
                                    control={
                                        <Switch
                                            checked={showOnlyEnabled}
                                            onChange={(e) => setShowOnlyEnabled(e.target.checked)}
                                        />
                                    }
                                    label="Show only enabled"
                                />
                            </Box>
                            <Paper sx={{ height: 400, width: '100%' }}>
                                <DataGrid
                                    rows={filteredActions}
                                    columns={columns}
                                    pageSize={5}
                                    rowsPerPageOptions={[5]}
                                    checkboxSelection
                                    disableSelectionOnClick
                                    loading={loading}
                                />
                            </Paper>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>

            <AddActionModal
                open={isAddModalOpen}
                onClose={() => setIsAddModalOpen(false)}
                onActionAdded={fetchActions}
                action={selectedAction}
            />
        </Box>
    );
};

export default Actions; 