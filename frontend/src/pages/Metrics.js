import React, { useState, useEffect } from 'react';
import {
    Box,
    Typography,
    Grid,
    Card,
    CardContent,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Alert,
    CircularProgress,
} from '@mui/material';
import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ResponsiveContainer,
    BarChart,
    Bar,
} from 'recharts';
import { metricsApi, usersApi } from '../services/api';

const Metrics = () => {
    const [metrics, setMetrics] = useState([]);
    const [users, setUsers] = useState([]);
    const [selectedUser, setSelectedUser] = useState('');
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
         fetchUsers();
         fetchAllMetrics();
    }, []);

    const fetchUsers = async () => {
        try {
            const response = await usersApi.getAllUsers();
            setUsers(response.data);
        } catch (err) {
            console.error('Error fetching users:', err);
            setError('Failed to fetch users');
        }
    };

    const fetchAllMetrics = async () => {
        try {
            debugger;
            const response = await metricsApi.getAllMetrics();
            console.log('Fetched metrics222:', response.data);
            setMetrics(response.data);
            setError('');
        } catch (err) {
            console.error('Error fetching metrics:', err);
            setError('Failed to fetch metrics');
        } finally {
            console.log('Fetched metrics222:', response.data);

            setLoading(false);
        }
    };

    const fetchUserMetrics = async (userId) => {
        setLoading(true);
        try {
            const response = await metricsApi.getMetricsByUserId(userId);
            setMetrics(response.data);
            setError('');
            return response.data;
        } catch (err) {
            console.error('Error fetching user metrics:', err);
            setError('Failed to fetch user metrics');
        } finally {
            setLoading(false);
        }
    };

    const handleUserChange = (event) => {
        const userId = event.target.value;
        setSelectedUser(userId);
        if (userId) {
            fetchUserMetrics(userId);
        } else {
            fetchAllMetrics();
        }
    };

    // Process metrics data for charts
    const processMetricsForCharts = () => {
        if (!metrics.length) return { timeSeriesData: [], summaryData: [] };

        const timeSeriesData = metrics.map(metric => ({
            timestamp: new Date(metric.timestamp).toLocaleDateString(),
            taskCount: metric.taskCount || 0,
            completionRate: (metric.completionRate || 0) * 100,
            averageTime: metric.averageCompletionTime || 0,
        }));

        const summaryData = [
            {
                name: 'Tasks',
                completed: metrics.reduce((sum, m) => sum + (m.completedTasks || 0), 0),
                pending: metrics.reduce((sum, m) => sum + (m.pendingTasks || 0), 0),
                total: metrics.reduce((sum, m) => sum + (m.taskCount || 0), 0),
            }
        ];

        return { timeSeriesData, summaryData };
    };

    const { timeSeriesData, summaryData } = processMetricsForCharts();

    if (loading) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
                <Typography variant="h4">Metrics Dashboard</Typography>
                <FormControl sx={{ minWidth: 200 }}>
                    <InputLabel>Filter by User</InputLabel>
                    <Select
                        value={selectedUser}
                        onChange={handleUserChange}
                        label="Filter by User"
                    >
                        <MenuItem value="">
                            <em>All Users</em>
                        </MenuItem>
                        {users.map((user) => (
                            <MenuItem key={user.id} value={user.id}>
                                {user.email}
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>
            </Box>

            {error && (
                <Alert severity="error" sx={{ mb: 2 }}>
                    {error}
                </Alert>
            )}

            <Grid container spacing={3}>
                {/* Summary Cards */}
                <Grid item xs={12} md={4}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Total Tasks
                            </Typography>
                            <Typography variant="h3">
                                {summaryData[0]?.total || 0}
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>
                <Grid item xs={12} md={4}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Completed Tasks
                            </Typography>
                            <Typography variant="h3" color="success.main">
                                {summaryData[0]?.completed || 0}
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>
                <Grid item xs={12} md={4}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Pending Tasks
                            </Typography>
                            <Typography variant="h3" color="warning.main">
                                {summaryData[0]?.pending || 0}
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>

                {/* Time Series Charts */}
                <Grid item xs={12}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Task Completion Rate Over Time
                            </Typography>
                            <ResponsiveContainer width="100%" height={300}>
                                <LineChart data={timeSeriesData}>
                                    <CartesianGrid strokeDasharray="3 3" />
                                    <XAxis dataKey="timestamp" />
                                    <YAxis />
                                    <Tooltip />
                                    <Legend />
                                    <Line
                                        type="monotone"
                                        dataKey="completionRate"
                                        name="Completion Rate (%)"
                                        stroke="#8884d8"
                                    />
                                </LineChart>
                            </ResponsiveContainer>
                        </CardContent>
                    </Card>
                </Grid>

                <Grid item xs={12}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Task Distribution
                            </Typography>
                            <ResponsiveContainer width="100%" height={300}>
                                <BarChart data={timeSeriesData}>
                                    <CartesianGrid strokeDasharray="3 3" />
                                    <XAxis dataKey="timestamp" />
                                    <YAxis />
                                    <Tooltip />
                                    <Legend />
                                    <Bar
                                        dataKey="taskCount"
                                        name="Total Tasks"
                                        fill="#8884d8"
                                    />
                                    <Bar
                                        dataKey="averageTime"
                                        name="Avg. Completion Time (hours)"
                                        fill="#82ca9d"
                                    />
                                </BarChart>
                            </ResponsiveContainer>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>
        </Box>
    );
};

export default Metrics; 