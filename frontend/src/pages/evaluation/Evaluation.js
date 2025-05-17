import React, { useState, useEffect } from 'react';
import {
    Box,
    Typography,
    Grid,
    Card,
    CardContent,
    Button,
    Alert,
    CircularProgress,
    TextField,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Chip,
    Stack,
} from '@mui/material';
import {
    Timeline,
    TimelineItem,
    TimelineSeparator,
    TimelineConnector,
    TimelineContent,
    TimelineDot,
    LoadingButton,
} from '@mui/lab';
import {
    Assessment as AssessmentIcon,
    CheckCircle as SuccessIcon,
    Error as ErrorIcon,
    Pending as PendingIcon,
    Send as SendIcon,
} from '@mui/icons-material';
import { evaluationApi } from '../../services/api';
import { useAuth } from '../../hooks/useAuth';

const Evaluation = () => {
    const [evaluations, setEvaluations] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [selectedLabel, setSelectedLabel] = useState('bug');
    const [timeRange, setTimeRange] = useState(30);
    const [mostActiveData, setMostActiveData] = useState(null);
    const [aggregateData, setAggregateData] = useState(null);
    const [submitting, setSubmitting] = useState(false);
    const [developerId, setDeveloperId] = useState('');
    const { user } = useAuth();

    const labels = ['bug', 'feature', 'documentation', 'enhancement', 'help wanted'];
    const timeRanges = [
        { value: 7, label: 'Last 7 days' },
        { value: 30, label: 'Last 30 days' },
        { value: 90, label: 'Last 90 days' },
        { value: 180, label: 'Last 180 days' },
    ];

    useEffect(() => {
        fetchMostActiveByLabel();
    }, [selectedLabel, timeRange]);

    const fetchMostActiveByLabel = async () => {
        setLoading(true);
        try {
            const response = await evaluationApi.getMostLabel(selectedLabel, timeRange);
            setMostActiveData(response.data);
            setError('');
        } catch (err) {
            console.error('Error fetching most active data:', err);
            setError('Failed to fetch evaluation data');
        } finally {
            setLoading(false);
        }
    };

    const fetchAggregateData = async (developerId) => {
        try {
            const response = await evaluationApi.getLabelAggregate(developerId, timeRange);
            setAggregateData(response.data);
            setError('');
        } catch (err) {
            console.error('Error fetching aggregate data:', err);
            setError('Failed to fetch aggregate data');
        }
    };

    const handleLabelChange = (event) => {
        setSelectedLabel(event.target.value);
    };

    const handleTimeRangeChange = (event) => {
        setTimeRange(event.target.value);
    };

    const handleDeveloperIdChange = (event) => {
        setDeveloperId(event.target.value);
    };

    const handleSubmitEvaluation = async () => {
        if (!developerId.trim()) {
            setError('Please enter a developer ID');
            return;
        }

        setSubmitting(true);
        setError('');
        setSuccess('');
        
        try {
            await evaluationApi.triggerEvaluation(developerId, {
                label: selectedLabel,
                timeRange: timeRange
            });
            setSuccess(`Evaluation triggered successfully for developer ${developerId} on ${selectedLabel} issues in the last ${timeRange} days`);
            // Refresh the data after successful submission
            await fetchMostActiveByLabel();
        } catch (err) {
            console.error('Error triggering evaluation:', err);
            setError('Failed to trigger evaluation. Please try again.');
        } finally {
            setSubmitting(false);
        }
    };

    const getStatusIcon = (status) => {
        switch (status?.toLowerCase()) {
            case 'completed':
                return <SuccessIcon color="success" />;
            case 'failed':
                return <ErrorIcon color="error" />;
            case 'pending':
                return <PendingIcon color="warning" />;
            default:
                return <AssessmentIcon color="primary" />;
        }
    };

    if (loading && !mostActiveData) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box>
            <Typography variant="h4" gutterBottom>
                Evaluation Dashboard
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
                {/* Submit Evaluation Button */}
                <Grid item xs={12}>
                    <Card>
                        <CardContent>
                            <Box display="flex" justifyContent="space-between" alignItems="center">
                                <Typography variant="h6">
                                    Trigger New Evaluation
                                </Typography>
                                <Box display="flex" alignItems="center" gap={2}>
                                    <TextField
                                        label="Developer ID"
                                        value={developerId}
                                        onChange={handleDeveloperIdChange}
                                        variant="outlined"
                                        size="small"
                                        error={Boolean(error && !developerId.trim())}
                                        helperText={error && !developerId.trim() ? 'Developer ID is required' : ''}
                                        sx={{ minWidth: 200 }}
                                    />

                                    <FormControl sx={{ minWidth: 200 }}>
                                        <InputLabel>Label</InputLabel>
                                        <Select
                                            value={selectedLabel}
                                            onChange={handleLabelChange}
                                            label="Label"
                                            size="small"
                                        >
                                            {labels.map((label) => (
                                                <MenuItem key={label} value={label}>
                                                    {label}
                                                </MenuItem>
                                            ))}
                                        </Select>
                                    </FormControl>

                                    <FormControl sx={{ minWidth: 200 }}>
                                        <InputLabel>Time Range</InputLabel>
                                        <Select
                                            value={timeRange}
                                            onChange={handleTimeRangeChange}
                                            label="Time Range"
                                            size="small"
                                        >
                                            {timeRanges.map((range) => (
                                                <MenuItem key={range.value} value={range.value}>
                                                    {range.label}
                                                </MenuItem>
                                            ))}
                                        </Select>
                                    </FormControl>

                                    <LoadingButton
                                        variant="contained"
                                        color="primary"
                                        onClick={handleSubmitEvaluation}
                                        loading={submitting}
                                        loadingPosition="end"
                                        endIcon={<SendIcon />}
                                        disabled={!developerId.trim()}
                                    >
                                        Submit Evaluation
                                    </LoadingButton>
                                </Box>
                            </Box>
                        </CardContent>
                    </Card>
                </Grid>

                {/* Filters */}
   

                {/* Most Active Developers */}
                <Grid item xs={12} md={6}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Most Active Developers - {selectedLabel}
                            </Typography>
                            {mostActiveData?.developers?.map((dev, index) => (
                                <Box
                                    key={dev.id}
                                    sx={{
                                        mb: 2,
                                        p: 2,
                                        border: '1px solid',
                                        borderColor: 'divider',
                                        borderRadius: 1,
                                        cursor: 'pointer',
                                        '&:hover': {
                                            bgcolor: 'action.hover',
                                        },
                                    }}
                                    onClick={() => fetchAggregateData(dev.id)}
                                >
                                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                        <Typography variant="subtitle1">
                                            {dev.name || dev.email}
                                        </Typography>
                                        <Chip
                                            label={`${dev.count} contributions`}
                                            color="primary"
                                            size="small"
                                        />
                                    </Box>
                                    <Typography variant="body2" color="text.secondary">
                                        Last active: {new Date(dev.lastActive).toLocaleDateString()}
                                    </Typography>
                                </Box>
                            ))}
                        </CardContent>
                    </Card>
                </Grid>

                {/* Developer Details */}
                <Grid item xs={12} md={6}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Developer Statistics
                            </Typography>
                            {aggregateData ? (
                                <Box>
                                    <Typography variant="subtitle1" gutterBottom>
                                        Label Distribution
                                    </Typography>
                                    <Stack direction="row" spacing={1} flexWrap="wrap" gap={1} sx={{ mb: 2 }}>
                                        {Object.entries(aggregateData.labelCounts || {}).map(([label, count]) => (
                                            <Chip
                                                key={label}
                                                label={`${label}: ${count}`}
                                                color="primary"
                                                variant="outlined"
                                            />
                                        ))}
                                    </Stack>

                                    <Typography variant="subtitle1" gutterBottom>
                                        Recent Activity
                                    </Typography>
                                    <Timeline>
                                        {aggregateData.recentActivity?.map((activity, index) => (
                                            <TimelineItem key={index}>
                                                <TimelineSeparator>
                                                    <TimelineDot color="primary">
                                                        {getStatusIcon(activity.status)}
                                                    </TimelineDot>
                                                    {index < aggregateData.recentActivity.length - 1 && <TimelineConnector />}
                                                </TimelineSeparator>
                                                <TimelineContent>
                                                    <Typography variant="subtitle2">
                                                        {activity.type}
                                                    </Typography>
                                                    <Typography variant="body2" color="text.secondary">
                                                        {new Date(activity.timestamp).toLocaleString()}
                                                    </Typography>
                                                </TimelineContent>
                                            </TimelineItem>
                                        ))}
                                    </Timeline>
                                </Box>
                            ) : (
                                <Typography color="text.secondary">
                                    Select a developer to view their statistics
                                </Typography>
                            )}
                        </CardContent>
                    </Card>
                </Grid>

                {/* Task Amount Statistics */}
                <Grid item xs={12}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Task Statistics
                            </Typography>
                            <Grid container spacing={2}>
                                {mostActiveData?.taskStats?.map((stat, index) => (
                                    <Grid item xs={12} sm={6} md={3} key={index}>
                                        <Box
                                            sx={{
                                                p: 2,
                                                border: '1px solid',
                                                borderColor: 'divider',
                                                borderRadius: 1,
                                                textAlign: 'center',
                                            }}
                                        >
                                            <Typography variant="h4" color="primary">
                                                {stat.value}
                                            </Typography>
                                            <Typography variant="body2" color="text.secondary">
                                                {stat.label}
                                            </Typography>
                                        </Box>
                                    </Grid>
                                ))}
                            </Grid>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>
        </Box>
    );
};

export default Evaluation; 