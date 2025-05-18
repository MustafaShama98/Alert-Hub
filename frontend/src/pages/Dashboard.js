import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
    Grid,
    Card,
    CardContent,
    Typography,
    Button,
    Box,
    CircularProgress,
} from '@mui/material';
import {
    Assessment as AssessmentIcon,
    People as PeopleIcon,
    AdminPanelSettings as AdminIcon,
    Timeline as TimelineIcon,
} from '@mui/icons-material';
import { useAuth } from '../context/AuthContext';
import { evaluationApi, usersApi, metricsApi } from '../services/api';

const DashboardCard = ({ title, icon, description, count, loading, error, onClick }) => (
    <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
        <CardContent sx={{ flexGrow: 1 }}>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                {icon}
                <Typography variant="h6" component="div" sx={{ ml: 1 }}>
                    {title}
                </Typography>
            </Box>
            <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
                {description}
            </Typography>
            {loading ? (
                <CircularProgress size={20} />
            ) : error ? (
                <Typography color="error">{error}</Typography>
            ) : (
                <Typography variant="h4" component="div">
                    {count}
                </Typography>
            )}
        </CardContent>
        <Box sx={{ p: 2, pt: 0 }}>
            <Button
                variant="outlined"
                fullWidth
                onClick={onClick}
            >
                View Details
            </Button>
        </Box>
    </Card>
);

const Dashboard = () => {
    const navigate = useNavigate();
    const { hasPermission } = useAuth();
    const [stats, setStats] = useState({
        evaluations: { loading: true, count: 0, error: null },
        users: { loading: true, count: 0, error: null },
        metrics: { loading: true, count: 0, error: null },
    });

    useEffect(() => {
        const fetchStats = async () => {
            try {
                // Fetch evaluation stats
                if (hasPermission('triggerEvaluation')) {
                    const evalResponse = await evaluationApi.getMostLabel('bug');
                    setStats(prev => ({
                        ...prev,
                        evaluations: {
                            loading: false,
                            count: evalResponse.data.count || 0,
                            error: null
                        }
                    }));
                }

                // Fetch users stats
                if (hasPermission('updateAction')) {
                    const usersResponse = await usersApi.getAllUsers();
                    setStats(prev => ({
                        ...prev,
                        users: {
                            loading: false,
                            count: usersResponse.data.length,
                            error: null
                        }
                    }));
                }

                // Fetch metrics stats
                const metricsResponse = await metricsApi.getAllMetrics();
                setStats(prev => ({
                    ...prev,
                    metrics: {
                        loading: false,
                        count: metricsResponse.data.length,
                        error: null
                    }
                }));

            } catch (error) {
                console.error('Error fetching stats:', error);
                setStats(prev => ({
                    evaluations: { loading: false, count: 0, error: 'Failed to load' },
                    users: { loading: false, count: 0, error: 'Failed to load' },
                    metrics: { loading: false, count: 0, error: 'Failed to load' },
                }));
            }
        };

        fetchStats();
    }, [hasPermission]);

    return (
        <Box>
            <Typography variant="h4" gutterBottom>
                Dashboard
            </Typography>
            <Grid container spacing={3}>
                {hasPermission('triggerEvaluation') && (
                    <Grid item xs={12} sm={6} md={4}>
                        <DashboardCard
                            title="Evaluations"
                            icon={<AssessmentIcon color="primary" />}
                            description="View and manage developer evaluations"
                            count={stats.evaluations.count}
                            loading={stats.evaluations.loading}
                            error={stats.evaluations.error}
                            onClick={() => navigate('/evaluation')}
                        />
                    </Grid>
                )}

                {hasPermission('updateAction') && (
                    <Grid item xs={12} sm={6} md={4}>
                        <DashboardCard
                            title="Users"
                            icon={<PeopleIcon color="primary" />}
                            description="Manage system users"
                            count={stats.users.count}
                            loading={stats.users.loading}
                            error={stats.users.error}
                            onClick={() => navigate('/users')}
                        />
                    </Grid>
                )}

                {hasPermission('admin') && (
                    <Grid item xs={12} sm={6} md={4}>
                        <DashboardCard
                            title="Admin"
                            icon={<AdminIcon color="primary" />}
                            description="Administrative tools and settings"
                            count="Admin"
                            onClick={() => navigate('/admin')}
                        />
                    </Grid>
                )}

                <Grid item xs={12} sm={6} md={4}>
                    <DashboardCard
                        title="Metrics"
                        icon={<TimelineIcon color="primary" />}
                        description="View system metrics and analytics"
                        count={stats.metrics.count}
                        loading={stats.metrics.loading}
                        error={stats.metrics.error}
                        onClick={() => navigate('/metrics')}
                    />
                </Grid>
            </Grid>
        </Box>
    );
};

export default Dashboard; 