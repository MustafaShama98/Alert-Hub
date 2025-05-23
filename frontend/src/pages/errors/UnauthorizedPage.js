import React from 'react';
import { Box, Button, Container, Typography } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import LockIcon from '@mui/icons-material/Lock';

const UnauthorizedPage = () => {
  const navigate = useNavigate();

  return (
    <Container maxWidth="sm">
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          minHeight: '100vh',
          textAlign: 'center',
        }}
      >
        <LockIcon sx={{ fontSize: 100, color: 'error.main', mb: 2 }} />
        <Typography variant="h1" component="h1" gutterBottom>
          403
        </Typography>
        <Typography variant="h5" component="h2" gutterBottom>
          Access Denied
        </Typography>
        <Typography color="text.secondary" paragraph>
          You don't have permission to access this page.
        </Typography>
        <Button
          variant="contained"
          color="primary"
          onClick={() => navigate('/')}
          sx={{ mt: 2 }}
        >
          Go to Dashboard
        </Button>
      </Box>
    </Container>
  );
};

export default UnauthorizedPage; 