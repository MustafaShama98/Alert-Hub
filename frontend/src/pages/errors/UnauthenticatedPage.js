import React from 'react';
import { Box, Button, Container, Typography } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';

const UnauthenticatedPage = () => {
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
        <AccountCircleIcon sx={{ fontSize: 100, color: 'primary.main', mb: 2 }} />
        <Typography variant="h4" component="h1" gutterBottom>
          Authentication Required
        </Typography>
        <Typography variant="h6" component="h2" gutterBottom>
          Please log in to access this page
        </Typography>
        <Typography color="text.secondary" paragraph>
          You need to be logged in to view this content.
        </Typography>
        <Box sx={{ mt: 2, display: 'flex', gap: 2 }}>
          <Button
            variant="contained"
            color="primary"
            onClick={() => navigate('/login')}
          >
            Log In
          </Button>
          <Button
            variant="outlined"
            color="primary"
            onClick={() => navigate('/signup')}
          >
            Sign Up
          </Button>
        </Box>
      </Box>
    </Container>
  );
};

export default UnauthenticatedPage; 