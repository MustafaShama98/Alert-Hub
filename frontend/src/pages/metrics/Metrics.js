import React, { useState, useEffect, useCallback } from 'react';
import {
    Box,
    Typography,
    Grid,
    Card,
    CardContent,
    Alert,
    TextField,
    Button,
    Paper,
    Chip,
    IconButton,
    Tooltip,
    Snackbar,
} from '@mui/material';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import {
    BarChart,
    Bar,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip as RechartsTooltip,
    Legend,
    ResponsiveContainer,
    PieChart,
    Pie,
    Cell,
} from 'recharts';
import { DataGrid } from '@mui/x-data-grid';
import { metricsApi } from '../../services/api';
import { useAuth } from '../../hooks/useAuth';
import { usePermissions, PERMISSIONS } from '../../hooks/usePermissions';
import AddMetricModal from './AddMetricModal';

// Label colors for charts
const LABEL_COLORS = {
    BUG: '#ff6b6b',
    DOCUMENTATION: '#48dbfb',
    DUPLICATE: '#feca57',
    ENHANCEMENT: '#1dd1a1',
    GOOD_FIRST_ISSUE: '#54a0ff',
    HELP_WANTED: '#5f27cd',
    INVALID: '#ff9f43',
    QUESTION: '#00d2d3',
    WONTFIX: '#8395a7',
};

// Get all available labels from the backend
const AVAILABLE_LABELS = [
    'BUG',
    'DOCUMENTATION',
    'DUPLICATE',
    'ENHANCEMENT',
    'GOOD_FIRST_ISSUE',
    'HELP_WANTED',
    'INVALID',
    'QUESTION',
    'WONTFIX'
];

