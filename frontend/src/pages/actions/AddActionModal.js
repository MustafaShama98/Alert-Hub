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
    FormHelperText
} from '@mui/material';
import { TimePicker } from '@mui/x-date-pickers/TimePicker';
import Modal from '../../components/common/Modal';
import { actionsApi } from '../../services/api';

const ACTION_TYPES = ['EMAIL', 'SMS'];
const RUN_DAYS = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'ALL'];

const AddActionModal = ({ open, onClose, onActionAdded, action }) => {
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
    }, [action]);

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
                runOnTime: formattedTime
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