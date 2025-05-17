import React, { useState, useEffect } from 'react';
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
} from 'recharts';
import { DataGrid } from '@mui/x-data-grid';
import { metricsApi } from '../../services/api';

const Metrics = () => {
    const [metrics, setMetrics] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [userId, setUserId] = useState('');
    const [chartData, setChartData] = useState([]);

    const fetchAllMetrics = async () => {
        try {
            const response = await metricsApi.getAllMetrics();
            setMetrics(response.data);
            
            // Process data for chart
            const processedData = response.data.reduce((acc, metric) => {
                const date = new Date(metric.timestamp).toLocaleDateString();
                const existing = acc.find(item => item.date === date);
                
                if (existing) {
                    existing.count += 1;
                } else {
                    acc.push({ date, count: 1 });
                }
                
                return acc;
            }, []);
            
            setChartData(processedData);
            setError(null);
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to fetch metrics');
        } finally {
            setLoading(false);
        }
    };

    const fetchUserMetrics = async () => {
        if (!userId) return;
        
        try {
            const response = await metricsApi.getMetricsByUserId(userId);
            setMetrics(response.data);
            
            // Process data for chart
            const processedData = response.data.reduce((acc, metric) => {
                const date = new Date(metric.timestamp).toLocaleDateString();
                const existing = acc.find(item => item.date === date);
                
                if (existing) {
                    existing.count += 1;
                } else {
                    acc.push({ date, count: 1 });
                }
                
                return acc;
            }, []);
            
            setChartData(processedData);
            setError(null);
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to fetch user metrics');
        }
    };

    useEffect(() => {
        fetchAllMetrics();
    }, []);

    const columns = [
        { field: 'id', headerName: 'ID', width: 90 },
        { field: 'userId', headerName: 'User ID', width: 130 },
        { field: 'metricType', headerName: 'Metric Type', width: 150 },
        { field: 'value', headerName: 'Value', width: 130 },
        {
            field: 'timestamp',
            headerName: 'Timestamp',
            width: 200,
            valueGetter: (params) =>
                new Date(params.row.timestamp).toLocaleString(),
        },
    ];

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
                            </Box>
                        </CardContent>
                    </Card>
                </Grid>

                {/* Metrics Chart */}
                <Grid item xs={12}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Metrics Over Time
                            </Typography>
                            <Box sx={{ height: 300 }}>
                                <ResponsiveContainer width="100%" height="100%">
                                    <LineChart
                                        data={chartData}
                                        margin={{
                                            top: 5,
                                            right: 30,
                                            left: 20,
                                            bottom: 5,
                                        }}
                                    >
                                        <CartesianGrid strokeDasharray="3 3" />
                                        <XAxis dataKey="date" />
                                        <YAxis />
                                        <Tooltip />
                                        <Legend />
                                        <Line
                                            type="monotone"
                                            dataKey="count"
                                            stroke="#8884d8"
                                            activeDot={{ r: 8 }}
                                        />
                                    </LineChart>
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
        </Box>
    );
};

export default Metrics; 