const Metrics = () => {
    const { user } = useAuth();
    const permissions = usePermissions();
    const [metrics, setMetrics] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [userId, setUserId] = useState('');
    const [labelDistribution, setLabelDistribution] = useState([]);
    const [thresholdData, setThresholdData] = useState([]);
    const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'info' });
    const [isAddModalOpen, setIsAddModalOpen] = useState(false);

    const fetchAllMetrics = async () => {
        try {
            const response = await metricsApi.getAllMetrics();
            const metricsData = response.data;
            setMetrics(metricsData);

            // Process data for label distribution chart
            const labelCounts = AVAILABLE_LABELS.reduce((acc, label) => {
                acc[label] = 0;
                return acc;
            }, {});

            metricsData.forEach(metric => {
                if (metric.label && labelCounts.hasOwnProperty(metric.label)) {
                    labelCounts[metric.label]++;
                }
            });

            const labelChartData = Object.keys(labelCounts).map(label => ({
                name: label,
                value: labelCounts[label],
                color: LABEL_COLORS[label] || '#000000',
            }));

            setLabelDistribution(labelChartData);

            // Process data for threshold chart
            const thresholdChartData = metricsData.map(metric => ({
                name: metric.name,
                threshold: metric.threshold,
                timeFrame: metric.timeFrameHours,
                label: metric.label,
            }));

            setThresholdData(thresholdChartData);
            setError(null);
        } catch (err) {
            console.error('Error fetching metrics:', err);
            setError(err.response?.data?.message || 'Failed to fetch metrics');
        } finally {
            setLoading(false);
        }
    };

    const fetchUserMetrics = async () => {
        if (!userId) return;

        try {
            const response = await metricsApi.getMetricsByUserId(userId);
            const metricsData = response.data;
            setMetrics(metricsData);

            // Process data for label distribution chart
            const labelCounts = AVAILABLE_LABELS.reduce((acc, label) => {
                acc[label] = 0;
                return acc;
            }, {});

            metricsData.forEach(metric => {
                if (metric.label && labelCounts.hasOwnProperty(metric.label)) {
                    labelCounts[metric.label]++;
                }
            });

            const labelChartData = Object.keys(labelCounts).map(label => ({
                name: label,
                value: labelCounts[label],
                color: LABEL_COLORS[label] || '#000000',
            })).filter(item => item.value > 0);

            setLabelDistribution(labelChartData);

            // Process data for threshold chart
            const thresholdChartData = metricsData.map(metric => ({
                name: metric.name,
                threshold: metric.threshold,
                timeFrame: metric.timeFrameHours,
                label: metric.label,
            }));

            setThresholdData(thresholdChartData);
            setError(null);
        } catch (err) {
            console.error('Error fetching user metrics:', err);
            setError(err.response?.data?.message || 'Failed to fetch user metrics');
        }
    };

    useEffect(() => {
        if (user && user.userId) {
            setUserId(user.userId);
            fetchUserMetrics();
        } else {
            fetchAllMetrics();
        }
    }, [user]);

    const handleEdit = async (metricId) => {
        try {
            // Call your edit API endpoint here
            // For now, we'll just show a message
            setSnackbar({
                open: true,
                message: 'Edit functionality coming soon!',
                severity: 'info'
            });
        } catch (error) {
            if (error.response?.status === 403) {
                setSnackbar({
                    open: true,
                    message: 'You do not have permission to edit metrics',
                    severity: 'error'
                });
            } else {
                setSnackbar({
                    open: true,
                    message: 'Failed to edit metric',
                    severity: 'error'
                });
            }
        }
    };

    const handleDelete = async (metricId) => {
        try {
            await metricsApi.deleteMetric(metricId);
            setSnackbar({
                open: true,
                message: 'Metric deleted successfully',
                severity: 'success'
            });
            // Refresh the metrics list
            if (userId) {
                fetchUserMetrics();
            } else {
                fetchAllMetrics();
            }
        } catch (error) {
            if (error.response?.status === 403) {
                setSnackbar({
                    open: true,
                    message: 'You do not have permission to delete metrics',
                    severity: 'error'
                });
            } else {
                setSnackbar({
                    open: true,
                    message: 'Failed to delete metric',
                    severity: 'error'
                });
            }
        }
    };

    const handleCloseSnackbar = () => {
        setSnackbar(prev => ({ ...prev, open: false }));
    };

    const handleAddMetric = () => {
        setIsAddModalOpen(true);
    };

    const handleMetricAdded = () => {
        // Refresh the metrics list
        if (userId) {
            fetchUserMetrics();
        } else {
            fetchAllMetrics();
        }
    };

    const columns = [
        { field: 'id', headerName: 'ID', width: 70 },
        { field: 'userId', headerName: 'User ID', width: 130 },
        { field: 'name', headerName: 'Name', width: 180 },
        { 
            field: 'label', 
            headerName: 'Label', 
            width: 150,
            renderCell: (params) => (
                <Chip 
                    label={params.value} 
                    style={{ 
                        backgroundColor: LABEL_COLORS[params.value] || '#000000',
                        color: '#ffffff'
                    }}
                />
            ),
        },
        { field: 'threshold', headerName: 'Threshold', width: 130, type: 'number' },
        { field: 'timeFrameHours', headerName: 'Time Frame (hours)', width: 180, type: 'number' },
        {
            field: 'actions',
            headerName: 'Actions',
            width: 120,
            sortable: false,
            renderCell: (params) => (
                <Box>
                    <Tooltip title={permissions.canUpdateMetric ? 'Edit metric' : 'You do not have permission to edit'}>
                        <span>
                            <IconButton
                                onClick={() => handleEdit(params.row.id)}
                                size="small"
                                disabled={!permissions.canUpdateMetric}
                            >
                                <EditIcon />
                            </IconButton>
                        </span>
                    </Tooltip>
                    <Tooltip title={permissions.canDeleteMetric ? 'Delete metric' : 'You do not have permission to delete'}>
                        <span>
                            <IconButton
                                onClick={() => handleDelete(params.row.id)}
                                size="small"
                                disabled={!permissions.canDeleteMetric}
                            >
                                <DeleteIcon />
                            </IconButton>
                        </span>
                    </Tooltip>
                </Box>
            ),
        },
    ];

    const RADIAN = Math.PI / 180;
    const renderCustomizedLabel = ({ cx, cy, midAngle, innerRadius, outerRadius, percent, index }) => {
        const radius = innerRadius + (outerRadius - innerRadius) * 0.5;
        const x = cx + radius * Math.cos(-midAngle * RADIAN);
        const y = cy + radius * Math.sin(-midAngle * RADIAN);

        return (
            <text x={x} y={y} fill="white" textAnchor={x > cx ? 'start' : 'end'} dominantBaseline="central">
                {`${(percent * 100).toFixed(0)}%`}
            </text>
        );
    };

    return (
        <Box>
            <Typography variant="h4" gutterBottom>
                Metrics Dashboard
            </Typography>

            {error && (
                <Alert severity="error" sx={{ mb: 2 }}>
                    {error}
                </Alert>
            )}

            <Grid container spacing={3}>
                {/* Search Controls */}
                <Grid item xs={12}>
                    <Card>
                        <CardContent>
                            <Box sx={{ display: 'flex', gap: 2 }}>
                                <TextField
                                    label="User ID"
                                    value={userId}
                                    onChange={(e) => setUserId(e.target.value)}
                                    sx={{ flexGrow: 1 }}
                                />
                                <Button
                                    variant="contained"
                                    onClick={fetchUserMetrics}
                                    disabled={!userId}
                                >
                                    Search
                                </Button>
                                <Button
                                    variant="outlined"
                                    onClick={() => {
                                        setUserId('');
                                        fetchAllMetrics();
                                    }}
                                >
                                    Show All
                                </Button>
                                {permissions.canCreateMetric && (
                                    <Button
                                        variant="contained"
                                        color="success"
                                        onClick={handleAddMetric}
                                    >
                                        Add New Metric
                                    </Button>
                                )}
                            </Box>
                        </CardContent>
                    </Card>
                </Grid>

                {/* Label Distribution Chart */}
                <Grid item xs={12} md={6}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Label Distribution
                            </Typography>
                            <Box sx={{ height: 300 }}>
                                <ResponsiveContainer width="100%" height="100%">
                                    <PieChart>
                                        <Pie
                                            data={labelDistribution}
                                            cx="50%"
                                            cy="50%"
                                            labelLine={false}
                                            label={renderCustomizedLabel}
                                            outerRadius={80}
                                            fill="#8884d8"
                                            dataKey="value"
                                        >
                                            {labelDistribution.map((entry, index) => (
                                                <Cell key={`cell-${index}`} fill={entry.color} />
                                            ))}
                                        </Pie>
                                        <RechartsTooltip />
                                        <Legend />
                                    </PieChart>
                                </ResponsiveContainer>
                            </Box>
                        </CardContent>
                    </Card>
                </Grid>

                {/* Threshold Chart */}
                <Grid item xs={12} md={6}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Thresholds by Metric
                            </Typography>
                            <Box sx={{ height: 300 }}>
                                <ResponsiveContainer width="100%" height="100%">
                                    <BarChart
                                        data={thresholdData}
                                        margin={{
                                            top: 5,
                                            right: 30,
                                            left: 20,
                                            bottom: 5,
                                        }}
                                    >
                                        <CartesianGrid strokeDasharray="3 3" />
                                        <XAxis dataKey="name" />
                                        <YAxis />
                                        <RechartsTooltip />
                                        <Legend />
                                        <Bar dataKey="threshold" name="Threshold" fill="#8884d8" />
                                        <Bar dataKey="timeFrame" name="Time Frame (hours)" fill="#82ca9d" />
                                    </BarChart>
                                </ResponsiveContainer>
                            </Box>
                        </CardContent>
                    </Card>
                </Grid>

                {/* Metrics Table */}
                <Grid item xs={12}>
                    <Paper sx={{ height: 400, width: '100%' }}>
                        <DataGrid
                            rows={metrics}
                            columns={columns}
                            pageSize={5}
                            rowsPerPageOptions={[5]}
                            checkboxSelection
                            disableSelectionOnClick
                            loading={loading}
                        />
                    </Paper>
                </Grid>
            </Grid>

            <Snackbar
                open={snackbar.open}
                autoHideDuration={6000}
                onClose={handleCloseSnackbar}
                message={snackbar.message}
                severity={snackbar.severity}
            />

            {/* Add Metric Modal */}
            <AddMetricModal
                open={isAddModalOpen}
                onClose={() => setIsAddModalOpen(false)}
                onMetricAdded={handleMetricAdded}
            />
        </Box>
    );
};

export default Metrics; 