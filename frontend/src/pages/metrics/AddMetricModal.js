import React, { useState } from 'react';
import {
    TextField,
    Button,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Grid,
    Alert
} from '@mui/material';
import Modal from '../../components/common/Modal';
import { metricsApi } from '../../services/api';

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

const AddMetricModal = ({ open, onClose, onMetricAdded }) => {
    const [formData, setFormData] = useState({
        label: '',
        threshold: '',
        timeFrameHours: '',
        name: ''
    });
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleSubmit = async () => {
        try {
            setLoading(true);
            setError(null);

            // Validate form
            if (!formData.label || !formData.threshold || !formData.timeFrameHours || !formData.name) {
                throw new Error('Please fill in all fields');
            }

            // Convert numeric fields
            const metricData = {
                ...formData,
                threshold: parseInt(formData.threshold),
                timeFrameHours: parseInt(formData.timeFrameHours)
            };

            // Validate numeric fields
            if (isNaN(metricData.threshold) || metricData.threshold < 0) {
                throw new Error('Threshold must be a positive number');
            }
            if (isNaN(metricData.timeFrameHours) || metricData.timeFrameHours < 1) {
                throw new Error('Time frame must be at least 1 hour');
            }

            // Call API to create metric
            await metricsApi.createMetric(metricData);
            
            // Clear form and close modal
            setFormData({
                label: '',
                threshold: '',
                timeFrameHours: '',
                name: ''
            });
            onMetricAdded();
            onClose();
        } catch (err) {
            setError(err.response?.data?.message || err.message);
        } finally {
            setLoading(false);
        }
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
                {loading ? 'Creating...' : 'Create Metric'}
            </Button>
        </>
    );

    return (
        <Modal
            open={open}
            onClose={onClose}
            title="Add New Metric"
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
                        label="Metric Name"
                        value={formData.name}
                        onChange={handleChange}
                        fullWidth
                        required
                        disabled={loading}
                    />
                </Grid>
                <Grid item xs={12}>
                    <FormControl fullWidth required>
                        <InputLabel>Label</InputLabel>
                        <Select
                            name="label"
                            value={formData.label}
                            onChange={handleChange}
                            label="Label"
                            disabled={loading}
                        >
                            {AVAILABLE_LABELS.map(label => (
                                <MenuItem key={label} value={label}>
                                    {label}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                </Grid>
                <Grid item xs={12} sm={6}>
                    <TextField
                        name="threshold"
                        label="Threshold"
                        type="number"
                        value={formData.threshold}
                        onChange={handleChange}
                        fullWidth
                        required
                        disabled={loading}
                        inputProps={{ min: 0 }}
                        helperText="Minimum value is 0"
                    />
                </Grid>
                <Grid item xs={12} sm={6}>
                    <TextField
                        name="timeFrameHours"
                        label="Time Frame (hours)"
                        type="number"
                        value={formData.timeFrameHours}
                        onChange={handleChange}
                        fullWidth
                        required
                        disabled={loading}
                        inputProps={{ min: 1 }}
                        helperText="Minimum 1 hour"
                    />
                </Grid>
            </Grid>
        </Modal>
    );
};

export default AddMetricModal; 