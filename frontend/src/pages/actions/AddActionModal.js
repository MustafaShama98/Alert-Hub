import React, { useState, useEffect } from 'react';
import {
    TextField,
    Button,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Grid,
    Alert,
    Box,
    Switch,
    FormControlLabel,
    FormHelperText,
    Chip,
    IconButton,
    Typography,
    Paper
} from '@mui/material';
import { TimePicker } from '@mui/x-date-pickers/TimePicker';
import Modal from '../../components/common/Modal';
import { actionsApi, metricsApi } from '../../services/api';
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import { useAuth } from '../../hooks/useAuth';

const ACTION_TYPES = ['EMAIL', 'SMS'];
const RUN_DAYS = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'ALL'];

const AddActionModal = ({ open, onClose, onActionAdded, action }) => {
    const { user } = useAuth();
    const [formData, setFormData] = useState({
        name: '',
        actionType: '',
        runOnDay: '',
        runOnTime: null,
        to: '',
        message: '',
        isEnabled: true,
        condition: [[]] // Default empty condition
    });
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);
    const [metrics, setMetrics] = useState([]);
    const [selectedMetric, setSelectedMetric] = useState('');
    const [selectedConditionGroup, setSelectedConditionGroup] = useState(0);

    useEffect(() => {
        if (action) {
            setFormData({
                ...action,
                runOnTime: new Date(action.runOnTime)
            });
        } else {
            setFormData({
                name: '',
                actionType: '',
                runOnDay: '',
                runOnTime: null,
                to: '',
                message: '',
                isEnabled: true,
                condition: [[]]
            });
        }
        fetchUserMetrics();
    }, [action]);

    const fetchUserMetrics = async () => {
        try {
            const response = await metricsApi.getMetricsByUserId(user.userId);
            setMetrics(response.data);
        } catch (err) {
            console.error('Error fetching metrics:', err);
            setError('Failed to fetch metrics');
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleTimeChange = (time) => {
        setFormData(prev => ({
            ...prev,
            runOnTime: time
        }));
    };

    const handleAddMetricToCondition = () => {
        if (!selectedMetric) return;

        setFormData(prev => {
            const newCondition = [...prev.condition];
            newCondition[selectedConditionGroup] = [
                ...(newCondition[selectedConditionGroup] || []),
                parseInt(selectedMetric)
            ];
            return { ...prev, condition: newCondition };
        });
        setSelectedMetric('');
    };

    const handleAddConditionGroup = () => {
        setFormData(prev => ({
            ...prev,
            condition: [...prev.condition, []]
        }));
        setSelectedConditionGroup(formData.condition.length);
    };

    const handleRemoveMetric = (groupIndex, metricIndex) => {
        setFormData(prev => {
            const newCondition = [...prev.condition];
            newCondition[groupIndex] = newCondition[groupIndex].filter((_, idx) => idx !== metricIndex);
            return { ...prev, condition: newCondition };
        });
    };

    const handleRemoveGroup = (groupIndex) => {
        setFormData(prev => ({
            ...prev,
            condition: prev.condition.filter((_, idx) => idx !== groupIndex)
        }));
        if (selectedConditionGroup === groupIndex) {
            setSelectedConditionGroup(Math.max(0, groupIndex - 1));
        }
    };

    const handleSubmit = async () => {
        try {
            setLoading(true);
            setError(null);

            // Validate form
            if (!formData.name || !formData.actionType || !formData.runOnDay || !formData.runOnTime || !formData.to || !formData.message) {
                throw new Error('Please fill in all required fields');
            }

            // Format time for backend
            const formattedTime = formData.runOnTime.toLocaleTimeString('en-US', {
                hour12: false,
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit'
            });

            const actionData = {
                ...formData,
                runOnTime: formattedTime,
                condition: formData.condition.filter(group => group.length > 0) // Remove empty groups
            };

            if (action?.id) {
                await actionsApi.updateAction(action.id, actionData);
            } else {
                await actionsApi.createAction(actionData);
            }

            onActionAdded();
            onClose();
        } catch (err) {
            setError(err.response?.data?.message || err.message);
        } finally {
            setLoading(false);
        }
    };

    const getMetricNameById = (id) => {
        const metric = metrics.find(m => m.id === id);
        return metric ? metric.name : `Metric ${id}`;
    };

    const modalActions = (
        <>
            <Button onClick={onClose} disabled={loading}>
                Cancel
            </Button>
            <Button 
                onClick={handleSubmit}
                variant="contained"
                color="primary"
                disabled={loading}
            >
                {loading ? 'Saving...' : action ? 'Update Action' : 'Create Action'}
            </Button>
        </>
    );

    return (
        <Modal
            open={open}
            onClose={onClose}
            title={action ? 'Edit Action' : 'Add New Action'}
            actions={modalActions}
        >
            <Grid container spacing={2}>
                {error && (
                    <Grid item xs={12}>
                        <Alert severity="error">{error}</Alert>
                    </Grid>
                )}
                <Grid item xs={12}>
                    <TextField
                        name="name"
                        label="Action Name"
                        value={formData.name}
                        onChange={handleChange}
                        fullWidth
                        required
                        disabled={loading}
                    />
                </Grid>
                <Grid item xs={12} sm={6}>
                    <FormControl fullWidth required>
                        <InputLabel>Action Type</InputLabel>
                        <Select
                            name="actionType"
                            value={formData.actionType}
                            onChange={handleChange}
                            label="Action Type"
                            disabled={loading}
                        >
                            {ACTION_TYPES.map(type => (
                                <MenuItem key={type} value={type}>
                                    {type}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                </Grid>
                <Grid item xs={12} sm={6}>
                    <FormControl fullWidth required>
                        <InputLabel>Run Day</InputLabel>
                        <Select
                            name="runOnDay"
                            value={formData.runOnDay}
                            onChange={handleChange}
                            label="Run Day"
                            disabled={loading}
                        >
                            {RUN_DAYS.map(day => (
                                <MenuItem key={day} value={day}>
                                    {day}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                </Grid>
                <Grid item xs={12} sm={6}>
                    <TimePicker
                        label="Run Time"
                        value={formData.runOnTime}
                        onChange={handleTimeChange}
                        slotProps={{ textField: { fullWidth: true, required: true } }}
                        disabled={loading}
                    />
                </Grid>
                <Grid item xs={12} sm={6}>
                    <TextField
                        name="to"
                        label={formData.actionType === 'EMAIL' ? 'Email Address' : 'Phone Number'}
                        value={formData.to}
                        onChange={handleChange}
                        fullWidth
                        required
                        disabled={loading}
                        type={formData.actionType === 'EMAIL' ? 'email' : 'tel'}
                    />
                </Grid>

                {/* Metric Conditions Section */}
                <Grid item xs={12}>
                    <Paper sx={{ p: 2, mb: 2 }}>
                        <Typography variant="h6" gutterBottom>
                            Metric Conditions
                        </Typography>
                        <Typography variant="body2" color="text.secondary" gutterBottom>
                            Add metrics to create condition groups. Metrics within a group use OR logic, while different groups use AND logic.
                        </Typography>
                        
                        <Box sx={{ mb: 2 }}>
                            <FormControl fullWidth sx={{ mb: 1 }}>
                                <InputLabel>Select Metric</InputLabel>
                                <Select
                                    value={selectedMetric}
                                    onChange={(e) => setSelectedMetric(e.target.value)}
                                    label="Select Metric"
                                    disabled={loading}
                                >
                                    <MenuItem value="">
                                        <em>None</em>
                                    </MenuItem>
                                    {metrics.map(metric => (
                                        <MenuItem key={metric.id} value={metric.id}>
                                            {metric.name} ({metric.label})
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                            
                            <Box sx={{ display: 'flex', gap: 1 }}>
                                <Button
                                    variant="outlined"
                                    onClick={handleAddMetricToCondition}
                                    disabled={!selectedMetric}
                                    startIcon={<AddIcon />}
                                >
                                    Add to Group {selectedConditionGroup + 1}
                                </Button>
                                <Button
                                    variant="outlined"
                                    onClick={handleAddConditionGroup}
                                    startIcon={<AddIcon />}
                                >
                                    New Group
                                </Button>
                            </Box>
                        </Box>

                        {formData.condition.map((group, groupIndex) => (
                            <Paper 
                                key={groupIndex}
                                variant="outlined"
                                sx={{ 
                                    p: 1,
                                    mb: 1,
                                    bgcolor: selectedConditionGroup === groupIndex ? 'action.selected' : 'background.paper'
                                }}
                                onClick={() => setSelectedConditionGroup(groupIndex)}
                            >
                                <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                                    <Typography variant="subtitle2">
                                        Group {groupIndex + 1}
                                    </Typography>
                                    <IconButton 
                                        size="small"
                                        onClick={() => handleRemoveGroup(groupIndex)}
                                        sx={{ ml: 'auto' }}
                                    >
                                        <DeleteIcon />
                                    </IconButton>
                                </Box>
                                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1 }}>
                                    {group.map((metricId, metricIndex) => (
                                        <Chip
                                            key={metricIndex}
                                            label={getMetricNameById(metricId)}
                                            onDelete={() => handleRemoveMetric(groupIndex, metricIndex)}
                                            color="primary"
                                            variant="outlined"
                                        />
                                    ))}
                                    {group.length === 0 && (
                                        <Typography variant="body2" color="text.secondary">
                                            No metrics in this group
                                        </Typography>
                                    )}
                                </Box>
                            </Paper>
                        ))}
                    </Paper>
                </Grid>

                <Grid item xs={12}>
                    <TextField
                        name="message"
                        label="Message"
                        value={formData.message}
                        onChange={handleChange}
                        fullWidth
                        required
                        disabled={loading}
                        multiline
                        rows={4}
                    />
                </Grid>
                <Grid item xs={12}>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                        <FormControlLabel
                            control={
                                <Switch
                                    checked={formData.isEnabled}
                                    onChange={(e) => handleChange({
                                        target: {
                                            name: 'isEnabled',
                                            value: e.target.checked
                                        }
                                    })}
                                    disabled={loading}
                                />
                            }
                            label="Enable Action"
                        />
                        <FormHelperText>
                            {formData.isEnabled 
                                ? 'Action will run on schedule' 
                                : 'Action is disabled and will not run'}
                        </FormHelperText>
                    </Box>
                </Grid>
            </Grid>
        </Modal>
    );
};

export default AddActionModal; 