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
    Paper,
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
    Timeline as TimelineIcon,
} from '@mui/icons-material';
import {
    PieChart,
    Pie,
    Cell,
    ResponsiveContainer,
    Tooltip as RechartsTooltip,
    Legend,
    BarChart,
    Bar,
    XAxis,
    YAxis,
    CartesianGrid,
} from 'recharts';
import { evaluationApi } from '../../services/api';
import { useAuth } from '../../hooks/useAuth';

const LABEL_COLORS = {
    bug: '#ff6b6b',
    feature: '#48dbfb',
    documentation: '#1dd1a1',
    enhancement: '#54a0ff',
    'help wanted': '#5f27cd',
};

const Evaluation = () => {
    const [evaluations, setEvaluations] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [selectedLabel, setSelectedLabel] = useState('bug');
    const [timeRange, setTimeRange] = useState(30);
    const [mostActiveData, setMostActiveData] = useState(null);
    const [aggregateData, setAggregateData] = useState(null);
    const [taskAmountData, setTaskAmountData] = useState(null);
    const [submitting, setSubmitting] = useState(false);
    const [developerId, setDeveloperId] = useState('');
    const [apiResponses, setApiResponses] = useState({});
    const { user } = useAuth();

    const labels = ['bug', 'feature', 'documentation', 'enhancement', 'help wanted'];
    const timeRanges = [
        { value: 7, label: 'Last 7 days' },
        { value: 30, label: 'Last 30 days' },
        { value: 90, label: 'Last 90 days' },
        { value: 180, label: 'Last 180 days' },
    ];

    const logApiResponse = (endpoint, response) => {
        setApiResponses(prev => ({
            ...prev,
            [endpoint]: {
                timestamp: new Date().toLocaleString(),
                data: response
            }
        }));
    };

    useEffect(() => {
        fetchUserEvaluations();
    }, []);

    const fetchUserEvaluations = async () => {
        setLoading(true);
        try {
            const response = await evaluationApi.getUserEvaluations(user.userId);
            setEvaluations(response.data);
            setError('');
        } catch (err) {
            console.error('Error fetching user evaluations:', err);
            setError('Failed to fetch your evaluations');
        } finally {
            setLoading(false);
        }
    };

    const fetchMostActiveByLabel = async () => {
        setLoading(true);
        try {
            const response = await evaluationApi.getMostLabel(selectedLabel, timeRange);
            console.log('Most active by label response:', response.data);
            logApiResponse('getMostLabel', response.data);
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
            const response = await evaluationApi.triggerEvaluation(developerId, {
                label: selectedLabel,
                timeRange: timeRange
            });
            logApiResponse('triggerEvaluation', response.data);
            setSuccess(`Evaluation triggered successfully for developer ${developerId}`);
            await fetchMostActiveByLabel();
        } catch (err) {
            console.error('Error triggering evaluation:', err);
            setError('Failed to trigger evaluation. Please try again.');
        } finally {
            setSubmitting(false);
        }
    };

    const handleFetchMostLabel = async () => {
        await fetchMostActiveByLabel();
        setSuccess(`Successfully fetched most active developers for ${selectedLabel} label`);
    };

    const handleFetchLabelAggregate = async (devId) => {
        setError('');
        try {
            const response = await evaluationApi.getLabelAggregate(devId || developerId, timeRange);
            logApiResponse('getLabelAggregate', response.data);
            setAggregateData(response.data);
            setSuccess(`Successfully fetched label aggregates for developer ${devId || developerId}`);
        } catch (err) {
            console.error('Error fetching aggregate data:', err);
            setError('Failed to fetch label aggregates');
        }
    };

    const handleFetchTaskAmount = async (devId) => {
        setError('');
        try {
            const response = await evaluationApi.getTaskAmount(devId || developerId, timeRange);
            logApiResponse('getTaskAmount', response.data);
            setTaskAmountData(response.data);
            setSuccess(`Successfully fetched task amount for developer ${devId || developerId}`);
        } catch (err) {
            console.error('Error fetching task amount:', err);
            setError('Failed to fetch task amount');
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

    const RADIAN = Math.PI / 180;
    const renderCustomizedLabel = ({ cx, cy, midAngle, innerRadius, outerRadius, percent }) => {
        const radius = innerRadius + (outerRadius - innerRadius) * 0.5;
        const x = cx + radius * Math.cos(-midAngle * RADIAN);
        const y = cy + radius * Math.sin(-midAngle * RADIAN);

        return (
            <text x={x} y={y} fill="white" textAnchor={x > cx ? 'start' : 'end'} dominantBaseline="central">
                {`${(percent * 100).toFixed(0)}%`}
            </text>
        );
    };

    const renderMostLabelResponse = (data) => {
        // Calculate percentages and prepare data for visualization
        const totalTasks = data.count;
        const labelPercentage = (data.count / totalTasks) * 100;

        return (
            <Card sx={{ mb: 3, borderRadius: 2, boxShadow: 3 }}>
                <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                        <AssessmentIcon color="primary" sx={{ mr: 1 }} />
                        <Typography variant="h6">Most Active by Label: {data.label}</Typography>
                    </Box>
                    <Grid container spacing={3}>
                        <Grid item xs={12} md={4}>
                            <Paper sx={{ p: 3, bgcolor: 'background.default', borderRadius: 2 }}>
                                <Box sx={{ textAlign: 'center' }}>
                                    <Typography variant="h5" gutterBottom>{data.developerName}</Typography>
                                    <Box sx={{ position: 'relative', display: 'inline-flex' }}>
                                        <CircularProgress
                                            variant="determinate"
                                            value={100}
                                            size={120}
                                            thickness={4}
                                            sx={{ color: LABEL_COLORS[data.label] || '#8884d8' }}
                                        />
                                        <Box
                                            sx={{
                                                top: 0,
                                                left: 0,
                                                bottom: 0,
                                                right: 0,
                                                position: 'absolute',
                                                display: 'flex',
                                                alignItems: 'center',
                                                justifyContent: 'center',
                                            }}
                                        >
                                            <Typography variant="h4" component="div" color="text.secondary">
                                                {data.count}
                                            </Typography>
                                        </Box>
                                    </Box>
                                    <Typography variant="subtitle1" color="text.secondary" sx={{ mt: 2 }}>
                                        Tasks with {data.label} label
                                    </Typography>
                                </Box>
                            </Paper>
                        </Grid>
                        <Grid item xs={12} md={8}>
                            <Stack spacing={2}>
                                <Paper sx={{ p: 2, bgcolor: 'background.default', borderRadius: 2 }}>
                                    <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 1 }}>
                                        <Typography variant="subtitle1">Developer</Typography>
                                        <Typography variant="h6">{data.developerName}</Typography>
                                    </Box>
                                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                        <Chip 
                                            label={data.developerId} 
                                            size="small" 
                                            variant="outlined" 
                                        />
                                    </Box>
                                </Paper>
                                <Paper sx={{ p: 2, bgcolor: 'background.default', borderRadius: 2 }}>
                                    <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 1 }}>
                                        <Typography variant="subtitle1">Label</Typography>
                                        <Chip 
                                            label={data.label}
                                            sx={{ 
                                                bgcolor: LABEL_COLORS[data.label] || '#8884d8',
                                                color: 'white'
                                            }}
                                        />
                                    </Box>
                                    <Box sx={{ mt: 1 }}>
                                        <Typography variant="body2" color="text.secondary">
                                            Most active label in the last {data.timeFrameDays} days
                                        </Typography>
                                    </Box>
                                </Paper>
                                <Paper 
                                    sx={{ 
                                        p: 2, 
                                        bgcolor: 'background.default', 
                                        borderRadius: 2,
                                        background: `linear-gradient(90deg, ${LABEL_COLORS[data.label] || '#8884d8'}33 ${labelPercentage}%, transparent ${labelPercentage}%)`
                                    }}
                                >
                                    <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 1 }}>
                                        <Typography variant="subtitle1">Task Count</Typography>
                                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                            <Typography variant="h6">{data.count}</Typography>
                                            <Typography variant="body2" color="text.secondary">tasks</Typography>
                                        </Box>
                                    </Box>
                                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                        <TimelineIcon fontSize="small" color="action" />
                                        <Typography variant="body2" color="text.secondary">
                                            In the last {data.timeFrameDays} days
                                        </Typography>
                                    </Box>
                                </Paper>
                            </Stack>
                        </Grid>
                    </Grid>
                </CardContent>
            </Card>
        );
    };

    const renderLabelAggregateResponse = (data) => {
        const chartData = data.map(item => ({
            name: item.label,
            count: item.label_counts,
            color: LABEL_COLORS[item.label] || '#8884d8'
        }));

        return (
            <Card sx={{ mb: 3, borderRadius: 2, boxShadow: 3 }}>
                <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                        <TimelineIcon color="primary" sx={{ mr: 1 }} />
                        <Typography variant="h6">Label Distribution</Typography>
                    </Box>
                    <Grid container spacing={3}>
                        <Grid item xs={12} md={8}>
                            <Paper sx={{ p: 3, bgcolor: 'background.default', borderRadius: 2 }}>
                                <ResponsiveContainer width="100%" height={300}>
                                    <BarChart data={chartData}>
                                        <CartesianGrid strokeDasharray="3 3" />
                                        <XAxis dataKey="name" />
                                        <YAxis />
                                        <RechartsTooltip />
                                        <Legend />
                                        <Bar dataKey="count" name="Tasks" fill="#8884d8">
                                            {chartData.map((entry, index) => (
                                                <Cell key={`cell-${index}`} fill={entry.color} />
                                            ))}
                                        </Bar>
                                    </BarChart>
                                </ResponsiveContainer>
                            </Paper>
                        </Grid>
                        <Grid item xs={12} md={4}>
                            <Stack spacing={2}>
                                {data.map((item, index) => (
                                    <Paper key={index} sx={{ p: 2, bgcolor: 'background.default', borderRadius: 2 }}>
                                        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                                            <Chip 
                                                label={item.label} 
                                                sx={{ 
                                                    bgcolor: LABEL_COLORS[item.label] || '#8884d8',
                                                    color: 'white'
                                                }}
                                            />
                                            <Typography variant="h6">{item.label_counts}</Typography>
                                        </Box>
                                    </Paper>
                                ))}
                            </Stack>
                        </Grid>
                    </Grid>
                </CardContent>
            </Card>
        );
    };

    const renderTaskAmountResponse = (data) => {
        const completionRate = (data.completedTasks / data.taskCount) * 100;

        return (
            <Card sx={{ mb: 3, borderRadius: 2, boxShadow: 3 }}>
                <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                        <AssessmentIcon color="primary" sx={{ mr: 1 }} />
                        <Typography variant="h6">Task Summary</Typography>
                    </Box>
                    <Grid container spacing={3}>
                        <Grid item xs={12} md={6}>
                            <Paper sx={{ p: 3, bgcolor: 'background.default', borderRadius: 2 }}>
                                <Box sx={{ textAlign: 'center' }}>
                                    <Typography variant="h5" gutterBottom>{data.developerName}</Typography>
                                    <Box sx={{ position: 'relative', display: 'inline-flex' }}>
                                        <CircularProgress
                                            variant="determinate"
                                            value={completionRate}
                                            size={120}
                                            thickness={4}
                                            sx={{
                                                color: completionRate > 75 ? 'success.main' : 
                                                       completionRate > 50 ? 'warning.main' : 
                                                       'error.main'
                                            }}
                                        />
                                        <Box
                                            sx={{
                                                top: 0,
                                                left: 0,
                                                bottom: 0,
                                                right: 0,
                                                position: 'absolute',
                                                display: 'flex',
                                                alignItems: 'center',
                                                justifyContent: 'center',
                                            }}
                                        >
                                            <Typography variant="h6" component="div" color="text.secondary">
                                                {`${completionRate.toFixed(0)}%`}
                                            </Typography>
                                        </Box>
                                    </Box>
                                    <Typography variant="subtitle1" color="text.secondary" sx={{ mt: 2 }}>
                                        Completion Rate
                                    </Typography>
                                </Box>
                            </Paper>
                        </Grid>
                        <Grid item xs={12} md={6}>
                            <Stack spacing={2}>
                                <Paper sx={{ p: 2, bgcolor: 'background.default', borderRadius: 2 }}>
                                    <Typography variant="subtitle2" color="text.secondary">Total Tasks</Typography>
                                    <Typography variant="h4">{data.taskCount}</Typography>
                                </Paper>
                                <Paper sx={{ p: 2, bgcolor: 'background.default', borderRadius: 2 }}>
                                    <Typography variant="subtitle2" color="text.secondary">Completed Tasks</Typography>
                                    <Typography variant="h4" color="success.main">{data.completedTasks}</Typography>
                                </Paper>
                                <Paper sx={{ p: 2, bgcolor: 'background.default', borderRadius: 2 }}>
                                    <Typography variant="subtitle2" color="text.secondary">Time Frame</Typography>
                                    <Typography variant="h4">{data.timeFrameDays} days</Typography>
                                </Paper>
                            </Stack>
                        </Grid>
                    </Grid>
                </CardContent>
            </Card>
        );
    };

    const renderResults = () => {
        return (
            <Grid container spacing={3} sx={{ mt: 2 }}>
                {/* Evaluation Results */}
                {success && (
                    <Grid item xs={12}>
                        <Card>
                            <CardContent>
                                <Typography variant="h6" color="primary" gutterBottom>
                                    Latest Evaluation Results
                                </Typography>
                                <Box sx={{ p: 2, bgcolor: 'background.default', borderRadius: 1 }}>
                                    <Typography variant="body1" component="pre" sx={{ whiteSpace: 'pre-wrap' }}>
                                        {success}
                                    </Typography>
                                </Box>
                            </CardContent>
                        </Card>
                    </Grid>
                )}

                {/* Most Active Developers Results */}
                {mostActiveData && (
                    <Grid item xs={12} md={6}>
                        <Card>
                            <CardContent>
                                <Typography variant="h6" color="primary" gutterBottom>
                                    Most Active Developers - {selectedLabel}
                                </Typography>
                                {mostActiveData.developers?.map((dev, index) => (
                                    <Box
                                        key={dev.id}
                                        sx={{
                                            mb: 2,
                                            p: 2,
                                            border: '1px solid',
                                            borderColor: 'divider',
                                            borderRadius: 1,
                                        }}
                                    >
                                        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                            <Box>
                                                <Typography variant="subtitle1">
                                                    {dev.name || dev.email}
                                                </Typography>
                                                <Typography variant="body2" color="text.secondary">
                                                    Last active: {new Date(dev.lastActive).toLocaleDateString()}
                                                </Typography>
                                            </Box>
                                            <Box>
                                                <Chip
                                                    label={`${dev.count} contributions`}
                                                    color="primary"
                                                    size="small"
                                                    sx={{ mr: 1 }}
                                                />
                                                <Button
                                                    size="small"
                                                    onClick={() => {
                                                        handleFetchLabelAggregate(dev.id);
                                                        handleFetchTaskAmount(dev.id);
                                                    }}
                                                >
                                                    View Details
                                                </Button>
                                            </Box>
                                        </Box>
                                    </Box>
                                ))}
                            </CardContent>
                        </Card>
                    </Grid>
                )}

                {/* Label Aggregates Results */}
                {aggregateData && (
                    <Grid item xs={12} md={6}>
                        <Card>
                            <CardContent>
                                <Typography variant="h6" color="primary" gutterBottom>
                                    Label Distribution for Developer {developerId}
                                </Typography>
                                <Box sx={{ p: 2, bgcolor: 'background.default', borderRadius: 1 }}>
                                    {aggregateData.map((item, index) => (
                                        <Typography key={index} variant="body1" gutterBottom>
                                            {item.label}: {item.count} tasks
                                        </Typography>
                                    ))}
                                </Box>
                            </CardContent>
                        </Card>
                    </Grid>
                )}

                {/* Task Amount Results */}
                {taskAmountData && (
                    <Grid item xs={12} md={6}>
                        <Card>
                            <CardContent>
                                <Typography variant="h6" color="primary" gutterBottom>
                                    Task Statistics for Developer {developerId}
                                </Typography>
                                <Box sx={{ p: 2, bgcolor: 'background.default', borderRadius: 1 }}>
                                    <Typography variant="body1" gutterBottom>
                                        Total Tasks: {taskAmountData.totalTasks}
                                    </Typography>
                                    <Typography variant="body1" gutterBottom>
                                        Completed Tasks: {taskAmountData.completedTasks}
                                    </Typography>
                                    <Typography variant="body1" gutterBottom>
                                        Completion Rate: {((taskAmountData.completedTasks / taskAmountData.totalTasks) * 100).toFixed(1)}%
                                    </Typography>
                                </Box>
                            </CardContent>
                        </Card>
                    </Grid>
                )}
            </Grid>
        );
    };

    const renderApiResponses = () => {
        return (
            <Box sx={{ mt: 4 }}>
                <Typography variant="h5" gutterBottom sx={{ mb: 3 }}>
                    API Response Details
                </Typography>
                {Object.entries(apiResponses).map(([endpoint, { timestamp, data }]) => {
                    let responseComponent;
                    switch (endpoint) {
                        case 'getMostLabel':
                            responseComponent = renderMostLabelResponse(data);
                            break;
                        case 'getLabelAggregate':
                            responseComponent = renderLabelAggregateResponse(data);
                            break;
                        case 'getTaskAmount':
                            responseComponent = renderTaskAmountResponse(data);
                            break;
                        default:
                            responseComponent = (
                                <Card sx={{ mb: 3 }}>
                                    <CardContent>
                                        <Typography variant="h6">{endpoint}</Typography>
                                        <pre>{JSON.stringify(data, null, 2)}</pre>
                                    </CardContent>
                                </Card>
                            );
                    }
                    return (
                        <Box key={endpoint}>
                            <Typography variant="caption" color="text.secondary" sx={{ ml: 1 }}>
                                {timestamp}
                            </Typography>
                            {responseComponent}
                        </Box>
                    );
                })}
            </Box>
        );
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
                {/* Manual Evaluation Trigger */}
                <Grid item xs={12}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6" gutterBottom>
                                Manual Evaluation Trigger
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
                            </Box>
                            <Box sx={{ mt: 2, display: 'flex', gap: 2 }}>
                                <LoadingButton
                                    variant="contained"
                                    color="primary"
                                    onClick={handleSubmitEvaluation}
                                    loading={submitting}
                                    loadingPosition="end"
                                    endIcon={<SendIcon />}
                                    disabled={!developerId.trim()}
                                >
                                    Trigger Evaluation
                                </LoadingButton>
                                <Button
                                    variant="outlined"
                                    onClick={handleFetchMostLabel}
                                    startIcon={<AssessmentIcon />}
                                >
                                    Fetch Most Active by Label
                                </Button>
                                <Button
                                    variant="outlined"
                                    onClick={() => handleFetchLabelAggregate()}
                                    startIcon={<TimelineIcon />}
                                    disabled={!developerId.trim()}
                                >
                                    Fetch Label Aggregates
                                </Button>
                                <Button
                                    variant="outlined"
                                    onClick={() => handleFetchTaskAmount()}
                                    startIcon={<AssessmentIcon />}
                                    disabled={!developerId.trim()}
                                >
                                    Fetch Task Amount
                                </Button>
                            </Box>
                        </CardContent>
                    </Card>
                </Grid>

                {/* Add the results section */}
                <Grid item xs={12}>
                    {renderResults()}
                </Grid>

                {/* API Responses Log */}
                <Grid item xs={12}>
                    {renderApiResponses()}
                </Grid>
            </Grid>

            {loading && (
                <Box display="flex" justifyContent="center" alignItems="center" mt={4}>
                    <CircularProgress />
                </Box>
            )}
        </Box>
    );
};

export default Evaluation; 